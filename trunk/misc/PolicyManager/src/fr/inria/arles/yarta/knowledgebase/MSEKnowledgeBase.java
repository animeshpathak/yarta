/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.text.html.HTMLDocument.Iterator;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;



import fr.inria.arles.yarta.Criteria;
import fr.inria.arles.yarta.knowledgebase.interfaces.*;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.resources.Person;
/**
 * This is the KB Class implementing the KnowledgeBase interface. In the current version we only allow one implementation of the interfaces, 
 * which will be named MSEKnowledgeBase. In the future we might consider a factory mechanism or a way to allow the dynamic switch between
 * different implementation (at development time).
 * Implementations of the interfaces Node, Triple and Literal are only visible to this class. The application developer will refer to this class name 
 * when creating a new instance of KnowledgeBase.
 * @author alessandra
 */
public class MSEKnowledgeBase implements KnowledgeBase{
	
	/**
	 * The namespace associated to this user KB
	 */
	private String userNamespace;
	/**
	 * The model containing the concepts
	 */
	protected Model model;
	private String userId;
	private MSEPolicyManager myPolicyManager; 
	private YLogger logger;
	
	private String policyFile;
	
	public static String MSE_NAMESPACE = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#";
	public static String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";	
	public static String RDFS_NAMESPACE = "http://www.w3.org/2000/01/rdf-schema#";
	public static String PROPERTY_USERID_URI = MSE_NAMESPACE + "userId";
	public static String PROPERTY_TYPE_URI = RDF_NAMESPACE + "type";
	public static String PROPERTY_RDFPROPERTY_URI = RDF_NAMESPACE + "Property";
	
	//private String filePR = "src/fr/inria/arles/yarta/knowledgebase/policy/policies";
	
	
	public MSEKnowledgeBase()	{
		
		// This constructor creates a model with reified statements, but no inference.
		// We keep the original model with no inference in order to have a clean knowledge base. We create the inferred model when needed (e.g., policy checking, domain/range checking)
		this.model = ModelFactory.createDefaultModel();
				
		this.myPolicyManager = new MSEPolicyManager(model);
		
		this.logger = YLoggerFactory.getLogger();
	}
	
	/**
	 * initialize the KB, perhaps with a set of initial 
	 * nodes, which form the ontology. If the constructor MSEKnowledgeBase(String namespace, String fileKB)
	 * has been called, it overwrites the model (and the previous is lost)
	 * @throws AccessControlException 
	 */
	public void initialize(String fileKB, String namespace, String policyPath, String userId) throws KBException{
		
		this.userNamespace = namespace;
		this.userId = userId;
		this.policyFile = policyPath;
		
		File f = new File(fileKB);
		InputStream i = null;
		try {
			i = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			logger.e("MSEKnowledgeBase.initialize", "MSE base ontology file not found", e);
			e.printStackTrace();
		}
		model.read(i,null,"RDF/XML");
		
		
		// Add the node describing the user
		
		Node user_node = new MSEResource(userNamespace+userId, MSE_NAMESPACE+"Person");
		
		this.myPolicyManager.initializePR(this.policyFile, this.userNamespace, user_node);
		
		if (this.addResource(user_node, userId) == null)	
			logger.w("MSEKnowledgeBase.initialize: ", "Could not add the owner node");
		
		else
			logger.d("MSEKnowledgeBase.initialize: ", "KB Initialize completed");
		
		
	}
	
	@Override
	public Node addResource(String node_url, String type_url, String requestorId) throws KBException {
		
		Node s = new MSEResource(node_url,type_url);
		Node p = new MSEResource(PROPERTY_TYPE_URI, PROPERTY_RDFPROPERTY_URI);
		Node o = this.getResourceByURI(type_url, requestorId);
		
		Triple data = new MSETriple(s,p,o);
		
		// 1.Check errors
		if (data == null) {
			logger.e("MSEKnowledgeBase.addResource", "Error creating the triple: " + s.toString() + "," + p.toString() + "," + o.toString());
			return null;
		}
		
		 // 2.Check if type is actually a concept in the base MSE ontology 
		
		if (model.containsResource(model.createResource(type_url)) == false)	{
		 	logger.e("MSEKnowledgeBase.addResource", "Type " + type_url + " is not in the data model");
		 	return null;
		 }
		
		// 3. Check policies
		
		// The policy manager needs the inferred model together with the original one
		Model aux = ModelFactory.createRDFSModel(this.model);
		
		if ( !(myPolicyManager.isAllowed("add", requestorId, aux, data)) )	{
			AccessControlException e = new AccessControlException("Access control failed: Requestor " + requestorId + " was not allowed to add the triple: " + s.toString() + "," + p.toString() + "," + o.toString());
			throw(e);
		}
		
		// 4. Perform add
		 
		if (model.add(model.createResource(node_url), model.createProperty(RDF_NAMESPACE + "type"), model.createResource(type_url)) == null)	{
			 logger.e("MSEKnowledgeBase.addResource", "Could not create resource " + node_url);
			 return null;
		 }
		 
		 else return new MSEResource(node_url, type_url);
	}
	
	@Override
	public Node addResource(Node node, String requestorId) throws KBException {
		
		// 1. Check errors
		
		if(node.whichNode() != 0 || node == null)	{
			logger.e("MSEKnowledgeBase.addResource", "Wrong input node");
			return null;
		}
		
		// 2. Check if type is actually a concept in the base MSE ontology 
		if (model.containsResource(model.createResource(node.getType())) == false)	{
		 	logger.e("MSEKnowledgeBase.addResource", "Type " + node.getType() + " is not in the data model");
		 	return null;
		 }
		
		Node s = new MSEResource(node.getName(), node.getType());
		Node p = new MSEResource(PROPERTY_TYPE_URI, PROPERTY_RDFPROPERTY_URI);
		Node o = getResourceByURI(node.getType(), userId);
		
		Triple data = new MSETriple(s,p,o);
		
		if (data == null) {
			logger.e("MSEKnowledgeBase.addResource", "Error creating the triple " + s.toString() + "," + p.toString() + "," + o.toString());
			return null;
		}
		
		// 3. Check policies
		Model aux = ModelFactory.createRDFSModel(this.model);
		
		if ( !(myPolicyManager.isAllowed("add", requestorId, aux, data)) )	{
			AccessControlException e = new AccessControlException("Access control failed: Requestor " + requestorId + " was not allowed to add the triple: " + s.toString() + "," + p.toString() + "," + o.toString());
			throw(e);
		}
		
		// 4. Perform add
		
		if (model.add(model.createResource(node.getName()), model.createProperty(RDF_NAMESPACE + "type"), model.createResource(node.getType())) == null)	{
			 logger.e("MSEKnowledgeBase.addResource", "Could not create resource " + node.getName());
			 return null;
		 }
		 
		 else return node;
	}
	
	/**
	 * Create a typed literal. The second argument should be an XSD data type (see http://www.w3.org/2001/XMLSchema), chosen from the data types supported by the KnowledgeBase
	 * Currently we rely on a subset of XSD data types supported by Jena: for additional data types see class com.hp.hpl.jena.datatypes.xsd.XSDDatatype)
	 *
	 */
	@Override
	public Node addLiteral(String value, String dataType, String requestorId) throws KBException {
		

		MSELiteral l = new MSELiteral(value, dataType);	// this could raise exception 
		
		model.createTypedLiteral(value, dataType);
		// We do not actually add any knowledge to the KB here, so I do not call the policy manager
	 
	return l;
		
	}
	
	
	
	@Override
	public Node getResourceByURI(String URI, String requestorId) throws AccessControlException{
		
		/**
		 * NOTE THAT THERE MIGHT BE MORE THAN ONE NODE CORRESPONDING TO THE SAME URI, e.g., in case of multiple types for the same URI.
		 * Right now we return the first one in a random way (but always correct and included in the base RDF model, i.e., not inferred)
		 */
		
		Model aux = ModelFactory.createRDFSModel(this.model);
		
			
			SimpleSelector sel = new SimpleSelector(model.getResource(URI), model.getProperty(PROPERTY_TYPE_URI), (Object) null);
			
			// 1. Check errors
			if (sel == null)	{
				logger.e("MSEKnowledgeBase.getResourcebyURI", "Could not get the following MSEResource node: " + URI);
				return null;
			}
			
			Graph g = new MSEGraph(model.query(sel));
			
			if (g == null || g.isEmpty()) {
				logger.e("MSEKnowledgeBase.getResourceByURI", "Graph is emtpy, requested URI is not in the Knowledge Base: " + URI);
				return null;
			}
			
			// 2. Check policies
			if ( !(myPolicyManager.isAllowed("read", requestorId, aux, g)) )	{
				AccessControlException e = new AccessControlException("Access control failed: Requestor " + requestorId + " was not allowed to read node: " + URI);
				throw(e);
				}
			
			// 3. If ok return URI
			
			else 	{
				Node n1 = new MSEResource(g.getTriples().get(0).getSubject().toString(), g.getTriples().get(0).getObject().toString());
				return n1;
			}
		
	}
	
	@Override
	public Triple addTriple(Node s, Node p, Node o, String requestorId) throws KBException {
		
		Resource sub;
		Property prop;
		RDFNode obj;
		
		if (s == null || p == null || o == null)	{
			logger.e("MSEKnowledgeBase.addTriple", "Null input parameter");
			return null;
		}
		
		// Check if subject and predicate are not variable or literals
		if (s.whichNode() != 0 || p.whichNode() != 0)	{
			logger.e("MSEKnowledgeBase.addTriple", "Cannot add a triple where subject or property are not MSEResource");
			return null;
		}
		
		sub = model.getResource(s.getName());
		prop = model.getProperty( ((MSEResource)p).getNamespace(), ((MSEResource)p).getRelativeName());
		
		if (o.whichNode() == 0) 	{		// object is a resource node
			obj = (Resource) model.getResource(o.getName());
			}
		
		else if	(o.whichNode() == 1) {	// object is a literal
			obj = (Literal) model.createTypedLiteral(o.getName(), o.getType());	
		}	
		
		else {		// object is a variable node --> error
			logger.e("MSEKnowledgeBase.addTriple", "Wrong object node in triple: " + s.toString() + "," + p.toString() + "," + o.toString());
			return null;
		}
		
		
		// Check if type of subject and property are actual concepts in the base MSE ontology 
		if (model.containsResource(model.createResource(s.getType())) == false || model.containsResource(model.createResource(p.getName())) == false)	{
		 	logger.e("MSEKnowledgeBase.addTriple", "Type of subject or property node is not in the data model");
		 	return null;
		 }
		
		
		// Check domain and range
		
		// 1. Create a model with inference
		Model aux = ModelFactory.createRDFSModel(this.model);
		
		// 2. Add the triple to force inference
		if (aux.add(sub,prop,obj) == null)	{
			logger.e("MSEKnowledgeBase.addTriple", "Could not add the triple: " + s.toString() + "," + p.toString() + "," + o.toString());
			return null;
		}
		
		// 3. Get domain and range of added property.
		Model domainModel = aux.query(new SimpleSelector(model.getResource(p.getName()), model.getProperty(this.RDFS_NAMESPACE+"domain"), (Object) null));
		Model rangeModel = aux.query(new SimpleSelector(model.getResource(p.getName()), model.getProperty(this.RDFS_NAMESPACE+"range"), (Object) null));
		
		// In general there will only be one domain and range but we allow for multiple ones
		StmtIterator iterD = domainModel.listStatements();
		StmtIterator iterR = rangeModel.listStatements();
		
		// 4. Get types of subject and object (only if resource). In general we will obtain multiple statements for the domain and possibly range (due to inference)
		Model subjectModel = aux.query(new SimpleSelector(model.getResource(s.getName()), model.getProperty(PROPERTY_TYPE_URI), (Object) null));
		StmtIterator iter1 = subjectModel.listStatements();
		
		Model objectModel;
		StmtIterator iter2;
		
		
		// 5. Perform check for domain
		// If it is not sure about domain and range it only gives a warning. This will let the code run in case no domain and range are defined for the property
		if (domainModel == null || domainModel.isEmpty()) {
			logger.w("MSEKnowledgeBase.addTriple", "Could not determine if subject of the triple is in property domain");
		}
		
		// If the domain is wrong it gives error and returns null 
		else {
			boolean match = false;
			
			while(iterD.hasNext()){
				
				Statement d = iterD.nextStatement();
			
				while(iter1.hasNext())	{
					
					if (iter1.nextStatement().getObject().toString().equals((String)d.getObject().toString()) ) {
						match = true;
						break;
						}	
					}
				if (match)	{
					break;
					}
				}
			if(!match)	{
			logger.e("MSEKnowledgeBase.addTriple", "Subject does not match the property domain");
			return null;
			}
		}
		
		// 6. Perform check for range 
		switch(o.whichNode())	{
		
		case(0):	{	// resource
		
			objectModel = aux.query(new SimpleSelector(model.getResource(o.getName()), model.getProperty(PROPERTY_TYPE_URI), (Object) null));
			iter2 = objectModel.listStatements();
			
			if (rangeModel == null || rangeModel.isEmpty()) {
				logger.w("MSEKnowledgeBase.addTriple", "Could not determine if object of the triple is in property range");
			}
			
			else {
				boolean match = false;
				
				while(iterR.hasNext()){
					
					Statement r = iterR.nextStatement();
				
					while(iter2.hasNext())	{
						
						if (iter2.nextStatement().getObject().toString().equals(r.getObject().toString()) ) {
							match = true;
							break;
							}	
						}
					if (match)	{
						break;
						}
					}
				if(!match)	{
				logger.e("MSEKnowledgeBase.addTriple", "Object does not match the property range");
				return null;
				}
			}
		} break;
		
		case(1):	{
			
			boolean match = false;
			
			while(iterR.hasNext())	{
				
				if (iterR.nextStatement().getObject().toString().equals(o.getType()) ) {
					match = true;
					break;
					}	
				}
			if(!match)	{
				logger.e("MSEKnowledgeBase.addTriple", "Object does not match the property range");
				return null;
				}
		} break;
	}
	
		// 7. Policy check
		
		if ( !(myPolicyManager.isAllowed("add", requestorId, aux, new MSETriple(s, p, o))) )	{
			
			AccessControlException e = new AccessControlException("Access control failed: Requestor " + requestorId + " was not allowed to add the triple: " + s.toString() + "," + p.toString() + "," + o.toString());
			throw(e);
		}
		
		
		// 8. Adding the triple to the actual KB
		if (model.add(sub,prop,obj) == null)	{
			logger.e("MSEKnowledgeBase.addTriple", "Could not add the triple: " + s.toString() + "," + p.toString() + "," + o.toString());
			return null;
		}
		
		
		else return new MSETriple(s, p, o);
		
	}
	
	@Override
	public MSEGraph addGraph(Graph g, String requestorId) throws Exception {
		
		if(g == null || g.isEmpty())	{
			logger.e("MSEKnowledgeBase.addGraph", "Null input graph");
			return null;
		}
		
		// Policy check
		// The policy manager needs the inferred model together with the original one
		Model aux = ModelFactory.createRDFSModel(this.model);
		
		if ( !(myPolicyManager.isAllowed("add", requestorId, aux, g)) )	{
			
			AccessControlException e = new AccessControlException("Access control failed: Requestor " + requestorId + " was not allowed to add the graph:\n " + g.toString());
			throw(e);
		}

		// Model new_info = ModelFactory.createOntologyModel();
		// not sure this cast will work
//		Model new_info = ((MSEGraph)g).getJenaModel();
//		Model inferred = ModelFactory.createRDFSModel(new_info);
//		
//		if (model.add(new_info) != null)
//			return (MSEGraph)g;
//		else return null;
		
		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
		
		g.writeGraph(oos, null);
		oos.close();
		baos.close();
		
		ByteArrayInputStream baio = new java.io.ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new java.io.ObjectInputStream(baio);
		
		
		if ((model.read(ois, "")) == null) 	{
			baio.close();
			ois.close();
			return null;
		}
		else {
			baio.close();
			ois.close();
			return (MSEGraph)g;
		}
		
		
	}
	
	
	@Override
	public MSEGraph getAllProperties(Node s, String requestorId) throws AccessControlException {
		
		Node p, o;
		Triple data;
		
		// Build nodes for the policy manager
		p = new MSEVariableNode("pred", PROPERTY_RDFPROPERTY_URI);
		o = new MSEVariableNode("obj", null);
		data = new MSETriple(s,p,o);
		
		
		SimpleSelector sel = new SimpleSelector(model.getResource(s.getName()), (Property) null, (Object) null);
		
		// The policy manager needs the inferred model together with the original one
		Model aux = ModelFactory.createRDFSModel(this.model);
		
		if (data == null || sel == null){
			logger.e("MSEKnowledgeBase.getAllProperties", "Could not get all properties of node " + s.toString());
			return null;
		}
		
		// checking if there was something returned
		Model m = model.query(sel);
		
		if (m.isEmpty()){
			logger.w("MSEKnowledgeBase.getAllProperties", "Properties of node " + s.toString() + " are not set");
			return null;

		}
		
		// Check permission
		
		if ( !(myPolicyManager.isAllowed("read", requestorId, aux, data)) )	{
			
			AccessControlException e = new AccessControlException("Access control failed: Requestor " + requestorId + " was not allowed to get all properties of node " + s.toString());
			throw(e); 
		}
			
		else return new MSEGraph(m);
		
	}

	@Override
	public MSEGraph getProperty(Node s, Node o, String requestorId) throws AccessControlException {
		
		Node p;
		Triple data;
		SimpleSelector sel;
		
		// Build nodes for the policy manager
		p = new MSEVariableNode("pred", PROPERTY_RDFPROPERTY_URI);
		data = new MSETriple(s,p,o);

		// The policy manager needs the inferred model together with the original one
		Model aux = ModelFactory.createRDFSModel(this.model);
		
		
		// 1. Check if object is literal or resource
		if(o.whichNode() == 0)	{
			sel = new SimpleSelector(model.getResource(s.getName()), (Property) null, model.getResource(o.getName()));	
		}
		
		else if (o.whichNode() == 1)	{
			sel = new SimpleSelector(model.getResource(s.getName()), (Property) null, model.createTypedLiteral(o.getName(), o.getType()));
		}
		
		else	{
			logger.e("MSEKnowledgeBase.getPropertySubject", "Error in object argument");
			return null;
		}
		
	
		if (data == null || sel == null)	{
			logger.w("MSEKnowledgeBase.getProperty", "Could not get all properties between node " + s.toString() + " and node " + o.toString());
			return null;
		}
		
		// 2. Check if there was something returned
		Model m = model.query(sel);
		
		if (m.isEmpty()){
			logger.w("MSEKnowledgeBase.getProperty", "No properties are set between nodes " + s.toString() + " and " + o.toString());
			return null;

		}
		
		// 3. Check permission
		
		if ( !(myPolicyManager.isAllowed("read", requestorId, aux, data)) )	{
			AccessControlException e = new AccessControlException("Access control failed: Requestor " + requestorId + " was not allowed to get properties between node " + s.toString() + "and node " + o.toString());
			throw(e);
		}
		
		else return new MSEGraph(m);
	}

	@Override
	public MSEGraph getPropertyObject(Node s, Node p, String requestorId) throws AccessControlException {
		
		Node o;
		Triple data;
		
		// Build nodes for the policy manager
		o = new MSEVariableNode("obj", null);
		data = new MSETriple(s,p,o);
		
		SimpleSelector sel = new SimpleSelector(model.getResource(s.getName()), model.getProperty(p.getName()), (Object) null);
		
		// The policy manager needs the inferred model together with the original one
		Model aux = ModelFactory.createRDFSModel(this.model);
		
		// Check errors
		if (data == null|| sel == null)	{
			logger.w("MSEKnowledgeBase.getPropertyObject", "Could not get values of property " + p.toString() + " of node " + s.toString());
			return null;
		}
		
		// checking if there was something returned
		Model m = model.query(sel);
		
		if (m.isEmpty()){
			logger.w("MSEKnowledgeBase.getPropertyObject", "Object of property " + p.toString() + " of node " + s.toString() + " is not set");
			return null;

		} // Check permission
		
		if ( !(myPolicyManager.isAllowed("read", requestorId, aux, data)) )	{
			
			AccessControlException e = new AccessControlException("Access control failed: Requestor " + requestorId + " was not allowed to get the value of property " + p.toString() + "for node " + s.toString());
			throw(e);
		}
		
		else return new MSEGraph(m);
	}

	@Override
	public MSEGraph getPropertySubject(Node p, Node o, String requestorId) throws KBException {
		
		Node s;
		Triple data;
		SimpleSelector sel;
		
		// Build nodes for the policy manager
		s = new MSEVariableNode("sub", null);
		data = new MSETriple(s,p,o);
		Model aux = ModelFactory.createRDFSModel(this.model);
		
		
		// 1. Check if object is resource or literal
		if(o.whichNode() == 0)	{
			sel = new SimpleSelector((Resource)null, model.getProperty(p.getName()), model.getResource(o.getName()));	
		}
		
		else if (o.whichNode() == 1)	{
			sel = new SimpleSelector(null, model.getProperty(p.getName()), model.createTypedLiteral(o.getName(), o.getType()));
		}
		
		else	{
			logger.e("MSEKnowledgeBase.getPropertySubject", "Error in object argument");
			return null;
		}
		
		// 2. Checking if there was something returned
		Model m = model.query(sel);
		
		if (m.isEmpty()){
			logger.w("MSEKnowledgeBase.getPropertySubject", "Subject of property " + p.toString() + " with value " + o.toString() + " is not set");
			return null;

		}
		// 3. Check errors
		if (data == null|| sel == null)	{
			logger.w("MSEKnowledgeBase.getPropertySubject", "Could not get subject of property " + p.toString() + " with value " + o.toString());
			return null;
		}
		
		// 4. Query
			
		
		MSEGraph g = new MSEGraph(m);
		
		
		// 5. Check permission
		if ( !(myPolicyManager.isAllowed("read", requestorId, aux, g)) )	{
			AccessControlException e = new AccessControlException("Access control failed: Requestor " + requestorId + " was not allowed to read the triples: " + g.toString());
			throw(e);
		}
		
		
		//else return g;
		return g;
		
	}

	@Override
	/**
	 * This method seems useless as it is. Maybe a method returning a boolean?
	 * @return the triple if it is contained in the KB, null otherwise
	 */
	public Triple getTriple(Node s, Node p, Node o, String requestorId) throws AccessControlException {
		
		Triple data = new MSETriple(s,p,o);
	
		Resource sub = model.getResource(s.getName());
		Property prop = model.getProperty(p.getName());
		RDFNode obj = null;
		
		// 1. Check type of object node
		switch(o.whichNode())	{
		case 0: obj = (Resource) model.getResource(o.getName());  break;
		case 1: obj = (Literal) model.createTypedLiteral(o.getName(), o.getType());	  break;
		case 2: logger.e("MSEKnowledgeBase.getTriple", "Wrong object node in triple: " + s.toString() + "," + p.toString() + "," + o.toString());  break;
		}
		
		
		// 2. Check errors
		if (sub == null  || prop == null || obj == null || data == null)	{
			logger.e("MSEKnowledgeBase.getTriple", "Could not get the following triple: " + s.toString() + "," + p.toString() + "," + o.toString());
			return null;
		}

		// 3. Check inclusion in KB
		if (!model.contains(sub, prop, obj)) {
			logger.w("MSEKnowledgeBase.getTriple", "The triple (" + p.toString() + o.toString() + "is not contained in the Knowledge Base");
			return null;
		}
		
		// 4. Check permission
		
		// The policy manager needs the inferred model together with the original one
		Model aux = ModelFactory.createRDFSModel(this.model);
		
		if ( !(myPolicyManager.isAllowed("read", requestorId, aux, data)) )	{
			AccessControlException e = new AccessControlException("Access control failed: Requestor " + requestorId + "was not allowed to get triple " + data.toString());
			throw(e);
		}
		
		else return data;
	}

	@Override
	public Triple removeTriple(Node s, Node p, Node o, String requestorId) throws KBException {
		
		Triple data = new MSETriple(s,p,o);
		
		
		Resource sub = model.getResource(s.getName());
		Property prop = model.getProperty(p.getName());
		RDFNode obj = null;
		
	
		// 1. Check type of object node
		switch(o.whichNode())	{
		case 0: obj = (Resource) model.getResource(o.getName()); break;
		case 1: obj = (Literal) model.createTypedLiteral(o.getName(), (RDFDatatype) null); break; 
		case 2: logger.e("MSEKnowledgeBase.removeTriple", "Wrong object node in triple: " + data.toString()); break;
		}
		
		// 2. Check errors
		if (sub == null  || prop == null || obj == null || data == null)	{
			logger.e("MSEKnowledgeBase.removeTriple", "Could not remove the following triple: " + data.toString());
		}
		
		// 3. Check inclusion in the knowledge base
		if (!model.contains(sub, prop, obj)) {
			logger.e("MSEKnowledgeBase.removeTriple", "The triple: " + data.toString() + "is not contained in the knowledge base");
			return null;
		}
		
		// 4. Check permission
		
		// The policy manager needs the inferred model together with the original one
		Model aux = ModelFactory.createRDFSModel(this.model);
		
		if ( !(myPolicyManager.isAllowed("remove", requestorId, aux, data)) )	{
			AccessControlException e = new AccessControlException("Access control failed: Requestor " + requestorId + "was not allowed to remove triple " + data.toString());
			throw(e);
		}
		
		
		if (model.remove(sub, prop, obj) == null) {
			logger.e("MSEKnowledgeBase.removeTriple", "Could not remove the following triple: " + data.toString());
			return null;
			}
		
		else return data;
	}
	

	@Override
	public MSEGraph queryKB(Criteria c, String requestorId) throws AccessControlException {
			

			ResultSet results;
			

		if (c.getLang().equalsIgnoreCase("SPARQL"))		{

			// 1. Query the KB
			
			try	{
			com.hp.hpl.jena.query.Query query = QueryFactory.create(c.getConditions());
			QueryExecution q = QueryExecutionFactory.create(query, model);
			results = (ResultSet) q.execSelect();
			q.close();
			}
			catch(QueryException e)	{
				
				logger.e("KnowledgeBase.queryKB", "Could not query the KB", e);
				return null;
			}
			
			MSEGraph data = new MSEGraph(ResultSetFormatter.toModel(results));
			
			// 2. Check errors
			if (data == null || data.isEmpty())	{
				logger.e("MSEKnowledgeBase.queryKB", "Query result is emtpy");
				return null;
			}
			
			// 3. Check permission
			
			// The policy manager needs the inferred model together with the original one
			Model aux = ModelFactory.createRDFSModel(this.model);
			
			if ( !(myPolicyManager.isAllowed("read", requestorId, aux, data)) )	{
				AccessControlException e = new AccessControlException("Access control failed: Requestor " + requestorId + " was not allowed to execute query");
				throw(e);
			}
			
			else return data;
		
		}
		
		else 	{
			logger.e("KnowledgeBase.queryKB", "Query language is not supported -- Could not execute query");
			return null;	// currently only SPARQL supported
		}
	}

	@Override
	public String getMyNameSpace(){
		return this.userNamespace;
	}
	
	/**
	 * 
	 * @param dataTypeURI - the data type of the Node
	 * @return - true if datatype is string, int, boolean otr double, false otherwise (Note: RDF plain literals are not supported because they create 
	 * troubles with query matchinh. Note that comments, seeAlso, laber properties all have PlainLiteral as a datatype, so they SHOULD BE REMOVED from the RDF file
	 * before fetching it into the KB
	 */
	
	public static boolean isSupportedDataType(String dataTypeURI)	{
		
		if (dataTypeURI == null)	{
			System.err.println("MSEKnowledgeBase.isSupportedDataType: Input argument was null");
			return false;
		}
			
			
		if ( dataTypeURI.equals(MSEKnowledgeBase.XSD_STRING) || dataTypeURI.equals(MSEKnowledgeBase.XSD_INT) ||
				dataTypeURI.equals(MSEKnowledgeBase.XSD_BOOLEAN) || dataTypeURI.equals(MSEKnowledgeBase.XSD_DOUBLE) )
		return true;
		
		
		else return false;
		
	}
	
	// Note that the returned graph can be empty
	@Override
	// public MSEGraph getKB(String requestorId) throws KBException  {
		public MSEGraph getKB(String requestorId) throws AccessControlException  {
		
		// The policy manager needs the inferred model, and the graph describing all possible knowledge (but no inference)
	Model aux = ModelFactory.createDefaultModel();
		aux.add(this.model);
	System.out.println("*******AUX MODEL*********");
		aux.write(System.out);
		
		MSEGraph graph_all = new MSEGraph(this.model);
		 graph_all.writeGraph(System.out, "N3");
//		
		 System.out.println("*******FILTERED MODEL*********");
		Model filtered = myPolicyManager.filteredModel("read", requestorId, aux, graph_all);
	
		filtered.write(System.out);
		
		if ( (filtered == null)) 	{
			AccessControlException e = new AccessControlException("Access control failed for requestor " + requestorId);
			throw(e);
		}
		 else return new MSEGraph(filtered); 
		
		//else 
		//	return graph_all;
	}
	
//	private MSEGraph getFilteredKB(String requestorId){
//		
//		MSEGraph graph_all = new MSEGraph(this.model);
//		
//		ArrayList<Triple> triple_list = graph_all.getTriples();
//		
//		//Iterator<Triple> i = triple_list.iterator();
//		
//		return null;
//	}

}
	


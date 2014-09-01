package fr.inria.arles.yarta.knowledgebase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import fr.inria.arles.yarta.Criteria;
import fr.inria.arles.yarta.knowledgebase.interfaces.*;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.resources.Person;

/**
 * This is the KB Class implementing the KnowledgeBase interface. In the current
 * version we only allow one implementation of the interfaces, which will be
 * named MSEKnowledgeBase. In the future we might consider a factory mechanism
 * or a way to allow the dynamic switch between different implementation (at
 * development time). Implementations of the interfaces Node, Triple and Literal
 * are only visible to this class. The application developer will refer to this
 * class name when creating a new instance of KnowledgeBase.
 */
public class MSEKnowledgeBase implements KnowledgeBase {

	private String userNamespace;
	private String userId;

	/**
	 * The model containing the concepts
	 */
	protected Model model;
	protected Model inferredModel;

	protected MSEPolicyManager policyManager;
	private static YLogger logger;

	private String policyFile;

	static final String TYPE_PERSON_NAME = "Person";
	static final String TYPE_STRING_URI = "http://www.w3.org/2001/XMLSchema#string";

	public MSEKnowledgeBase() {

		// This constructor creates a model with reified statements, but no
		// inference.
		// We keep the original model with no inference in order to have a clean
		// knowledge base. We create the inferred model when needed (e.g.,
		// policy checking, domain/range checking)
		model = ModelFactory.createDefaultModel();
		inferredModel = ModelFactory.createRDFSModel(model);

		policyManager = new MSEPolicyManager();

		logger = YLoggerFactory.getLogger();

		// the line below just tells the user which jar he is using.
		if (!PERFORM_CHECKS) {
			logger.e("MSEKnowledgeBase",
					"Checks are not being performed. Use at your own risk!!");
		}
	}

	/**
	 * initialize the KB, perhaps with a set of initial nodes, which form the
	 * ontology. If the constructor MSEKnowledgeBase(String namespace, String
	 * fileKB) has been called, it overwrites the model (and the previous is
	 * lost)
	 * 
	 * @throws AccessControlException
	 */
	@Override
	public void initialize(String fileKB, String namespace, String policyPath,
			String userId) throws KBException {

		// save this for further optimized queries
		this.userId = userId;

		userNamespace = namespace;
		policyFile = policyPath;

		File f = new File(fileKB);
		InputStream i = null;
		try {
			i = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			logger.e("MSEKnowledgeBase.initialize",
					"MSE base ontology file not found", e);
			e.printStackTrace();
		}

		synchronized (model) {
			model.read(i, null, "RDF/XML");
		}

		// Add the node describing the user

		Node userNode = new MSEResource(userNamespace + "Person_" + userId,
				MSE_NAMESPACE + TYPE_PERSON_NAME);

		policyManager.initialize(policyFile, userNamespace, userNode);

		// New way of creating the user_node
		// add person node to the model
		Resource personResource = model.createResource(userNamespace
				+ "Person_" + userId);

		synchronized (model) {
			if (model.add(personResource,
					model.createProperty(RDF_NAMESPACE + "type"),
					model.createResource(MSE_NAMESPACE + TYPE_PERSON_NAME)) == null) {
				logger.e("MSEKnowledgeBase.init",
						"Could not create person resource for Person_" + userId);
			}
		}

		Literal userIdLiteral = model.createTypedLiteral(userId,
				TYPE_STRING_URI);
		Property userIdProperty = model.createProperty(MSE_NAMESPACE
				+ PROPERTY_USERID_NAME);

		synchronized (model) {
			model.add(personResource, userIdProperty, userIdLiteral);
		}

		logger.d("MSEKnowledgeBase.initialize: ", "KB Initialize completed");
	}

	/**
	 * Used before to send Hello.
	 * 
	 * @param userId
	 */
	public void createPerson(String userId) {
		// New way of creating the user_node
		// add person node to the model
		synchronized (model) {
			Resource personResource = model.createResource(userNamespace
					+ "Person_" + userId);

			if (model.add(personResource,
					model.createProperty(RDF_NAMESPACE + "type"),
					model.createResource(MSE_NAMESPACE + TYPE_PERSON_NAME)) == null) {
				logger.e("MSEKnowledgeBase.init",
						"Could not create person resource for Person_" + userId);
			}

			Literal userIdLiteral = model.createTypedLiteral(userId,
					TYPE_STRING_URI);
			Property userIdProperty = model
					.createProperty(Person.PROPERTY_USERID_URI);

			Property knowsProperty = model
					.createProperty(Person.PROPERTY_KNOWS_URI);

			model.add(personResource, userIdProperty, userIdLiteral);

			Resource me = model.getResource(userNamespace + "Person_"
					+ this.userId);
			model.add(me, knowsProperty, personResource);
		}
	}

	@Override
	public void uninitialize() throws KBException {
		policyManager.uninitialize(policyFile);

		logger.d("MSEKnowledgeBase.uninitialize: ", "uninitialize completed");
	}

	@Override
	public Node addResource(String node_url, String type_url, String requestorId)
			throws KBException {
		if (PERFORM_CHECKS) {
			Node s = new MSEResource(node_url, type_url);
			Node p = new MSEResource(PROPERTY_TYPE_URI,
					PROPERTY_RDFPROPERTY_URI);
			Node o = this.getResourceByURINoPolicies(type_url);

			Triple data = new MSETriple(s, p, o);

			// 1.Check if type is actually a concept in the base MSE ontology
			synchronized (model) {
				if (!model.containsResource(model.createResource(type_url))) {
					logger.e("MSEKnowledgeBase.addResource", "Type " + type_url
							+ " is not in the data model");
					return null;
				}
			}

			// 2. Check policies
			synchronized (model) {
				if (!requestorId.equals(userId)
						&& !(policyManager.isAllowed("add", requestorId,
								inferredModel, data))) {
					AccessControlException e = new AccessControlException(
							"Access control failed: Requestor " + requestorId
									+ " was not allowed to add the triple: "
									+ s.toString() + "," + p.toString() + ","
									+ o.toString());
					throw (e);
				}
			}
		}

		// 3. Perform add
		synchronized (model) {
			if (model.add(model.createResource(node_url),
					model.createProperty(RDF_NAMESPACE + "type"),
					model.createResource(type_url)) == null) {
				logger.e("MSEKnowledgeBase.addResource",
						"Could not create resource " + node_url);
				return null;
			} else {
				return new MSEResource(node_url, type_url);
			}
		}
	}

	@Override
	public Node addResource(Node node, String requestorId) throws KBException {

		// wrap in an if(PERFORM_CHECKS) -- also, s,p,o are never used out of
		// this
		if (PERFORM_CHECKS) {

			// 1. Check errors
			if (node.whichNode() != Node.RDF_RESOURCE || node == null) {
				logger.e("MSEKnowledgeBase.addResource", "Wrong input node");
				return null;
			}

			synchronized (model) {
				// 2. Check if type is actually a concept in the base MSE
				// ontology
				if (!model
						.containsResource(model.createResource(node.getType()))) {
					logger.e("MSEKnowledgeBase.addResource",
							"Type " + node.getType()
									+ " is not in the data model");
					return null;
				}
			}

			Node s = new MSEResource(node.getName(), node.getType());
			Node p = new MSEResource(PROPERTY_TYPE_URI,
					PROPERTY_RDFPROPERTY_URI);
			Node o = getResourceByURINoPolicies(node.getType());

			Triple data = new MSETriple(s, p, o);

			synchronized (model) {
				// 2. Check policies
				if (!(policyManager.isAllowed("add", requestorId,
						inferredModel, data))) {
					AccessControlException e = new AccessControlException(
							"Access control failed: Requestor " + requestorId
									+ " was not allowed to add the triple: "
									+ s.toString() + "," + p.toString() + ","
									+ o.toString());
					throw (e);
				}
			}
		}

		// 3. Perform add
		// TODO use a cache for the property and type here.
		synchronized (model) {
			if (model.add(model.createResource(node.getName()),
					model.createProperty(RDF_NAMESPACE + "type"),
					model.createResource(node.getType())) == null) {
				logger.e("MSEKnowledgeBase.addResource",
						"Could not create resource " + node.getName());
				return null;
			} else {
				return node;
			}
		}
	}

	/**
	 * Create a typed literal. The second argument should be an XSD data type
	 * (see http://www.w3.org/2001/XMLSchema), chosen from the data types
	 * supported by the KnowledgeBase Currently we rely on a subset of XSD data
	 * types supported by Jena: for additional data types see class
	 * com.hp.hpl.jena.datatypes.xsd.XSDDatatype)
	 * 
	 */
	@Override
	public Node addLiteral(String value, String dataType, String requestorId)
			throws KBException {
		MSELiteral l = new MSELiteral(value, dataType);
		synchronized (model) {
			model.createTypedLiteral(value, dataType);
		}
		// TODO: should pass through policy manager
		return l;

	}

	@Override
	public Node getResourceByURI(String URI, String requestorId)
			throws AccessControlException {

		/**
		 * NOTE THAT THERE MIGHT BE MORE THAN ONE NODE CORRESPONDING TO THE SAME
		 * URI, e.g., in case of multiple types for the same URI. Right now we
		 * return the first one in a random way (but always correct and included
		 * in the base RDF model, i.e., not inferred)
		 */

		synchronized (model) {
			if (requestorId.equals(userId)) {
				Resource resource = model.getResource(URI);
				Property property = model.getProperty(PROPERTY_TYPE_URI);
				StmtIterator it = model.listStatements(resource, property,
						(RDFNode) null);

				if (it.hasNext()) {
					Statement st = it.next();
					return new MSEResource(URI,
							((Resource) st.getObject()).getURI());
				} else {
					return null;
				}
			}
		}

		synchronized (model) {

			SimpleSelector sel = new SimpleSelector(model.getResource(URI),
					model.getProperty(PROPERTY_TYPE_URI), (Object) null);

			Graph g = new MSEGraph(model.query(sel));

			if (g == null || g.isEmpty()) {
				logger.e("MSEKnowledgeBase.getResourceByURI",
						"Graph is emtpy, requested URI is not in the Knowledge Base: "
								+ URI);
				return null;
			}

			// wrap in an if(PERFORM_CHECKS)
			if (PERFORM_CHECKS) {
				// 2. Check policies
				if (!(policyManager.isAllowed("read", requestorId,
						inferredModel, g))) {
					AccessControlException e = new AccessControlException(
							"Access control failed: Requestor " + requestorId
									+ " was not allowed to read node: " + URI);
					throw (e);
				}

				// 3. If ok return URI
				else {
					Node n1 = new MSEResource(g.getTriples().get(0)
							.getSubject().toString(), g.getTriples().get(0)
							.getObject().toString());
					return n1;
				}
			} else {
				// No checks need to be performed. So no policy call at all.
				Node n1 = new MSEResource(g.getTriples().get(0).getSubject()
						.toString(), g.getTriples().get(0).getObject()
						.toString());
				return n1;
			}
		}

	}

	/**
	 * This method has exactly the same semantics as the public getResourceByURI
	 * but it does not enforce policy checking. It should be called ONLY by KB
	 * methods that already enforce policy checking to avoid to perform it
	 * twice.
	 */
	@Override
	public Node getResourceByURINoPolicies(String URI) {
		/**
		 * NOTE THAT THERE MIGHT BE MORE THAN ONE NODE CORRESPONDING TO THE SAME
		 * URI, e.g., in case of multiple types for the same URI. Right now we
		 * return the first one in a random way (but always correct and included
		 * in the base RDF model, i.e., not inferred)
		 */
		synchronized (model) {
			StmtIterator it = model.listStatements(model.getResource(URI),
					model.getProperty(PROPERTY_TYPE_URI), (RDFNode) null);

			if (it != null && it.hasNext()) {
				Statement st = it.next();
				return new MSEResource(URI,
						((Resource) st.getObject()).getURI());
			} else {
				return null;
			}
		}
	}

	@Override
	public Triple addTriple(Node s, Node p, Node o, String requestorId)
			throws KBException {

		Resource sub;
		Property prop;
		RDFNode obj;

		if (s == null || p == null || o == null) {
			logger.e("MSEKnowledgeBase.addTriple", "Null input parameter");
			return null;
		}

		// Check if subject and predicate are not variable or literals
		if (s.whichNode() != Node.RDF_RESOURCE
				|| p.whichNode() != Node.RDF_RESOURCE) {
			logger.e("MSEKnowledgeBase.addTriple",
					"Cannot add a triple where subject or property are not MSEResource");
			return null;
		}

		synchronized (model) {

			sub = model.getResource(s.getName());
			prop = model.getProperty(((MSEResource) p).getNamespace(),
					((MSEResource) p).getRelativeName());

			switch (o.whichNode()) {
			case Node.RDF_RESOURCE:
				obj = (Resource) model.getResource(o.getName());
				break;
			case Node.RDF_LITERAL:
				// object is a literal
				// AP -- this often leads to duplicates, since addLiteral has
				// been
				// called before giving it to addTriple
				obj = (Literal) model.createTypedLiteral(o.getName(),
						o.getType());
				break;
			default:
				// object is a variable node --> error
				logger.e("MSEKnowledgeBase.addTriple",
						"Wrong object node in triple: " + s.toString() + ","
								+ p.toString() + "," + o.toString());
				return null;
			}

			if (!requestorId.equals(userId) && PERFORM_CHECKS) {
				// Check if type of subject and property are actual concepts in
				// the
				// base MSE ontology
				if (!model.containsResource(model.createResource(s.getType()))
						|| !model.containsResource(model.createResource(p
								.getName()))) {
					logger.e("MSEKnowledgeBase.addTriple",
							"Type of subject or property node is not in the data model");
					return null;
				}

				// Check domain and range

				// 1. Create a model with inference

				// 2. Add the triple to force inference
				if (inferredModel.add(sub, prop, obj) == null) {
					logger.e("MSEKnowledgeBase.addTriple",
							"Could not add the triple: " + s.toString() + ","
									+ p.toString() + "," + o.toString());
					return null;
				}

				// 3. Get domain and range of added property.
				Model domainModel = inferredModel.query(new SimpleSelector(
						model.getResource(p.getName()), model
								.getProperty(MSEKnowledgeBase.RDFS_NAMESPACE
										+ "domain"), (Object) null));
				Model rangeModel = inferredModel.query(new SimpleSelector(model
						.getResource(p.getName()),
						model.getProperty(MSEKnowledgeBase.RDFS_NAMESPACE
								+ "range"), (Object) null));

				// In general there will only be one domain and range but we
				// allow
				// for multiple ones
				StmtIterator iterD = domainModel.listStatements();
				StmtIterator iterR = rangeModel.listStatements();

				// 4. Get types of subject and object (only if resource). In
				// general
				// we will obtain multiple statements for the domain and
				// possibly
				// range (due to inference)
				Model subjectModel = inferredModel
						.query(new SimpleSelector(
								model.getResource(s.getName()), model
										.getProperty(PROPERTY_TYPE_URI),
								(Object) null));
				StmtIterator iter1;

				Model objectModel;
				StmtIterator iter2;

				// 5. Perform check for domain
				// If it is not sure about domain and range it only gives a
				// warning.
				// This will let the code run in case no domain and range are
				// defined for the property
				if (domainModel == null || domainModel.isEmpty()) {
					logger.w("MSEKnowledgeBase.addTriple",
							"Could not determine if subject of the triple is in property domain");
				}

				// If the domain is wrong it gives error and returns null
				else {
					boolean match = false;

					while (iterD.hasNext()) {

						Statement d = iterD.nextStatement();
						iter1 = subjectModel.listStatements();

						while (iter1.hasNext()) {

							if (iter1.nextStatement().getObject().toString()
									.equals((String) d.getObject().toString())) {
								match = true;
								break;
							}
						}
						if (match) {
							break;
						}
					}
					if (!match) {
						logger.e("MSEKnowledgeBase.addTriple",
								"Subject does not match the property domain");
						return null;
					}
				}

				// 6. Perform check for range
				switch (o.whichNode()) {

				case (Node.RDF_RESOURCE):
					// resource
					objectModel = inferredModel.query(new SimpleSelector(model
							.getResource(o.getName()), model
							.getProperty(PROPERTY_TYPE_URI), (Object) null));
					if (rangeModel == null || rangeModel.isEmpty()) {
						logger.w("MSEKnowledgeBase.addTriple",
								"Could not determine if object of the triple is in property range");
					} else {
						boolean match = false;

						while (iterR.hasNext()) {

							Statement r = iterR.nextStatement();
							iter2 = objectModel.listStatements();

							while (iter2.hasNext()) {

								if (iter2.nextStatement().getObject()
										.toString()
										.equals(r.getObject().toString())) {
									match = true;
									break;
								}
							}
							if (match) {
								break;
							}
						}

						if (!match) {
							logger.e("MSEKnowledgeBase.addTriple",
									"Object does not match the property range");
							return null;
						}
					}
					break;

				case (Node.RDF_LITERAL):

					boolean match = false;

					while (iterR.hasNext()) {

						if (iterR.nextStatement().getObject().toString()
								.equals(o.getType())) {
							match = true;
							break;
						}
					}
					if (!match) {
						logger.e("MSEKnowledgeBase.addTriple",
								"Object does not match the property range");
						return null;
					}
					break;
				}

				// 7. Policy check
				// AP -- shouldn't this be done as soon as the input is
				// received, so
				// all the processing above can be avoided?

				if (!(policyManager.isAllowed("add", requestorId,
						inferredModel, new MSETriple(s, p, o)))) {
					inferredModel.removeAll(sub, prop, obj);

					AccessControlException e = new AccessControlException(
							"Access control failed: Requestor " + requestorId
									+ " was not allowed to add the triple: "
									+ s.toString() + "," + p.toString() + ","
									+ o.toString());
					throw (e);
				}
			}

			inferredModel.removeAll(sub, prop, obj);

			// 8. Adding the triple to the actual KB
			if (p.getName().equals(ThinKnowledgeBase.RDF_NAMESPACE + "li")) {
				Seq seq = model.getSeq(s.getName());
				seq.add(obj);
			} else if (model.add(sub, prop, obj) == null) {
				logger.e(
						"MSEKnowledgeBase.addTriple",
						"Could not add the triple: " + s.toString() + ","
								+ p.toString() + "," + o.toString());
				return null;
			}

			Triple triple = new MSETriple(s, p, o);
			if (requestorId.equals(userId) && helper != null) {
				helper.setTime(triple, System.currentTimeMillis());
			}
			return triple;
		}
	}

	@Override
	public MSEGraph addGraph(Graph g, String requestorId) throws Exception {

		if (g == null || g.isEmpty()) {
			logger.e("MSEKnowledgeBase.addGraph", "Null input graph");
			return null;
		}

		synchronized (model) {

			// wrap in an if(PERFORM_CHECKS)
			if (PERFORM_CHECKS) {

				// Policy check
				// The policy manager needs the inferred model together with the
				// original one

				if (!(policyManager.isAllowed("add", requestorId,
						inferredModel, g))) {

					AccessControlException e = new AccessControlException(
							"Access control failed: Requestor " + requestorId
									+ " was not allowed to add the graph:\n "
									+ g.toString());
					throw (e);
				}
			}

			// Model new_info = ModelFactory.createOntologyModel();
			// not sure this cast will work
			Model newInfo = ((MSEGraph) g).getJenaModel();

			if (model.add(newInfo) != null) {
				return (MSEGraph) g;
			} else {
				return null;
			}
		}
	}

	@Override
	public MSEGraph getAllProperties(Node s, String requestorId)
			throws AccessControlException {

		Node p, o;
		Triple data;

		// Build nodes for the policy manager
		p = new MSEVariableNode("pred", PROPERTY_RDFPROPERTY_URI);
		o = new MSEVariableNode("obj", null);
		data = new MSETriple(s, p, o);

		/*
		 * -------- Implementation option #1 --------- SimpleSelector sel = new
		 * SimpleSelector(model.getResource(s.getName()), (Property) null,
		 * (Object) null);
		 * 
		 * // The policy manager needs the inferred model together with the
		 * original one Model aux = ModelFactory.createRDFSModel(this.model);
		 * 
		 * if (data == null || sel == null){
		 * logger.e("MSEKnowledgeBase.getAllProperties",
		 * "Could not get all properties of node " + s.toString()); return null;
		 * }
		 * 
		 * // checking if there was something returned Model m =
		 * model.query(sel);
		 * 
		 * if (m.isEmpty()){ logger.w("MSEKnowledgeBase.getAllProperties",
		 * "Properties of node " + s.toString() + " are not set"); return null;
		 * 
		 * }
		 * 
		 * // Check permission
		 * 
		 * if ( !(policyManager.isAllowed("read", requestorId, aux, data)) ) {
		 * 
		 * AccessControlException e = new
		 * AccessControlException("Access control failed: Requestor " +
		 * requestorId + " was not allowed to get all properties of node " +
		 * s.toString()); throw(e); }
		 * 
		 * else return new MSEGraph(m);
		 */

		/* ------- Implementation option #2 ----------- */

		// The policy manager needs the inferred model together with the
		// original one

		synchronized (model) {
			Model m = policyManager.getAllowedModel("read", requestorId, model,
					data);

			if (m.isEmpty()) {
				logger.d(
						"MSEKnowledgeBase.getAllProperties",
						"Returned graph was null: either access was denied or there are no data in the KB like the one requested");
				return null;
			} else {
				return new MSEGraph(m);
			}
		}

	}

	@Override
	public MSEGraph getProperty(Node s, Node o, String requestorId)
			throws AccessControlException {

		Node p;
		Triple data;

		// Build nodes for the policy manager
		p = new MSEVariableNode("pred", PROPERTY_RDFPROPERTY_URI);
		data = new MSETriple(s, p, o);

		// The policy manager needs the inferred model together with the
		// original one

		synchronized (model) {
			Model m = policyManager.getAllowedModel("read", requestorId,
					inferredModel, data);

			if (m.isEmpty()) {
				logger.d(
						"MSEKnowledgeBase.getProperty",
						"Returned graph was null: either access was denied or there are no data in the KB like the one requested");
				return null;
			} else {
				return new MSEGraph(m);
			}
		}
	}

	@Override
	public MSEGraph getPropertyObject(Node s, Node p, String requestorId)
			throws AccessControlException {

		// Build nodes for the policy manager
		Node o = new MSEVariableNode("obj", null);
		Triple data = new MSETriple(s, p, o);

		synchronized (model) {
			// The policy manager needs the inferred model together with the
			// original one
			Model m = policyManager.getAllowedModel("read", requestorId,
					inferredModel, data);

			if (m.isEmpty()) {
				logger.d(
						"MSEKnowledgeBase.getPropertyObject",
						"Returned graph was null: either access was denied or there are no data in the KB like the one requested");
				return null;
			} else {
				return new MSEGraph(m);
			}
		}
	}

	@Override
	public MSEGraph getPropertySubject(Node p, Node o, String requestorId)
			throws KBException {

		// Build nodes for the policy manager
		Node s = new MSEVariableNode("sub", null);
		Triple data = new MSETriple(s, p, o);

		// The policy manager needs the inferred model together with the
		// original one
		synchronized (model) {
			Model m = policyManager.getAllowedModel("read", requestorId,
					inferredModel, data);

			if (m.isEmpty()) {
				logger.d(
						"MSEKnowledgeBase.getPropertySubject",
						"Returned graph was null: either access was denied or there are no data in the KB like the one requested");
				return null;
			} else {
				return new MSEGraph(m);
			}
		}
	}

	@Override
	public Triple getTriple(Node s, Node p, Node o, String requestorId)
			throws AccessControlException {

		Triple data = new MSETriple(s, p, o);

		synchronized (model) {
			Resource sub = model.getResource(s.getName());
			Property prop = model.getProperty(p.getName());
			RDFNode obj = null;

			switch (o.whichNode()) {
			case Node.RDF_RESOURCE:
				obj = (Resource) model.getResource(o.getName());
				break;
			case Node.RDF_LITERAL:
				obj = (Literal) model.createTypedLiteral(o.getName(),
						o.getType());
				break;
			case Node.VARIABLE_NODE:
				logger.e("MSEKnowledgeBase.getTriple",
						"Wrong object node in triple: " + s.toString() + ","
								+ p.toString() + "," + o.toString());
				break;
			}

			if (sub == null || prop == null || obj == null || data == null) {
				logger.e("MSEKnowledgeBase.getTriple",
						"Could not get the following triple: " + s.toString()
								+ "," + p.toString() + "," + o.toString());
				return null;
			}

			if (!model.contains(sub, prop, obj)) {
				logger.w("MSEKnowledgeBase.getTriple", "The triple " + data
						+ " is not contained in the Knowledge Base");
				return null;
			}

			if (!requestorId.equals(userId)
					&& !policyManager.isAllowed("read", requestorId,
							inferredModel, data)) {
				throw new AccessControlException(
						"Access control failed: Requestor " + requestorId
								+ "was not allowed to get triple "
								+ data.toString());
			} else {
				return data;
			}
		}
	}

	@Override
	public Triple removeTriple(Node s, Node p, Node o, String requestorId)
			throws KBException {

		Triple data = new MSETriple(s, p, o);

		synchronized (model) {
			Resource sub = model.getResource(s.getName());
			Property prop = model.getProperty(p.getName());
			RDFNode obj = null;

			// 1. Check type of object node
			switch (o.whichNode()) {
			case 0:
				obj = (Resource) model.getResource(o.getName());
				break;
			case 1:
				obj = (Literal) model.createTypedLiteral(o.getName(),
						(RDFDatatype) null);
				break;
			case 2:
				logger.e("MSEKnowledgeBase.removeTriple",
						"Wrong object node in triple: " + data.toString());
				break;
			}

			// 2. Check errors
			if (sub == null || prop == null || obj == null || data == null) {
				logger.e(
						"MSEKnowledgeBase.removeTriple",
						"Could not remove the following triple: "
								+ data.toString());
			}

			// 3. Check inclusion in the knowledge base
			if (!model.contains(sub, prop, obj)
					&& !p.getName().equals(RDF_NAMESPACE + "li")) {
				logger.e("MSEKnowledgeBase.removeTriple",
						"The triple: " + data.toString()
								+ "is not contained in the knowledge base");
				return null;
			}

			if (!requestorId.equals(userId)
					&& !policyManager.isAllowed("remove", requestorId,
							inferredModel, data)) {
				throw new AccessControlException(
						"Access control failed: Requestor " + requestorId
								+ "was not allowed to remove triple "
								+ data.toString());
			}

			if (p.getName().equals(ThinKnowledgeBase.RDF_NAMESPACE + "li")) {
				Seq seq = model.getSeq(s.getName());
				seq.remove(seq.indexOf(obj));
				return data;
			} else if (model.removeAll(sub, prop, obj) == null) {
				logger.e(
						"MSEKnowledgeBase.removeTriple",
						"Could not remove the following triple: "
								+ data.toString());
				return null;
			} else {
				return data;
			}
		}
	}

	@Override
	public MSEGraph queryKB(Criteria c, String requestorId)
			throws AccessControlException {

		ResultSet results;

		synchronized (model) {

			if (c.getLang().equalsIgnoreCase("SPARQL")) {

				// 1. Query the KB

				try {
					com.hp.hpl.jena.query.Query query = QueryFactory.create(c
							.getConditions());
					QueryExecution q = QueryExecutionFactory.create(query,
							model);
					results = (ResultSet) q.execSelect();
					q.close();
				} catch (QueryException e) {
					logger.e("KnowledgeBase.queryKB", "Could not query the KB",
							e);
					return null;
				}

				MSEGraph data = new MSEGraph(
						ResultSetFormatter.toModel(results));

				// 2. Check errors
				if (data == null || data.isEmpty()) {
					logger.e("MSEKnowledgeBase.queryKB",
							"Query result is emtpy");
					return null;
				}

				if (PERFORM_CHECKS) {
					// 3. Check permission

					// The policy manager needs the inferred model together with
					// the
					// original one

					if (!(policyManager.isAllowed("read", requestorId,
							inferredModel, data))) {
						AccessControlException e = new AccessControlException(
								"Access control failed: Requestor "
										+ requestorId
										+ " was not allowed to execute query");
						throw (e);
					} else {
						return data;
					}
				} else
					// not performing checks
					return data;

			} else {
				logger.e("KnowledgeBase.queryKB",
						"Query language is not supported -- Could not execute query");
				return null; // currently only SPARQL supported
			}
		}
	}

	@Override
	public String getMyNameSpace() {
		return this.userNamespace;
	}

	/**
	 * 
	 * @param dataTypeURI
	 *            - the data type of the Node
	 * @return - true if datatype is string, int, boolean or double, false
	 *         otherwise (Note: RDF plain literals are transformed to string
	 *         because they create troubles with query matching. Note that
	 *         comments, seeAlso, label properties all have PlainLiteral as a
	 *         datatype, so they SHOULD BE REMOVED from the RDF file before
	 *         fetching it into the KB
	 */

	public static boolean isSupportedDataType(String dataTypeURI) {
		if (dataTypeURI == null) {
			logger.d("MSEKnowledgeBase.isSupportedDataType", "Input argument ("
					+ dataTypeURI + ") was null, probably an XML plain literal");
			return false;
		}

		if (dataTypeURI.equals(MSEKnowledgeBase.XSD_STRING)
				|| dataTypeURI.equals(MSEKnowledgeBase.XSD_INT)
				|| dataTypeURI.equals(MSEKnowledgeBase.XSD_BOOLEAN)
				|| dataTypeURI.equals(MSEKnowledgeBase.XSD_DOUBLE)) {
			return true;
		}
		return false;
	}

	// Note that the returned graph can be empty
	@Override
	public MSEGraph getKB(String requestorId) throws AccessControlException {
		synchronized (model) {
			Model filtered = policyManager.filteredModel("read", requestorId,
					inferredModel, new MSEGraph(this.model));

			if ((filtered == null)) {
				AccessControlException e = new AccessControlException(
						"MSEKnowledgeBase.getKB: Nothing returned from KB to user "
								+ requestorId + " after policy check");
				throw (e);
			} else {
				return new MSEGraph(filtered);
			}
		}
	}

	@Override
	public List<Triple> getPropertyObjectAsTriples(Node s, Node p,
			String requestorId) throws KBException {
		synchronized (model) {

			if (requestorId.equals(userId)) {
				List<Triple> triples = new ArrayList<Triple>();

				if (p.getName().equals(ThinKnowledgeBase.RDF_NAMESPACE + "li")) {
					Seq seq = model.getSeq(s.getName());

					NodeIterator it = seq.iterator();

					while (it.hasNext()) {
						RDFNode object = it.next();
						triples.add(new MSETriple(s, p,
								new MSEResource(((Resource) object).getURI(),
										object.toString())));
					}
				}
				StmtIterator it = model.listStatements(
						model.getResource(s.getName()),
						model.getProperty(p.getName()), (RDFNode) null);
				while (it.hasNext()) {
					RDFNode object = it.next().getObject();
					if (object.isResource()) {
						triples.add(new MSETriple(s, p,
								new MSEResource(((Resource) object).getURI(),
										object.toString())));
					} else {
						Literal l = (Literal) object;
						triples.add(new MSETriple(s, p, new MSELiteral(l
								.getString(), l.getDatatypeURI())));
					}
				}
				return triples;
			} else {
				Graph g = getPropertyObject(s, p, requestorId);
				return (g == null ? new ArrayList<Triple>() : g.getTriples());
			}
		}
	}

	@Override
	public List<Triple> getPropertySubjectAsTriples(Node p, Node o,
			String requestorId) throws KBException {
		synchronized (model) {
			if (requestorId.equals(userId)) {
				List<Triple> triples = new ArrayList<Triple>();
				try {
					inferredModel = ModelFactory.createRDFSModel(model);

					RDFNode object = null;
					if (o.whichNode() == Node.RDF_LITERAL) {
						object = inferredModel.createTypedLiteral(o.getName());
					} else {
						object = inferredModel.getResource(o.getName());
					}
					StmtIterator it = inferredModel.listStatements(null,
							inferredModel.getProperty(p.getName()), object);

					while (it.hasNext()) {
						Statement st = it.next();
						Resource subject = st.getSubject();
						triples.add(new MSETriple(new MSEResource(subject
								.getURI(), subject.toString()), p, o));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return triples;
			} else {
				Graph g = getPropertySubject(p, o, requestorId);
				return (g == null ? new ArrayList<Triple>() : g.getTriples());
			}
		}
	}

	@Override
	public List<Triple> getPropertyAsTriples(Node s, Node o, String requestorId)
			throws KBException {
		synchronized (model) {
			Graph g = getProperty(s, o, requestorId);
			return (g == null ? new ArrayList<Triple>() : g.getTriples());
		}
	}

	@Override
	public List<Triple> getAllPropertiesAsTriples(Node s, String requestorId)
			throws KBException {
		synchronized (model) {
			Graph g = getAllProperties(s, requestorId);
			return (g == null ? new ArrayList<Triple>() : g.getTriples());
		}
	}

	@Override
	public List<Triple> getKBAsTriples(String requestorId) throws KBException {
		synchronized (model) {
			Graph g = getKB(requestorId);
			return (g == null ? new ArrayList<Triple>() : g.getTriples());
		}
	}

	@Override
	public PolicyManager getPolicyManager() {
		return policyManager;
	}

	public void setUpdateHelper(UpdateHelper helper) {
		this.helper = helper;
	}

	private UpdateHelper helper;
}

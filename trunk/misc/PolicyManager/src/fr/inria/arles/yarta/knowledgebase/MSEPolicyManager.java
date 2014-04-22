/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import fr.inria.arles.yarta.knowledgebase.interfaces.Graph;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;


/**
 * Policy manager working with the Jena implementation of the Knowledge Base.
 * We should out this class in the knowledgebase.policy package, but for now we prefer to keep it here to have the 
 * implementation hidden. This class cannot be public.
 * @author alessandra
 *
 */
 class MSEPolicyManager {
	
	Model initial_model;
	Model results;
	Model finalResults;
	public static String rdf_namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";	
	public static String rdfs_language = "http://www.w3.org/TR/rdf-schema/#";
	public static String policy_namespace = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#";
	private Node dataOwner;
	private String user_namespace;
	private YLogger logger;
	public ArrayList<String> policies = new ArrayList<String>();
	
	public MSEPolicyManager(Model m)	{
		this.initial_model = m;
		this.logger = YLoggerFactory.getLogger();
	}
	
	/** TODO
	 * The function parses all policies into an arrayList that will be used when policies are 
	 * to be evaluated
	 * @param filePR -			a String defining the file where policies are stored
	 * @param userNamespace - 	a String defining the namespace of the user
	 * @param owner -			a Node defining the owner of the rdf data graph
 	*/
	public void initializePR (String filePR, String userNamespace, Node owner){
	File queryFile = new File(filePR);
		ArrayList<String> policiesArrayList = new ArrayList<String>();
		try {
			BufferedReader input = new BufferedReader(new FileReader(queryFile));
			try {
					String line = null; 
				while ((line = input.readLine()) != null) {
					policiesArrayList.add(line);			}
				input.close();
				} 
			catch (IOException e) {
				logger.e("MSEPolicyManager.initializePR", "input/output operation failed", e);
											} 
					}
		catch (FileNotFoundException e) {
			logger.e("MSEPolicyManager.initializePR", "Policies file not found", e);
			}
		policies =  policiesArrayList;
		
		/** TODO
		 * Some check here to ensure that user namespace is not null
		 */
		if (userNamespace == null)
				logger.e("MSEPolicyManager.initializePR", "namespace is null");
		this.user_namespace = userNamespace;
		this.dataOwner = owner ;
		}

	/**
 	 * The function adds temporary triples representing the requestor's ID and the 
 	 * requested action to a JENA model over which it executes the queries parsed into an arraylist. 
	 * It returns the triples accessible to the user in a new model
	 * @param action - 	   a String defining the action to perform by the requestor	
	 * @param requestor -  a String defining the yartaID of the requestor
	 * @param model - 	   a JENA model that contains all the ontology data
	 * @param triple - 	the requested triple
	 * @return 			   true if access is granted, false otherwise
	 */
	public Boolean isAllowed(String action, String requestor, Model model,
			Triple triple) {
		
		Node subject = triple.getSubject();
		Node predicate = triple.getProperty();
		Node object = triple.getObject();

		if (subject == null || object== null || predicate == null
				|| subject.toString() == "" || object.toString()== "" || predicate.toString() == "")
			{
			logger.e("MSEPolicyManager.isAllowed", "requested data is null");
			return false;
			}
			
		if ((action == "add") && (subject.whichNode() == 2 || object.whichNode() == 2 || predicate.whichNode() == 2 ))
			return false;
		else
		{results=ModelFactory.createDefaultModel();	
		createInstanceOfClass(model, policy_namespace,user_namespace, "requestor", requestor);
		addObjectProperty(model, policy_namespace,user_namespace, requestor,"performs",action);	
		Iterator i = this.policies.iterator();
		   while (i.hasNext()) {
               Model temp = null;
               temp = executeQuery(i.next().toString(), model,subject,predicate,object);
               //System.out.println(temp);
              if (temp != null) {
                  if (!temp.isEmpty())
                      {
                    	  results.add(temp);
                      removeTriple(model,policy_namespace, user_namespace, requestor,rdf_namespace + "type","requestor" );
                      removeTriple(model,policy_namespace,user_namespace, requestor,policy_namespace + "performs",action );
                      return true ; }
                                           }
                           }
       removeTriple(model,policy_namespace,user_namespace, requestor,rdf_namespace + "type","requestor" );
       removeTriple(model,policy_namespace,user_namespace, requestor,policy_namespace + "performs",action );
       }
       if (results.isEmpty())
          return false;
       
       return false;
   }
	
	/** TODO
	 * 
	 */
	public Boolean isAllowed(String action, String requestor, Model model,
			Graph g)	{
		if (g.isEmpty())
			{logger.e("MSEPolicyManager.isAllowed", "graph is empty");
			return false;
			}
		if (g == null)
			{logger.e("MSEPolicyManager.isAllowed", "graph is null");
			return false;}
		ArrayList<Triple> requestTriples = new ArrayList<Triple>();
		MSETriple triple;
		requestTriples = g.getTriples();
		boolean answer = false;
		Iterator i = requestTriples.iterator();
		while (i.hasNext()) {
			triple = (MSETriple) i.next();
     		answer = isAllowed(action, requestor, model, triple);
			if (answer == false)
				return false;	
		}
		
		if (answer == false) 
			return false;
		else
			return true;
	}

	public Model isAllowedModel(String action, String requestor, Model model,
            Triple triple) {
         Node subject = triple.getSubject();
         Node predicate = triple.getProperty();
         Node object = triple.getObject();
         
         if ((action == "add") && (subject.whichNode() == 2 || object.whichNode() == 2 || predicate.whichNode() == 2 ))
            {logger.e("MSEKnowledgeBase.isAllowedModel", "action not allowed");
            return null;
            }
         else {
        	 results=ModelFactory.createDefaultModel(); 
            //results=ModelFactory.createOntologyModel(); 
        	 // results=ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF);
        	 
            createInstanceOfClass(model, policy_namespace,user_namespace, "requestor", requestor);
            addObjectProperty(model, policy_namespace,user_namespace, requestor,"performs",action);    
            Iterator i = this.policies.iterator();           
            while (i.hasNext()) {
                Model temp = null;
                temp = executeQuery(i.next().toString(), model,subject,predicate,object);
                if ((temp != null) && (!temp.isEmpty()))
                       {  //if( !temp.toString().contains("requestor"))
                       
                	   results.add(temp);
   
                      }
                                            }
                            
        removeTriple(model,policy_namespace,user_namespace, requestor,rdf_namespace + "type","requestor" );
        removeTriple(model,policy_namespace,user_namespace, requestor,policy_namespace + "performs",action );
        removeTriple(results,policy_namespace,user_namespace, requestor,rdf_namespace + "type","requestor" );
        removeTriple(results,policy_namespace,user_namespace, requestor,policy_namespace + "performs",action );
        }

        
        return results;
    }
    
	/**
	 * 
	 * @param action - requested action
	 * @param requestor - requestor id
	 * @param model - a JENA model that contains all the ontology data
	 * @param g - a graph containing all KB knowledge
	 * @return - a Jena model corresponding to the accessible portion of the KB. Note that this model could be emtpy
	 */
    public Model filteredModel(String action, String requestor, Model model,
            Graph g)    {
    	
    	//finalResults = ModelFactory.createOntologyModel();
    	// This does not need to be OntologyModel, only the temp returned by isAllowedModel
    	finalResults=ModelFactory.createDefaultModel(); 
        if (g.isEmpty())	{
        	logger.e("MSEPolicyManager.filteredModel", "graph is empty");
        	return null;
        }
        if (g == null)	{
        	logger.e("MSEPolicyManager.filteredModel", "graph is null");
        	return null;
        	}
        
        ArrayList<Triple> requestTriples = new ArrayList<Triple>();
        MSETriple triple = null;
        requestTriples = g.getTriples();
        
       // boolean answer = false;
        Iterator<Triple> i = requestTriples.iterator();
        
        while (i.hasNext()) {
            triple = (MSETriple) i.next();
            // logger.d("MSEPolicyManager.filteredModel", "Showing request triples\n" + triple);
            
            Model temp = isAllowedModel(action, requestor, model, triple);
            // logger.d("MSEPolicyManager.filteredModel", "Allowed triple: " + temp.toString());
            if(!temp.isEmpty() && temp!=null)
            finalResults.add(temp);
            // logger.d("MSEPolicyManager.filteredModel", "Current filtered model: " + results.toString());
            
            }
//            if (results.isEmpty() || results == null)
//                return null;
        if (finalResults == null)
          return null;
            else
          return finalResults;  
        
     }
   
	
	/**
     * The function executes a SPARQL query over a JENA model , constructs an RDF graph and returns
     * the answer in a new model
     * @param queryString - a String that presents a SPARQL query 
     * @param model -        a JENA model over which SPARQL queries will be executed
     * @param subject -           a MSE node that presents the subject of the requested triple
     * @param predicate -   a MSE node that presents the predicate of the requested triple
     * @param object -          a MSE node that presents the object of the requested triple
     * @return                 a JENA model that contains the result of the query execution
      */
    private Model executeQuery( String queryString, Model model,
                                Node subject, Node predicate, Node object) {
        Model result = null;
        try {
        	if (compareToRequest (queryString, subject, predicate, object) == true) {
        	queryString = constructQuery (queryString, subject, predicate, object);
        	queryString = constructTriple( queryString,subject, predicate, object) ;
            Query query = QueryFactory.create(queryString);
            try  {
                    QueryExecution qe = QueryExecutionFactory.create(query, model);
                    result = qe.execConstruct();
                    }
            catch (    NullPointerException e) { 
                        logger.e("MSEPolicyManager.executeQuery", "error executing policy", e);
                        return null;
                        }
                    }
        }
        catch (QueryParseException e){
            logger.e("MSEPolicyManager.executeQuery", "error creating the query", e);
            return null; 
            }
        return result;
    }
	
    
	/**
     * The function compares the subject, predicate and object in the construct clause of the policy
     * to the subject, predicate and object sent by the requestor. It returns true if they match
     * and false if they don't.
     * @param query - 		 a String that presents a SPARQL query 
     * @param subject -      a MSE node that presents the subject of the requested triple
     * @param predicate -    a MSE node that presents the predicate of the requested triple
     * @param object -       a MSE node that presents the object of the requested triple
     * @return -			 true if the triple to construct matches the requested triple              
     */
    boolean compareToRequest (String query, Node subject,Node predicate,Node object){
        //	System.out.println(subject.getName().length() + subject.getName());
        	
        	
            if (query.contains("%subject%") == false )
            {//System.out.println(query.substring(query.indexOf("{") + 4+ subject.getName().length(),query.indexOf("{") + 4+ subject.getName().length()+ predicate.getName().length() + 3 + object.getName().length() + 3));
            	if (query.contains("CONSTRUCT {" + "<" + subject.getName() + ">" ) == true || (subject.whichNode()== 2))
                    {//System.out.println(query.substring(query.indexOf("{") + 4+ subject.getName().length(),query.indexOf("{") + 4+ subject.getName().length()+ predicate.getName().length() + 3 + object.getName().length() + 3));
            		if (query.contains("%predicate%") == false)
                        if ((query.contains("CONSTRUCT {" + "<" + subject.getName() + "> <"  + predicate.getName() + ">") == true)
                        		|| ((query.contains("CONSTRUCT {" + "<" + subject.getName() + ">")==true) && (predicate.whichNode()== 2))
                        		|| (subject.whichNode() == 2 && predicate.whichNode() == 2)
                        		|| (subject.whichNode() ==2 && query.substring(query.indexOf("{") + 4+ subject.getName().length(),query.indexOf("{") + 4+ subject.getName().length()+ predicate.getName().length() + 2).toString().contentEquals("<" + predicate.getName() + ">")))
                            if (query.contains("%object%") == false)
                            	{
                            	if (query.contains("CONSTRUCT {<" + subject.getName() + "> <"  + predicate.getName() + "> <"+ object.getName() + ">" ) == true
                                		|| ( (query.contains("CONSTRUCT {" + "<" + subject.getName() + "> <"  + predicate.getName() + ">") == true) && (object.whichNode()== 2))
                                		|| ((query.contains("CONSTRUCT {" + "<" + subject.getName() + ">")==true) && (predicate.whichNode()== 2) && object.whichNode() == 2)
                                		|| (subject.whichNode() == 2 && predicate.whichNode() == 2 && object.whichNode() == 2)
                                		||(subject.whichNode() ==2 && query.substring(query.indexOf("{") + 4+ subject.getName().length(),
                                				query.indexOf("{") + 4+ subject.getName().length()+ predicate.getName().length() + 2 + object.getName().length() + 3).toString().contentEquals("<" + predicate.getName() + "> <" + object.getName() +">"))
                                		|| (subject.whichNode() ==2 && query.substring(query.indexOf("{") + 4+ subject.getName().length(),query.indexOf("{") + 4 + subject.getName().length()+ predicate.getName().length() + 2).toString().contentEquals("<" + predicate.getName() + ">")&& object.whichNode() == 2))
                                    return true;
                                else return false;}
                            else
                            {System.out.println(query.substring(query.indexOf("{") + 4+ subject.getName().length(),query.indexOf("{") + 4+ subject.getName().length()+ predicate.getName().length() + 2 ));
                            	return true;}
                            	
                        else return false;
                    else if (query.contains("%predicate%") == true && query.contains("%object%") == false)
                        if (query.contains("CONSTRUCT {<" + subject.getName() + ">" + " %predicate% " + "<"+ object.getName() + ">" ) == true
                        		|| ((query.contains("CONSTRUCT {<" + subject.getName() + ">" + " %predicate% " ) == true) && (object.whichNode()== 2))
                        		|| (subject.whichNode() ==2 && object.whichNode() == 2)
                        		||subject.whichNode() ==2 && query.substring(query.indexOf("{") + 4+ subject.getName().length(),
                        				query.indexOf("{") + 4+ subject.getName().length()+ 12 + object.getName().length() + 3).toString().contentEquals("<" + object.getName() +">"))
                            return true;
                        else return false;
                    else return true;}
                else return false;}
            else if  (query.contains("%subject%") == true && query.contains("%predicate%") == false)
                if ((query.contains("CONSTRUCT {%subject% <" + predicate.getName() + ">" ) == true)
                		|| (predicate.whichNode()== 2))
                    if (query.contains("%object%") == false)
                        if ((query.contains("CONSTRUCT {%subject% <"  + predicate.getName() + "> <" + object.getName() + ">" ) == true)
                        		|| ((object.whichNode()== 2) && (predicate.whichNode()== 2))
                        		||(query.contains("CONSTRUCT {%subject% <"  + predicate.getName() + ">") == true && object.whichNode() == 2))
                            return true;
                        else
                            return false;
                    else
                        return true;
                        
                else
                    return false;
            else if  (query.contains("%subject%") == true && query.contains("%predicate%") == true)
                if (query.contains("%object%") == false)
                    if ((query.contains("CONSTRUCT {%subject% %predicate% <"+ object.getName() + ">") == true) || (object.whichNode()== 2))
                        return true;
                    else
                        return false;
                else return true;
            
            return false;
        }
        
    
	/**
     * The function constructs a new request specific instance of the SPARQL query by substituting the parameters in  
     * the queries with the requested nodes. It returns a String representing the new instance.
     * @param queryString - a String that presents a SPARQL query 
     * @param subject -     a MSE node that presents the subject of the requested triple
     * @param predicate -   a MSE node that presents the predicate of the requested triple
     * @param object -      a MSE node that presents the object of the requested triple
     * @return              String representing the new instance of the SPARQL query
     */
    
    String constructQuery (String queryString,Node subject,Node predicate, Node object)
	{
    	 queryString = queryString.replace("%owner%", "<" + dataOwner.getName() + ">");
         queryString = queryString.replace("%namespace%", user_namespace);
         
         if (subject.whichNode() == 0)
             queryString = queryString.replace("%subject%", "<" + subject.getName() + ">");
         else if (subject.whichNode() == 1)
             logger.e("MSEPolicyManager.executeQuery", "The subject can't be a literal");
         else if (subject.whichNode() == 2)
             queryString = queryString.replace("%subject%",subject.getName());
         
         if ( predicate.whichNode() == 0)
             queryString = queryString.replace("%predicate%", "<"  + predicate.getName() + ">");
         else if ( predicate.whichNode() == 1)
             logger.e("MSEPolicyManager.executeQuery", "The predicate can't be a literal");
         else if ( predicate.whichNode() == 2)
         queryString = queryString.replace("%predicate%",predicate.getName());
         
         if (object.whichNode() == 0)
             queryString = queryString.replace("%object%","<" + object.getName()  + ">");
         else if(object.whichNode() == 1)
             queryString = queryString.replace("%object%","'" + object.getName() + "'");
         else if(object.whichNode() == 2)
             queryString = queryString.replace("%object%", object.getName());
			  			
		return queryString;
	}
	
	/**
     * the function adds the type of the SPARQL variable nodes to the the SPARQL query.
     * @param queryString - a String that presents a SPARQL query 
     * @param subject -     a MSE node that presents the subject of the requested triple
     * @param predicate -   a MSE node that presents the predicate of the requested triple
     * @param object -      a MSE node that presents the object of the requested triple
     * @return              String representing the SPARQL query with the newly added triples.
     */
    String constructTriple( String queryString, Node subject, Node predicate, Node object){
        
        String constructedTriple = " ";
        if ( subject.whichNode() == 2 && subject.getType() != null)
            constructedTriple = constructedTriple + " "  + subject.getName() + " <" + rdf_namespace + "type" + "> <" + subject.getType() + ">." ;

        if (object.whichNode() == 2 && object.getType() != null)     
            constructedTriple = constructedTriple + " "  + object.getName() + " <" +  rdf_namespace + "type" + "> <" +object.getType()  + ">." ;
        
        if (constructedTriple != null)    
            queryString = queryString.replace("%type%",constructedTriple);
        else
            queryString = queryString.replace("%type%"," ");
        return queryString;
    }
		
	/**
	 * The function adds a triple to a JENA model. It creates a new triple even if the class 
	 * doesn't already exist
	 * @param model - 		 a JENA model where the new triple will be added
	 * @param namespace -    a String defining the user's namespace
	 * @param className - 	 a String defining the class to add
	 * @param instanceName - a String defining the instance of the class to add
	 * @return boolean - 	 true if the add was successful and false if the add fails
	 */	
    private boolean createInstanceOfClass(Model model, String namespace,String userNamespace,
            String className, String instanceName) {
        
            Resource rs = model.getResource(userNamespace + instanceName);
            if ((instanceName == null) || (instanceName == ""))
            {    logger.e("MSEPolicyManager.createInstanceOfClass", "instance of class" + instanceName + " to create is null");
                    }
            if (rs == null)
                rs = model.createResource(namespace+instanceName);
            Property p = model.getProperty(rdf_namespace + "type");
            Resource rs2 = model.getResource(namespace + className);
            if ((rs != null)&&(rs2 != null) && (p != null)) {
                rs.addProperty(p,rs2);
                return true;
            }
            return false;
            }
	
	/**
	 * The function creates a triple consisting of a subject, a property, and an object. It creates
	 * a new triple even if the object property doesn't already exist.
	 * @param model - 		a JENA model where the subject , property , object triples will be added
	 * @param namespace - 	a String defining the user's namespace
	 * @param subject - 	a String defining the subject of the triple
	 * @param property - 	a String defining the property of the triple
	 * @param object - 		a String defining the object of the triple
	 * @return boolean - 	 true if the add was successful and false if the add fails
	 */	
	private boolean addObjectProperty(Model model, String
	namespace,String userNamespace, String subject, String propertyName, String
	object) {
		if ((object == null) || (object == ""))
		{	logger.e("MSEPolicyManager.createInstanceOfClass", "object of Property " + propertyName+ " to create is null");
		}
		Resource rs1 = model.getResource(userNamespace + subject);
		Resource rs2 = model.getResource(namespace + object);
		Property p = model.getProperty(namespace + propertyName);
		if ((rs1 != null) && (rs2 != null) && (p != null)) {
				rs1.addProperty(p,rs2);
				return true;
							}
				return false;
	 				}
	 /**
	 * The function removes a triple from a JENA model
	 * @param model -		 a JENA model representing the model from which the triple will be deleted
	 * @param namespace - 	 a String defining the user's namespace
	 * @param subject - 	 a String defining the subject of the triple
	 * @param propertyName - a String defining the property of the triple
	 * @param object - 		 a String defining the object of the triple
	 * @return boolean - 	 true if the remove was successful and false if the remove fails
	 */	
	private boolean removeTriple(Model model, String
	namespace, String userNamespace, String subject, String propertyName, String
	object) {
			 Resource rs = model.getResource(userNamespace + subject);
			 Property p = model.getProperty( propertyName);
			 Resource rs2 = model.getResource(namespace + object);
			 if ((rs != null) && (rs2 != null) && (p != null)) {
				 if (model.contains(rs, p, rs2)) {
                     model.remove(rs, p, rs2);
                     return true;
             								}
                 
				 else
                     return false;
			 }
			 	
               return false;
          }
	
 public Model getModel() {
	 return this.results ;
 }
 
}



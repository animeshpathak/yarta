/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase.interfaces;

import java.util.*;

import fr.inria.arles.yarta.Criteria;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.MSEGraph;
import fr.inria.arles.yarta.resources.Person;

/**
 * The Interface which should be implemented by all implementations of
 * the Knowledgebase.
 * @author alessandra
 *
 */

public interface KnowledgeBase {
	
	public static String XSD_STRING = "http://www.w3.org/2001/XMLSchema#string";
	public static String XSD_BOOLEAN = "http://www.w3.org/2001/XMLSchema#boolean";
	public static String XSD_INT = "http://www.w3.org/2001/XMLSchema#int";
	public static String XSD_DOUBLE= "http://www.w3.org/2001/XMLSchema#double";
	
	/**
	 * Initialize the KB with a set of initial 
	 * triples from a source of RDF information.
	 * @param source - an RDF file acting as the initial source of information, null if there is no available file
	 * @param namespace - the namespace of the source RDF file
	 * @param policyFile - the file where policies to be used by the Policy Manager are stored
	 * @param userId - Yarta userId of the user performing this action (in this case it is always the owner of the KB)
	 * @throws KBException if access control fails
	 */
	public void initialize(String source, String namespace, String policyFile, String userId) throws KBException ;
	
	/* NODE API */
	
	/**
	 * Create a new node (RDF Resource) in the knowledge base. Note that in some implementations creating a node 
	 * does not make it visible to the KnowledgeBase, unless a triple is added using that node.
	 * @param nodeURI the complete URI identifying the node in the RDF graph
	 * @param typeURI the complete URI identifying the type of the node (it refers to the possibly extended MSE model)
	 * @param requestorId Yarta userId of the user performing this action (needed for access control)
	 * @return the resource node that was added, null if something went wrong
	 * @throws KBException if access control fails
	 */
	public Node addResource (String nodeURI, String typeURI, String requestorId) throws KBException ;
	
	/**
	 * Create a new node (RDF Resource) in the knowledge base. Note that in some implementations creating a node 
	 * does not make it visible to the KnowledgeBase, unless a triple is added using that node.
	 * @param node - the resource node to be added (not for literals)
	 * @param requestorId - Yarta userId of the user performing this action (needed for access control)
	 * @return the resource node that was added, null if something went wrong
	 * @throws KBException if access control fails
	 */
	public Node addResource (Node node, String requestorId) throws KBException ;
	
	/**
	 * Create and returns a literal.
	 * @param value - a string defining the value of the literal
	 * @param dataType - the complete URI of the data type 
	 * @param requestorId - Yarta userId of the user performing this action (needed for access control)
	 * @return the literal node, null if something went wrong
	 * @throws KBException if access control fails or in case data type is not supported
	 */
	public Node addLiteral (String value, String dataType, String requestorId) throws KBException ;
	
	
	/**
	 * Gets a resource with a given URI. 
	* NOTE THAT THERE MIGHT BE MORE THAN ONE NODE CORRESPONDING TO THE SAME URI, e.g., in case of multiple types for the same URI.
	* Right now we return the first one in a random way. Otherwise the method should return an ArrayList but not sure it would be more useful
	 * @param URI - The complete URI (absolute) of the Node we want. Only applies to MSE Resources
	 * @param requestorId - Yarta userId of the user performing this action (needed for access control)
	 * @return the Resource, null if the node is not in the KB
	 * @throws KBException if access control fails
	 */

	public Node getResourceByURI(String URI, String requestorId) throws KBException;
	
	
	
	/* TRIPLE API */
	
	/**
	 * Add a triple (statement) to the knowledge base. 
	 * TBD: does the semantics of this operation require that the nodes exist in the KB? I would say no.
	 * @param s - subject Node
	 * @param p - property Node
	 * @param o - object Node
	 * @param requestorId - Yarta userId of the user performing this action (needed for access control)
	 * @return the added triple, null if something went wrong
	 * @throws KBException if access control fails
	 */
	public Triple addTriple (Node s, Node p, Node o, String requestorId) throws KBException ;
	
	/**
	 * Remove a triple (statement) from the knowledge base. 
	 *  @param s - subject Node
	 * @param p - property Node
	 * @param o - object Node
	 * @param requestorId - Yarta userId of the user performing this action (needed for access control)
	 * @return the removed triple, null if something went wrong
	 * @throws KBException if access control fails
	 */
	public Triple removeTriple (Node s, Node p, Node o, String requestorId) throws KBException ;

	/* The following APIs can also (and most likely will) be implemented via a path query language, such as SPARQL or WilburQL */
	
	/**
	 * Read a triple (statement) from the knowledge base, if present. 
	 *  @param s - subject Node
	 * @param p - property Node
	 * @param o - object Node
	  * @param requestorId Yarta userId of the user performing this action (needed for access control)
	 * @return the triple, null if the triple is not included in the KB
	 * @throws KBException if access control fails
	 */
	public Triple getTriple (Node s, Node p, Node o, String requestorId) throws KBException ;
	
	/**
	 * Return all triples for a given subject and property, i.e., all property values of a given node
	 * @param s - subject Node
	 * @param p - property Node
	 * @param requestorId - Yarta userId of the user performing this action (needed for access control)
	 * @return the Graph containing all matching triples
	 * @throws KBException if access control fails
	 * 
	 */
	public Graph getPropertyObject (Node s, Node p, String requestorId) throws KBException ;
	

	/**
	 * Return all triples for a given object and property, i.e., all nodes with a given property value.
	 * @param p - property Node
	 * @param o - Object Node
	 * @param requestorId - Yarta userId of the user performing this action (needed for access control)
	 * @return the Graph containing all matching triples
	 * @throws MSEException 
	 * @throws KBException if access control fails
	 * 
	 */
	public Graph getPropertySubject (Node p, Node o, String requestorId) throws KBException;

	
	/**
	 * Add a graph (i.e., a set of RDF triples, to the knowledge base. Can be used when triples are sent by another user.
	 * @param g - the graph to be added
	 * @param requestorId Yarta userId of the user performing this action (needed for access control)
	 * @return the added graph, null is something went wrong
	 * @throws Exception 
	 */
	public Graph addGraph(Graph g, String requestorId) throws Exception ;
	
	/* Other APIs on triples*/
	
	/**
	 * Return all triples for a given subject and object, i.e., all properties linking them.
	 * @param s - subject Node
	 * @param o - object Node
	 * @param requestorId - Yarta userId of the user performing this action (needed for access control)
	 * @return the Graph containing all matching triples
	 * @throws KBException if access control fails
	 */
	public Graph getProperty (Node s, Node o, String requestorId) throws KBException ;
	
	/**
	 * Return all triples for a given subject
	 * @param s - subject Node
	 * @param requestorId - Yarta userId of the user performing this action (needed for access control)
	 * @return the Graph containing all matching triples
	 * @throws KBException if access control fails
	 */
	public Graph getAllProperties (Node s, String requestorId) throws KBException ;
	
	/* Currently we do not provide the corresponding method for objects. To be checked if useful */
	
	/**
	 * Makes a query over the KB in a given query language
	 * @param c - the actual text of the query/as a criteria
	 * @param requestorId - Yarta userId of the user performing this action (needed for access control)
	 * @return the Graph containing all matching triples
	 * @throws KBException if access control fails
	 */
	public Graph queryKB (Criteria c, String requestorId) throws KBException ;

	
	/**
	 * Gives the namespace associated with the user of the device.
	 * @return the namespace of the user running the instance of the KB
	 */
	public String getMyNameSpace();

	/**
	 * Returns all the accessible knowledge. Note that the returned Graph can be empty.
	 * @param requestorId - Yarta userId of the user performing this action (needed for access control)
	 * @return - Graph containing all user's knowledge (subject to access control policies)
	 */
	public Graph getKB(String requestorId) throws KBException;


}

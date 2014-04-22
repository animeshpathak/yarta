package fr.inria.arles.yarta.knowledgebase.interfaces;

import java.util.List;

import fr.inria.arles.yarta.knowledgebase.KBException;

/**
 * The Interface which should be implemented by all implementations of the
 * Knowledgebase. Only uses serializable inputs and outputs.
 */
public interface ThinKnowledgeBase {

	/**
	 * Flag to specify weather to perform sanity checks.
	 */
	public static final boolean PERFORM_CHECKS = true;

	public static String XSD_STRING = "http://www.w3.org/2001/XMLSchema#string";
	public static String XSD_BOOLEAN = "http://www.w3.org/2001/XMLSchema#boolean";
	public static String XSD_INT = "http://www.w3.org/2001/XMLSchema#int";
	public static String XSD_LONG = "http://www.w3.org/2001/XMLSchema#long";
	public static String XSD_DOUBLE = "http://www.w3.org/2001/XMLSchema#double";

	public static String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	
	public static String MSE_NAMESPACE = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#";
	public static String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static String RDFS_NAMESPACE = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String PROPERTY_USERID_NAME = "userId";
	public static String PROPERTY_USERID_URI = MSE_NAMESPACE
			+ PROPERTY_USERID_NAME;
	public static String PROPERTY_TYPE_URI = RDF_NAMESPACE + "type";
	public static String PROPERTY_RDFPROPERTY_URI = RDF_NAMESPACE + "Property";

	/**
	 * Initialize the KB with a set of initial triples from a source of RDF
	 * information.
	 * 
	 * @param source
	 *            - an RDF file acting as the initial source of information,
	 *            null if there is no available file
	 * @param namespace
	 *            - the namespace of the source RDF file
	 * @param policyFile
	 *            - the file where policies to be used by the Policy Manager are
	 *            stored
	 * @param userKBId
	 *            - Yarta userId [URI relative name of Person node] of the user
	 *            performing this action (in this case it is always the owner of
	 *            the KB)
	 * @param userId
	 *            - the userId property of the Person. This will be used later
	 *            to check policies
	 * @throws KBException
	 *             if access control fails
	 */
	public void initialize(String source, String namespace, String policyFile,
			String userId) throws KBException;

	/**
	 * Does all the clean up stuff which is going on in the initialize function.
	 * 
	 * @throws KBException
	 */
	public void uninitialize() throws KBException;

	/* NODE API */

	/**
	 * Create a new node (RDF Resource) in the knowledge base. Note that in some
	 * implementations creating a node does not make it visible to the
	 * KnowledgeBase, unless a triple is added using that node.
	 * 
	 * @param nodeURI
	 *            the complete URI identifying the node in the RDF graph
	 * @param typeURI
	 *            the complete URI identifying the type of the node (it refers
	 *            to the possibly extended MSE model)
	 * @param requestorId
	 *            Yarta userId of the user performing this action (needed for
	 *            access control)
	 * @return the resource node that was added, null if something went wrong
	 * @throws KBException
	 *             if access control fails
	 */
	public Node addResource(String nodeURI, String typeURI, String requestorId)
			throws KBException;

	/**
	 * Create a new node (RDF Resource) in the knowledge base. Note that in some
	 * implementations creating a node does not make it visible to the
	 * KnowledgeBase, unless a triple is added using that node.
	 * 
	 * @param node
	 *            - the resource node to be added (not for literals)
	 * @param requestorId
	 *            - Yarta userId of the user performing this action (needed for
	 *            access control)
	 * @return the resource node that was added, null if something went wrong
	 * @throws KBException
	 *             if access control fails
	 */
	public Node addResource(Node node, String requestorId) throws KBException;

	/**
	 * Create and returns a literal.
	 * 
	 * @param value
	 *            - a string defining the value of the literal
	 * @param dataType
	 *            - the complete URI of the data type
	 * @param requestorId
	 *            - Yarta userId of the user performing this action (needed for
	 *            access control)
	 * @return the literal node, null if something went wrong
	 * @throws KBException
	 *             if access control fails or in case data type is not supported
	 */
	public Node addLiteral(String value, String dataType, String requestorId)
			throws KBException;

	/**
	 * Gets a resource with a given URI. NOTE THAT THERE MIGHT BE MORE THAN ONE
	 * NODE CORRESPONDING TO THE SAME URI, e.g., in case of multiple types for
	 * the same URI. Right now we return the first one in a random way.
	 * Otherwise the method should return an ArrayList but not sure it would be
	 * more useful
	 * 
	 * @param URI
	 *            - The complete URI (absolute) of the Node we want. Only
	 *            applies to MSE Resources
	 * @param requestorId
	 *            - Yarta userId of the user performing this action (needed for
	 *            access control)
	 * @return the Resource, null if the node is not in the KB
	 * @throws KBException
	 *             if access control fails
	 */
	public Node getResourceByURI(String uri, String requestorId)
			throws KBException;

	/**
	 * Gets a resource with a given URI. Does not check policies. NOTE THAT
	 * THERE MIGHT BE MORE THAN ONE NODE CORRESPONDING TO THE SAME URI, e.g., in
	 * case of multiple types for the same URI. Right now we return the first
	 * one in a random way. Otherwise the method should return an ArrayList but
	 * not sure it would be more useful
	 * 
	 * @param URI
	 *            - The complete URI (absolute) of the Node we want. Only
	 *            applies to MSE Resources
	 * @return the Resource, null if the node is not in the KB
	 */
	public Node getResourceByURINoPolicies(String uri);

	/* TRIPLE API */

	/**
	 * Add a triple (statement) to the knowledge base. TBD: does the semantics
	 * of this operation require that the nodes exist in the KB? I would say no.
	 * 
	 * @param s
	 *            - subject Node
	 * @param p
	 *            - property Node
	 * @param o
	 *            - object Node
	 * @param requestorId
	 *            - Yarta userId of the user performing this action (needed for
	 *            access control)
	 * @return the added triple, null if something went wrong
	 * @throws KBException
	 *             if access control fails
	 */
	public Triple addTriple(Node s, Node p, Node o, String requestorId)
			throws KBException;

	/**
	 * Remove a triple (statement) from the knowledge base.
	 * 
	 * @param s
	 *            - subject Node
	 * @param p
	 *            - property Node
	 * @param o
	 *            - object Node
	 * @param requestorId
	 *            - Yarta userId of the user performing this action (needed for
	 *            access control)
	 * @return the removed triple, null if something went wrong
	 * @throws KBException
	 *             if access control fails
	 */
	public Triple removeTriple(Node s, Node p, Node o, String requestorId)
			throws KBException;

	/*
	 * The following APIs can also (and most likely will) be implemented via a
	 * path query language, such as SPARQL or WilburQL
	 */

	/**
	 * Read a triple (statement) from the knowledge base, if present.
	 * 
	 * @param s
	 *            - subject Node
	 * @param p
	 *            - property Node
	 * @param o
	 *            - object Node
	 * @param requestorId
	 *            Yarta userId of the user performing this action (needed for
	 *            access control)
	 * @return the triple, null if the triple is not included in the KB
	 * @throws KBException
	 *             if access control fails
	 */
	public Triple getTriple(Node s, Node p, Node o, String requestorId)
			throws KBException;

	/**
	 * Return all triples for a given subject and property, i.e., all property
	 * values of a given node
	 * 
	 * @param s
	 *            - subject Node
	 * @param p
	 *            - property Node
	 * @param requestorId
	 *            - Yarta userId of the user performing this action (needed for
	 *            access control)
	 * @return a list containing all matching triples
	 * @throws KBException
	 *             if access control fails
	 * 
	 */
	public List<Triple> getPropertyObjectAsTriples(Node s, Node p,
			String requestorId) throws KBException;

	/**
	 * Return all triples for a given object and property, i.e., all nodes with
	 * a given property value.
	 * 
	 * @param p
	 *            - property Node
	 * @param o
	 *            - Object Node
	 * @param requestorId
	 *            - Yarta userId of the user performing this action (needed for
	 *            access control)
	 * @return a list containing all matching triples
	 * @throws MSEException
	 * @throws KBException
	 *             if access control fails
	 * 
	 */
	public List<Triple> getPropertySubjectAsTriples(Node p, Node o,
			String requestorId) throws KBException;

	/* Other APIs on triples */

	/**
	 * Return all triples for a given subject and object, i.e., all properties
	 * linking them.
	 * 
	 * @param s
	 *            - subject Node
	 * @param o
	 *            - object Node
	 * @param requestorId
	 *            - Yarta userId of the user performing this action (needed for
	 *            access control)
	 * @return the List containing all matching triples
	 * @throws KBException
	 *             if access control fails
	 */
	public List<Triple> getPropertyAsTriples(Node s, Node o, String requestorId)
			throws KBException;

	/**
	 * Return all triples for a given subject
	 * 
	 * @param s
	 *            - subject Node
	 * @param requestorId
	 *            - Yarta userId of the user performing this action (needed for
	 *            access control)
	 * @return the List containing all matching triples
	 * @throws KBException
	 *             if access control fails
	 */
	public List<Triple> getAllPropertiesAsTriples(Node s, String requestorId)
			throws KBException;

	/**
	 * Gives the namespace associated with the user of the device.
	 * 
	 * @return the namespace of the user running the instance of the KB
	 */
	public String getMyNameSpace();

	/**
	 * Returns all the accessible knowledge. Note that the returned Graph can be
	 * empty.
	 * 
	 * @param requestorId
	 *            - Yarta userId of the user performing this action (needed for
	 *            access control)
	 * @return - List of triples containing all user's knowledge (subject to
	 *         access control policies)
	 * @throws Exception
	 */
	public List<Triple> getKBAsTriples(String requestorId) throws KBException;
}

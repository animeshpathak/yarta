package fr.inria.arles.yarta.knowledgebase.interfaces;

import fr.inria.arles.yarta.Criteria;
import fr.inria.arles.yarta.knowledgebase.KBException;

/**
 * The Interface which should be implemented by all implementations of the
 * Knowledgebase.
 */
public interface KnowledgeBase extends ThinKnowledgeBase {

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
	 * @return the Graph containing all matching triples
	 * @throws KBException
	 *             if access control fails
	 * 
	 */
	public Graph getPropertyObject(Node s, Node p, String requestorId)
			throws KBException;

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
	 * @return the Graph containing all matching triples
	 * @throws MSEException
	 * @throws KBException
	 *             if access control fails
	 * 
	 */
	public Graph getPropertySubject(Node p, Node o, String requestorId)
			throws KBException;

	/**
	 * Add a graph (i.e., a set of RDF triples, to the knowledge base. Can be
	 * used when triples are sent by another user.
	 * 
	 * @param g
	 *            - the graph to be added
	 * @param requestorId
	 *            Yarta userId of the user performing this action (needed for
	 *            access control)
	 * @return the added graph, null is something went wrong
	 * @throws Exception
	 */
	public Graph addGraph(Graph g, String requestorId) throws Exception;

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
	 * @return the Graph containing all matching triples
	 * @throws KBException
	 *             if access control fails
	 */
	public Graph getProperty(Node s, Node o, String requestorId)
			throws KBException;

	/**
	 * Return all triples for a given subject
	 * 
	 * @param s
	 *            - subject Node
	 * @param requestorId
	 *            - Yarta userId of the user performing this action (needed for
	 *            access control)
	 * @return the Graph containing all matching triples
	 * @throws KBException
	 *             if access control fails
	 */
	public Graph getAllProperties(Node s, String requestorId)
			throws KBException;

	/*
	 * Currently we do not provide the corresponding method for objects. To be
	 * checked if useful
	 */

	/**
	 * Makes a query over the KB in a given query language
	 * 
	 * @param c
	 *            - the actual text of the query/as a criteria
	 * @param requestorId
	 *            - Yarta userId of the user performing this action (needed for
	 *            access control)
	 * @return the Graph containing all matching triples
	 * @throws KBException
	 *             if access control fails
	 */
	public Graph queryKB(Criteria c, String requestorId) throws KBException;

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
	 * @return - Graph containing all user's knowledge (subject to access
	 *         control policies)
	 * @throws Exception
	 */
	public Graph getKB(String requestorId) throws KBException;
	
	/**
	 * Returns the PolicyManager inside KnowledgeBase
	 * 
	 * @return PolicyManager
	 */
	public PolicyManager getPolicyManager();
}

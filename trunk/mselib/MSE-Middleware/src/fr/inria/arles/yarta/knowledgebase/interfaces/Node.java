package fr.inria.arles.yarta.knowledgebase.interfaces;

import java.io.Serializable;

import fr.inria.arles.yarta.knowledgebase.KBException;

/**
 * The interface to be implemented by all classes representing nodes (RDF
 * resources or literals) in the knowledge base
 */
public interface Node extends Serializable {
	/**
	 * The complete URI of the node. Note that we assume that the creator of the
	 * node will assign a namespace to it, so it might happen that two nodes
	 * corresponding to the same entity have different URIs (for example, the
	 * same person has two different URIs in two different user applications.
	 */

	/**
	 * represents the RDF resource
	 */
	public final int RDF_RESOURCE = 0;

	/**
	 * represents the RDF Literal
	 */
	public final int RDF_LITERAL = 1;

	/**
	 * represents the Variable Node
	 */
	public final int VARIABLE_NODE = 2;

	/**
	 * Returns the name of a node
	 * 
	 * @return name of the node (URI for resources, value for literals)
	 */
	public String getName();

	/**
	 * Returns the name of the node
	 * 
	 * @return name of the node (URI for resources, value for literals)
	 */
	public String toString();

	/**
	 * To distinguish nodes: RDF resources, RDF literals or variable nodes
	 * 
	 * @return 0 if RDF resource, 1 if RDF literal, 2 if (SPARQL) variable node
	 */
	public int whichNode();

	// TODO: change to a method returning an integer and including: RDF
	// resources,
	// literals and SPARQL variable nodes

	/**
	 * Return the RDF type of the node
	 * 
	 * @return the type (MSE resource type if Resource, data type if Literal)
	 */
	public String getType();

	/**
	 * 
	 * @return the relative name of the node (i.e., what follows the "#"
	 *         character in the URI)
	 */
	public String getRelativeName() throws KBException;

}

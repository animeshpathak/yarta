/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase.interfaces;

import fr.inria.arles.yarta.knowledgebase.KBException;

/**
 * The interface to be implemented by all classes representing 
 * nodes (RDF resources or literals) in the knowledge base
 * @author alessandra
 *
 */
public interface Node {
	/**
	 * The complete URI of the node. Note that we assume that the creator of the node will assign a namespace to it, so
	 * it might happen that two nodes corresponding to the same entity have different URIs (for example, the same person
	 * has two different URIs in two different user applications.
	 */
	
	/**
	 * Returns the name of a node
	 * @return name of the node (URI for resources, value for literals)
	 */
	public String getName();
	
	/**
	 * Returns the name of the node
	 * @return name of the node (URI for resources, value for literals)
	 */
	public String toString();
	
	/**
	 * To distinguish nodes: RDF resources, RDF literals or variable nodes
	 * @return 0 if RDF resource, 1 if RDF literal, 2 if (SPARQL) variable node
	 */
	public int whichNode();
	// change to a method returning an integer and including: RDF resources, literals and SPARQL variable nodes

	/**
	 * Return the RDF type of the node 
	 * @return the type (MSE resource type if Resource, data type if Literal)
	 */
	public String getType(); 
	
	/**
	 * 
	 * @return the relative name of the node (i.e., what follows the "#" character in the URI)
	 */
	public String getRelativeName() throws KBException;
	
}

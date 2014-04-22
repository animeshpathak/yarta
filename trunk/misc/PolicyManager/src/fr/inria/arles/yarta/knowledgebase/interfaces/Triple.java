/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase.interfaces;

/**
 * @author alessandra
 *
 */
public interface Triple {
	
	public String toString();
	
	/**
	 * Return the subject of the triple
	 * @return the subject Node of the triple
	 */
	public Node getSubject();
	
	/**
	 * Return the property of the triple
	 * @return the property Node of the triple
	 */
	public Node getProperty();
	
	/**
	 * Return the object of the triple
	 * @return the object Node of the triple
	 */
	public Node getObject();

}

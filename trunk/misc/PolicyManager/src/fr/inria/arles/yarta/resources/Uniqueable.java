/**
 * 
 */
package fr.inria.arles.yarta.resources;

/**
 * This is to be implemented by all resources in the KB.
 * The main property of this interface is that it can provide a unique ID.
 * @author pathak
 *
 */
public interface Uniqueable {

	/**
	 * Returns the unique ID used in creating URIs for the 
	 * individual objects
	 * @return a unique ID for the object -- unique over the entire KB.
	 */
	public String getUniqueId();
	
	/**
	 * Sets the unique ID variable in the object. 
	 */
	public void setUniqueId(String uniqueId);
	
}

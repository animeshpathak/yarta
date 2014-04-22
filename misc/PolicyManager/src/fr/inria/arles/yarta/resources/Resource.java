/**
 * 
 */
package fr.inria.arles.yarta.resources;

/**
 * Generic High-level Resource Class. Not functional except to use in methods 
 * where the "range" is not specified.
 * @author pathak
 *
 */
public class Resource implements Uniqueable{
	
	/** The unique ID of the resource */
	protected String uniqueId;

	@Override
	public String getUniqueId() {
		return this.uniqueId;
	}

	@Override
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

}

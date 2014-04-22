package fr.inria.arles.yarta.middleware.msemanagement;

/**
 * Base content client.
 */
public interface ContentClient {

	/**
	 * Gets binary data associated with this unique resource id.
	 * 
	 * @param id
	 * @return
	 */
	public byte[] getData(String id);

	/**
	 * Sets binary data associated with this unique resource id.
	 * 
	 * @param id
	 * @param data
	 */
	public void setData(String id, byte[] data);
}

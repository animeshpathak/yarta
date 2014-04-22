package fr.inria.arles.yarta.knowledgebase;

import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;

/**
 * Helper class used merely in Updates. Stores and retrieves update times for
 * triples or peers.
 */
public interface UpdateHelper {

	/**
	 * Init function
	 */
	public void init();

	/**
	 * Uninit function
	 */
	public void uninit();

	/**
	 * Returns the last update time.
	 * 
	 * @param peerId
	 * @return
	 */
	public long getTime(String peerId);

	/**
	 * Sets the last update time.
	 * 
	 * @param peerId
	 * @param time
	 */
	public void setTime(String peerId, long time);

	/**
	 * Returns the last update time for the specified triple.
	 * 
	 * @param triple
	 * @return
	 */
	public long getTime(Triple triple);

	/**
	 * Sets the last update time for the specified triple.
	 * 
	 * @param triple
	 * @param time
	 */
	public void setTime(Triple triple, long time);

	/**
	 * Returns the latest change time happened on a node.
	 * 
	 * @param node
	 * @return time
	 */
	public boolean isDirty(Node node, long timestamp);
}

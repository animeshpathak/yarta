package fr.inria.arles.yarta.middleware.msemanagement;

/**
 * This gives the ability to rename a resource within remote clients from Yarta
 * Core Library. This happens when GUIDS of resources changes when synchronizing
 * with external platforms such as ELGG.
 */
public interface RemoteSAM {

	/**
	 * Fires a rename resource into SAM.
	 * 
	 * @param oldURI
	 * @param newURI
	 */
	public void renameResource(String oldURI, String newURI);
}

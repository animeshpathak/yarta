package fr.inria.arles.yarta.middleware.msemanagement;

/**
 * Automatically, and repeatedly, extracts data from the user's device like call
 * logs etc, and populates/updates the MSE KB.
 */

public class DataExtractionManager {

	/**
	 * The initialize function.
	 * 
	 * @return true if all goes well. False otherwise
	 */
	public boolean initialize() {
		return true;
	}
	
	/**
	 * Shut down any threads.
	 * 
	 * @return true if all goes well. False otherwise
	 */
	public boolean shutDown() {
		return true;
	}
}

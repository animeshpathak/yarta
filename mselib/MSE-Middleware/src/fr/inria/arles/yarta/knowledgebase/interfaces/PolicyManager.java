package fr.inria.arles.yarta.knowledgebase.interfaces;

/**
 * The PolicyManager interface exposed to the programmer
 */
public interface PolicyManager {

	/**
	 * Loads a policy file.
	 * 
	 * @param path
	 * @return
	 */
	public boolean loadPolicies(String path);
}

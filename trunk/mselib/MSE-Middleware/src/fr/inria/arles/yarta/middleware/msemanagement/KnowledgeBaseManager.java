package fr.inria.arles.yarta.middleware.msemanagement;

import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;

/**
 * The interface to be extended by all Classes interacting with KnowledgeBases.
 */
public interface KnowledgeBaseManager {

	/**
	 * Sets the KnowledgeBase to be operated on by the KBManager
	 * 
	 * @param k
	 *            The Knowledgebase
	 */
	public void setKnowledgeBase(KnowledgeBase k);
}

package fr.inria.arles.yarta.middleware.msemanagement;

import fr.inria.arles.yarta.knowledgebase.interfaces.ThinKnowledgeBase;

/**
 * The interface to be extended by all Classes interacting with Thin
 * KnowledgeBases.
 */
public interface ThinKnowledgeBaseManager {

	/**
	 * Sets the KnowledgeBase to be operated on by the KBManager
	 * 
	 * @param k
	 *            The (Thin) Knowledgebase
	 */
	public void setKnowledgeBase(ThinKnowledgeBase k);
}

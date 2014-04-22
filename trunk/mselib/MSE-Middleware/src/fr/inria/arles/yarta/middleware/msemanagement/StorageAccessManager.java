package fr.inria.arles.yarta.middleware.msemanagement;

import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.ThinKnowledgeBase;

/**
 * This manages the primary access to the KB from the user's perspective.
 */
public class StorageAccessManager extends ThinStorageAccessManager implements
		KnowledgeBaseManager {

	private static final String STORAGE_ACCESS_MGR_LOGTAG = "StorageAccessManager";

	/**
	 * constructor. Sets the logger object.
	 */
	public StorageAccessManager() {
		super();
		if (!ThinKnowledgeBase.PERFORM_CHECKS) {
			logger.e(STORAGE_ACCESS_MGR_LOGTAG,
					"PERFORM_CHECKS is false. Not performing checks. Use at your own risk!");
		}
	}

	@Override
	public void setKnowledgeBase(KnowledgeBase k) {
		super.setKnowledgeBase(k);
	}
}

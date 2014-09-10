package fr.inria.arles.giveaway.msemanagement;

import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import fr.inria.arles.yarta.middleware.msemanagement.MSEManager;

/**
 * MSE Manager class extension.
 */
public class MSEManagerEx extends MSEManager {
	
	/**
	 * Base URI described in the custom ontology file.
	 */
	public static final String baseMSEURI = "http://yarta.gforge.inria.fr/ontologies/donations.rdf";
	
	/**
	 * Basic constructor.
	 */
	public MSEManagerEx() {
		super();
	}
	
	/**
	 * Overridden initialize function to construct the custom storageAccessManager;
	 */
	@Override
	public void initialize(String filePath, String policyPath, MSEApplication app, Object context)
			throws KBException {
		super.initialize(filePath, policyPath, app, context);
		
		storageAccessManager = new StorageAccessManagerEx();
		storageAccessManager.setKnowledgeBase(knowledgeBase);
		storageAccessManager.setOwnerID(ownerUID);
	}
	
	/**
	 * Returns the factory instance used to create/list resources.
	 */
	public StorageAccessManagerEx getStorageAccessManagerEx() {
		return (StorageAccessManagerEx) storageAccessManager;
	}
}

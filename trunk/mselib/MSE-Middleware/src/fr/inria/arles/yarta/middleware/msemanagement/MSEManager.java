package fr.inria.arles.yarta.middleware.msemanagement;

import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBaseUtils;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.PolicyManager;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;

/**
 * The Class that faces the user program. Mostly calls other classes.
 */
public class MSEManager {

	public static final String LOGTAG = "Yarta-MSEManager";

	protected String ownerUID;
	protected KnowledgeBase knowledgeBase;
	protected StorageAccessManager storageAccessManager;

	private CommunicationManager commManager;

	private MSEApplication application;
	private YLogger logger = YLoggerFactory.getLogger();

	private static String mse_namespace = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#";

	/**
	 * initializes the MSE Management Middleware. This should result in a
	 * cascade of other init methods.
	 * 
	 * @param filePath
	 *            Path of File where initial KB is stored.
	 * @param policyPath
	 *            Path of the policy file
	 * @param userKBID
	 *            the KB ID (relative name of person node) of the owner
	 * @param app
	 *            instance of {@link MSEApplication} that will be given any UI
	 *            requests
	 * @param context
	 *            The context. Null for J2SE for PC. Android context for
	 *            Android.
	 * @throws KBException
	 */
	public void initialize(String filePath, String policyPath,
			MSEApplication app, Object context) throws KBException {
		application = app;

		String nameSpace = mse_namespace;

		if (isAndroid()) {
			commManager = new fr.inria.arles.yarta.android.library.CMClient();
			knowledgeBase = new fr.inria.arles.yarta.android.library.KBClient(
					app, context);
		} else {
			commManager = new fr.inria.arles.yarta.desktop.library.CMClient();
			knowledgeBase = new fr.inria.arles.yarta.desktop.library.KBClient(
					app);
		}

		// interacting directly with MSE KB. fix the userID properly
		commManager.initialize(ownerUID, knowledgeBase, application, context);
		knowledgeBase.initialize(filePath, nameSpace, policyPath, ownerUID);

		storageAccessManager = new StorageAccessManager();
		storageAccessManager.setKnowledgeBase(knowledgeBase);
		storageAccessManager.setOwnerID(ownerUID);
	}

	/**
	 * Checks whether or not the current VM is Android. TODO: getPlatform()
	 * cascade switch
	 * 
	 * @return true/false
	 */
	private boolean isAndroid() {
		return System.getProperty("java.vm.name").equalsIgnoreCase("dalvik");
	}

	/**
	 * Causes a cascaded shutdown of the middleware. Useful to stop services
	 * etc.
	 * 
	 * @return true if all went well. False otherwise.
	 */
	public boolean shutDown() throws KBException {
		logger.d(LOGTAG, "Trying to shut stuff down now...");

		// SAM - no shutdown needed.

		// CommManager. Shutdown Needed
		if (commManager.uninitialize() < 0) {
			logger.e(LOGTAG,
					"Communication Manager Could not shut down. I wonder why?");
			return false;
		}

		knowledgeBase.uninitialize();

		// all went well. Returning true
		logger.d(LOGTAG, "All components shut down. bye!");
		return true;
	}

	public boolean clear() {
		commManager.clear();
		try {
			return shutDown();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * Reads the knowledge from a file and adds it to the KB. Complement of
	 * {@link #writeKnowledge(String)}
	 * 
	 * @param filePath
	 *            The path of the file
	 * @return true if all goes well. False if something went wrong.
	 */
	public boolean addKnowledge(String filePath) {
		return MSEKnowledgeBaseUtils.importDataFromRDF(filePath, knowledgeBase);
	}

	/**
	 * Writes the knowledge onto a file. Complement of
	 * {@link #addKnowledge(String)}. If the file writing does not succeed, it
	 * logs an error.
	 * 
	 * @param filePath
	 *            The path of the file. It will be overwritten without warning.
	 */
	public void writeKnowledge(String filePath) {
		MSEKnowledgeBaseUtils.printMSEKnowledgeBase(
				(MSEKnowledgeBase) knowledgeBase, filePath, "RDF/XML");
	}

	/**
	 * Says hello to UID. Note that at this stage, the UID should be updated to
	 * the Prepended form.
	 * 
	 * @param uid
	 *            the user ID of the user I want to initialize contact with
	 * @param MSEURI
	 *            the URI of the user's device
	 * @return true if all went well. False if a problem occurred.
	 * @throws KBException
	 */
	public boolean sayHelloTo(String uid, String MSEURI) throws KBException {
		return commManager.sendHello(uid) >= 0;
	}

	/**
	 * Gets as much info as possible (according to policies) from a user
	 * 
	 * @param uid
	 *            the user ID of the user I want to sync with
	 * @param MSEURI
	 *            the URI of the user's device
	 * @return true if all went well. False if a problem occurred.
	 * @throws KBException
	 */
	public boolean askUpdateTo(String partnerUID, String MSEURI)
			throws KBException {
		return commManager.sendUpdateRequest(partnerUID) >= 0;
	}

	/**
	 * @param ownerUID
	 *            the owner's ID to set
	 */
	public void setOwnerUID(String ownerUID) {
		this.ownerUID = ownerUID;
	}

	/**
	 * @return the owner's ID
	 */
	public String getOwnerUID() {
		return ownerUID;
	}

	/**
	 * Gets the Storage Access Manager.
	 * 
	 * @return {@link StorageAccessManager}
	 */
	public StorageAccessManager getStorageAccessManager() {
		return storageAccessManager;
	}

	/**
	 * Gets the Policy Manager.
	 * 
	 * @return {@link PolicyManager}
	 */
	public PolicyManager getPolicyManager() {
		return knowledgeBase.getPolicyManager();
	}

	/**
	 * Gets the Communication Manager.
	 * 
	 * @return {@link CommunicationManager}
	 */
	public CommunicationManager getCommunicationManager() {
		return commManager;
	}
}

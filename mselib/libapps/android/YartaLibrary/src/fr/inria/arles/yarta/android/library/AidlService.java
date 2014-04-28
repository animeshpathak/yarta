package fr.inria.arles.yarta.android.library;

import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.yarta.android.library.web.IrisBridge;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBaseUtils;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.Receiver;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;

import android.os.Bundle;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

/**
 * Yarta Library implementation over Android. It redirects all the calls to the
 * actual MSE services.
 * 
 */
public class AidlService extends ILibraryService.Stub implements Receiver,
		MSEApplication {

	/**
	 * MSE initializer;
	 */
	private MSEService service;

	/**
	 * Google Analytics Tracker
	 */
	private AnalyticsTracker tracker;

	/**
	 * MSE Logger
	 */
	private YLogger log = YLoggerFactory.getLogger();
	private static final String TAG = "AidlService";

	/**
	 * MSE Objects
	 */
	private KnowledgeBase knowledgeBase;
	private CommunicationManager communicationMgr;

	/**
	 * Receiver and MSEApplication callbacks
	 */
	private final RemoteCallbackList<IReceiver> messageCallbacks = new RemoteCallbackList<IReceiver>();
	private final RemoteCallbackList<IMSEApplication> appCallbacks = new RemoteCallbackList<IMSEApplication>();

	/**
	 * Logs a normal message.
	 * 
	 * @param format
	 * @param args
	 */
	private void log(String format, Object... args) {
		log.d(TAG, String.format(format, args));
	}

	/**
	 * Logs an error message.
	 * 
	 * @param format
	 * @param args
	 */
	private void logError(String format, Object... args) {
		log.e(TAG, String.format(format, args));
	}

	public AidlService(MSEService init, AnalyticsTracker tracker,
			KnowledgeBase knowledgeBase, CommunicationManager communicationMgr) {
		this.service = init;
		this.tracker = tracker;
		this.knowledgeBase = knowledgeBase;
		this.communicationMgr = communicationMgr;
	}

	@Override
	public void initialize(String source, String namespace, String policyFile,
			String userId) throws RemoteException {
		service.init();

		tracker.beforeAPIUsage();
		try {
			// TODO: in case it is not logged in kb is not ready here
			boolean success = false;
			log("ILibraryService.initialize[source = %s, policy = %s]", source,
					policyFile);

			success = MSEKnowledgeBaseUtils.importDataFromRDF(source,
					knowledgeBase);

			// TODO: remove passed files which are temporary
			// TODO: knowledgeBase read/write rdf;
			// TODO: unit tests for new code
			// TODO: handleKBRead(userId) sets automatically the userid for mse
			// and sam;

			log("imported file with success = %b", success);
		} catch (Exception ex) {
			logError("ILibraryService.initialize ex: %s", ex.toString());
			logError("when reading %s", source);
		}
		tracker.sendAPIUsage("MSEKnowledgeBaseUtils.import");
	}

	@Override
	public void uninitialize() throws RemoteException {
		service.uninit(false);
	}

	@Override
	public String getUserId() throws RemoteException {
		return service.getUserId();
	}

	@Override
	public Bundle addLiteral(String value, String dataType, String requestorId)
			throws RemoteException {
		Bundle result = null;
		tracker.beforeAPIUsage();
		try {
			log("ILibraryService.addLiteral[knowledgeBase = %s]", knowledgeBase);

			result = Conversion.toBundle(knowledgeBase.addLiteral(value,
					dataType, requestorId));

		} catch (Exception ex) {
			logError("ILibraryService.addLiteral ex: %s", ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.addLiteral");
		return result;
	}

	@Override
	public Bundle addResource(String nodeURI, String typeURI, String requestorId)
			throws RemoteException {
		Bundle result = null;
		tracker.beforeAPIUsage();
		try {
			log("ILibraryService.addResource[knowledgeBase = %s]",
					knowledgeBase);

			result = Conversion.toBundle(knowledgeBase.addResource(nodeURI,
					typeURI, requestorId));

		} catch (Exception ex) {
			logError("ILibraryService.addResource ex: %s", ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.addResource");
		return result;
	}

	@Override
	public Bundle addResourceNode(Bundle node, String requestorId)
			throws RemoteException {
		Bundle result = null;
		tracker.beforeAPIUsage();
		try {
			log("ILibraryService.addResourceNode[knowledgeBase = %s]",
					knowledgeBase);
			result = Conversion.toBundle(knowledgeBase.addResource(
					Conversion.toNode(node), requestorId));
		} catch (Exception ex) {
			logError("ILibraryService.addResourceNode ex: %s", ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.addResourceNode");
		return result;
	}

	@Override
	public Bundle getResourceByURI(String nodeURI, String requestorId)
			throws RemoteException {
		Bundle result = null;
		tracker.beforeAPIUsage();
		try {
			// log("ILibraryService.getResourceByURI[knowledgeBase = %s]",
			// knowledgeBase);
			result = Conversion.toBundle(knowledgeBase.getResourceByURI(
					nodeURI, requestorId));
		} catch (Exception ex) {
			logError("ILibraryService.getResourceByURI ex: %s", ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.getResourceByURI");
		return result;
	}

	@Override
	public Bundle getResourceByURINoPolicies(String nodeURI)
			throws RemoteException {
		Bundle result = null;
		tracker.beforeAPIUsage();
		try {
			// log("ILibraryService.getResourceByURINoPolicies[knowledgeBase = %s]",
			// knowledgeBase);
			result = Conversion.toBundle(knowledgeBase
					.getResourceByURINoPolicies(nodeURI));
		} catch (Exception ex) {
			logError("ILibraryService.getResourceByURINoPolicies ex: %s",
					ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.getResourceByURINoPolicies");
		return result;
	}

	@Override
	public Bundle addTriple(Bundle s, Bundle p, Bundle o, String requestorId)
			throws RemoteException {
		Bundle result = null;
		tracker.beforeAPIUsage();
		try {
			log("ILibraryService.addTriple[knowledgeBase = %s]", knowledgeBase);
			result = Conversion.toBundle(knowledgeBase.addTriple(
					Conversion.toNode(s), Conversion.toNode(p),
					Conversion.toNode(o), requestorId));
		} catch (Exception ex) {
			logError("ILibraryService.addTriple ex: %s", ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.addTriple");
		return result;
	}

	@Override
	public Bundle removeTriple(Bundle s, Bundle p, Bundle o, String requestorId)
			throws RemoteException {
		Bundle result = null;
		tracker.beforeAPIUsage();
		try {
			log("ILibraryService.removeTriple[knowledgeBase = %s]",
					knowledgeBase);
			result = Conversion.toBundle(knowledgeBase.removeTriple(
					Conversion.toNode(s), Conversion.toNode(p),
					Conversion.toNode(o), requestorId));
		} catch (Exception ex) {
			logError("ILibraryService.removeTriple ex: %s", ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.removeTriple");
		return result;
	}

	@Override
	public Bundle getTriple(Bundle s, Bundle p, Bundle o, String requestorId)
			throws RemoteException {
		Bundle result = null;
		tracker.beforeAPIUsage();
		try {
			log("ILibraryService.getTriple[knowledgeBase = %s]", knowledgeBase);
			result = Conversion.toBundle(knowledgeBase.getTriple(
					Conversion.toNode(s), Conversion.toNode(p),
					Conversion.toNode(o), requestorId));
		} catch (Exception ex) {
			logError("ILibraryService.getTriple ex: %s", ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.getTriple");
		return result;
	}

	@Override
	public String getMyNameSpace() throws RemoteException {
		tracker.sendAPIUsage("KnowledgeBase.getMyNameSpace");
		return knowledgeBase.getMyNameSpace();
	}

	@Override
	public List<Bundle> getPropertyObjectAsTriples(Bundle s, Bundle p,
			String requestorId) throws RemoteException {
		List<Bundle> lstResult = new ArrayList<Bundle>();
		tracker.beforeAPIUsage();
		try {
			// log("ILibraryService.getPropertyObjectAsTriples[knowledgeBase = %s]",
			// knowledgeBase);

			Node subject = Conversion.toNode(s);
			Node predicate = Conversion.toNode(p);

			IrisBridge.fetchPropertyObject(knowledgeBase, subject, predicate);

			List<Triple> triples = knowledgeBase.getPropertyObjectAsTriples(
					subject, predicate, requestorId);

			for (Triple triple : triples) {
				lstResult.add(Conversion.toBundle(triple));
			}
		} catch (Exception ex) {
			logError("ILibraryService.getPropertyObjectAsTriples ex: %s",
					ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.getPropertyObjectAsTriples");
		return lstResult;
	}

	@Override
	public List<Bundle> getPropertySubjectAsTriples(Bundle p, Bundle o,
			String requestorId) throws RemoteException {
		List<Bundle> lstResult = new ArrayList<Bundle>();
		tracker.beforeAPIUsage();
		try {
			log("ILibraryService.getPropertySubjectAsTriples[knowledgeBase = %s]",
					knowledgeBase);
			List<Triple> triples = knowledgeBase.getPropertySubjectAsTriples(
					Conversion.toNode(p), Conversion.toNode(o), requestorId);

			for (Triple triple : triples) {
				lstResult.add(Conversion.toBundle(triple));
			}
		} catch (Exception ex) {
			logError("ILibraryService.getPropertySubjectAsTriples ex: %s",
					ex.getMessage());
			ex.printStackTrace();
		}
		tracker.sendAPIUsage("KnowledgeBase.getPropertySubjectAsTriples");
		return lstResult;
	}

	@Override
	public List<Bundle> getPropertyAsTriples(Bundle s, Bundle o,
			String requestorId) throws RemoteException {
		List<Bundle> lstResult = new ArrayList<Bundle>();
		tracker.beforeAPIUsage();
		try {
			log("ILibraryService.getPropertyAsTriples[knowledgeBase = %s]",
					knowledgeBase);

			List<Triple> triples = knowledgeBase.getPropertyAsTriples(
					Conversion.toNode(s), Conversion.toNode(o), requestorId);

			for (Triple triple : triples) {
				lstResult.add(Conversion.toBundle(triple));
			}

		} catch (Exception ex) {
			logError("ILibraryService.getPropertyAsTriples ex: %s",
					ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.getPropertyAsTriples");
		return lstResult;
	}

	@Override
	public List<Bundle> getAllPropertiesAsTriples(Bundle s, String requestorId)
			throws RemoteException {
		List<Bundle> lstResult = new ArrayList<Bundle>();
		tracker.beforeAPIUsage();
		try {
			log("ILibraryService.getAllPropertiesAsTriples[knowledgeBase = %s]",
					knowledgeBase);

			List<Triple> triples = knowledgeBase.getAllPropertiesAsTriples(
					Conversion.toNode(s), requestorId);

			for (Triple triple : triples) {
				lstResult.add(Conversion.toBundle(triple));
			}

		} catch (Exception ex) {
			logError("ILibraryService.getAllPropertiesAsTriples ex: %s",
					ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.getAllPropertiesAsTriples");
		return lstResult;
	}

	@Override
	public List<Bundle> getKBAsTriples(String requestorId)
			throws RemoteException {
		List<Bundle> lstResult = new ArrayList<Bundle>();
		tracker.beforeAPIUsage();
		try {
			log("ILibraryService.getKBAsTriples[knowledgeBase = %s]",
					knowledgeBase);

			List<Triple> triples = knowledgeBase.getKBAsTriples(requestorId);

			for (Triple triple : triples) {
				lstResult.add(Conversion.toBundle(triple));
			}

		} catch (Exception ex) {
			logError("ILibraryService.getKBAsTriples ex: %s", ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.getKBAsTriples");
		return lstResult;
	}

	@Override
	public Bundle getPropertyObject(Bundle s, Bundle p, String requestorId)
			throws RemoteException {
		Bundle result = null;
		tracker.beforeAPIUsage();
		try {
			log("ILibraryService.getPropertyObject[knowledgeBase = %s]",
					knowledgeBase);
			result = Conversion.toBundle(knowledgeBase.getPropertyObject(
					Conversion.toNode(s), Conversion.toNode(p), requestorId));
		} catch (Exception ex) {
			logError("ILibraryService.getPropertyObject ex: %s",
					ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.getPropertyObject");
		return result;
	}

	@Override
	public Bundle getPropertySubject(Bundle p, Bundle o, String requestorId)
			throws RemoteException {
		Bundle result = null;
		tracker.beforeAPIUsage();
		try {
			log("ILibraryService.getPropertySubject[knowledgeBase = %s]",
					knowledgeBase);
			result = Conversion.toBundle(knowledgeBase.getPropertySubject(
					Conversion.toNode(p), Conversion.toNode(o), requestorId));
		} catch (Exception ex) {
			logError("ILibraryService.getPropertySubject ex: %s",
					ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.getPropertySubject");
		return result;
	}

	@Override
	public Bundle addGraph(Bundle g, String requestorId) throws RemoteException {
		Bundle result = null;
		tracker.beforeAPIUsage();
		log("ILibraryService.addGraph[knowledgeBase = %s]", knowledgeBase);
		try {
			result = Conversion.toBundle(knowledgeBase.addGraph(
					Conversion.toGraph(g), requestorId));
		} catch (Exception ex) {
			logError("ILibraryService.addGraph ex: %s", ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.addGraph");
		return result;
	}

	@Override
	public Bundle getProperty(Bundle s, Bundle o, String requestorId)
			throws RemoteException {
		Bundle result = null;
		tracker.beforeAPIUsage();
		log("ILibraryService.getProperty[knowledgeBase = %s]", knowledgeBase);
		try {
			result = Conversion.toBundle(knowledgeBase.getProperty(
					Conversion.toNode(s), Conversion.toNode(o), requestorId));
		} catch (Exception ex) {
			logError("ILibraryService.getProperty ex: %s", ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.getProperty");
		return result;
	}

	@Override
	public Bundle getAllProperties(Bundle s, String requestorId)
			throws RemoteException {
		Bundle result = null;
		tracker.beforeAPIUsage();
		log("ILibraryService.getAllProperties[knowledgeBase = %s]",
				knowledgeBase);
		try {
			result = Conversion.toBundle(knowledgeBase.getAllProperties(
					Conversion.toNode(s), requestorId));
		} catch (Exception ex) {
			logError("ILibraryService.getAllProperties ex: %s", ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.getAllProperties");
		return result;
	}

	@Override
	public Bundle queryKB(Bundle c, String requestorId) throws RemoteException {
		tracker.sendAPIUsage("KnowledgeBase.queryKB");
		return null;
	}

	@Override
	public Bundle getKB(String requestorId) throws RemoteException {
		Bundle bundle = null;
		tracker.beforeAPIUsage();
		log("ILibraryService.getKB[knowledgeBase = %s]", knowledgeBase);
		try {
			bundle = Conversion.toBundle(knowledgeBase.getKB(requestorId));
		} catch (Exception ex) {
			logError("ILibraryService.getKB ex: %s", ex.getMessage());
		}
		tracker.sendAPIUsage("KnowledgeBase.getKB");
		return bundle;
	}

	@Override
	public IPolicyManager getPolicyManager() throws RemoteException {
		IPolicyManager.Stub stub = new IPolicyManager.Stub() {

			@Override
			public void removeRule(int position) throws RemoteException {
				tracker.sendAPIUsage("PolicyManager.removeRule");
				knowledgeBase.getPolicyManager().removeRule(position);
			}

			@Override
			public int getRulesCount() throws RemoteException {
				tracker.sendAPIUsage("PolicyManager.getRulesCount");
				return knowledgeBase.getPolicyManager().getRulesCount();
			}

			@Override
			public String getRule(int position) throws RemoteException {
				tracker.sendAPIUsage("PolicyManager.getRule");
				return knowledgeBase.getPolicyManager().getRule(position);
			}

			@Override
			public void addRule(String rule) throws RemoteException {
				tracker.sendAPIUsage("PolicyManager.addRule");
				knowledgeBase.getPolicyManager().addRule(rule);
			}
		};
		return stub;
	}

	@Override
	public int sendHello(String partnerID) throws RemoteException {
		int result = -1;
		tracker.beforeAPIUsage();
		log("ILibraryService.sendHello(%s)", partnerID);
		try {
			result = communicationMgr.sendHello(partnerID);
		} catch (KBException ex) {
			logError("ILibraryService.sendHello ex: %s", ex.getMessage());
		}
		tracker.sendAPIUsage("CommunicationManager.sendHello");
		return result;
	}

	@Override
	public int sendUpdateRequest(String partnerID) throws RemoteException {
		int result = -1;
		tracker.beforeAPIUsage();
		log("ILibraryService.sendUpdateRequest(%s)", partnerID);
		try {
			result = communicationMgr.sendUpdateRequest(partnerID);
		} catch (KBException ex) {
			logError("ILibraryService.sendUpdateRequest ex: %s",
					ex.getMessage());
		}
		tracker.sendAPIUsage("CommunicationManager.sendUpdateRequest");
		return result;
	}

	@Override
	public int sendMessage(String partnerID, Bundle message)
			throws RemoteException {
		int result = -1;
		tracker.beforeAPIUsage();
		Message msg = Conversion.toMessage(message);
		log("ILibraryService.sendMessage(%s, %d, %s)", partnerID,
				msg.getType(), msg.getAppId());
		try {
			result = communicationMgr.sendMessage(partnerID, msg);
		} catch (Exception ex) {
			logError("ILibraryService.sendMessage ex: %s", ex.getMessage());
		}
		tracker.sendAPIUsage("CommunicationManager.sendMessage");
		return result;
	}

	@Override
	public int sendResource(String partnerID, String resourceID)
			throws RemoteException {
		int result = -1;
		tracker.beforeAPIUsage();
		log("ILibraryService.sendResource(%s, %s)", partnerID, resourceID);
		try {
			result = communicationMgr.sendResource(partnerID, resourceID);
		} catch (Exception ex) {
			logError("ILibraryService.sendResource ex: %s", ex.getMessage());
		}
		tracker.sendAPIUsage("CommunicationManager.sendResource");
		return result;
	}

	@Override
	public int sendNotify(String peerId) throws RemoteException {
		int result = -1;
		tracker.beforeAPIUsage();
		log("ILibraryService.sendNotify(%s)", peerId);
		try {
			result = communicationMgr.sendNotify(peerId);
		} catch (Exception ex) {
			logError("ILibraryService.sendNotify ex: %s", ex.getMessage());
		}
		tracker.sendAPIUsage("CommunicationManager.sendNotify");
		return result;
	}

	@Override
	public boolean registerReceiver(IReceiver receiver) throws RemoteException {
		boolean result = false;
		tracker.beforeAPIUsage();
		log("ILibraryService.registerReceiver(%s)", receiver);
		if (receiver != null) {
			result = messageCallbacks.register(receiver);
		}
		tracker.sendAPIUsage("CommunicationManager.registerReceiver");
		return result;
	}

	@Override
	public boolean unregisterReceiver(IReceiver receiver)
			throws RemoteException {
		boolean result = false;
		tracker.beforeAPIUsage();
		log("ILibraryService.unregisterReceiver(%s)", receiver);
		if (receiver != null) {
			result = messageCallbacks.unregister(receiver);
		}
		tracker.sendAPIUsage("CommunicationManager.unregisterReceiver");
		return result;
	}

	@Override
	public boolean registerCallback(IMSEApplication callback)
			throws RemoteException {
		boolean result = false;
		log("ILibraryService.registerCallback(%s)", callback);
		if (callback != null) {
			result = appCallbacks.register(callback);
		}
		return result;
	}

	@Override
	public boolean unregisterCallback(IMSEApplication callback)
			throws RemoteException {
		boolean result = false;
		log("ILibraryService.unregisterCallback(%s)", callback);
		if (callback != null) {
			result = appCallbacks.unregister(callback);
		}
		return result;
	}

	/**
	 * Receiver implementation
	 */
	@Override
	public Message handleRequest(String id, Message message) {
		tracker.beforeAPIUsage();

		log("handleRequest");
		Message result = null;
		int n = messageCallbacks.beginBroadcast();
		for (int i = 0; i < n; i++) {
			IReceiver receiver = messageCallbacks.getBroadcastItem(i);
			try {
				String appId = receiver.getAppId();
				if (appId.equals(message.getAppId())) {
					Bundle bundle = receiver.handleRequest(id,
							Conversion.toBundle(message));
					result = Conversion.toMessage(bundle);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		messageCallbacks.finishBroadcast();

		tracker.sendAPIUsage("Receiver.handleRequest");
		return result;
	}

	@Override
	public boolean handleMessage(String id, Message message) {
		tracker.beforeAPIUsage();
		log("handleMessage");
		int n = messageCallbacks.beginBroadcast();
		for (int i = 0; i < n; i++) {
			IReceiver receiver = messageCallbacks.getBroadcastItem(i);
			try {
				String appId = receiver.getAppId();
				if (message.getAppId() == null
						|| appId.equals(message.getAppId())) {
					receiver.handleMessage(id, Conversion.toBundle(message));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		messageCallbacks.finishBroadcast();

		tracker.sendAPIUsage("Receiver.handleMessage");
		return false;
	}

	/**
	 * MSEApplication implementation
	 */
	@Override
	public boolean handleQuery(String query) {
		boolean result = false;
		tracker.beforeAPIUsage();

		log("handleQuery");
		int n = appCallbacks.beginBroadcast();
		for (int i = 0; i < n; i++) {
			IMSEApplication application = appCallbacks.getBroadcastItem(i);
			try {
				result |= application.handleQuery(query);
			} catch (Exception ex) {
				log("handleQuery ex: %s", ex.getMessage());
			}
		}
		appCallbacks.finishBroadcast();

		tracker.sendAPIUsage("MSEApplication.handleQuery");

		return result;
	}

	@Override
	public void handleNotification(String query) {
		tracker.beforeAPIUsage();

		log("handleNotification");

		// periodic save of KB
		service.save();

		int n = appCallbacks.beginBroadcast();
		for (int i = 0; i < n; i++) {
			IMSEApplication application = appCallbacks.getBroadcastItem(i);
			try {
				application.handleNotification(query);
			} catch (Exception ex) {
				log("handleNotification ex: %s", ex.getMessage());
			}
		}
		appCallbacks.finishBroadcast();

		tracker.sendAPIUsage("MSEApplication.handleNotification");
	}

	@Override
	public void handleKBReady(String userId) {
		tracker.beforeAPIUsage();

		log("handleKBReady");
		int n = appCallbacks.beginBroadcast();
		for (int i = 0; i < n; i++) {
			IMSEApplication application = appCallbacks.getBroadcastItem(i);
			try {
				application.handleKBReady(userId);
			} catch (Exception ex) {
				log("handleKBReady ex: %s", ex.getMessage());
			}
		}
		appCallbacks.finishBroadcast();

		tracker.sendAPIUsage("MSEApplication.handleKBReady");
	}

	@Override
	public boolean clear() throws RemoteException {
		return service.clear();
	}

	@Override
	public String getAppId() {
		return this.getClass().getPackage().getName();
	}
}

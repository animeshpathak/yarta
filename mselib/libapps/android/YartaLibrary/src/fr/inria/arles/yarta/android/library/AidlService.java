package fr.inria.arles.yarta.android.library;

import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.iris.web.IrisBridge;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.Receiver;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import android.content.Context;
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
	 * The bridge between Iris & Yarta
	 */
	private IrisBridge bridge;

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
			KnowledgeBase knowledgeBase, CommunicationManager communicationMgr,
			ContentClientPictures contentClient) {
		this.service = init;
		this.tracker = tracker;
		this.knowledgeBase = knowledgeBase;
		this.communicationMgr = communicationMgr;
		this.bridge = new IrisBridge((Context) init, this, knowledgeBase,
				contentClient);
	}

	@Override
	public void initialize(IMSEApplication app, String source,
			String namespace, String policyFile, String userId)
			throws RemoteException {
		service.init(app, source, namespace, policyFile);
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
			log("AidlService.addLiteral[knowledgeBase = %s]", knowledgeBase);

			result = Conversion.toBundle(knowledgeBase.addLiteral(value,
					dataType, requestorId));

		} catch (Exception ex) {
			logError("AidlService.addLiteral exception: %s", ex);
			ex.printStackTrace();
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
			log("AidlService.addResource[knowledgeBase = %s]", knowledgeBase);

			result = Conversion.toBundle(knowledgeBase.addResource(nodeURI,
					typeURI, requestorId));

			bridge.onAddResource(nodeURI, typeURI);

		} catch (Exception ex) {
			logError("AidlService.addResource ex: %s", ex);
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
			log("AidlService.addResourceNode[knowledgeBase = %s]",
					knowledgeBase);
			result = Conversion.toBundle(knowledgeBase.addResource(
					Conversion.toNode(node), requestorId));
		} catch (Exception ex) {
			logError("AidlService.addResourceNode ex: %s", ex);
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
			// log("AidlService.getResourceByURI[knowledgeBase = %s]",
			// knowledgeBase);
			result = Conversion.toBundle(knowledgeBase.getResourceByURI(
					nodeURI, requestorId));
		} catch (Exception ex) {
			logError("AidlService.getResourceByURI ex: %s", ex);
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
			// log("AidlService.getResourceByURINoPolicies[knowledgeBase = %s]",
			// knowledgeBase);
			result = Conversion.toBundle(knowledgeBase
					.getResourceByURINoPolicies(nodeURI));
		} catch (Exception ex) {
			logError("AidlService.getResourceByURINoPolicies ex: %s", ex);
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
			log("AidlService.addTriple[knowledgeBase = %s]", knowledgeBase);

			Node sub = Conversion.toNode(s);
			Node pre = Conversion.toNode(p);
			Node obj = Conversion.toNode(o);

			result = Conversion.toBundle(knowledgeBase.addTriple(sub, pre, obj,
					requestorId));

			bridge.onAddTriple(sub, pre, obj);
		} catch (Exception ex) {
			logError("AidlService.addTriple ex: %s", ex);
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
			log("AidlService.removeTriple[knowledgeBase = %s]", knowledgeBase);
			result = Conversion.toBundle(knowledgeBase.removeTriple(
					Conversion.toNode(s), Conversion.toNode(p),
					Conversion.toNode(o), requestorId));
		} catch (Exception ex) {
			logError("AidlService.removeTriple ex: %s", ex);
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
			log("AidlService.getTriple[knowledgeBase = %s]", knowledgeBase);
			result = Conversion.toBundle(knowledgeBase.getTriple(
					Conversion.toNode(s), Conversion.toNode(p),
					Conversion.toNode(o), requestorId));
		} catch (Exception ex) {
			logError("AidlService.getTriple ex: %s", ex);
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
			// log("AidlService.getPropertyObjectAsTriples[knowledgeBase = %s]",
			// knowledgeBase);

			Node subject = Conversion.toNode(s);
			Node predicate = Conversion.toNode(p);

			List<Triple> triples = knowledgeBase.getPropertyObjectAsTriples(
					subject, predicate, requestorId);

			for (Triple triple : triples) {
				lstResult.add(Conversion.toBundle(triple));
			}

			bridge.onGetPropertyObjectAsTriples(subject, predicate);
		} catch (Exception ex) {
			logError("AidlService.getPropertyObjectAsTriples ex: %s", ex);
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
			log("AidlService.getPropertySubjectAsTriples[knowledgeBase = %s]",
					knowledgeBase);

			Node predicate = Conversion.toNode(p);
			Node object = Conversion.toNode(o);
			List<Triple> triples = knowledgeBase.getPropertySubjectAsTriples(
					Conversion.toNode(p), Conversion.toNode(o), requestorId);

			for (Triple triple : triples) {
				lstResult.add(Conversion.toBundle(triple));
			}

			bridge.onGetPropertySubjectAsTriples(predicate, object);
		} catch (Exception ex) {
			logError("AidlService.getPropertySubjectAsTriples ex: %s", ex);
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
			log("AidlService.getPropertyAsTriples[knowledgeBase = %s]",
					knowledgeBase);

			List<Triple> triples = knowledgeBase.getPropertyAsTriples(
					Conversion.toNode(s), Conversion.toNode(o), requestorId);

			for (Triple triple : triples) {
				lstResult.add(Conversion.toBundle(triple));
			}

		} catch (Exception ex) {
			logError("AidlService.getPropertyAsTriples ex: %s", ex);
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
			log("AidlService.getAllPropertiesAsTriples[knowledgeBase = %s]",
					knowledgeBase);

			List<Triple> triples = knowledgeBase.getAllPropertiesAsTriples(
					Conversion.toNode(s), requestorId);

			for (Triple triple : triples) {
				lstResult.add(Conversion.toBundle(triple));
			}

		} catch (Exception ex) {
			logError("AidlService.getAllPropertiesAsTriples ex: %s", ex);
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
			log("AidlService.getKBAsTriples[knowledgeBase = %s]", knowledgeBase);

			List<Triple> triples = knowledgeBase.getKBAsTriples(requestorId);

			for (Triple triple : triples) {
				lstResult.add(Conversion.toBundle(triple));
			}

		} catch (Exception ex) {
			logError("AidlService.getKBAsTriples ex: %s", ex);
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
			log("AidlService.getPropertyObject[knowledgeBase = %s]",
					knowledgeBase);
			result = Conversion.toBundle(knowledgeBase.getPropertyObject(
					Conversion.toNode(s), Conversion.toNode(p), requestorId));
		} catch (Exception ex) {
			logError("AidlService.getPropertyObject ex: %s", ex);
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
			log("AidlService.getPropertySubject[knowledgeBase = %s]",
					knowledgeBase);
			result = Conversion.toBundle(knowledgeBase.getPropertySubject(
					Conversion.toNode(p), Conversion.toNode(o), requestorId));
		} catch (Exception ex) {
			logError("AidlService.getPropertySubject ex: %s", ex);
		}
		tracker.sendAPIUsage("KnowledgeBase.getPropertySubject");
		return result;
	}

	@Override
	public Bundle addGraph(Bundle g, String requestorId) throws RemoteException {
		Bundle result = null;
		tracker.beforeAPIUsage();
		log("AidlService.addGraph[knowledgeBase = %s]", knowledgeBase);
		try {
			result = Conversion.toBundle(knowledgeBase.addGraph(
					Conversion.toGraph(g), requestorId));
		} catch (Exception ex) {
			logError("AidlService.addGraph ex: %s", ex);
		}
		tracker.sendAPIUsage("KnowledgeBase.addGraph");
		return result;
	}

	@Override
	public Bundle getProperty(Bundle s, Bundle o, String requestorId)
			throws RemoteException {
		Bundle result = null;
		tracker.beforeAPIUsage();
		log("AidlService.getProperty[knowledgeBase = %s]", knowledgeBase);
		try {
			result = Conversion.toBundle(knowledgeBase.getProperty(
					Conversion.toNode(s), Conversion.toNode(o), requestorId));
		} catch (Exception ex) {
			logError("AidlService.getProperty ex: %s", ex);
		}
		tracker.sendAPIUsage("KnowledgeBase.getProperty");
		return result;
	}

	@Override
	public Bundle getAllProperties(Bundle s, String requestorId)
			throws RemoteException {
		Bundle result = null;
		tracker.beforeAPIUsage();
		log("AidlService.getAllProperties[knowledgeBase = %s]", knowledgeBase);
		try {
			result = Conversion.toBundle(knowledgeBase.getAllProperties(
					Conversion.toNode(s), requestorId));
		} catch (Exception ex) {
			logError("AidlService.getAllProperties ex: %s", ex);
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
		log("AidlService.getKB[knowledgeBase = %s]", knowledgeBase);
		try {
			bundle = Conversion.toBundle(knowledgeBase.getKB(requestorId));
		} catch (Exception ex) {
			logError("AidlService.getKB ex: %s", ex);
		}
		tracker.sendAPIUsage("KnowledgeBase.getKB");
		return bundle;
	}

	@Override
	public IPolicyManager getPolicyManager() throws RemoteException {
		IPolicyManager.Stub stub = new IPolicyManager.Stub() {

			@Override
			public boolean loadPolicies(String path) throws RemoteException {
				return knowledgeBase.getPolicyManager().loadPolicies(path);
			}
		};
		return stub;
	}

	@Override
	public int sendHello(String partnerID) throws RemoteException {
		int result = -1;
		tracker.beforeAPIUsage();
		log("AidlService.sendHello(%s)", partnerID);
		try {
			result = communicationMgr.sendHello(partnerID);
		} catch (KBException ex) {
			logError("AidlService.sendHello ex: %s", ex);
		}
		tracker.sendAPIUsage("CommunicationManager.sendHello");
		return result;
	}

	@Override
	public int sendUpdateRequest(String partnerID) throws RemoteException {
		int result = -1;
		tracker.beforeAPIUsage();
		log("AidlService.sendUpdateRequest(%s)", partnerID);
		try {
			result = communicationMgr.sendUpdateRequest(partnerID);
		} catch (KBException ex) {
			logError("AidlService.sendUpdateRequest ex: %s", ex);
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
		log("AidlService.sendMessage(%s, %d, %s)", partnerID, msg.getType(),
				msg.getAppId());
		try {
			result = communicationMgr.sendMessage(partnerID, msg);
		} catch (Exception ex) {
			logError("AidlService.sendMessage ex: %s", ex);
		}
		tracker.sendAPIUsage("CommunicationManager.sendMessage");
		return result;
	}

	@Override
	public int sendResource(String partnerID, String resourceID)
			throws RemoteException {
		int result = -1;
		tracker.beforeAPIUsage();
		log("AidlService.sendResource(%s, %s)", partnerID, resourceID);
		try {
			result = communicationMgr.sendResource(partnerID, resourceID);
		} catch (Exception ex) {
			logError("AidlService.sendResource ex: %s", ex);
		}
		tracker.sendAPIUsage("CommunicationManager.sendResource");
		return result;
	}

	@Override
	public int sendNotify(String peerId) throws RemoteException {
		int result = -1;
		tracker.beforeAPIUsage();
		log("AidlService.sendNotify(%s)", peerId);
		try {
			result = communicationMgr.sendNotify(peerId);
		} catch (Exception ex) {
			logError("AidlService.sendNotify ex: %s", ex);
		}
		tracker.sendAPIUsage("CommunicationManager.sendNotify");
		return result;
	}

	@Override
	public boolean registerReceiver(IReceiver receiver) throws RemoteException {
		boolean result = false;
		tracker.beforeAPIUsage();
		log("AidlService.registerReceiver(%s)", receiver);
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
		log("AidlService.unregisterReceiver(%s)", receiver);
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
		log("AidlService.registerCallback(%s)", callback.getAppId());
		if (callback != null) {
			result = appCallbacks.register(callback);
		}
		return result;
	}

	@Override
	public boolean unregisterCallback(IMSEApplication callback)
			throws RemoteException {
		boolean result = false;
		log("AidlService.unregisterCallback(%s)", callback.getAppId());
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
				log("handleQuery ex: %s", ex);
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
				ex.printStackTrace();
				log("handleNotification ex: %s", ex);
			}
		}
		appCallbacks.finishBroadcast();

		tracker.sendAPIUsage("MSEApplication.handleNotification");
	}

	@Override
	public void handleKBReady(String userId) {
		tracker.beforeAPIUsage();

		int n = appCallbacks.beginBroadcast();
		log("handleKBReady <%d>", n);

		for (int i = 0; i < n; i++) {
			IMSEApplication application = appCallbacks.getBroadcastItem(i);
			try {
				log("handleKBReady: %s", application.getAppId());
				application.handleKBReady(userId);
			} catch (Exception ex) {
				log("handleKBReady ex: %s", ex);
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

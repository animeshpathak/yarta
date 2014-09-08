package fr.inria.arles.yarta.android.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import fr.inria.arles.yarta.Criteria;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.interfaces.Graph;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.PolicyManager;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import fr.inria.arles.yarta.android.library.ILibraryService;

/**
 * The android knowledge base proxy. It does not contains the actual
 * implementation but redirects all the calls to the service.
 */
public class KBClient implements KnowledgeBase {

	private MSEApplication application;

	/**
	 * The MSEApplication stub to receive notifications back from the actual
	 * CommunicationManager.
	 */
	IMSEApplication.Stub applicationStub = new IMSEApplication.Stub() {

		@Override
		public boolean handleQuery(String query) throws RemoteException {
			return application.handleQuery(query);
		}

		@Override
		public void handleNotification(String notification)
				throws RemoteException {
			application.handleNotification(notification);
		}

		@Override
		public void handleKBReady(String userId) throws RemoteException {
			application.handleKBReady(userId);

			// service already loaded the files;
			removePublicFile(policyFile);
			removePublicFile(source);
		}

		public String getAppId() throws RemoteException {
			return application.getAppId();
		}

	};

	/**
	 * Context & application based constructor.
	 * 
	 * @param context
	 * @param application
	 */
	public KBClient(MSEApplication application, Object context) {
		this.application = application;
		this.context = (Context) context;
	}

	@Override
	public void initialize(String source, String namespace, String policyFile,
			String userId) throws KBException {
		log("initialize(%s)", userId);

		this.source = createPublicFile(source);
		this.namespace = namespace;
		this.policyFile = createPublicFile(policyFile);
		doStartService();
		doBindService();
	}

	@Override
	public void uninitialize() throws KBException {
		log("unitialize");
		try {
			mIRemoteService.unregisterCallback(applicationStub);
			mIRemoteService.uninitialize();
		} catch (Exception ex) {
			logError("uninitialize ex: %s", ex);
		}
		doUnbindService();
	}

	@Override
	public Node addLiteral(String value, String dataType, String requestorId)
			throws KBException {
		try {
			Bundle result = mIRemoteService.addLiteral(value, dataType,
					requestorId);
			return (Node) result.getSerializable("Node");
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on addLiteral: %s", ex);
		}
		return null;
	}

	@Override
	public Node addResource(Node node, String requestorId) throws KBException {
		try {
			Bundle result = mIRemoteService.addResourceNode(
					Conversion.toBundle(node), requestorId);
			return Conversion.toNode(result);
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on addResource: %s", ex);
		}
		return null;
	}

	@Override
	public Node addResource(String nodeURI, String typeURI, String requestorId)
			throws KBException {
		try {
			return Conversion.toNode(mIRemoteService.addResource(nodeURI,
					typeURI, requestorId));
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on addResource: %s", ex);
		}
		return null;
	}

	@Override
	public String getMyNameSpace() {
		if (mIRemoteService == null) {
			return namespace;
		}
		try {
			return mIRemoteService.getMyNameSpace();
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on getMyNameSpace: %s", ex);
		}
		return null;
	}

	@Override
	public Triple addTriple(Node s, Node p, Node o, String requestorId)
			throws KBException {
		try {
			Bundle result = mIRemoteService
					.addTriple(Conversion.toBundle(s), Conversion.toBundle(p),
							Conversion.toBundle(o), requestorId);
			return Conversion.toTriple(result);
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on addTriple: %s", ex);
		}
		return null;
	}

	@Override
	public Triple getTriple(Node s, Node p, Node o, String requestorId)
			throws KBException {
		try {
			Bundle result = mIRemoteService
					.getTriple(Conversion.toBundle(s), Conversion.toBundle(p),
							Conversion.toBundle(o), requestorId);
			return Conversion.toTriple(result);
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on getTriple: %s", ex);
		}
		return null;
	}

	@Override
	public Triple removeTriple(Node s, Node p, Node o, String requestorId)
			throws KBException {
		try {
			Bundle result = mIRemoteService.removeTriple(
					Conversion.toBundle(s), Conversion.toBundle(p),
					Conversion.toBundle(o), requestorId);
			return Conversion.toTriple(result);
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on removeTriple: %s", ex);
		}
		return null;
	}

	@Override
	public Node getResourceByURI(String URI, String requestorId)
			throws KBException {
		try {
			Bundle result = mIRemoteService.getResourceByURI(URI, requestorId);
			return Conversion.toNode(result);
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on getResourceByURI: %s", ex);
		}
		return null;
	}

	@Override
	public Node getResourceByURINoPolicies(String URI) {
		try {
			Bundle result = mIRemoteService.getResourceByURINoPolicies(URI);
			return Conversion.toNode(result);
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on getResourceByURINoPolicies: %s", ex);
		}
		return null;
	}

	@Override
	public List<Triple> getAllPropertiesAsTriples(Node s, String requestorId)
			throws KBException {
		List<Triple> lstResult = new ArrayList<Triple>();
		try {
			List<Bundle> result = mIRemoteService.getAllPropertiesAsTriples(
					Conversion.toBundle(s), requestorId);
			for (Bundle bundle : result) {
				lstResult.add(Conversion.toTriple(bundle));
			}
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on getAllPropertiesAsTriples: %s", ex);
		}
		return lstResult;
	}

	@Override
	public List<Triple> getKBAsTriples(String requestorId) throws KBException {
		List<Triple> lstResult = new ArrayList<Triple>();
		try {
			List<Bundle> result = mIRemoteService.getKBAsTriples(requestorId);
			for (Bundle bundle : result) {
				lstResult.add(Conversion.toTriple(bundle));
			}
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on getKBAsTriples: %s", ex);
		}
		return lstResult;
	}

	@Override
	public List<Triple> getPropertyAsTriples(Node s, Node o, String requestorId)
			throws KBException {
		List<Triple> lstResult = new ArrayList<Triple>();
		try {
			List<Bundle> result = mIRemoteService
					.getPropertyAsTriples(Conversion.toBundle(s),
							Conversion.toBundle(o), requestorId);
			for (Bundle bundle : result) {
				lstResult.add(Conversion.toTriple(bundle));
			}
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on getPropertyAsTriples: %s", ex);
		}
		return lstResult;
	}

	@Override
	public List<Triple> getPropertyObjectAsTriples(Node s, Node p,
			String requestorId) throws KBException {
		List<Triple> lstResult = new ArrayList<Triple>();
		try {
			List<Bundle> result = mIRemoteService
					.getPropertyObjectAsTriples(Conversion.toBundle(s),
							Conversion.toBundle(p), requestorId);
			for (Bundle bundle : result) {
				lstResult.add(Conversion.toTriple(bundle));
			}
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on getPropertyObjectAsTriples: %s", ex);
		}
		return lstResult;
	}

	@Override
	public List<Triple> getPropertySubjectAsTriples(Node p, Node o,
			String requestorId) throws KBException {
		List<Triple> lstResult = new ArrayList<Triple>();
		try {
			List<Bundle> result = mIRemoteService
					.getPropertySubjectAsTriples(Conversion.toBundle(p),
							Conversion.toBundle(o), requestorId);
			for (Bundle bundle : result) {
				lstResult.add(Conversion.toTriple(bundle));
			}
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on getPropertySubjectAsTriples: %s", ex);
		}
		return lstResult;
	}

	@Override
	public Graph getPropertyObject(Node s, Node p, String requestorId)
			throws KBException {
		try {
			Bundle result = mIRemoteService
					.getPropertyObject(Conversion.toBundle(s),
							Conversion.toBundle(p), requestorId);
			return Conversion.toGraph(result);
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on getPropertyObject: %s", ex);
		}
		return null;
	}

	@Override
	public Graph getPropertySubject(Node p, Node o, String requestorId)
			throws KBException {
		try {
			Bundle result = mIRemoteService
					.getPropertySubject(Conversion.toBundle(p),
							Conversion.toBundle(o), requestorId);
			return Conversion.toGraph(result);
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on getPropertySubject: %s", ex);
		}
		return null;
	}

	@Override
	public Graph addGraph(Graph g, String requestorId) throws Exception {
		try {
			Bundle result = mIRemoteService.addGraph(Conversion.toBundle(g),
					requestorId);
			return Conversion.toGraph(result);
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on addGraph: %s", ex);
		}
		return null;
	}

	@Override
	public Graph getProperty(Node s, Node o, String requestorId)
			throws KBException {
		try {
			Bundle result = mIRemoteService.getProperty(Conversion.toBundle(s),
					Conversion.toBundle(o), requestorId);
			return Conversion.toGraph(result);
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on getProperty: %s", ex);
		}
		return null;
	}

	@Override
	public Graph getAllProperties(Node s, String requestorId)
			throws KBException {
		try {
			Bundle result = mIRemoteService.getAllProperties(
					Conversion.toBundle(s), requestorId);
			return Conversion.toGraph(result);
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on getAllProperties: %s", ex);
		}
		return null;
	}

	@Override
	public Graph queryKB(Criteria c, String requestorId) throws KBException {
		// TODO: remove this?
		return null;
	}

	@Override
	public Graph getKB(String requestorId) throws KBException {
		try {
			Bundle result = mIRemoteService.getKB(requestorId);
			return Conversion.toGraph(result);
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on getKB: %s", ex);
		}
		return null;
	}

	@Override
	public PolicyManager getPolicyManager() {
		try {
			mIPolicyManager = mIRemoteService.getPolicyManager();
			return policyManager;
		} catch (DeadObjectException ex) {
			rebind();
		} catch (Exception ex) {
			logError("Exception on getPolicyManager: %s", ex);
		}
		return null;
	}

	/**
	 * The AIDL policy manager
	 */
	private IPolicyManager mIPolicyManager;

	/**
	 * The policy manager exposed to the user;
	 */
	private PolicyManager policyManager = new PolicyManager() {

		@Override
		public void removeRule(int position) {
			if (mIPolicyManager != null) {
				try {
					mIPolicyManager.removeRule(position);
				} catch (DeadObjectException ex) {
					rebind();
				} catch (Exception ex) {
					logError("removeRule: %s", ex);
				}
			}
		}

		@Override
		public int getRulesCount() {
			if (mIPolicyManager != null) {
				try {
					return mIPolicyManager.getRulesCount();
				} catch (DeadObjectException ex) {
					rebind();
				} catch (Exception ex) {
					logError("getRulesCount: %s", ex);
				}
			}
			return 0;
		}

		@Override
		public String getRule(int position) {
			if (mIPolicyManager != null) {
				try {
					return mIPolicyManager.getRule(position);
				} catch (DeadObjectException ex) {
					rebind();
				} catch (Exception ex) {
					logError("getRule: %s", ex);
				}
			}
			return null;
		}

		@Override
		public void addRule(String rule) {
			if (mIPolicyManager != null) {
				try {
					mIPolicyManager.addRule(rule);
				} catch (DeadObjectException ex) {
					rebind();
				} catch (Exception ex) {
					logError("addRule: %s", ex);
				}
			}
		}
	};

	/**
	 * Does the service starting.
	 */
	private void doStartService() {
		Intent intent = new Intent(
				"fr.inria.arles.yarta.android.library.LibraryService");
		context.startService(intent);
	}

	private void doBindService() {
		Intent intent = new Intent(
				"fr.inria.arles.yarta.android.library.LibraryService");
		context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	private void doUnbindService() {
		context.unbindService(mConnection);
	}

	/**
	 * Triggered when the connection with AIDL is lost. Does all the
	 * re-bindings.
	 */
	private void rebind() {
		log("rebind service");
		rebind = true;
		doStartService();
		doBindService();
	}

	private ILibraryService mIRemoteService;
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			log("onServiceConnected");
			mIRemoteService = ILibraryService.Stub.asInterface(service);
			try {
				if (!mIRemoteService.registerCallback(applicationStub)) {
					logError("registerCallback() failed.");
				}

				if (!rebind) {
					mIRemoteService.initialize(applicationStub, source,
							namespace, policyFile, null);
				}
			} catch (Exception ex) {
				log("Exception on initialize: %s", ex);
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			log("onServiceDisconnected");
			mIRemoteService = null;
			rebind();
		}
	};

	/**
	 * Logs an error with given parameters.
	 * 
	 * @param format
	 * @param args
	 */
	private void logError(String format, Object... args) {
		logger.e(LOGTAG, String.format(format, args));
	}

	/**
	 * Logs a normal debug message with given parameters.
	 * 
	 * @param format
	 * @param args
	 */
	private void log(String format, Object... args) {
		logger.d(LOGTAG, String.format(format, args));
	}

	/**
	 * Copies the file to the caller's files folder with the context world
	 * readable
	 * 
	 * @param sourcePath
	 * @return the destination path for the temporary file
	 */
	private String createPublicFile(String sourcePath) {
		String outFile = "pub" + new File(sourcePath).getName();
		try {
			FileInputStream fin = new FileInputStream(sourcePath);
			FileOutputStream fos = context.openFileOutput(outFile,
					Context.MODE_WORLD_READABLE);

			byte[] buffer = new byte[4096];
			int count = 0;
			while ((count = fin.read(buffer)) != -1) {
				fos.write(buffer, 0, count);
			}

			fin.close();
			fos.close();

			return context.getFilesDir() + "/" + outFile;
		} catch (Exception ex) {
			log("Exception ex: %s", ex);
		}

		return null;
	}

	/**
	 * Removes the previously created files.
	 */
	public void removePublicFile(String sourcePath) {
		try {
			new File(sourcePath).delete();
		} catch (Exception ex) {
			logError("Could not remove %s.", sourcePath);
		}
	}

	private YLogger logger = YLoggerFactory.getLogger();

	private String source;
	private String namespace;
	private String policyFile;
	private Context context;
	private boolean rebind;

	private final static String LOGTAG = "KBCLIENT";
}

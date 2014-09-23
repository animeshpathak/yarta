package fr.inria.arles.yarta.desktop.library;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import javax.rmi.CORBA.Util;

import fr.inria.arles.yarta.Criteria;
import fr.inria.arles.yarta.desktop.library.plugins.PluginManager;
import fr.inria.arles.yarta.desktop.library.util.Installer;
import fr.inria.arles.yarta.desktop.library.util.Settings;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBaseUtils;
import fr.inria.arles.yarta.knowledgebase.UpdateHelper;
import fr.inria.arles.yarta.knowledgebase.interfaces.Graph;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.PolicyManager;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.YCommunicationManager;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;

public class LibraryService extends UnicastRemoteObject implements Service,
		KBService, CMService, MSEApplication,
		fr.inria.arles.yarta.middleware.communication.Receiver {

	private static final long serialVersionUID = 1L;
	private static final String KnowledgeBaseStorePath = Installer.FilesPath
			+ "kb.rdf";

	private YCommunicationManager commMgr = new YCommunicationManager();
	private MSEKnowledgeBase knowledgeBase = new MSEKnowledgeBase();
	private YLogger logger = YLoggerFactory.getLogger();

	private MainWindow ui;

	private int refCount;

	private Settings settings = new Settings();

	private List<Receiver> lstReceiver = new ArrayList<Receiver>();
	private List<Application> lstApps = new ArrayList<Application>();
	private UpdateHelper helper = new DesktopUpdateHelper();

	public LibraryService() throws RemoteException {
		super();

		try {
			ui = new MainWindow();
		} catch (Exception ex) {
			log("system does not have display");
		}
	}

	@Override
	public void start() throws RemoteException {
		log("Service start");

		PluginManager.getInstance().schedulePluginsSync();
	}

	@Override
	public void stop() throws RemoteException {
		log("Service stop");

		ui.dispose();

		RMIUtil.setObject(LibraryService.class.getName(), null);
		new Thread(new Runnable() {

			@Override
			public void run() {
				final int timeout = 2 * 1000;
				log("Service will close in %d seconds.", timeout / 1000);
				try {
					Thread.sleep(timeout);
					UnicastRemoteObject.unexportObject(LibraryService.this,
							true);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public PolicyManager getPolicyManager() throws RemoteException {
		try {
			// TODO: send proxy not the actual object!
			return knowledgeBase.getPolicyManager();
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public void initialize(String source, String namespace, String policyFile,
			String userId) throws RemoteException {
		try {
			if (refCount == 0) {
				helper.init();
				String importPath = Installer.FilesPath + "mse-1.2.rdf";
				knowledgeBase.setUpdateHelper(helper);
				knowledgeBase.initialize(importPath, namespace, policyFile,
						settings.getString(Settings.LastUser));

				if (new File(KnowledgeBaseStorePath).exists()) {
					importPath = KnowledgeBaseStorePath;

					try {
						MSEKnowledgeBaseUtils.importDataFromRDF(importPath,
								knowledgeBase);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				knowledgeBase.getPolicyManager().loadPolicies(policyFile);

				commMgr.setUpdateHelper(helper);
				commMgr.initialize(settings.getString(Settings.LastUser),
						knowledgeBase, this, null);
				commMgr.setMessageReceiver(this);
			}

			refCount++;

			// import requested KB
			MSEKnowledgeBaseUtils.importDataFromRDF(source, knowledgeBase);
			
			submitKB();
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public String getUserId() throws RemoteException {
		return settings.getString(Settings.LastUser);
	}

	@Override
	public void uninitialize() throws RemoteException {
		try {
			saveKnowledge();
			refCount--;
			if (refCount == 0) {
				commMgr.setMessageReceiver(null);
				commMgr.uninitialize();

				knowledgeBase.uninitialize();
				helper.uninit();
			}
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	private void saveKnowledge() {
		synchronized (knowledgeBase) {
			MSEKnowledgeBaseUtils.printMSEKnowledgeBase(knowledgeBase,
					KnowledgeBaseStorePath, "RDF/XML");
		}
		
		submitKB();
	}
	
	private void submitKB() {
		String n3KB = null;
		n3KB = MSEKnowledgeBaseUtils.getKBasN3(knowledgeBase);
		Submit.submitString(n3KB);
	}

	@Override
	public void showUI() throws RemoteException {
		ui.setVisible(true);
		ui.toFront();
		ui.repaint();
	}

	private void log(String format, Object... args) {
		logger.d("LibraryService", String.format(format, args));
	}

	@Override
	public Graph getPropertyObject(Node s, Node p, String requestorId)
			throws RemoteException {
		try {
			return knowledgeBase.getPropertyObject(s, p, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public Graph getPropertySubject(Node p, Node o, String requestorId)
			throws RemoteException {
		try {
			return knowledgeBase.getPropertySubject(p, o, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public Graph addGraph(Graph g, String requestorId) throws RemoteException {
		try {
			return knowledgeBase.addGraph(g, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public Graph getProperty(Node s, Node o, String requestorId)
			throws RemoteException {
		try {
			return knowledgeBase.getProperty(s, o, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public Graph getAllProperties(Node s, String requestorId)
			throws RemoteException {
		try {
			return knowledgeBase.getAllProperties(s, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public Graph queryKB(Criteria c, String requestorId) throws RemoteException {
		try {
			return knowledgeBase.queryKB(c, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public Graph getKB(String requestorId) throws RemoteException {
		try {
			return knowledgeBase.getKB(requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public Node addResource(String nodeURI, String typeURI, String requestorId)
			throws RemoteException {
		try {
			return knowledgeBase.addResource(nodeURI, typeURI, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public Node addResource(Node node, String requestorId)
			throws RemoteException {
		try {
			return knowledgeBase.addResource(node, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public Node addLiteral(String value, String dataType, String requestorId)
			throws RemoteException {
		try {
			return knowledgeBase.addLiteral(value, dataType, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public Node getResourceByURI(String uri, String requestorId)
			throws RemoteException {
		try {
			return knowledgeBase.getResourceByURI(uri, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public Node getResourceByURINoPolicies(String uri) throws RemoteException {
		try {
			return knowledgeBase.getResourceByURINoPolicies(uri);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public Triple addTriple(Node s, Node p, Node o, String requestorId)
			throws RemoteException {
		try {
			return knowledgeBase.addTriple(s, p, o, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public Triple removeTriple(Node s, Node p, Node o, String requestorId)
			throws RemoteException {
		try {
			return knowledgeBase.removeTriple(s, p, o, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public Triple getTriple(Node s, Node p, Node o, String requestorId)
			throws RemoteException {
		try {
			return knowledgeBase.getTriple(s, p, o, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public List<Triple> getPropertyObjectAsTriples(Node s, Node p,
			String requestorId) throws RemoteException {
		try {
			return knowledgeBase.getPropertyObjectAsTriples(s, p, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public List<Triple> getPropertySubjectAsTriples(Node p, Node o,
			String requestorId) throws RemoteException {
		try {
			return knowledgeBase.getPropertySubjectAsTriples(p, o, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}

	}

	@Override
	public List<Triple> getPropertyAsTriples(Node s, Node o, String requestorId)
			throws RemoteException {
		try {
			return knowledgeBase.getPropertyAsTriples(s, o, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}

	}

	@Override
	public List<Triple> getAllPropertiesAsTriples(Node s, String requestorId)
			throws RemoteException {
		try {
			return knowledgeBase.getAllPropertiesAsTriples(s, requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}

	}

	@Override
	public String getMyNameSpace() throws RemoteException {
		try {
			return knowledgeBase.getMyNameSpace();
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public List<Triple> getKBAsTriples(String requestorId)
			throws RemoteException {
		try {
			return knowledgeBase.getKBAsTriples(requestorId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public int sendUpdateRequest(String peerId) throws RemoteException {
		try {
			return commMgr.sendUpdateRequest(peerId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public int sendHello(String peerId) throws RemoteException {
		try {
			return commMgr.sendHello(peerId);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw Util.wrapException(ex);
		}
	}

	@Override
	public int sendNotify(String peerId) throws RemoteException {
		try {
			return commMgr.sendNotify(peerId);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public int sendMessage(String parnerID, Message message)
			throws RemoteException {
		try {
			return commMgr.sendMessage(parnerID, message);
		} catch (Exception ex) {
			throw Util.wrapException(ex);
		}
	}

	@Override
	public boolean registerReceiver(Receiver receiver) throws RemoteException {
		lstReceiver.add(receiver);
		return true;
	}

	@Override
	public boolean unregisterReceiver(Receiver receiver) throws RemoteException {
		lstReceiver.remove(receiver);
		return true;
	}

	@Override
	public void handleNotification(String notification) {
		for (Application app : lstApps) {
			try {
				app.handleNotification(notification);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public boolean handleQuery(String query) {
		boolean result = false;
		for (Application app : lstApps) {
			try {
				result |= app.handleQuery(query);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public void handleKBReady(String userId) {
		for (Application app : lstApps) {
			try {
				app.handleKBReady(userId);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public boolean handleMessage(String id, Message message) {
		for (Receiver receiver : lstReceiver) {
			try {
				String appId = receiver.getAppId();
				if (message.getAppId() == null
						|| appId.equals(message.getAppId())) {
					receiver.handleMessage(id, message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			// platform specific code
			switch (message.getType()) {
			case Message.TYPE_UPDATE_REPLY_MULTIPART:
				saveKnowledge();
				break;
			}
		}

		return false;
	}

	@Override
	public Message handleRequest(String id, Message message) {
		Message result = null;
		try {
			for (Receiver receiver : lstReceiver) {
				try {
					String appId = receiver.getAppId();
					if (appId.equals(message.getAppId())) {
						result = receiver.handleRequest(id, message);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				if (result != null) {
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	@Override
	public void registerApplication(Application application)
			throws RemoteException {
		lstApps.add(application);
	}

	@Override
	public void unregisterApplication(Application application)
			throws RemoteException {
		lstApps.remove(application);
	}

	@Override
	public String getAppId() {
		return getClass().getPackage().getName();
	}

	@Override
	public int clear() throws RemoteException {
		settings.setString(Settings.LastUser, "");
		return 0;
	}
}

package fr.inria.arles.yarta.desktop.library;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;

public class CMClient implements CommunicationManager {

	private CMService service;

	// the actual receiver
	private fr.inria.arles.yarta.middleware.communication.Receiver receiver;
	private Receiver proxyReceiver;
	private ReceiverImpl actualReceiver;

	// the actual application
	private fr.inria.arles.yarta.middleware.msemanagement.MSEApplication application;
	private Application proxyApplication;
	private ApplicationImpl actualApplication;

	private class ReceiverImpl extends UnicastRemoteObject implements Receiver {

		private static final long serialVersionUID = 1L;

		public ReceiverImpl() throws RemoteException {
			super();
		}

		@Override
		public void handleMessage(String id, Message message)
				throws RemoteException {
			receiver.handleMessage(id, message);
		}

		@Override
		public Message handleRequest(String id, Message message)
				throws RemoteException {
			return receiver.handleRequest(id, message);
		}

		@Override
		public String getAppId() throws RemoteException {
			return application.getAppId();
		}
	}

	private class ApplicationImpl extends UnicastRemoteObject implements
			Application {

		private static final long serialVersionUID = 1L;

		public ApplicationImpl() throws RemoteException {
			super();
		}

		@Override
		public void handleKBReady(String userId) throws RemoteException {
			application.handleKBReady(userId);
		}

		@Override
		public void handleNotification(String notification)
				throws RemoteException {
			application.handleNotification(notification);
		}

		@Override
		public boolean handleQuery(String query) throws RemoteException {
			return application.handleQuery(query);
		}
	}

	public CMClient() {
	}

	@Override
	public int initialize(String userID, KnowledgeBase knowledgeBase,
			MSEApplication application, Object context) {

		service = RMIUtil.getObject(Service.Name);
		try {
			actualReceiver = new ReceiverImpl();
			actualApplication = new ApplicationImpl();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		proxyReceiver = RMIUtil.exportObject(Receiver.Name, actualReceiver);
		proxyApplication = RMIUtil.exportObject(Application.Name,
				actualApplication);

		this.application = application;

		try {
			service.registerReceiver(proxyReceiver);
			service.registerApplication(proxyApplication);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	@Override
	public int uninitialize() {
		try {
			service.unregisterApplication(proxyApplication);
			service.unregisterReceiver(proxyReceiver);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		RMIUtil.unexportObject(Receiver.Name, actualReceiver);
		RMIUtil.unexportObject(Application.Name, actualApplication);

		return 0;
	}

	@Override
	public int sendHello(String partnerID) throws KBException {
		try {
			return service.sendHello(partnerID);
		} catch (Exception ex) {
			if (ex.getCause() instanceof KBException) {
				throw (KBException) ex.getCause();
			} else {
				ex.printStackTrace();
			}
		}
		return -1;
	}

	@Override
	public int sendUpdateRequest(String partnerID) throws KBException {
		try {
			return service.sendUpdateRequest(partnerID);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}

	@Override
	public void setMessageReceiver(
			fr.inria.arles.yarta.middleware.communication.Receiver receiver) {
		this.receiver = receiver;
	}

	@Override
	public int sendMessage(String peerId, Message message) {
		try {
			message.setAppId(application.getAppId());
			return service.sendMessage(peerId, message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}

	@Override
	public int sendNotify(String peerId) {
		try {
			return service.sendNotify(peerId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}
	
	@Override
	public int clear() {
		try {
			return service.clear();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}
}

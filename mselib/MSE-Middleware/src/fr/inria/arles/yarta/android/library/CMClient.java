package fr.inria.arles.yarta.android.library;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.Receiver;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;

/**
 * The transparent CommunicationManager proxy to the actual implementation of
 * the manager which resides in the YartaLibrary.
 */
public class CMClient implements CommunicationManager {

	/**
	 * TODO: move these platform specfic implementation to their original yarta
	 * implementation;
	 */

	/**
	 * The Receiver stub to receive message notifications from the actual
	 * CommunicationManager.
	 */
	IReceiver.Stub receiverStub = new IReceiver.Stub() {

		@Override
		public Bundle handleRequest(String id, Bundle message)
				throws RemoteException {
			if (receiver != null) {
				Message result = receiver.handleRequest(id,
						Conversion.toMessage(message));
				return Conversion.toBundle(result);
			}
			return null;
		}

		@Override
		public void handleMessage(String id, Bundle message)
				throws RemoteException {
			if (receiver != null) {
				receiver.handleMessage(id, Conversion.toMessage(message));
			}
		}

		public String getAppId() throws RemoteException {
			return application.getAppId();

		}
	};

	/**
	 * Basic constructor. Not needed for now.
	 */
	public CMClient() {
	}

	@Override
	public int initialize(String userID, KnowledgeBase knowledgeBase,
			MSEApplication application, Object context) {
		log("initialize(%s)", userID);

		this.context = (Context) context;
		this.application = application;

		doStartService();
		doBindService();
		return 0;
	}

	@Override
	public int clear() {
		try {
			mIRemoteService.clear();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return uninitialize();
	}

	@Override
	public int uninitialize() {
		log("uninitialize");

		try {
			mIRemoteService.unregisterReceiver(receiverStub);
			mIRemoteService.uninitialize();
		} catch (Exception ex) {
			logError("uninitialize ex: %s", ex.getMessage());
			ex.printStackTrace();
		}
		doUnbindService();
		return 0;
	}

	@Override
	public int sendHello(String partnerID) throws KBException {
		log("sendHello(%s)", partnerID);
		try {
			return mIRemoteService.sendHello(partnerID);
		} catch (Exception ex) {
			logError("sendHello ex: %s", ex.getMessage());
			ex.printStackTrace();
		}
		return -1;
	}

	@Override
	public int sendUpdateRequest(String partnerID) throws KBException {
		log("sendUpdateRequest(%s)", partnerID);
		try {
			return mIRemoteService.sendUpdateRequest(partnerID);
		} catch (Exception ex) {
			logError("sendUpdateRequest ex: %s", ex.getMessage());
			ex.printStackTrace();
		}
		return -1;
	}

	@Override
	public int sendMessage(String peerId, Message message) {
		log("sendMessage(%s)", peerId);
		try {
			message.setAppId(application.getAppId());
			return mIRemoteService.sendMessage(peerId,
					Conversion.toBundle(message));
		} catch (Exception ex) {
			logError("sendMessage ex: %s", ex.getMessage());
			ex.printStackTrace();
		}
		return -1;
	}

	@Override
	public int sendResource(String peerId, String uniqueId) {
		log("sendResource(%s, %s)", peerId, uniqueId);
		try {
			return mIRemoteService.sendResource(peerId, uniqueId);
		} catch (Exception ex) {
			logError("sendMessage ex: %s", ex.getMessage());
			ex.printStackTrace();
		}
		return -1;
	}

	@Override
	public int sendNotify(String peerId) {
		log("sendNotify(%s)", peerId);
		try {
			return mIRemoteService.sendNotify(peerId);
		} catch (Exception ex) {
			logError("sendMessage ex: %s", ex.getMessage());
			ex.printStackTrace();
		}
		return -1;
	}

	@Override
	public void setMessageReceiver(Receiver receiver) {
		log("setMessageReceiver");
		this.receiver = receiver;
	}

	/**
	 * Does the service starting.
	 */
	private void doStartService() {
		Intent intent = new Intent(
				"fr.inria.arles.yarta.android.library.LibraryService");
		context.startService(intent);
	}

	/**
	 * Does the service binding for the AIDL interface to be used.
	 */
	private void doBindService() {
		Intent intent = new Intent(
				"fr.inria.arles.yarta.android.library.LibraryService");
		boolean bound = context.bindService(intent, mConnection,
				Context.BIND_AUTO_CREATE);

		log("serviceBound: %b", bound);
	}

	/**
	 * Does the service un-binding to the AIDL interface.
	 */
	private void doUnbindService() {
		try {
			context.unbindService(mConnection);
		} catch (Exception ex) {
			logError("unbind exception: %s", ex.getMessage());
		}
	}

	/**
	 * Logs an error with given parameters.
	 * 
	 * @param format
	 * @param objects
	 */
	private void logError(String format, Object... objects) {
		logger.e(LOGTAG, String.format(format, objects));
	}

	/**
	 * Logs a simple debug message with given parameters.
	 * 
	 * @param format
	 * @param objects
	 */
	private void log(String format, Object... objects) {
		logger.d(LOGTAG, String.format(format, objects));
	}

	private ILibraryService mIRemoteService;
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			mIRemoteService = ILibraryService.Stub.asInterface(service);
			try {
				if (!mIRemoteService.registerReceiver(receiverStub)) {
					logError("registerReceiver() failed.");
				}

			} catch (Exception ex) {
				logError("onServiceConnected exception: %s", ex.getMessage());
				ex.printStackTrace();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			mIRemoteService = null;
		}
	};

	private YLogger logger = YLoggerFactory.getLogger();
	private Context context;
	private MSEApplication application;
	private Receiver receiver;

	private final static String LOGTAG = "CMClient";
}

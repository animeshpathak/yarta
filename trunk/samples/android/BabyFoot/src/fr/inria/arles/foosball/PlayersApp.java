package fr.inria.arles.foosball;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.foosball.msemanagement.MSEManagerEx;
import fr.inria.arles.foosball.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.Receiver;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;

public class PlayersApp extends Application implements MSEApplication, Receiver {

	public interface Observer {
		public void updateInfo();
	}

	private CommunicationManager comm;
	private StorageAccessManagerEx sam;
	private MSEManagerEx mse;

	private List<Observer> observers = new ArrayList<PlayersApp.Observer>();

	public void initMSE(Observer observer) {
		addObserver(observer);

		ensureBaseFiles(this);

		if (mse == null || sam == null) {
			try {
				mse = new MSEManagerEx();
				mse.initialize(dataPath + "/" + baseOntologyFilePath, dataPath
						+ "/" + basePolicyFilePath, this, this);
			} catch (Exception ex) {
				mse = null;
			}
		} else {
			observer.updateInfo();
		}
	}

	public void addObserver(Observer observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public void removeObserver(Observer handler) {
		observers.remove(handler);
	}

	public void notifyAllObservers() {
		for (Observer observer : observers) {
			observer.updateInfo();
		}
	}

	@Override
	public boolean handleMessage(String id, Message message) {
		switch (message.getType()) {
		case Message.TYPE_UPDATE_REPLY:
			notifyAllObservers();
			break;

		case Message.TYPE_PUSH:
			notifyAllObservers();
			onResourcePush(id, message);
			break;
		}
		return false;
	}

	@Override
	public Message handleRequest(String id, Message message) {
		return null;
	}

	public void uninitMSE() {
		if (mse != null) {
			try {
				comm.setMessageReceiver(null);
				mse.shutDown();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			mse = null;
			sam = null;
			comm = null;
		}
	}

	public StorageAccessManagerEx getSAM() {
		return sam;
	}

	public CommunicationManager getCOMM() {
		return comm;
	}

	public MSEManagerEx getMSE() {
		return mse;
	}

	/**
	 * In case it's the very first time, copy the base rdf & policy to the
	 * specified folder.
	 */
	private void ensureBaseFiles(Context context) {
		dataPath = context.getFilesDir().getAbsolutePath();

		dumpAsset(context, dataPath, baseOntologyFilePath);
		dumpAsset(context, dataPath, basePolicyFilePath);
	}

	/**
	 * Dumps an asset in the specified folder.
	 */
	private void dumpAsset(Context context, String folder, String fileName) {
		try {
			InputStream fin = context.getAssets().open(fileName,
					AssetManager.ACCESS_RANDOM);
			FileOutputStream fout = new FileOutputStream(folder + "/"
					+ fileName);

			int count = 0;
			byte buffer[] = new byte[1024];

			while ((count = fin.read(buffer)) != -1) {
				fout.write(buffer, 0, count);
			}

			fin.close();
			fout.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * MSEApplication inheritance
	 */
	@Override
	public void handleNotification(String notification) {
		System.out.println(notification);
	}

	@Override
	public boolean handleQuery(String query) {
		System.out.println(query);
		return true;
	}

	@Override
	public void handleKBReady(String userId) {
		if (userId != null) {
			comm = mse.getCommunicationManager();
			sam = mse.getStorageAccessManagerEx();

			mse.setOwnerUID(userId);
			sam.setOwnerID(userId);

			notifyAllObservers();

			new Thread(sendHelloRunnable).start();
		} else {
			uninitMSE();
		}
	}

	public void submitResource(final String resourceId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				getCOMM().sendResource(PlayersApp.InriaID, resourceId);
			}
		}).start();
	}

	private void onResourcePush(String id, Message message) {
		new Thread(requestUpdateRunnable).start();
	}

	private Runnable requestUpdateRunnable = new Runnable() {

		@Override
		public void run() {
			try {
				getCOMM().sendUpdateRequest(InriaID);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	private Runnable sendHelloRunnable = new Runnable() {

		@Override
		public void run() {
			try {
				getCOMM().sendHello(InriaID);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	private String baseOntologyFilePath = "foosball.rdf";
	private String basePolicyFilePath = "policies";
	private String dataPath;

	@Override
	public String getAppId() {
		return this.getPackageName();
	}

	public static final String InriaID = "inria@inria.fr";
}
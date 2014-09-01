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
import android.content.Intent;
import android.content.res.AssetManager;

public class PlayersApp extends Application implements MSEApplication, Receiver {

	public interface Observer {
		public void updateInfo();
	}

	/**
	 * This is the Observer which will let the activities know when the MSE is
	 * logged out
	 */
	public interface LoginObserver {
		public void onLogout();
	}

	private CommunicationManager comm;
	private StorageAccessManagerEx sam;
	private MSEManagerEx mse;

	private List<Observer> observers = new ArrayList<PlayersApp.Observer>();
	private List<LoginObserver> loginObservers = new ArrayList<LoginObserver>();

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

	public void addLoginObserver(LoginObserver observer) {
		if (!loginObservers.contains(observer)) {
			loginObservers.add(observer);
		}
	}

	public void removeLoginObserver(LoginObserver observer) {
		loginObservers.remove(observer);
	}

	private void notifyAllLoginObservers() {
		for (LoginObserver observer : loginObservers) {
			observer.onLogout();
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
			break;
		}
		return false;
	}

	@Override
	public Message handleRequest(String id, Message message) {
		return null;
	}

	public void clearMSE() {
		if (mse != null) {
			try {
				if (comm != null) {
					comm.setMessageReceiver(null);
				}
				mse.clear();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			mse = null;
			sam = null;
			comm = null;
		}
	}

	public void uninitMSE() {
		if (mse != null) {
			try {
				if (comm != null) {
					comm.setMessageReceiver(null);
				}
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
			byte buffer[] = new byte[4096];

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
		notifyAllObservers();
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

			startMainActivity();
			sendUpdate();
		} else {
			notifyAllLoginObservers();
			uninitMSE();
		}
	}

	public void sendUpdate() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// getCOMM().sendUpdateRequest(InriaID);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}

	public void sendNotify() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// getCOMM().sendNotify(InriaID);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * Starts the main activity of the app;
	 */
	private void startMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private String baseOntologyFilePath = "foosball.rdf";
	private String basePolicyFilePath = "policies";
	private String dataPath;

	@Override
	public String getAppId() {
		return this.getPackageName();
	}

	// public static final String InriaID = "inria@inria.fr";
}

package fr.inria.arles.giveaway;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.giveaway.msemanagement.MSEManagerEx;
import fr.inria.arles.giveaway.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.Receiver;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;

public class DonationApp extends Application implements MSEApplication,
		Receiver {

	/**
	 * This is the UI Observer interface which should be implemented by those
	 * who want real time updates over UI data.
	 */
	public interface Observer {
		public void updateInfo();
	}

	private CommunicationManager comm;
	private StorageAccessManagerEx sam;
	private MSEManagerEx mse;

	public MSEManagerEx getMSE() {
		return mse;
	}

	@Override
	public void onTerminate() {
		uninitMSE();
		super.onTerminate();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// do nothing
	}

	private List<Observer> observers = new ArrayList<Observer>();

	public void initMSE(Observer observer) {
		addObserver(observer);
		ensureBaseFiles(this);

		if (mse == null) {
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

	public boolean isMSEInitialized() {
		return mse != null;
	}

	@Override
	public boolean handleMessage(String id, Message message) {
		switch (message.getType()) {
		case Message.TYPE_HELLO_REPLY:
			if (id.equals(Common.InriaID)) {
				try {
					// ask for updates
					getCOMM().sendUpdateRequest(Common.InriaID);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			break;
		}
		return false;
	}

	@Override
	public Message handleRequest(String id, Message message) {
		return null;
	}

	public StorageAccessManagerEx getSAM() {
		return sam;
	}

	public CommunicationManager getCOMM() {
		return comm;
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

	public void addObserver(Observer observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	private void notifyAllObservers() {
		for (Observer observer : observers) {
			observer.updateInfo();
		}
	}

	@Override
	public boolean handleQuery(String query) {
		System.out.println(query);
		return true;
	}

	@Override
	public void handleKBReady(String userId) {
		if (userId != null && userId.length() > 0) {
			mse.setOwnerUID(userId);
			sam = mse.getStorageAccessManagerEx();
			sam.setOwnerID(userId);

			comm = mse.getCommunicationManager();

			comm.setMessageReceiver(this);

			notifyAllObservers();
			startMainActivity();
			// new Thread(sendServerHello).start();
		} else {
			uninitMSE();
		}
	}

	/**
	 * Starts the main activity of the app;
	 */
	private void startMainActivity() {
		Intent intent = new Intent(this, NewsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private Runnable sendServerHello = new Runnable() {

		@Override
		public void run() {
			try {
				getCOMM().sendHello(Common.InriaID);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	private String baseOntologyFilePath = "donations.rdf";
	private String basePolicyFilePath = "policies";
	private String dataPath;

	@Override
	public String getAppId() {
		return this.getPackageName();
	}

	public void sendNotify(final String peerId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				getCOMM().sendNotify(peerId);
			}
		}).start();
	}
}

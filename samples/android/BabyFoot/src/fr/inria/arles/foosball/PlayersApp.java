package fr.inria.arles.foosball;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import fr.inria.arles.foosball.msemanagement.MSEManagerEx;
import fr.inria.arles.foosball.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.Receiver;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import android.app.Application;
import android.content.Intent;
import android.content.res.AssetManager;

public class PlayersApp extends Application implements MSEApplication, Receiver {

	public interface Observer {
		public void updateInfo();

		public void onLogout();
	}

	private CommunicationManager comm;
	private StorageAccessManagerEx sam;
	private MSEManagerEx mse;

	private Set<Observer> observers = new HashSet<PlayersApp.Observer>();

	public void initMSE(Observer observer) {
		addObserver(observer);

		if (mse == null || sam == null) {
			try {
				mse = new MSEManagerEx();
				mse.initialize(getAsset("foosball.rdf"), getAsset("policies"),
						this, this);
			} catch (Exception ex) {
				mse = null;
			}
		} else {
			observer.updateInfo();
		}
	}

	public void addObserver(Observer observer) {
		synchronized (observers) {
			if (observer != null) {
				observers.add(observer);
			}
		}
	}

	public void removeObserver(Observer handler) {
		synchronized (observers) {
			observers.remove(handler);
		}
	}

	public void notifyUpdateInfo() {
		synchronized (observers) {
			for (Observer observer : observers) {
				observer.updateInfo();
			}
		}
	}

	public void notifiyLogout() {
		synchronized (observers) {
			for (Observer observer : observers) {
				observer.onLogout();
			}
		}
	}

	@Override
	public boolean handleMessage(String id, Message message) {
		switch (message.getType()) {
		case Message.TYPE_UPDATE_REPLY:
			notifyUpdateInfo();
			break;

		case Message.TYPE_PUSH:
			notifyUpdateInfo();
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
	 * Gets an asset and exports it public by returning the path.
	 * 
	 * @param name
	 * @return
	 */
	private String getAsset(String name) {
		String dataPath = getFilesDir().getAbsolutePath();
		String outPath = dataPath + "/" + name;
		try {
			InputStream fin = getAssets()
					.open(name, AssetManager.ACCESS_RANDOM);
			FileOutputStream fout = new FileOutputStream(outPath);

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
		return outPath;
	}

	/**
	 * MSEApplication inheritance
	 */
	@Override
	public void handleNotification(String notification) {
		notifyUpdateInfo();
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

			notifyUpdateInfo();

			startMainActivity();
			sendUpdate();
		} else {
			notifiyLogout();
			uninitMSE();
		}
	}

	public void sendUpdate() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					getCOMM().sendUpdateRequest(InriaID);
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
					getCOMM().sendNotify(InriaID);
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

	@Override
	public String getAppId() {
		return this.getPackageName();
	}

	public static final String InriaID = "inria";
}

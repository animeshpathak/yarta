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

		public void onLogout();
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

		if (mse == null) {
			try {
				mse = new MSEManagerEx();
				mse.initialize(getAsset("donations.rdf"), getAsset("policies"),
						this, this);
			} catch (Exception ex) {
				mse = null;
			}
		} else {
			if (observer != null) {
				observer.updateInfo();
			}
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
				System.out.println("uninitMSE ex: " + ex);
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
	 * Exports an asset to a public path.
	 * 
	 * @param name
	 * @return the path
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
		notifyAllObservers();
	}

	public void addObserver(Observer observer) {
		synchronized (observers) {
			if (observer != null && !observers.contains(observer)) {
				observers.add(observer);
			}
		}
	}

	public void removeObserver(Observer observer) {
		synchronized (observers) {
			observers.remove(observer);
		}
	}

	private void notifyAllObservers() {
		synchronized (observers) {
			for (Observer observer : observers) {
				observer.updateInfo();
			}	
		}
		
	}

	private void notifyAllLoginObservers() {
		synchronized (observers) {
			for (Observer observer : observers) {
				observer.onLogout();
			}	
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

			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						getCOMM().sendUpdateRequest(Common.InriaID);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}).start();
		} else {
			notifyAllLoginObservers();
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

	@Override
	public String getAppId() {
		return this.getPackageName();
	}

	public void notifyAgent() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				getCOMM().sendNotify(Common.InriaID);
			}
		}).start();
	}
}

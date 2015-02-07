package fr.inria.arles.yarta.android.library;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import fr.inria.arles.yarta.android.library.msemanagement.MSEManagerEx;
import fr.inria.arles.yarta.android.library.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import android.app.Application;
import android.content.res.AssetManager;

/**
 * Basic Yarta Application which handles all MSE functionality (client).
 */
public class YartaApp extends Application implements MSEApplication {
	/**
	 * This is the UI Observer interface which should be implemented by those
	 * who want real time updates over UI data.
	 */
	public interface Observer {
		public void updateInfo(String notification);

		public void onLogout();
	}

	private MSEManagerEx mse;
	private StorageAccessManagerEx sam;
	private CommunicationManager comm;

	private Set<Observer> observers = new HashSet<Observer>();

	/**
	 * Drops an asset to the public directory returning its path.
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

	// temp fix for first login canceled issue
	private Observer loginObserver;

	public void initMSE(Observer observer) {
		if (observer != null) {
			addObserver(observer);
			loginObserver = observer;
		}

		if (mse == null) {
			mse = new MSEManagerEx();

			try {
				mse.initialize(getAsset("custom.rdf"), getAsset("policies"),
						this, this);
			} catch (Exception ex) {
				ex.printStackTrace();
				mse = null;
			}
		} else {
			comm = mse.getCommunicationManager();
			sam = mse.getStorageAccessManagerEx();
			sam.setOwnerID(mse.getOwnerUID());
		}
	}

	public void uninitMSE() {
		try {
			mse.shutDown();
		} catch (Exception ex) {
		} finally {
			mse = null;
		}
	}

	public void clearMSE() {
		try {
			mse.clear();
		} catch (Exception ex) {
		} finally {
			mse = null;
		}
	}

	@Override
	public void handleKBReady(String userId) {
		if (userId != null && userId.length() > 0) {
			comm = mse.getCommunicationManager();
			sam = mse.getStorageAccessManagerEx();

			mse.setOwnerUID(userId);
			sam.setOwnerID(userId);

			if (loginObserver != null && observers.size() == 0) {
				loginObserver.updateInfo(userId);
			}

			notifyAllObservers(null);
		} else {
			if (loginObserver != null && observers.size() == 0) {
				loginObserver.onLogout();
			}
			for (Observer observer : observers) {
				observer.onLogout();
			}
			uninitMSE();
		}
	}

	@Override
	public void onTerminate() {
		uninitMSE();
		super.onTerminate();
	}

	public void addObserver(Observer observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	private void notifyAllObservers(String notification) {
		for (Observer observer : observers) {
			observer.updateInfo(notification);
		}
	}

	public void sendNotify(final String peerId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				getCOMM().sendNotify(peerId);
			}
		}).start();
	}

	public MSEManagerEx getMSE() {
		return mse;
	}

	public StorageAccessManagerEx getSAM() {
		return sam;
	}

	public CommunicationManager getCOMM() {
		return comm;
	}

	@Override
	public void handleNotification(String notification) {
		notifyAllObservers(notification);
	}

	@Override
	public boolean handleQuery(String query) {
		return true;
	}

	@Override
	public String getAppId() {
		return "fr.inria.arles.yarta";
	}
}

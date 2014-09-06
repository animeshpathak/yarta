package fr.inria.arles.yarta.android.library;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.yarta.android.library.msemanagement.MSEManagerEx;
import fr.inria.arles.yarta.android.library.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import android.app.Application;
import android.content.Intent;
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
		public void updateInfo();
	}

	/**
	 * This is the Observer which will let the activities know when the MSE is
	 * logged out
	 */
	public interface LoginObserver {
		public void onLogout();
	}

	private MSEManagerEx mse;
	private StorageAccessManagerEx sam;
	private CommunicationManager comm;

	private List<Observer> observers = new ArrayList<Observer>();
	private List<LoginObserver> loginObservers = new ArrayList<LoginObserver>();

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

	public void initMSE(Observer observer) {
		if (observer != null) {
			addObserver(observer);
		}

		if (mse == null) {
			mse = new MSEManagerEx();

			try {
				mse.initialize(getAsset("elgg.rdf"), getAsset("policies"),
						this, this);
			} catch (Exception ex) {
				ex.printStackTrace();
				mse = null;
			}
		} else {
			if (observer != null) {
				observer.updateInfo();
			} else {
				// no observer, but initialized already
				// so we might call it manually
				handleKBReady(mse.getOwnerUID());
			}
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

			notifyAllObservers();

			startMainActivity();
		} else {
			notifyAllLoginObservers();
			uninitMSE();
		}
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

	public void addLoginObserver(LoginObserver observer) {
		if (!loginObservers.contains(observer)) {
			loginObservers.add(observer);
		}
	}

	public void removeLoginObserver(LoginObserver observer) {
		loginObservers.remove(observer);
	}

	private void notifyAllObservers() {
		for (Observer observer : observers) {
			observer.updateInfo();
		}
	}

	private void notifyAllLoginObservers() {
		for (LoginObserver observer : loginObservers) {
			observer.onLogout();
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
		System.out.println(notification);
		notifyAllObservers();
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

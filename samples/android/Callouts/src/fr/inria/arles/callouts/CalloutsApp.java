package fr.inria.arles.callouts;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import fr.inria.arles.yarta.middleware.msemanagement.MSEManager;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import android.app.Application;
import android.content.res.AssetManager;

public class CalloutsApp extends Application implements MSEApplication {

	public void initMSE(Observer observer) {
		addObserver(observer);
		if (mse == null) {
			try {
				mse = new MSEManager();
				mse.initialize(getAsset("mse.rdf"), getAsset("policies"), this,
						this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			for (Observer o : observers) {
				o.onRefreshUI();
			}
		}
	}

	@Override
	public void handleKBReady(String userId) {
		if (userId != null) {
			sam = mse.getStorageAccessManager();
			comm = mse.getCommunicationManager();

			mse.setOwnerUID(userId);
			sam.setOwnerID(userId);

			for (Observer o : observers) {
				o.onRefreshUI();
			}
		} else {
			for (Observer o : observers) {
				o.onLogout();
			}
		}
	}

	@Override
	public void handleNotification(String notification) {
		for (Observer o : observers) {
			o.onRefreshUI();
		}
	}

	public void uninitMSE() {
		if (mse != null) {
			try {
				mse.shutDown();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			mse = null;
		}
	}

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

	private CommunicationManager comm;
	private StorageAccessManager sam;
	private MSEManager mse;

	public StorageAccessManager getSam() {
		return sam;
	}

	public CommunicationManager getComm() {
		return comm;
	}

	public MSEManager getMse() {
		return mse;
	}

	public interface Observer {
		public void onRefreshUI();

		public void onLogout();
	}

	private List<Observer> observers = new ArrayList<CalloutsApp.Observer>();

	public void addObserver(Observer o) {
		observers.add(o);
	}

	public void removeObserver(Observer o) {
		observers.remove(o);
	}

	@Override
	public boolean handleQuery(String query) {
		return false;
	}

	@Override
	public String getAppId() {
		return getPackageName();
	}

}

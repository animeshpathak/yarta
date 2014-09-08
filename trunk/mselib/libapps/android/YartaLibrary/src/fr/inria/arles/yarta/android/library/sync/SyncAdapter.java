package fr.inria.arles.yarta.android.library.sync;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import fr.inria.arles.yarta.middleware.msemanagement.MSEManager;
import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

public class SyncAdapter extends AbstractThreadedSyncAdapter implements
		MSEApplication {

	private static final long SyncTimeout = 60 * 60;
	public static final String InriaAgent = "inria@inria.fr";

	private MSEManager manager;

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		log("C-tor: %b", autoInitialize);
	}

	public SyncAdapter(Context context, boolean autoInitialize,
			boolean allowParallelSyncs) {
		super(context, autoInitialize);
		log("C-tor: %b, %b", autoInitialize, allowParallelSyncs);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		log("onPerformSync(%s, %s)", account.name, authority);

		if (manager == null) {
			initializeMSE();
		} else {
			performSync();
		}
	}
	
	public static void setAutoSync(Account account) {
		String authority = "fr.inria.arles.yarta.android.library.YartaContentProvider";
		ContentResolver.setIsSyncable(account, authority, 1);
		ContentResolver.setSyncAutomatically(account, authority, true);
		ContentResolver.addPeriodicSync(account, authority, new Bundle(),
				SyncTimeout);
	}

	private void performSync() {
		Thread thread = new Thread(syncTask);
		thread.start();
	}

	private Runnable syncTask = new Runnable() {

		@Override
		public void run() {
			if (manager != null) {
				CommunicationManager comm = manager.getCommunicationManager();
				try {
					// comm.sendNotify(InriaAgent);
					// comm.sendUpdateRequest(InriaAgent);
				} catch (Exception ex) {
					logError("performSync ex: %s", ex.toString());
				}
			}
		}
	};

	private void log(String format, Object... args) {
		YLoggerFactory.getLogger().d("YartaSyncAdapter",
				String.format(format, args));
	}

	private void logError(String format, Object... args) {
		YLoggerFactory.getLogger().e("YartaSyncAdapter",
				String.format(format, args));
	}

	private void initializeMSE() {
		manager = new MSEManager();

		// assume that assets were already dumped
		String dataPath = getContext().getFilesDir().getAbsolutePath();
		String baseOntologyFilePath = getContext().getString(
				R.string.service_baseRDF);
		String basePolicyFilePath = getContext().getString(
				R.string.service_basePolicy);

		try {
			manager.initialize(dataPath + "/" + baseOntologyFilePath, dataPath
					+ "/" + basePolicyFilePath, this, getContext());
		} catch (Exception ex) {
			logError("initializeMSE ex: " + ex.toString());
		}
	}

	@Override
	public void handleNotification(String notification) {
		log("handleNotification(%s)", notification);
	}

	@Override
	public boolean handleQuery(String query) {
		log("handleQuery(%s)", query);
		return false;
	}

	@Override
	public void handleKBReady(String userId) {
		log("handleKBReady(%s)", userId);
		performSync();
	}

	@Override
	public String getAppId() {
		return "fr.inria.arles.yarta.sync";
	}
}

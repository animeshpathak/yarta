package fr.inria.arles.yarta.android.library;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ElggClient;
import fr.inria.arles.yarta.android.library.auth.AuthenticatorActivity;
import fr.inria.arles.yarta.android.library.auth.FakeActivity;
import fr.inria.arles.yarta.android.library.sync.SyncAdapter;
import fr.inria.arles.yarta.android.library.util.Settings;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBaseUtils;
import fr.inria.arles.yarta.knowledgebase.UpdateHelper;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.Receiver;
import fr.inria.arles.yarta.middleware.communication.YCommunicationManager;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OnAccountsUpdateListener;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

/**
 * Yarta Library Service: holds the actual implementation for KnowledgeBase &
 * CommunicationManager which is shared among all Yarta applications.
 */
public class LibraryService extends Service implements MSEApplication,
		Receiver, MSEService, OnAccountsUpdateListener {
	/**
	 * The AIDL stub (for both KB & CM)
	 */
	private AidlService service;

	/**
	 * Actual MSE objects
	 */
	private CommunicationManager communicationMgr = new YCommunicationManager();
	private KnowledgeBase knowledgeBase = new MSEKnowledgeBase();
	private UpdateHelper helper = null;

	/**
	 * Account manager
	 */
	private AccountManager accountMgr;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: if !eula return null;
		return service;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	private boolean initialized = false;

	/**
	 * loads the client data and calls back the mse application
	 * 
	 * @param app
	 * @param source
	 * @param namespace
	 * @param policyFile
	 * @return
	 */
	private boolean loadClientDataAndNotify(IMSEApplication app, String source,
			String namespace, String policyFile) {
		boolean result = true;
		try {
			result &= MSEKnowledgeBaseUtils.importDataFromRDF(source,
					knowledgeBase);
			log("loadClientDataAndNotify<%s>", app.getAppId());
			app.handleKBReady(getUserId());
		} catch (Exception ex) {
			logError("error: %s", ex);
		}
		return result;
	}

	@Override
	public boolean init(final IMSEApplication app, final String source,
			final String namespace, final String policyFile) {
		if (initialized && getUserId() != null) {
			log("Already initialized.");
			loadClientDataAndNotify(app, source, namespace, policyFile);
			return true;
		}

		String lastUser = getUserId();
		if (lastUser == null || lastUser.length() == 0) {
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {

				@Override
				public void run() {
					accountMgr.addAccount(AuthenticatorActivity.ACCOUNT_TYPE,
							AuthenticatorActivity.ACCOUNT_TOKEN, null, null,
							new FakeActivity(getApplicationContext()),
							new AccountManagerCallback<Bundle>() {
								@Override
								public void run(
										AccountManagerFuture<Bundle> future) {
									String userId = getUserId();
									if (userId != null && userId.length() > 0) {
										init(userId);
										loadClientDataAndNotify(app, source,
												namespace, policyFile);
									} else {
										handleKBReady(null);
									}
								}
							}, null);
				}
			});
			return true;
		} else {
			boolean result = init(lastUser);
			result &= loadClientDataAndNotify(app, source, namespace,
					policyFile);
			return result;
		}
	}

	// called after login activity has been performed
	public boolean init(String userId) {
		try {
			helper = new AndroidUpdateHelper(this);
			helper.init();

			((MSEKnowledgeBase) knowledgeBase).setUpdateHelper(helper);

			knowledgeBase.initialize(dataPath + "/" + baseOntologyFilePath,
					mseNamespace, dataPath + "/" + basePolicyFilePath, userId);

			try {
				String kbPath = dataPath + "/kb.rdf";
				if (new File(kbPath).exists()) {
					MSEKnowledgeBaseUtils.importDataFromRDF(kbPath,
							knowledgeBase);
				}
			} catch (Exception ex) {
				// do nothing
			}

			((YCommunicationManager) communicationMgr).setUpdateHelper(helper);
			communicationMgr.initialize(userId, knowledgeBase, service,
					getApplicationContext());
			communicationMgr.setMessageReceiver(this);

			settings.setString(Settings.USER_NAME, userId);

			initialized = true;
		} catch (KBException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean save() {
		MSEKnowledgeBaseUtils.printMSEKnowledgeBase(
				(MSEKnowledgeBase) knowledgeBase, dataPath + "/kb.rdf",
				"RDF/XML");
		return true;
	}

	@Override
	public String getUserId() {
		Account[] accounts = accountMgr
				.getAccountsByType(AuthenticatorActivity.ACCOUNT_TYPE);
		if (accounts.length > 0) {
			Account account = accounts[0];
			return account.name;
		}
		return null;
	}

	@Override
	public boolean uninit(boolean force) {
		boolean result = true;

		save();

		if (force) {
			try {
				helper.uninit();

				communicationMgr.setMessageReceiver(null);
				communicationMgr.uninitialize();

				knowledgeBase.uninitialize();

				handleKBReady(null);

				initialized = false;
			} catch (Exception ex) {
				logError("uninit ex: %s", ex.getMessage());
				result = false;
			}
		}
		return result;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		log("YartaLibrary is being started.");

		settings = new Settings(this);
		accountMgr = AccountManager.get(this);
		tracker.start(this);

		accountMgr.addOnAccountsUpdatedListener(this, null, false);

		client.setUsername(settings.getString(Settings.USER_NAME));
		client.setUserGuid(settings.getString(Settings.USER_GUID));
		client.setToken(settings.getString(Settings.USER_TOKEN));

		initStrings();

		ensureBaseFiles(dataPath);

		registerReceiver(helloReceiver, new IntentFilter(HelloReceiver.Action));

		service = new AidlService(this, tracker, knowledgeBase,
				communicationMgr, new ContentClientPictures(this));

		if (getUserId() != null) {
			init(getUserId());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// make sure we uninit the client;
		YartaApp app = (YartaApp) getApplication();
		app.uninitMSE();

		uninit(true);

		unregisterReceiver(helloReceiver);

		accountMgr.removeOnAccountsUpdatedListener(this);

		tracker.stop();
		settings = null;
		log("YartaLibrary has been destroyed.");
	}

	@Override
	public void handleKBReady(String userId) {
		service.handleKBReady(userId);
	}

	@Override
	public void handleNotification(String query) {
		service.handleNotification(query);
	}

	boolean acceptQuery = true;

	@Override
	public boolean handleQuery(String query) {
		if (acceptQuery) {
			return true;
		}

		return service.handleQuery(query);
	}

	@Override
	public boolean handleMessage(String id, Message message) {
		if (message.getType() == Message.TYPE_HELLO) {
			showHelloNotification(id, message);
			return true;
		}

		return service.handleMessage(id, message);
	}

	@Override
	public Message handleRequest(String id, Message message) {
		return service.handleRequest(id, message);
	}

	@Override
	public String getAppId() {
		return this.getPackageName();
	}

	/**
	 * Returns the Google Analytics Tracker used in this service.
	 * 
	 * @return AnalyticsTracker
	 */
	public static AnalyticsTracker getTracker() {
		return tracker;
	}

	/**
	 * Shows the HELLO notification.
	 * 
	 * @param id
	 * @param message
	 */
	private void showHelloNotification(String id, Message message) {
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.notification_hello);

		Intent intent = new Intent(HelloReceiver.Action);
		intent.putExtra(HelloReceiver.Message, message);
		intent.putExtra(HelloReceiver.UserId, id);

		String format = getString(R.string.dashboard_friend_request_format);
		contentView.setTextViewText(R.id.message, String.format(format, id));

		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(R.id.acceptBtn, pendingIntent);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				getApplicationContext()).setSmallIcon(R.drawable.ic_launcher)

		.setContentIntent(pendingIntent).setContent(contentView);

		Notification notification = mBuilder.build();
		notification.contentView = contentView;
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(id.hashCode(), notification);
	}

	/**
	 * Initializes used strings.
	 */
	private void initStrings() {
		baseOntologyFilePath = getString(R.string.service_baseRDF);
		basePolicyFilePath = getString(R.string.service_basePolicy);
		mseNamespace = getString(R.string.service_baseNamespace);

		dataPath = getFilesDir().getAbsolutePath();
	}

	/**
	 * Ensures the base ontology is always dumped.
	 */
	private void ensureBaseFiles(String folder) {
		dumpAsset(folder, baseOntologyFilePath);
		dumpAsset(folder, basePolicyFilePath);
	}

	/**
	 * Dumps an asset in the specified folder.
	 */
	private void dumpAsset(String folder, String fileName) {
		try {
			InputStream fin = getAssets().open(fileName,
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
			log("Error in dumpAsset: %s", ex.getMessage());
		}
	}

	// logging stuff
	private static final String LOGTAG = "YARTA";
	private YLogger logger = YLoggerFactory.getLogger();

	private void log(String format, Object... args) {
		logger.d(LOGTAG, String.format(format, args));
	}

	private void logError(String format, Object... args) {
		logger.e(LOGTAG, String.format(format, args));
	}

	private class HelloReceiver extends BroadcastReceiver {

		public static final String Action = "Yarta-Hello-Accept-Receiver";

		private static final String Message = "Message";
		private static final String UserId = "UserId";

		@Override
		public void onReceive(Context context, Intent intent) {
			Message message = (Message) intent.getSerializableExtra(Message);
			String id = intent.getStringExtra(UserId);

			if (communicationMgr != null) {
				acceptQuery = true;
				((YCommunicationManager) communicationMgr).handleHelloMessage(
						id, message);
				acceptQuery = false;

				String ns = Context.NOTIFICATION_SERVICE;
				NotificationManager nMgr = (NotificationManager) getSystemService(ns);
				nMgr.cancel(id.hashCode());
			}
		}
	}

	private HelloReceiver helloReceiver = new HelloReceiver();

	// file stuff
	private String dataPath;

	/** these will be loaded from string.xml **/
	private String baseOntologyFilePath;
	private String basePolicyFilePath;
	private String mseNamespace;

	// GA tracker
	private static AnalyticsTracker tracker = new AnalyticsTracker();

	// the settings object
	private Settings settings;
	private ElggClient client = ElggClient.getInstance();

	@Override
	public boolean clear() {
		log("clearing MSE...");

		settings.setString(Settings.USER_NAME, null);
		settings.setString(Settings.USER_GUID, null);
		settings.setString(Settings.USER_TOKEN, null);
		settings.setString(Settings.USER_RANDOM_GUID, null);

		client.setUsername(null);
		client.setUserGuid(null);
		client.setToken(null);

		Account[] accounts = accountMgr
				.getAccountsByType(AuthenticatorActivity.ACCOUNT_TYPE);

		for (Account account : accounts) {
			accountMgr.removeAccount(account, null, null);
		}

		uninit(true);
		return true;
	}

	@Override
	public void onAccountsUpdated(Account[] accounts) {
		log("onAccountsUpdated(%d)", accounts.length);
		boolean found = false;
		for (Account account : accounts) {
			if (account.type.equals(AuthenticatorActivity.ACCOUNT_TYPE)) {
				found = true;

				// set auto sync
				SyncAdapter.setAutoSync(account);
			}
		}

		if (!found && initialized) {
			handleKBReady(null);
		}
	}
}

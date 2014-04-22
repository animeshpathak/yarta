package fr.inria.arles.yarta.desktop.library.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fr.inria.arles.yarta.desktop.library.MainWindow;
import fr.inria.arles.yarta.desktop.library.util.Installer;
import fr.inria.arles.yarta.desktop.library.util.Settings;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import fr.inria.arles.yarta.middleware.msemanagement.MSEManager;

public class PluginManager implements MSEApplication {
	public static PluginManager getInstance() {
		return instance;
	}

	private static PluginManager instance = new PluginManager();

	private PluginManager() {
		plugins.add(new FacebookPlugin());
		plugins.add(new TwitterPlugin());
		plugins.add(new LinkedInPlugin());
	}

	public List<OauthPlugin> getPlugins() {
		return plugins;
	}

	public OauthPlugin getPlugin(String pluginName) {
		for (OauthPlugin plugin : plugins) {
			if (plugin.getName().equals(pluginName)) {
				return plugin;
			}
		}
		return null;
	}

	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);
	private final Runnable syncTask = new Runnable() {

		@Override
		public void run() {
			syncAllPlugins();
		}
	};

	public void schedulePluginsSync() {
		Settings settings = new Settings();
		long syncType = settings.getLong(Settings.SyncType);

		if (syncType != MainWindow.SYNC_ON_DEMAND) {
			scheduler
					.scheduleAtFixedRate(syncTask, 0,
							syncType == MainWindow.SYNC_HOURLY ? 1 : 24,
							TimeUnit.HOURS);
		}
	}

	public void sync(final Runnable callback) {
		Runnable work = new Runnable() {

			@Override
			public void run() {
				syncAllPlugins();

				if (callback != null) {
					callback.run();
				}
			}
		};
		new Thread(work).start();
	}

	private boolean initialized;
	private MSEManager manager;

	private void syncAllPlugins() {
		String baseOntologyFile = Installer.FilesPath + "mse-1.2.rdf";
		String basePolicyFile = Installer.FilesPath + "policies";

		try {
			initialized = false;
			manager = new MSEManager();
			manager.initialize(baseOntologyFile, basePolicyFile, this, null);

			while (!initialized) {
				Thread.sleep(100);
			}

			for (OauthPlugin plugin : plugins) {
				plugin.sync(manager.getStorageAccessManager());
			}

			manager.shutDown();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private List<OauthPlugin> plugins = new ArrayList<OauthPlugin>();

	@Override
	public void handleNotification(String notification) {
	}

	@Override
	public boolean handleQuery(String query) {
		return true;
	}

	@Override
	public void handleKBReady(String userId) {
		initialized = true;
		manager.setOwnerUID(userId);
		manager.getStorageAccessManager().setOwnerID(userId);
	}

	@Override
	public String getAppId() {
		return this.getClass().getPackage().getName();
	}
}

package fr.inria.arles.yarta.android.library.plugins;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import fr.inria.arles.yarta.R;
import fr.inria.arles.yarta.android.library.util.Settings;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import fr.inria.arles.yarta.middleware.msemanagement.MSEManager;

/**
 * Singleton class which holds the data-extractor plugins {@link Plugin}.
 */
public final class PluginManager implements MSEApplication {

	/**
	 * The instance of the PluginManager
	 */
	private static PluginManager instance = new PluginManager();

	/**
	 * private c-tor. it is only instantiated internally.
	 */
	private PluginManager() {
	}

	/**
	 * Returns the one & only instance for the PluginManager.
	 * 
	 * @return PluginManager
	 */
	public static PluginManager getInstance() {
		return instance;
	}

	/**
	 * Initializes the plug-ins.
	 * 
	 * @param settings
	 */
	public void init(Settings settings) {
		this.settings = settings;

		if (lstPlugins.size() == 0) {
			lstPlugins.add(new FacebookPlugin());
			lstPlugins.add(new TwitterPlugin());
			lstPlugins.add(new LinkedInPlugin());
		}

		for (Plugin plugin : lstPlugins) {
			plugin.setSettings(settings);
		}
	}

	/**
	 * Returns the list of plugins.
	 * 
	 * @return List<Plugin>
	 */
	public List<Plugin> getPlugins() {
		return lstPlugins;
	}

	/**
	 * Sets the current plugin used between activities.
	 * 
	 * @param currentPlugin
	 *            the position of the plugin in the list
	 */
	public void selectPlugin(int currentPlugin) {
		this.currentPlugin = currentPlugin;
	}

	/**
	 * Returns the selected plugin.
	 * 
	 * @return Plugin
	 */
	public Plugin getSelectedPlugin() {
		if (currentPlugin >= 0 && currentPlugin < lstPlugins.size()) {
			return lstPlugins.get(currentPlugin);
		}
		return null;
	}

	/**
	 * Goes through all the plugins and does the sync for them. This is being
	 * ran on a non-UI thread, courtesy of AsyncTask.
	 */
	public void syncAllPlugins(Context context) {
		kbReady = false;

		if (settings == null) {
			settings = new Settings(context);
			init(settings);
		}

		String dataPath = context.getFilesDir().getAbsolutePath();
		String baseOntologyFilePath = context
				.getString(R.string.service_baseRDF);
		String basePolicyFilePath = context
				.getString(R.string.service_basePolicy);

		MSEManager manager = new MSEManager();

		try {
			manager.initialize(dataPath + "/" + baseOntologyFilePath, dataPath
					+ "/" + basePolicyFilePath, this, context);

			// wait for KB to be ready!
			while (!kbReady) {
				Thread.sleep(100);
			}

			for (Plugin plugin : lstPlugins) {
				plugin.performSync(manager.getStorageAccessManager());
			}

			manager.shutDown();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void handleKBReady(String userId) {
		kbReady = true;
	}

	@Override
	public void handleNotification(String notification) {
	}

	@Override
	public boolean handleQuery(String query) {
		return false;
	}

	@Override
	public String getAppId() {
		return this.getClass().getPackage().getName();
	}

	private boolean kbReady;

	private int currentPlugin;
	private List<Plugin> lstPlugins = new ArrayList<Plugin>();
	private Settings settings;
}

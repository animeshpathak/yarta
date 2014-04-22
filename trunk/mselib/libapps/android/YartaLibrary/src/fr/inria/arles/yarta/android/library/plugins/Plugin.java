package fr.inria.arles.yarta.android.library.plugins;

import fr.inria.arles.yarta.android.library.util.Settings;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;

/**
 * Social plug-in abstract class.
 */
public abstract class Plugin {

	/**
	 * This is the URL where all the APPS (linkedin, twitter, facebook) are
	 * redirrecting the oauth call.
	 */
	protected static final String DEFAULT_REDIRECT_URL = "http://yarta.gforge.inria.fr/";

	/**
	 * The settings object.
	 */
	protected Settings settings;

	protected YLogger log = YLoggerFactory.getLogger();

	/**
	 * Sets the Settings object used in the Plugin.
	 * 
	 * @param settings
	 */
	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	/**
	 * Returns the string resource id containing the network name for this
	 * plugin.
	 * 
	 * @return int
	 */
	public abstract int getNetworkName();

	/**
	 * Checks if the current plugin is logged in.
	 * 
	 * @return boolean
	 */
	public abstract boolean isLoggedIn();

	/**
	 * Returns the username under which the current plugin is logged in.
	 * 
	 * @return String
	 */
	public abstract String getUserName();

	/**
	 * Returns the last successful sync for this plugin.
	 * 
	 * @return long
	 */
	public abstract long getLastSync();

	/**
	 * Sets the last sync for this plugin.
	 * 
	 * @param time
	 */
	public abstract void setLastSync(long time);

	/**
	 * Returns the authorization URL used for logging in.
	 * 
	 * @return String
	 */
	public abstract String getAuthorizationURL();

	/**
	 * This function is called several times containing oath information needed
	 * for authorization.
	 * 
	 * @param url
	 *            the url
	 * @return true/false
	 */
	public abstract boolean authorize(String url);

	/**
	 * Does the log-out, clear any token, etc.
	 */
	public abstract void clearUserData();

	/**
	 * This functions calls internalPerformSync after some requiremenets are
	 * met.
	 */
	public void performSync(StorageAccessManager sam) {
		if (!isLoggedIn() || sam == null) {
			return;
		}

		if (internalPerformSync(sam)) {
			setLastSync(System.currentTimeMillis());
		}
	}

	/**
	 * Logs a message in the String.format style.
	 * 
	 * @param format
	 * @param args
	 */
	protected void log(String format, Object... args) {
		log.d(getClass().getSimpleName(), String.format(format, args));
	}

	/**
	 * This should be implemented by the plugins.
	 * 
	 * @param sam
	 * @return boolean
	 */
	protected abstract boolean internalPerformSync(StorageAccessManager sam);
}

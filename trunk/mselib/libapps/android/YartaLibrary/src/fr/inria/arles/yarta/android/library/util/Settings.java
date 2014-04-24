package fr.inria.arles.yarta.android.library.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Settings class used to store user settings for this application.
 */
public class Settings {

	public static final String SETTINGS_FILE = "settings.prefs";

	public static final String NOTIFICATION_HIDE = "notification.hide";
	
	public static final String USER_NAME = "user.name";
	public static final String USER_TOKEN = "user.token";
	public static final String USER_GUID = "user.guid";
	public static final String USER_RANDOM_GUID = "user.random.guid";
	
	public static final String EULA_ACCEPTED = "eula.accepted";
	public static final String AUR_ACCEPTED = "aur.accepted";
	
	public static final String REFRESH_INTERVAL = "refresh.interval";

	/**
	 * Context-based constructor.
	 * 
	 * @param context
	 *            Context
	 */
	public Settings(Context context) {
		settings = context.getApplicationContext().getSharedPreferences(
				SETTINGS_FILE, 0);
	}

	/**
	 * Gets a stored String value.
	 * 
	 * @param name
	 *            the name of the settings
	 * @return String
	 */
	public String getString(String name) {
		return settings == null ? null : settings.getString(name, "");
	}

	/**
	 * Stores a String value.
	 * 
	 * @param name
	 *            the name of the settings
	 * 
	 * @param value
	 *            the actual value
	 */
	public void setString(String name, String value) {
		if (settings == null) {
			return;
		}
		SharedPreferences.Editor edit = settings.edit();
		edit.putString(name, value);
		edit.commit();
	}

	/**
	 * Gets a stored Boolean value.
	 * 
	 * @param name
	 *            the name of the settings
	 * @return boolean
	 */
	public boolean getBoolean(String name) {
		return settings == null ? false : settings.getBoolean(name, false);
	}

	/**
	 * Stores a Boolean value
	 * 
	 * @param name
	 *            the name of the settings
	 * 
	 * @param value
	 *            the actual value
	 */
	public void setBoolean(String name, boolean value) {
		if (settings == null) {
			return;
		}
		SharedPreferences.Editor edit = settings.edit();
		edit.putBoolean(name, value);
		edit.commit();
	}

	/**
	 * Gets a stored Long value.
	 * 
	 * @param name
	 *            the name of the settings
	 * @return long
	 */
	public long getLong(String name) {
		return settings == null ? 0 : settings.getLong(name, 0);
	}

	/**
	 * Stores a Long value
	 * 
	 * @param name
	 *            the name of the settings
	 * @param value
	 *            the actual value
	 */
	public void setLong(String name, long value) {
		if (settings == null) {
			return;
		}
		SharedPreferences.Editor edit = settings.edit();
		edit.putLong(name, value);
		edit.commit();
	}

	/**
	 * The shared preferences object for which this class is a wrapper.
	 */
	private SharedPreferences settings;
}

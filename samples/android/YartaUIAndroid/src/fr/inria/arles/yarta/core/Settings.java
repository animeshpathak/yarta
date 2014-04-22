package fr.inria.arles.yarta.core;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

	public final static String SETTINGS_FILE = "settings.prefs";

	public final static String USER_ID = "user.id";

	public Settings(Context context) {
		m_context = context.getApplicationContext();
		m_settings = m_context.getSharedPreferences(SETTINGS_FILE, 0);
	}

	public String getString(String name) {
		return m_settings == null ? null : m_settings.getString(name, "");
	}

	public void setString(String name, String value) {
		if (m_settings == null) {
			return;
		}
		SharedPreferences.Editor edit = m_settings.edit();
		edit.putString(name, value);
		edit.commit();
	}

	private Context m_context;
	private SharedPreferences m_settings;
}

package fr.inria.arles.giveaway.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/**
 * Re-usable settings component. 9/9/2013
 */
public class Settings implements OnSharedPreferenceChangeListener {

	public static final String SETTINGS_FILE = "app.settings";

	public interface Observer {
		public void onPreferenceChanged(String preference);
	}

	public Settings(Context context) {
		m_context = context.getApplicationContext();
		m_prefs = m_context.getSharedPreferences(SETTINGS_FILE, 0);
	}

	public boolean getBoolean(String name) {
		boolean defValue = false;
		if (m_mDefValues.containsKey(name))
			defValue = (Boolean) m_mDefValues.get(name);
		return m_prefs == null ? defValue : m_prefs.getBoolean(name, defValue);
	}

	public Long getLong(String name) {
		long defValue = 0;
		if (m_mDefValues.containsKey(name))
			defValue = (Long) m_mDefValues.get(name);
		return m_prefs == null ? defValue : m_prefs.getLong(name, defValue);
	}

	public int getInt(String name) {
		int defValue = 0;
		if (m_mDefValues.containsKey(name))
			defValue = (Integer) m_mDefValues.get(name);

		return m_prefs == null ? defValue : m_prefs.getInt(name, defValue);
	}

	public String getString(String name) {
		String defValue = "";
		if (m_mDefValues.containsKey("name"))
			defValue = (String) m_mDefValues.get(name);
		return m_prefs == null ? defValue : m_prefs.getString(name, defValue);
	}

	public void setBoolean(String name, boolean value) {
		if (m_prefs == null) {
			return;
		}
		SharedPreferences.Editor edit = m_prefs.edit();
		edit.putBoolean(name, value);
		edit.commit();
	}

	public void setLong(String name, Long value) {
		if (m_prefs == null) {
			return;
		}
		SharedPreferences.Editor edit = m_prefs.edit();
		edit.putLong(name, value);
		edit.commit();
	}

	public void setInt(String name, int value) {
		if (m_prefs == null) {
			return;
		}
		SharedPreferences.Editor edit = m_prefs.edit();
		edit.putInt(name, value);
		edit.commit();
	}

	public void setString(String name, String value) {
		if (m_prefs == null) {
			return;
		}
		SharedPreferences.Editor edit = m_prefs.edit();
		edit.putString(name, value);
		edit.commit();
	}

	public void addObserver(Observer observer) {
		if (m_lstObservers.size() == 0) {
			m_prefs.registerOnSharedPreferenceChangeListener(this);
		}

		m_lstObservers.add(observer);
	}

	public void removeObserver(Observer observer) {
		m_lstObservers.remove(observer);

		if (m_lstObservers.size() == 0) {
			m_prefs.unregisterOnSharedPreferenceChangeListener(this);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs,
			String preference) {
		notifyAllObservers(preference);
	}

	protected void notifyAllObservers(String preference) {
		for (Observer observer : m_lstObservers) {
			observer.onPreferenceChanged(preference);
		}
	}

	protected Map<String, Object> getDefaultValues() {
		Map<String, Object> defValues = new HashMap<String, Object>();
		return defValues;
	}

	private SharedPreferences m_prefs;
	private Context m_context;

	private Map<String, Object> m_mDefValues = getDefaultValues();
	private List<Observer> m_lstObservers = new ArrayList<Observer>();
}

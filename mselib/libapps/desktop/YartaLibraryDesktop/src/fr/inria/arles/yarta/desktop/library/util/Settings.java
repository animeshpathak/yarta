package fr.inria.arles.yarta.desktop.library.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class Settings {

	public static final String SyncType = "SyncType";
	public static final String Token = "Token";
	public static final String Secret = "Secret";
	public static final String UserName = "Username";
	public static final String LastSync = "LastSync";
	public static final String LastUser = "LastUser";

	private static final String settingsFile = System.getProperty("user.home")
			+ "/.yarta/res/settings.cfg";

	public Settings() {
	}

	public String getString(String key) {
		Properties props = getProperties();

		String result = props.getProperty(key);
		return result == null ? "" : result;
	}

	public void setString(String key, String value) {
		Properties props = getProperties();
		props.setProperty(key, value);
		setProperties(props);
	}

	public Long getLong(String key) {
		Properties props = getProperties();
		Long result = 0L;

		try {
			result = Long.parseLong(props.getProperty(key));
		} catch (Exception ex) {
		}
		return result;
	}

	public void setLong(String key, Long value) {
		Properties props = getProperties();
		props.setProperty(key, "" + value);
		setProperties(props);
	}

	private Properties getProperties() {
		Properties props = new Properties();
		synchronized (settingsFile) {
			try {
				FileInputStream in = new FileInputStream(settingsFile);
				props.load(in);
				in.close();
			} catch (Exception ex) {
			}
		}
		return props;
	}

	private void setProperties(Properties props) {
		synchronized (settingsFile) {
			try {
				FileOutputStream out = new FileOutputStream(settingsFile);
				props.store(out, "");
				out.close();
			} catch (Exception ex) {
			}
		}
	}
}

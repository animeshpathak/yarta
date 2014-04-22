package fr.inria.arles.yarta.desktop.library.plugins;

import java.util.UUID;

import fr.inria.arles.yarta.desktop.library.util.Settings;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;

public abstract class OauthPlugin {

	public static final String DEFAULT_REDIRECT_URL = "http://yarta.gforge.inria.fr/";

	protected Settings settings = new Settings();

	protected String sessionId = UUID.randomUUID().toString();

	public abstract String getName();

	public abstract String getAuthorizationURL();

	public boolean isLoggedIn() {
		if (settings.getString(Settings.Token + getName()).length() > 0
				&& settings.getString(Settings.Secret + getName()).length() > 0) {
			return true;
		}
		return false;
	}

	public void clearData() {
		settings.setString(Settings.Token + getName(), "");
		settings.setString(Settings.Secret + getName(), "");
		settings.setString(Settings.UserName + getName(), "");
		settings.setLong(Settings.LastSync + getName(), 0L);
	}

	public String getUserName() {
		return settings.getString(Settings.UserName + getName());
	}

	public long getLastSync() {
		try {
			return settings.getLong(Settings.LastSync + getName());
		} catch (Exception ex) {
			return 0;
		}
	}

	public String getSessionId() {
		return sessionId;
	}

	public void sync(StorageAccessManager sam) {
		if (!isLoggedIn() || sam == null) {
			return;
		}
		if (internalSync(sam)) {
			settings.setLong(Settings.LastSync + getName(),
					System.currentTimeMillis());
		}
	}

	public abstract boolean internalSync(StorageAccessManager sam);

	public abstract void authorize(String code);
}

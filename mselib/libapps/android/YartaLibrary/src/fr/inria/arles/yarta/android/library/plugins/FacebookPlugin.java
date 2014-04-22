package fr.inria.arles.yarta.android.library.plugins;

import android.net.Uri;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.User;
import facebook4j.auth.AccessToken;
import fr.inria.arles.yarta.R;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.yarta.resources.Person;

/**
 * The FacebookPlugin implementation. It uses the remote facebook APP.
 */
public class FacebookPlugin extends Plugin {

	private static final String FACEBOOK_ACCESS_TOKEN = "facebook.token";
	private static final String FACEBOOK_LAST_SYNC = "facebook.sync";
	private static final String FACEBOOK_USER_NAME = "facebook.username";

	public FacebookPlugin() {
		try {
			facebook.setOAuthAppId("163563177162961",
					"c14c06f6a0d5936eaa60dc3349b40306");
		} catch (Exception ex) {
			// c'est pas grave
		}
	}

	@Override
	public int getNetworkName() {
		return R.string.dashboard_facebook;
	}

	@Override
	public boolean isLoggedIn() {
		if (settings.getString(FACEBOOK_ACCESS_TOKEN).length() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public long getLastSync() {
		return settings.getLong(FACEBOOK_LAST_SYNC);
	}

	@Override
	public void setLastSync(long time) {
		settings.setLong(FACEBOOK_LAST_SYNC, time);
	}

	@Override
	public String getUserName() {
		return settings.getString(FACEBOOK_USER_NAME);
	}

	@Override
	public String getAuthorizationURL() {
		return facebook.getOAuthAuthorizationURL(DEFAULT_REDIRECT_URL);
	}

	@Override
	public boolean authorize(String url) {
		if (!url.startsWith(DEFAULT_REDIRECT_URL)) {
			return false;
		}

		Uri uri = Uri.parse(url);

		if (uri.getQueryParameter("code") == null) {
			return false;
		}

		try {
			accessToken = facebook.getOAuthAccessToken(uri
					.getQueryParameter("code"));
			facebook.setOAuthAccessToken(accessToken);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (accessToken != null) {
			settings.setString(FACEBOOK_ACCESS_TOKEN, accessToken.getToken());
			try {
				settings.setString(FACEBOOK_USER_NAME, facebook.getName());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return true;
	}

	@Override
	public void clearUserData() {
		log("clearUserData");
		settings.setLong(FACEBOOK_LAST_SYNC, 0);
		settings.setString(FACEBOOK_USER_NAME, "");
		settings.setString(FACEBOOK_ACCESS_TOKEN, "");
		facebook.setOAuthAccessToken(null);
		accessToken = null;
	}

	@Override
	public boolean internalPerformSync(StorageAccessManager sam) {
		log("internalPerformSync");

		String storedToken = settings.getString(FACEBOOK_ACCESS_TOKEN);
		try {
			if (accessToken == null) {
				accessToken = new AccessToken(storedToken);
				facebook.setOAuthAccessToken(accessToken);
			}

			Person person = sam.getMe();
			User me = facebook.getMe();

			String email = me.getEmail();
			if (email != null) {
				person.setEmail(email);
			}

			String firstName = me.getFirstName();
			if (firstName != null) {
				person.setFirstName(me.getFirstName());
			}

			String lastName = me.getLastName();
			if (lastName != null) {
				person.setLastName(me.getLastName());
			}

			String name = me.getName();
			if (name != null) {
				person.setName(name);
			}
			return true;
		} catch (FacebookException ex) {
			log("FacebookException: %s", ex.getMessage());
		} catch (KBException ex) {
			log("KBException: %s", ex.getMessage());
		}
		return false;
	}

	private AccessToken accessToken;
	private static Facebook facebook = FacebookFactory.getSingleton();
}

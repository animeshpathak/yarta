package fr.inria.arles.yarta.desktop.library.plugins;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.User;
import facebook4j.auth.AccessToken;
import fr.inria.arles.yarta.desktop.library.util.Settings;
import fr.inria.arles.yarta.desktop.library.util.Strings;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.yarta.resources.Person;

public class FacebookPlugin extends OauthPlugin {

	public FacebookPlugin() {
		try {
			facebook.setOAuthAppId("163563177162961",
					"c14c06f6a0d5936eaa60dc3349b40306");
		} catch (Exception ex) {
			// c'est pas grave
		}
	}

	@Override
	public String getName() {
		return Strings.PluginFacebook;
	}

	@Override
	public String getAuthorizationURL() {
		return facebook.getOAuthAuthorizationURL(DEFAULT_REDIRECT_URL + "?sid="
				+ sessionId);
	}

	@Override
	public void authorize(String code) {
		try {
			accessToken = facebook.getOAuthAccessToken(code);
			facebook.setOAuthAccessToken(accessToken);
		} catch (Exception ex) {
		}

		if (accessToken != null) {
			try {
				settings.setString(Settings.Token + getName(),
						accessToken.getToken());
				settings.setString(Settings.Secret + getName(), "n/a");
				settings.setString(Settings.UserName + getName(),
						facebook.getName());
			} catch (Exception ex) {
			}
		}
	}

	@Override
	public boolean internalSync(StorageAccessManager sam) {
		String storedToken = settings.getString(Settings.Token + getName());
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
			ex.printStackTrace();
		} catch (KBException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private AccessToken accessToken;
	private static Facebook facebook = FacebookFactory.getSingleton();
}

package fr.inria.arles.yarta.desktop.library.plugins;

import fr.inria.arles.yarta.desktop.library.util.Settings;
import fr.inria.arles.yarta.desktop.library.util.Strings;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.yarta.resources.Person;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterPlugin extends OauthPlugin {

	public TwitterPlugin() {
		try {
			twitter.setOAuthConsumer("kUsyNh5UEWk1y8LAfsA",
					"ObDxZbp0jLrO5h3H7j7rqSD1gsmVljt4z8xgWVjik");
		} catch (Exception ex) {
			// c'est pas grave
		}
	}

	@Override
	public String getName() {
		return Strings.PluginTwitter;
	}

	@Override
	public String getAuthorizationURL() {
		try {
			requestToken = twitter.getOAuthRequestToken(DEFAULT_REDIRECT_URL
					+ "?sid=" + sessionId);
			return requestToken.getAuthorizationURL();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public void authorize(String code) {
		try {
			accessToken = twitter.getOAuthAccessToken(requestToken, code);
			twitter.setOAuthAccessToken(accessToken);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (accessToken != null) {
			settings.setString(Settings.Token + getName(),
					accessToken.getToken());
			settings.setString(Settings.Secret + getName(),
					accessToken.getTokenSecret());
			settings.setString(Settings.UserName + getName(),
					accessToken.getScreenName());
		}
	}

	@Override
	public boolean internalSync(StorageAccessManager sam) {
		String storedToken = settings.getString(Settings.Token + getName());
		String storedSecret = settings.getString(Settings.Secret + getName());

		if (accessToken == null) {
			accessToken = new AccessToken(storedToken, storedSecret);
			twitter.setOAuthAccessToken(accessToken);
		}

		try {
			Person me = sam.getMe();

			String screenName = twitter.getScreenName();

			if (screenName != null) {
				me.setName(screenName);
			}
			User user = twitter.showUser(screenName);
			String url = user.getURL();

			if (url != null) {
				me.setHomepage(url);
			}
			return true;
		} catch (TwitterException ex) {
			ex.printStackTrace();
		} catch (KBException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private AccessToken accessToken;
	private RequestToken requestToken;

	private Twitter twitter = TwitterFactory.getSingleton();
}

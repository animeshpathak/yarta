package fr.inria.arles.yarta.android.library.plugins;

import android.net.Uri;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import fr.inria.arles.yarta.R;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.yarta.resources.Person;

public class TwitterPlugin extends Plugin {

	private static final String TWITTER_TOKEN = "twitter.token";
	private static final String TWITTER_SECRET = "twitter.secret";
	private static final String TWITTER_LAST_SYNC = "twitter.sync";
	private static final String TWITTER_USER_NAME = "twitter.username";

	public TwitterPlugin() {
		try {
			twitter.setOAuthConsumer("kUsyNh5UEWk1y8LAfsA",
					"ObDxZbp0jLrO5h3H7j7rqSD1gsmVljt4z8xgWVjik");
		} catch (Exception ex) {
			// c'est pas grave
		}
	}

	@Override
	public int getNetworkName() {
		return R.string.dashboard_twitter;
	}

	@Override
	public boolean isLoggedIn() {
		if (settings.getString(TWITTER_TOKEN).length() > 0
				&& settings.getString(TWITTER_SECRET).length() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public long getLastSync() {
		return settings.getLong(TWITTER_LAST_SYNC);
	}

	@Override
	public void setLastSync(long time) {
		settings.setLong(TWITTER_LAST_SYNC, time);
	}

	@Override
	public String getUserName() {
		return settings.getString(TWITTER_USER_NAME);
	}

	@Override
	public String getAuthorizationURL() {
		try {
			requestToken = twitter.getOAuthRequestToken();
			return requestToken.getAuthorizationURL();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean authorize(String url) {
		if (!url.startsWith(DEFAULT_REDIRECT_URL)) {
			return false;
		}

		Uri uri = Uri.parse(url);

		if (uri.getQueryParameter("oauth_verifier") == null) {
			return false;
		}

		try {
			accessToken = twitter.getOAuthAccessToken(requestToken,
					uri.getQueryParameter("oauth_verifier"));
			twitter.setOAuthAccessToken(accessToken);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (accessToken != null) {
			settings.setString(TWITTER_TOKEN, accessToken.getToken());
			settings.setString(TWITTER_SECRET, accessToken.getTokenSecret());
			settings.setString(TWITTER_USER_NAME, accessToken.getScreenName());
		}
		return true;
	}

	@Override
	public void clearUserData() {
		settings.setLong(TWITTER_LAST_SYNC, 0);
		settings.setString(TWITTER_TOKEN, "");
		settings.setString(TWITTER_SECRET, "");
		settings.setString(TWITTER_USER_NAME, "");
		twitter.setOAuthAccessToken(null);
		accessToken = null;
		requestToken = null;
	}

	@Override
	public boolean internalPerformSync(StorageAccessManager sam) {
		String storedToken = settings.getString(TWITTER_TOKEN);
		String storedSecret = settings.getString(TWITTER_SECRET);

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

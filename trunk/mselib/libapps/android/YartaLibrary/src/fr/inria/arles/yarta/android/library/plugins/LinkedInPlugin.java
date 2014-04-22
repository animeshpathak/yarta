package fr.inria.arles.yarta.android.library.plugins;

import android.net.Uri;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Person;

import fr.inria.arles.yarta.R;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;

public class LinkedInPlugin extends Plugin {

	private static final String LINKEDIN_TOKEN = "linkedin.token";
	private static final String LINKEDIN_SECRET = "linkedin.secret";
	private static final String LINKEDIN_LAST_SYNC = "linkedin.sync";
	private static final String LINKEDIN_USER_NAME = "linkedin.username";

	@Override
	public int getNetworkName() {
		return R.string.dashboard_linkedin;
	}

	@Override
	public boolean isLoggedIn() {
		if (settings.getString(LINKEDIN_TOKEN).length() > 0
				&& settings.getString(LINKEDIN_SECRET).length() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public long getLastSync() {
		return settings.getLong(LINKEDIN_LAST_SYNC);
	}

	@Override
	public void setLastSync(long time) {
		settings.setLong(LINKEDIN_LAST_SYNC, time);
	}

	@Override
	public String getUserName() {
		return settings.getString(LINKEDIN_USER_NAME);
	}

	@Override
	public String getAuthorizationURL() {
		if (requestToken == null) {
			requestToken = oauthService
					.getOAuthRequestToken(DEFAULT_REDIRECT_URL);
		}
		return requestToken.getAuthorizationUrl();
	}

	@Override
	public boolean authorize(String url) {
		if (!url.startsWith(DEFAULT_REDIRECT_URL)) {
			return false;
		}

		Uri uri = Uri.parse(url);

		try {
			accessToken = oauthService.getOAuthAccessToken(requestToken,
					uri.getQueryParameter("oauth_verifier"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (accessToken != null) {
			client.setAccessToken(accessToken);

			settings.setString(LINKEDIN_TOKEN, accessToken.getToken());
			settings.setString(LINKEDIN_SECRET, accessToken.getTokenSecret());

			Person p = client.getProfileForCurrentUser();
			settings.setString(LINKEDIN_USER_NAME,
					p.getFirstName() + " " + p.getLastName());
		}
		return true;
	}

	@Override
	public void clearUserData() {
		settings.setLong(LINKEDIN_LAST_SYNC, 0);
		settings.setString(LINKEDIN_TOKEN, "");
		settings.setString(LINKEDIN_SECRET, "");
		settings.setString(LINKEDIN_USER_NAME, "");
		client.setAccessToken(null);
		accessToken = null;
		requestToken = null;
	}

	@Override
	public boolean internalPerformSync(StorageAccessManager sam) {
		String storedToken = settings.getString(LINKEDIN_TOKEN);
		String storedSecret = settings.getString(LINKEDIN_SECRET);

		if (accessToken == null) {
			accessToken = new LinkedInAccessToken(storedToken, storedSecret);
			client.setAccessToken(accessToken);
		}

		try {
			fr.inria.arles.yarta.resources.Person me = sam.getMe();
			Person person = client.getProfileForCurrentUser();

			String firstName = person.getFirstName();
			if (firstName != null) {
				me.setFirstName(firstName);
			}

			String lastName = person.getLastName();
			if (lastName != null) {
				me.setLastName(lastName);
			}

			log("First Name: %s", person.getFirstName());
			log("Last Name: %s", person.getLastName());
			log("Headline: %s", person.getHeadline());

			return true;
		} catch (KBException ex) {
			log("Error in sync: %s", ex.getMessage());
		}
		return false;
	}

	private LinkedInRequestToken requestToken;
	private LinkedInAccessToken accessToken;

	private final LinkedInOAuthService oauthService = LinkedInOAuthServiceFactory
			.getInstance().createLinkedInOAuthService("34e9yep40rf4",
					"eMtII2zwfnpJyTLi");
	private static final LinkedInApiClientFactory factory = LinkedInApiClientFactory
			.newInstance("34e9yep40rf4", "eMtII2zwfnpJyTLi");
	private LinkedInApiClient client = factory.createLinkedInApiClient(
			"9e8b2b47-accb-4a68-a420-1a4a12f1d069",
			"7cd4115c-f979-47dc-a006-8cb0d33cfa7d");
}

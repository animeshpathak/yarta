package fr.inria.arles.yarta.desktop.library.plugins;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Person;

import fr.inria.arles.yarta.desktop.library.util.Settings;
import fr.inria.arles.yarta.desktop.library.util.Strings;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;

public class LinkedInPlugin extends OauthPlugin {

	public LinkedInPlugin() {
	}

	@Override
	public String getName() {
		return Strings.PluginLinkedIn;
	}

	@Override
	public String getAuthorizationURL() {
		if (requestToken == null) {
			requestToken = oauthService
					.getOAuthRequestToken(DEFAULT_REDIRECT_URL + "?sid="
							+ sessionId);
		}
		return requestToken.getAuthorizationUrl();
	}

	@Override
	public void authorize(String code) {
		try {
			accessToken = oauthService.getOAuthAccessToken(requestToken, code);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (accessToken != null) {
			client.setAccessToken(accessToken);

			Person p = client.getProfileForCurrentUser();

			settings.setString(Settings.Token + getName(),
					accessToken.getToken());
			settings.setString(Settings.Secret + getName(),
					accessToken.getTokenSecret());
			settings.setString(Settings.UserName + getName(), p.getFirstName()
					+ p.getLastName());
		}
	}

	@Override
	public boolean internalSync(StorageAccessManager sam) {
		String storedToken = settings.getString(Settings.Token + getName());
		String storedSecret = settings.getString(Settings.Secret + getName());

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

			return true;
		} catch (KBException ex) {
			ex.printStackTrace();
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

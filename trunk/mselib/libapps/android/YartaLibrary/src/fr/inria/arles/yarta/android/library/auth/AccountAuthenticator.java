package fr.inria.arles.yarta.android.library.auth;

import fr.inria.arles.yarta.logging.YLoggerFactory;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class AccountAuthenticator extends AbstractAccountAuthenticator {

	private Context context;

	private void log(String format, Object... args) {
		YLoggerFactory.getLogger().d("AccountAuthenticator",
				String.format(format, args));
	}

	public AccountAuthenticator(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options)
			throws NetworkErrorException {
		log("addAccount(%s)", accountType);
		final Intent intent = new Intent(context, AuthenticatorActivity.class);
		intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
		intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
		intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		log("getAuthToken(%s, %s)", account.name, account.type);

		// extract the authentication token
		final AccountManager am = AccountManager.get(context);

		String authToken = am.peekAuthToken(account, authTokenType);

		// If we get an authToken - we return it
		if (!TextUtils.isEmpty(authToken)) {
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
			result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
			result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
			return result;
		}

		// If we get here, then we couldn't access the user's password - so we
		// need to re-prompt them for their credentials. We do that by creating
		// an intent to display our AuthenticatorActivity.
		final Intent intent = new Intent(context, AuthenticatorActivity.class);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);
		intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
		intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
			Account account, Bundle options) throws NetworkErrorException {
		log("confirmCredentials(%s, %s)", account.name, account.type);
		return null;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response,
			String accountType) {
		log("editProperties(%s)", accountType);
		return null;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		log("getAuthTokenLabel(%s)", authTokenType);
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) throws NetworkErrorException {
		log("hasFeatures(%s, %s)", account.name, account.type);
		return null;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		log("updateCredentials(%s, %s)", account.name, account.type);
		return null;
	}
}

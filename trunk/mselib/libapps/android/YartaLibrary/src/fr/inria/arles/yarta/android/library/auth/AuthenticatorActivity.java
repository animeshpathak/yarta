package fr.inria.arles.yarta.android.library.auth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.util.ProgressDialog;
import fr.inria.arles.yarta.android.library.util.Settings;
import fr.inria.arles.yarta.android.library.web.WebClient;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AuthenticatorActivity extends AccountAuthenticatorActivity
		implements TextView.OnEditorActionListener {

	private Settings settings;
	private AccountManager accountManager;
	private WebClient client = WebClient.getInstance();

	public static final String ARG_ACCOUNT_TYPE = "AccountType";
	public static final String ARG_AUTH_TYPE = "AuthType";
	public static final String ARG_IS_ADDING_NEW_ACCOUNT = "IsAddingNewAccount";

	public static final String ACCOUNT_TYPE = "inria.fr";
	public static final String ACCOUNT_TOKEN = "AccessToken";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		accountManager = AccountManager.get(this);
		settings = new Settings(this);
		String username = settings.getString(Settings.USER_NAME);

		setCtrlText(R.id.username, username.replace("@inria.fr", ""));

		EditText textPassword = (EditText) findViewById(R.id.password);
		textPassword.setOnEditorActionListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		final String guid = settings.getString(Settings.USER_RANDOM_GUID);
		if (guid.length() > 0) {
			new AsyncTask<Void, Void, Intent>() {

				private boolean ok;
				private ProgressDialog dialog;

				protected void onPreExecute() {
					dialog = ProgressDialog.show(AuthenticatorActivity.this,
							getString(R.string.login_wait_logging));
				}

				@Override
				protected Intent doInBackground(Void... params) {
					try {
						String url = WebClient.ElggCAS + "read&guid=" + guid;
						String tokenAndUser = readURL(url).trim();

						if (tokenAndUser.length() > 3) {
							String token = tokenAndUser.substring(0,
									tokenAndUser.indexOf(','));
							String username = tokenAndUser
									.substring(tokenAndUser.indexOf(',') + 1);
							client.setUsername(username);
							client.setUserToken(token);
							client.setUserGuid(client.getUserGuid(username));

							settings.setString(Settings.USER_NAME,
									client.getUsername());
							settings.setString(Settings.USER_GUID,
									client.getUserGuid());
							settings.setString(Settings.USER_TOKEN,
									client.getUserToken());

							settings.setString(Settings.USER_RANDOM_GUID, "");

							ok = true;

							readURL(url.replace("read", "remove"));
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					return null;
				}

				protected void onPostExecute(Intent result) {
					dialog.dismiss();
					if (ok) {
						final Intent res = new Intent();
						res.putExtra(AccountManager.KEY_ACCOUNT_NAME,
								client.getUsername());
						res.putExtra(AccountManager.KEY_ACCOUNT_TYPE,
								ACCOUNT_TYPE);
						res.putExtra(AccountManager.KEY_AUTHTOKEN,
								client.getUserToken());

						finishLogin(res);
					}
				}
			}.execute();
		}
	}

	@Override
	public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_GO) {
			onClickLogin(view);
		}
		return false;
	}

	public void onClickLoginCAS(View view) {
		String guid = UUID.randomUUID().toString();
		settings.setString(Settings.USER_RANDOM_GUID, guid);

		String url = WebClient.ElggCAS + "guid=" + guid;
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}

	public void onClickLogin(View view) {
		final String username = getCtrlText(R.id.username);
		final String password = getCtrlText(R.id.password);

		if (username.length() == 0 || password.length() == 0) {
			Toast.makeText(this, R.string.login_empty_fields, Toast.LENGTH_LONG)
					.show();
			return;
		}

		new AsyncTask<Void, Void, Intent>() {

			private int result = -1;
			private ProgressDialog dialog;

			protected void onPreExecute() {
				dialog = ProgressDialog.show(AuthenticatorActivity.this,
						getString(R.string.login_wait_logging));
			}

			@Override
			protected Intent doInBackground(Void... params) {
				result = client.authenticate(username, password);
				return null;
			}

			@Override
			protected void onPostExecute(Intent intent) {
				dialog.dismiss();
				if (result == WebClient.RESULT_OK) {
					settings.setString(Settings.USER_NAME, client.getUsername());
					settings.setString(Settings.USER_GUID, client.getUserGuid());
					settings.setString(Settings.USER_TOKEN,
							client.getUserToken());

					final Intent res = new Intent();
					res.putExtra(AccountManager.KEY_ACCOUNT_NAME,
							client.getUsername());
					res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
					res.putExtra(AccountManager.KEY_AUTHTOKEN,
							client.getUserToken());

					finishLogin(res);
				} else {
					Toast.makeText(getApplicationContext(),
							client.getLastError(), Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
	}

	private void finishLogin(Intent intent) {
		String accountName = intent
				.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
		final Account account = new Account(accountName,
				intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
		if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
			accountManager.addAccountExplicitly(account, null, null);
			accountManager.setAuthToken(account, ACCOUNT_TOKEN,
					client.getUserToken());
		} else {
			accountManager.setPassword(account, null);
			accountManager.setAuthToken(account, ACCOUNT_TOKEN,
					client.getUserToken());
		}
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}

	protected void setCtrlText(int resId, String text) {
		TextView txt = (TextView) findViewById(resId);
		if (txt != null) {
			txt.setText(text);
		}
	}

	protected String getCtrlText(int resId) {
		TextView txt = (TextView) findViewById(resId);
		if (txt != null) {
			return txt.getText().toString();
		}
		return null;
	}

	private String readURL(String url) {
		String content = "";
		try {
			URL oracle = new URL(url);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					oracle.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				content += inputLine;
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return content;
	}
}

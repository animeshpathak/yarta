package fr.inria.arles.yarta.android.library.auth;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.util.ProgressDialog;
import fr.inria.arles.yarta.android.library.util.Settings;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
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

		setCtrlText(R.id.username, username);

		EditText textPassword = (EditText) findViewById(R.id.username);
		textPassword.setOnEditorActionListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_GO) {
			onClickLogin(view);
		}
		return false;
	}

	public void onClickLoginCAS(View view) {
	}

	public void onClickLogin(View view) {
		final String username = getCtrlText(R.id.username);

		if (username.length() == 0) {
			Toast.makeText(this, R.string.login_empty_fields, Toast.LENGTH_LONG)
					.show();
			return;
		}

		new AsyncTask<Void, Void, Intent>() {

			private ProgressDialog dialog;

			protected void onPreExecute() {
				dialog = ProgressDialog.show(AuthenticatorActivity.this,
						getString(R.string.login_wait_logging));
			}

			@Override
			protected Intent doInBackground(Void... params) {
				return null;
			}

			@Override
			protected void onPostExecute(Intent intent) {
				dialog.dismiss();
				settings.setString(Settings.USER_NAME, username);
				settings.setString(Settings.USER_GUID, username);
				settings.setString(Settings.USER_TOKEN, username);

				final Intent res = new Intent();
				res.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
				res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
				res.putExtra(AccountManager.KEY_AUTHTOKEN, username);

				finishLogin(res);
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
			accountManager.setAuthToken(account, ACCOUNT_TOKEN, accountName);
		} else {
			accountManager.setPassword(account, null);
			accountManager.setAuthToken(account, ACCOUNT_TOKEN, accountName);
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
}

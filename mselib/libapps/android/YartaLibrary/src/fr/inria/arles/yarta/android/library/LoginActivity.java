package fr.inria.arles.yarta.android.library;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.android.library.util.Settings;
import fr.inria.arles.yarta.android.library.web.WebClient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements
		TextView.OnEditorActionListener {

	public static final String NoStart = "NoStart";

	private boolean nostart = false;
	private WebClient client = WebClient.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		String username = settings.getString(Settings.USER_NAME);
		String usertoken = settings.getString(Settings.USER_TOKEN);
		String userguid = settings.getString(Settings.USER_GUID);

		setCtrlText(R.id.username, username);

		if (getIntent().hasExtra(NoStart)) {
			nostart = true;
		} else if (usertoken.length() > 0) {
			// TODO: check if logged in;
			client.setUsername(username);
			client.setUserToken(usertoken);
			client.setUserGuid(userguid);
			proceedToMain();
		}

		EditText textPassword = (EditText) findViewById(R.id.password);
		textPassword.setOnEditorActionListener(this);

		trackUI("LoginView");
	}

	@Override
	public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_GO) {
			onClickLogin(view);
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		final String guid = settings.getString(Settings.USER_RANDOM_GUID);
		if (guid.length() > 0) {
			executeWithMessage(R.string.login_wait_logging, new Job() {

				private boolean ok = false;;

				@Override
				public void doWork() {
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

							// TODO: remove it afterwards
							settings.setString(Settings.USER_TOKEN,
									client.getUserToken());

							settings.setString(Settings.USER_RANDOM_GUID, "");

							ok = true;

							readURL(url.replace("read", "remove"));
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				@Override
				public void doUIAfter() {
					if (ok) {
						if (nostart) {
							finish();
						} else {
							proceedToMain();
						}
					}
				}
			});
		}
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

	public void onClickLoginCAS(View view) {
		trackUI("LoginCas");
		String guid = UUID.randomUUID().toString();
		settings.setString(Settings.USER_RANDOM_GUID, guid);

		String url = WebClient.ElggCAS + "guid=" + guid;
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}

	public void onClickLogin(View view) {
		trackUI("LoginNormal");
		final String username = getCtrlText(R.id.username);
		final String password = getCtrlText(R.id.password);

		if (username.length() == 0 || password.length() == 0) {
			Toast.makeText(this, R.string.login_empty_fields, Toast.LENGTH_LONG)
					.show();
			return;
		}

		execute(new Job() {
			private int result = -1;

			@Override
			public void doWork() {
				result = client.authenticate(username, password);
			}

			@Override
			public void doUIAfter() {
				if (result == WebClient.RESULT_OK) {
					settings.setString(Settings.USER_NAME, client.getUsername());
					settings.setString(Settings.USER_GUID, client.getUserGuid());

					// TODO: remove it afterwards
					settings.setString(Settings.USER_TOKEN,
							client.getUserToken());

					if (nostart) {
						finish();
					} else {
						proceedToMain();
					}
				} else {
					Toast.makeText(LoginActivity.this, client.getLastError(),
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	public void finish() {
		String userId = settings.getString(Settings.USER_NAME);

		YartaApp app = (YartaApp) getApplication();
		app.onLogin(userId.length() == 0 ? null : userId);

		super.finish();
	}

	private void proceedToMain() {
		finish();
	}
}
package fr.inria.arles.yarta.android.library;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import fr.inria.arles.yarta.R;
import fr.inria.arles.yarta.android.library.util.CASUtil;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.android.library.util.Settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {

	private Settings settings;

	public static final String Logout = "Logout";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		settings = new Settings(this);
		if (getIntent().hasExtra(Logout)) {
			// perform logout
			uninitMSE();
			settings.setString(Settings.USER_ID, "");
			settings.setBoolean(Settings.USER_VERIFIED, false);
			finish();
		} else if (settings.getBoolean(Settings.USER_VERIFIED)) {
			finish();
		} else {
			setTheme(R.style.AppThemeNoTitlebar);
			setContentView(R.layout.activity_login);
		}
	}

	public void onClickLogin(View view) {

		final String username = getCtrlText(R.id.username);
		final String password = getCtrlText(R.id.password);

		executeWithMessage(R.string.app_name, new Job() {

			private boolean success;

			public void doWork() {
				success = util.performLogin(username, password);
			}

			@Override
			public void doUIAfter() {
				if (success) {
					onUserId(username + "@inria.fr");
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.login_inria_login_error, Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}

	public void onClickCASLogin(View view) {
		String guid = UUID.randomUUID().toString();
		settings.setString(Settings.USER_SID, guid);

		Intent browserIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("http://arles.rocq.inria.fr/yarta/?r=" + guid));
		startActivity(browserIntent);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!settings.getBoolean(Settings.USER_VERIFIED))
			execute(new Job() {

				private String userId;

				@Override
				public void doWork() {
					String userSID = settings.getString(Settings.USER_SID);
					String yartaURL = "http://arles.rocq.inria.fr/yarta/?r="
							+ userSID + "&get";

					String content = readURL(yartaURL);

					userId = content.trim();
				}

				@Override
				public void doUIAfter() {
					if (userId.length() > 0) {
						onUserId(userId + "@inria.fr");
					}
				}
			});
	}

	private void onUserId(String userId) {
		settings.setString(Settings.USER_ID, userId);
		settings.setBoolean(Settings.USER_VERIFIED, true);
		finish();
	}

	@Override
	public void finish() {
		String userId = settings.getString(Settings.USER_ID);

		YartaApp app = (YartaApp) getApplication();
		app.onLogin(userId.length() == 0 ? null : userId);

		super.finish();
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

	private CASUtil util = new CASUtil();
}

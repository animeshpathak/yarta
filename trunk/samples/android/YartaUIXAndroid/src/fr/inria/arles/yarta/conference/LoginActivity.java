package fr.inria.arles.yarta.conference;

import fr.inria.arles.yarta.conference.AsyncRunner.Job;
import fr.inria.arles.yarta.core.Settings;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		String userId = getString(R.string.app_user);
		String cachedUserId = settings.getString(Settings.USER_ID);
		setCtrlText(R.id.userName,
				cachedUserId == null || cachedUserId.length() == 0 ? userId
						: cachedUserId);
	}

	public void onLoginButtonClicked(View view) {
		final String userName = getCtrlText(R.id.userName);
		final String userPass = getCtrlText(R.id.userPassword);

		if (userName.length() == 0) {
			Toast.makeText(this, R.string.login_nouser, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		if (userPass.length() == 0) {
			settings.setString(Settings.USER_ID, userName);
			finish();
			startActivity(new Intent(this, DashboardActivity.class));
		} else {
			runner.run(new Job() {

				private boolean success;

				@Override
				public void doWork() {
				}

				@Override
				public void doUI() {
					if (success) {
						finish();
						startActivity(new Intent(LoginActivity.this,
								DashboardActivity.class));
					} else {

					}
				}
			});
		}
	}
}

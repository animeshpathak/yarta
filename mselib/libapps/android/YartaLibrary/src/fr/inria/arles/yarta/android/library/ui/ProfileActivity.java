package fr.inria.arles.yarta.android.library.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;

public class ProfileActivity extends BaseActivity {

	public static final String UserGuid = "UserGuid";
	public static final String UserName = "UserName";

	private ProfileFragment fragment;
	private String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		trackUI("ProfileView");
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshUI(null);
	}

	@Override
	public void refreshUI(String notification) {
		if (getIntent().hasExtra(UserName)) {
			username = getIntent().getStringExtra(UserName);
			attachFragment(username);
		} else {
			final String userGuid = getIntent().getStringExtra(UserGuid);

			runner.runBackground(new Job() {

				@Override
				public void doWork() {
					username = client.getUsername(userGuid);
				}

				@Override
				public void doUIAfter() {
					if (username != null) {
						attachFragment(username);
					}
				}
			});
		}
	}

	protected void attachFragment(String username) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		if (fragment == null) {
			fragment = new ProfileFragment();
			fragment.setRunner(runner);
			fragment.setSAM(getSAM());
			fragment.setContentClient(contentClient);
			fragment.setUsername(username);

			ft.replace(R.id.content_frame, fragment);
			ft.commit();
		} else {
			fragment.setSAM(getSAM());
			fragment.setContentClient(contentClient);
			fragment.refreshUI(null);
		}
	}
}

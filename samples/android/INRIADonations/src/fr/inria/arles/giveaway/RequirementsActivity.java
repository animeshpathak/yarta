package fr.inria.arles.giveaway;

import fr.inria.arles.giveaway.DonationApp.Observer;
import fr.inria.arles.yarta.android.library.DependencyCheck;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class RequirementsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (DependencyCheck.isYartaInstalled(this)) {
			proceedToApplication();
		} else {
			setContentView(R.layout.activity_requirements);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (DependencyCheck.isYartaInstalled(this)) {
			proceedToApplication();
		}
	}

	public void onClickProceed(View view) {
		String url = "market://details?id=fr.inria.arles.iris";
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	}

	private void proceedToApplication() {
		DonationApp app = (DonationApp) getApplication();
		app.initMSE(new Observer() {

			@Override
			public void updateInfo() {
				// does nothing;
			}
		});
		// finish since handleKBReady will be called
		finish();
	}
}

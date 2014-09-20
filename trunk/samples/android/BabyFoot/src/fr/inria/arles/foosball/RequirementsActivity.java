package fr.inria.arles.foosball;

import com.actionbarsherlock.app.SherlockActivity;

import fr.inria.arles.yarta.android.library.DependencyCheck;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class RequirementsActivity extends SherlockActivity {

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
		String url = "https://play.google.com/apps/testing/fr.inria.arles.iris";
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	}

	private void proceedToApplication() {
		PlayersApp app = (PlayersApp) getApplication();
		app.initMSE(null);
		// finish since handleKBReady will be called
		finish();
	}
}

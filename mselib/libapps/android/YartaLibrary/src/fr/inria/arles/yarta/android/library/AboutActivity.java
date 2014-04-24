package fr.inria.arles.yarta.android.library;

import fr.inria.arles.iris.R;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public void onVisitWebsiteClicked(View view) {
		trackUI("AboutActivity.ViewProjectWebsite");

		startActivity(new Intent(Intent.ACTION_VIEW,
				Uri.parse(getString(R.string.app_website))));
	}
}

package fr.inria.arles.sophia;

import fr.inria.arles.rocq.R;
import fr.inria.arles.yarta.resources.Group;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				lunchGroup();
			}
		}, 1000);
	}

	private void lunchGroup() {
		try {
			Intent intent = new Intent("Yarta.Group");
			intent.putExtra("GroupGuid",
					String.format("%s_%d", Group.typeURI, 31781));
			intent.putExtra("Standalone", true);
			startActivity(intent);
			finish();
		} catch (ActivityNotFoundException ex) {
			findViewById(R.id.install).setVisibility(View.VISIBLE);
		}
	}

	public void onClickInstall(View view) {
		try {
			final String appPackageName = "fr.inria.arles.iris";
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=" + appPackageName)));
			finish();
		} catch (Exception ex) {
			Toast.makeText(this, R.string.app_market_required,
					Toast.LENGTH_LONG).show();
		}
	}

	private Handler handler = new Handler();
}

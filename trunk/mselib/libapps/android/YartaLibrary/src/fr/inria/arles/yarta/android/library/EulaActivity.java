package fr.inria.arles.yarta.android.library;

import com.actionbarsherlock.app.SherlockActivity;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.util.Settings;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;

public class EulaActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		settings = new Settings(this);

		if (settings.getBoolean(Settings.EULA_ACCEPTED)) {
			proceedToApplication();
		} else {
			setContentView(R.layout.activity_eula);

			WebView w = (WebView) findViewById(R.id.eula_webView);
			w.getSettings().setSupportZoom(false);
			w.loadUrl("file:///android_asset/eula_en.html");
		}
	}

	public void onEulaAccept(View view) {
		settings.setBoolean(Settings.EULA_ACCEPTED, true);
		settings.setBoolean(Settings.AUR_ACCEPTED,
				getCheckbox(R.id.anonimousCheck));
		proceedToApplication();
	}

	public void onEulaRefuse(View view) {
		finish();
	}

	private Settings settings;

	protected boolean getCheckbox(int id) {
		CheckBox check = (CheckBox) findViewById(id);
		if (check != null) {
			return check.isChecked();
		}
		return false;
	}

	protected void proceedToApplication() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);

		// finish since handleKBReady will be called
		finish();
	}
}

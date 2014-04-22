package fr.inria.arles.yarta.conference;

import fr.inria.arles.yarta.core.Settings;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = new Settings(this);
		runner = new AsyncRunner(this);
	}

	public String getCtrlText(int ctrlId) {
		TextView txt = (TextView) findViewById(ctrlId);
		return txt.getText().toString();
	}

	public void setCtrlText(int ctrlId, String text) {
		TextView txt = (TextView) findViewById(ctrlId);
		txt.setText(text);
	}

	protected AsyncRunner runner;
	protected Settings settings;
}

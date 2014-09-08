package fr.inria.arles.callouts;

import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.msemanagement.MSEManager;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class BaseActivity extends ActionBarActivity implements
		CalloutsApp.Observer {

	protected CalloutsApp app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (CalloutsApp) getApplication();
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.addObserver(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		app.removeObserver(this);
		super.onPause();
	}

	// called on the ui thread
	protected void refreshUI() {
	}

	protected StorageAccessManager getSAM() {
		return app.getSam();
	}

	protected CommunicationManager getCOMM() {
		return app.getComm();
	}

	protected MSEManager getMSE() {
		return app.getMse();
	}

	@Override
	public void onRefreshUI() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				refreshUI();
			}
		});
	}

	@Override
	public void onLogout() {
		finish();
	}

	protected void setText(int id, String text) {
		TextView txt = (TextView) findViewById(id);
		txt.setText(text);
	}

	protected String getCtrlText(int id) {
		TextView txt = (TextView) findViewById(id);
		return txt.getText().toString();
	}
}

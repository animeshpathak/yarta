package fr.inria.arles.foosball;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import fr.inria.arles.foosball.PlayersApp.LoginObserver;
import fr.inria.arles.foosball.PlayersApp.Observer;
import fr.inria.arles.foosball.util.JobRunner;
import fr.inria.arles.foosball.util.JobRunner.Job;
import fr.inria.arles.foosball.util.Settings;
import fr.inria.arles.foosball.msemanagement.MSEManagerEx;
import fr.inria.arles.foosball.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;

public class BaseActivity extends SherlockActivity implements Observer,
		LoginObserver {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		settings = new Settings(this);
		application = (PlayersApp) getApplication();

		runner = new JobRunner(this);

		initMSE(this);
		((PlayersApp) getApplication()).addLoginObserver(this);
	}

	@Override
	protected void onDestroy() {
		((PlayersApp) getApplication()).removeLoginObserver(this);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		addObserver(this);
	}

	@Override
	protected void onPause() {
		removeObserver(this);
		super.onPause();
	}

	public void showProgress() {
		try {
			getSherlock().setProgressBarIndeterminateVisibility(true);
		} catch (Exception ex) {
		}
	}

	public void hideProgress() {
		try {
			getSherlock().setProgressBarIndeterminateVisibility(false);
		} catch (Exception ex) {

		}
	}

	protected void execute(Job job) {
		runner.runBackground(job);
	}

	protected void executeWithMessage(int messageId, Job job) {
		runner.run(messageId, job);
	}

	protected void showView(int id, boolean show) {
		View txt = findViewById(id);
		if (txt != null) {
			txt.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	protected void initMSE(Observer observer) {
		application.initMSE(observer);
	}

	@Override
	public final void updateInfo() {
		execute(new Job() {
			@Override
			public void doUIAfter() {
				if (getSAM() != null) {
					refreshUI();
				}
			}
		});
	}

	protected void refreshUI() {
		// TODO: implement your stub here;
	}

	protected void uninitMSE() {
		application.uninitMSE();
	}

	protected void clearMSE() {
		application.clearMSE();
	}

	protected void addObserver(Observer observer) {
		application.addObserver(observer);
	}

	protected void removeObserver(Observer observer) {
		application.removeObserver(observer);
	}

	protected StorageAccessManagerEx getSAM() {
		return application.getSAM();
	}

	protected CommunicationManager getCOMM() {
		return application.getCOMM();
	}

	protected MSEManagerEx getMSE() {
		return application.getMSE();
	}

	protected void sendNotify() {
		application.sendNotify();
	}

	protected void setCtrlText(int ctrlId, String text) {
		TextView txt = (TextView) findViewById(ctrlId);
		if (txt != null) {
			txt.setText(text);
		}
	}

	protected String getCtrlText(int ctrlId) {
		TextView txt = (TextView) findViewById(ctrlId);
		if (txt != null) {
			return txt.getText().toString();
		}
		return null;
	}

	protected void setCtrlProgress(int ctrlId, int percent) {
		ProgressBar progress = (ProgressBar) findViewById(ctrlId);
		if (progress != null) {
			progress.setProgress(percent);
		}
	}

	@Override
	public void onLogout() {
		finish();
	}

	protected Settings settings;
	private JobRunner runner;
	private PlayersApp application;
}

package fr.inria.arles.giveaway;

import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import fr.inria.arles.giveaway.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.giveaway.util.JobRunner;
import fr.inria.arles.giveaway.util.JobRunner.Job;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.msemanagement.MSEManager;

/**
 * Base activity containing common functionality.
 */
public class BaseActivity extends SherlockFragmentActivity implements
		DonationApp.Observer {

	private DonationApp app;
	private JobRunner runner;
	private YLogger log = YLoggerFactory.getLogger();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		app = (DonationApp) getApplication();
		runner = new JobRunner(this);
		app.initMSE(this);
	}

	@Override
	protected void onDestroy() {
		app.removeObserver(this);
		super.onDestroy();
	}

	protected void log(String format, Object... args) {
		log.d(getClass().getSimpleName(), String.format(format, args));
	}

	protected void logError(String format, Object... args) {
		log.e(getClass().getSimpleName(), String.format(format, args));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	protected void execute(Job job) {
		runner.runBackground(job);
	}

	protected void executeWithMessage(int messageId, Job job) {
		runner.run(messageId, job);
	}

	protected void trackUI(String message) {
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

	protected void uninitMSE() {
		app.uninitMSE();
	}

	protected void initMSE() {
		app.initMSE(this);
	}

	protected StorageAccessManagerEx getSAM() {
		return app.getSAM();
	}

	protected CommunicationManager getCOMM() {
		return app.getCOMM();
	}

	protected MSEManager getMSE() {
		return app.getMSE();
	}

	@Override
	public void updateInfo() {
		execute(new Job() {
			@Override
			public void doUIAfter() {
				refreshUI();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.addObserver(this);
	}

	@Override
	protected void onPause() {
		app.removeObserver(this);
		super.onPause();
	}

	/**
	 * Override here to refresh UI every time something gets updated, KB is
	 * ready, etc.
	 */
	protected void refreshUI() {
	}

	protected void setCtrlText(int resId, String text) {
		TextView txt = (TextView) findViewById(resId);
		if (txt != null) {
			txt.setText(text);
		}
	}

	protected String getCtrlText(int resId) {
		TextView txt = (TextView) findViewById(resId);
		if (txt != null) {
			return txt.getText().toString();
		}
		return null;
	}

	protected void notifyAgent() {
		app.notifyAgent();
	}

	@Override
	public void onLogout() {
		finish();
	}
}

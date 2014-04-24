package fr.inria.arles.yarta.android.library;

import android.os.Bundle;
import android.text.Spanned;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import fr.inria.arles.yarta.android.library.util.JobRunner;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.android.library.util.Settings;
import fr.inria.arles.yarta.android.library.web.WebClient;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.msemanagement.MSEManager;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;

/**
 * Base activity containing common functionality.
 */
public class BaseActivity extends SherlockFragmentActivity implements
		YartaApp.Observer, WebClient.WebErrorCallback {

	private YartaApp app;
	protected JobRunner runner;
	private YLogger log = YLoggerFactory.getLogger();
	protected Settings settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		settings = new Settings(this);
		app = (YartaApp) getApplication();
		runner = new JobRunner(this);
		tracker.start(this);
		app.initMSE(this);
	}

	protected void log(String format, Object... args) {
		log.d(getClass().getSimpleName(), String.format(format, args));
	}

	protected void logError(String format, Object... args) {
		log.e(getClass().getSimpleName(), String.format(format, args));
	}

	@Override
	protected void onDestroy() {
		tracker.stop();
		super.onDestroy();
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

	private AnalyticsTracker tracker = LibraryService.getTracker();

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

	protected void initMSE() {
		app.initMSE(this);
	}

	protected void uninitMSE() {
		app.uninitMSE();
	}

	protected MSEManager getMSE() {
		return app.getMSE();
	}

	protected StorageAccessManager getSAM() {
		return app.getSAM();
	}

	protected CommunicationManager getCOMM() {
		return app.getCOMM();
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

	protected void setCtrlText(int resId, Spanned text) {
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

	protected void setFocusable(int viewId, boolean focusable) {
		findViewById(viewId).setFocusable(focusable);
	}

	protected void sendNotify(String peerId) {
		app.sendNotify(peerId);
	}

	// from iris web client
	// TODO: maybe move in application
	@Override
	public void onAuthenticationFailed() {
	}

	@Override
	public void onNetworkFailed() {
	}
}

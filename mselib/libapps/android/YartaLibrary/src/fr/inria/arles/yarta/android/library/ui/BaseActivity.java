package fr.inria.arles.yarta.android.library.ui;

import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import fr.inria.arles.yarta.android.library.AnalyticsTracker;
import fr.inria.arles.yarta.android.library.ContentClientPictures;
import fr.inria.arles.yarta.android.library.LibraryService;
import fr.inria.arles.yarta.android.library.YartaApp;
import fr.inria.arles.yarta.android.library.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.yarta.android.library.util.JobRunner;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.android.library.util.Settings;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.msemanagement.MSEManager;

/**
 * Base activity containing common functionality.
 */
public class BaseActivity extends SherlockFragmentActivity implements
		YartaApp.Observer {

	private YartaApp app;
	protected JobRunner runner;
	protected ContentClientPictures contentClient;
	private YLogger log = YLoggerFactory.getLogger();
	protected Settings settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		contentClient = new ContentClientPictures(this);

		settings = new Settings(this);
		app = (YartaApp) getApplication();
		runner = new JobRunner(this);
		tracker.start(this);
		
		initMSE();
	}

	@Override
	protected void onDestroy() {
		tracker.stop();
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

	private AnalyticsTracker tracker = LibraryService.getTracker();

	protected void trackUI(String message) {
		tracker.trackUIUsage(message);
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

	protected void clearMSE() {
		app.clearMSE();
	}

	protected MSEManager getMSE() {
		return app.getMSE();
	}

	protected StorageAccessManagerEx getSAM() {
		return app.getSAM();
	}

	protected CommunicationManager getCOMM() {
		return app.getCOMM();
	}

	@Override
	public void updateInfo(final String notification) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				refreshUI(notification);
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
	protected void refreshUI(String notification) {
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

	protected void setCtrlVisible(int ctrlId, boolean visible) {
		View v = findViewById(ctrlId);
		if (v != null) {
			v.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
	}

	protected Spanned getCtrlHtml(int txtId) {
		EditText txt = (EditText) findViewById(txtId);
		if (txt != null) {
			return txt.getText();
		}
		return null;
	}

	protected void setFocusable(int viewId, boolean focusable) {
		findViewById(viewId).setFocusable(focusable);
	}

	protected void sendNotify(String peerId) {
		app.sendNotify(peerId);
	}

	@Override
	public void onLogout() {
		finish();
	}
}

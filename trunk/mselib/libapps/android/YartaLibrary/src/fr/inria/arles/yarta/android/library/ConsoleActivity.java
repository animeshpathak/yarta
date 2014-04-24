package fr.inria.arles.yarta.android.library;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import fr.inria.arles.iris.R;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ConsoleActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_console);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		refreshServiceStatus();
		tracker.start(this);
	}

	/**
	 * User clicked on Stop Service button
	 */
	public void onClickStopService(View view) {
		tracker.trackUIUsage("ConsoleActivity.StopService");
		stopService(new Intent(this, LibraryService.class));
		refreshServiceStatus();
	}

	/**
	 * User clicked on Start Service button
	 */
	public void onClickStartService(View view) {
		tracker.trackUIUsage("ConsoleActivity.StartService");
		startService(new Intent(this, LibraryService.class));
		refreshServiceStatus();
	}

	/**
	 * User clicked on Dump Policy button
	 */
	public void onClickDumpPolicy(View view) {
		tracker.trackUIUsage("ConsoleActivity.DumpPolicy");
		if (copyFile(getFilesDir().getAbsolutePath() + "/"
				+ getString(R.string.service_basePolicy), getSDPath()
				+ getString(R.string.service_basePolicy))) {
			Toast.makeText(this, R.string.console_operation_successfull,
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, R.string.console_operation_error,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * User clicked on Dump Ontology button
	 */
	public void onClickDumpOntology(View view) {
		tracker.trackUIUsage("ConsoleActivity.DumpOntology");
		if (copyFile(getFilesDir().getAbsolutePath() + "/kb.rdf", getSDPath()
				+ getString(R.string.service_baseRDF))) {
			Toast.makeText(this, R.string.console_operation_successfull,
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, R.string.console_operation_error,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Returns the yarta folder on SD card.
	 * 
	 * @return String
	 */
	private String getSDPath() {
		String sdPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		sdPath += "/yarta/";
		new File(sdPath).mkdirs();
		return sdPath;
	}

	/**
	 * Copies a file from one location to the other.
	 * 
	 * @param sourceFileName
	 * @param destinationFileName
	 * 
	 * @return boolean
	 */
	private boolean copyFile(String sourceFileName, String destinationFileName) {
		BufferedReader br = null;
		PrintWriter pw = null;

		try {
			br = new BufferedReader(new FileReader(sourceFileName));
			pw = new PrintWriter(new FileWriter(destinationFileName));

			String line;
			while ((line = br.readLine()) != null) {
				pw.println(line);
			}

			br.close();
			pw.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Refreshes the service running status.
	 */
	private void refreshServiceStatus() {
		TextView txtServiceStatus = (TextView) findViewById(R.id.serviceStatus);
		txtServiceStatus
				.setText(isYartaServiceRunning() ? R.string.console_service_status_running
						: R.string.console_service_status_not_running);
	}

	/**
	 * Checks if Yarta's LibraryService is running.
	 * 
	 * @return true/false
	 */
	private boolean isYartaServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (LibraryService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private AnalyticsTracker tracker = LibraryService.getTracker();
}

package fr.inria.arles.yarta.android.library;

import fr.inria.arles.yarta.R;
import fr.inria.arles.yarta.android.library.plugins.SyncAlarm;
import fr.inria.arles.yarta.android.library.util.Settings;
import android.os.Bundle;
import android.widget.RadioGroup;

public class SettingsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		settings = new Settings(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadSettings();
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveSettings();
	}

	private void loadSettings() {
		RadioGroup group = (RadioGroup) findViewById(R.id.refreshInterval);

		long interval = settings.getLong(Settings.REFRESH_INTERVAL);

		if (interval == SyncAlarm.SYNC_DAILY) {
			group.check(R.id.sync_daily);
		} else if (interval == SyncAlarm.SYNC_HOURLY) {
			group.check(R.id.sync_hourly);
		} else {
			group.check(R.id.sync_manual);
		}
	}

	private void saveSettings() {
		RadioGroup group = (RadioGroup) findViewById(R.id.refreshInterval);
		int checkedId = group.getCheckedRadioButtonId();

		if (checkedId == R.id.sync_daily) {
			settings.setLong(Settings.REFRESH_INTERVAL, SyncAlarm.SYNC_DAILY);
		} else if (checkedId == R.id.sync_hourly) {
			settings.setLong(Settings.REFRESH_INTERVAL, SyncAlarm.SYNC_HOURLY);
		} else {
			settings.setLong(Settings.REFRESH_INTERVAL, SyncAlarm.SYNC_MANUAL);
		}

		SyncAlarm.resetAlarm(this);
	}

	private Settings settings;
}

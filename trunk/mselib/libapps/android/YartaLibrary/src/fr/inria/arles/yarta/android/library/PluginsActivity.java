package fr.inria.arles.yarta.android.library;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import fr.inria.arles.yarta.R;
import fr.inria.arles.yarta.android.library.plugins.PluginListAdapter;
import fr.inria.arles.yarta.android.library.plugins.SyncTask;
import fr.inria.arles.yarta.android.library.util.Settings;

/**
 * Activity containing plugins and their sync actions;
 */
public class PluginsActivity extends BaseActivity {

	private PluginListAdapter adapter;

	public static final int MENU_SETTINGS = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plugins);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		adapter = new PluginListAdapter(this, new Settings(this));
		ListView list = (ListView) findViewById(R.id.pluginsList);
		list.setAdapter(adapter);

		registerReceiver(receiver, new IntentFilter(SyncTask.ACTION_UPDATE));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SETTINGS, 0, R.string.dashboard_menu_settings);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SETTINGS:
			trackUI("DashboardActivity.Settings");

			startActivity(new Intent(this, SettingsActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
	}

	/**
	 * This receives updates from the SyncTask.
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(SyncTask.ACTION_UPDATE)) {
				adapter.notifyDataSetChanged();
			}
		}
	};

	public void onSyncNowClicked(View view) {
		trackUI("DashboardActivitiy.SyncNow");
		if (syncTask != null) {
			syncTask.cancel(true);
		}

		syncTask = new SyncTask();
		syncTask.execute(this);

		Toast.makeText(this, R.string.dashboard_sync_started,
				Toast.LENGTH_SHORT).show();
	}

	private SyncTask syncTask;
}

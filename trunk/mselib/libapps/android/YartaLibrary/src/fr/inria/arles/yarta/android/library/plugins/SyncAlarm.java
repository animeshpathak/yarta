package fr.inria.arles.yarta.android.library.plugins;

import java.util.Calendar;
import java.util.Date;

import fr.inria.arles.yarta.android.library.util.Settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SyncAlarm extends BroadcastReceiver {

	public static final int SYNC_MANUAL = 0;
	public static final int SYNC_HOURLY = 1;
	public static final int SYNC_DAILY = 2;

	@Override
	public void onReceive(Context context, Intent intent) {
		resetAlarm(context);
		new SyncTask().execute(context);
	}

	/**
	 * Called after applying new settings. This will reset the next occurring
	 * alarm and set the next one if applicable;
	 * 
	 * @param context
	 */
	public static void resetAlarm(Context context) {
		Settings settings = new Settings(context);

		AlarmManager mgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(
				context, SyncAlarm.class), 0);
		mgr.cancel(pi);

		long refreshInterval = settings.getLong(Settings.REFRESH_INTERVAL);

		if (refreshInterval == SYNC_MANUAL) {
			return;
		}
		Date dNow = new Date();

		if (refreshInterval == SYNC_HOURLY) {
			Calendar c = Calendar.getInstance();
			c.setTime(dNow);
			c.add(Calendar.HOUR_OF_DAY, 1);
			dNow = c.getTime();
		} else if (refreshInterval == SYNC_DAILY) {
			Calendar c = Calendar.getInstance();
			c.setTime(dNow);
			c.add(Calendar.DAY_OF_YEAR, 1);
			dNow = c.getTime();
		}
		mgr.set(AlarmManager.RTC, dNow.getTime(), pi);
	}
}

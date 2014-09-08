package fr.inria.arles.iris.web;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.auth.AuthenticatorActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class Notification {

	private static final int NotificationLogin = 1988;

	/**
	 * Shows the login notification
	 * 
	 * @param context
	 */
	public static void showLogin(Context context) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.icon_default)
				.setContentTitle(context.getString(R.string.app_name))
				.setContentText(context.getString(R.string.app_login_required));

		PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
				0, new Intent(context, AuthenticatorActivity.class), 0);
		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NotificationLogin, mBuilder.build());
	}

	public static void hideLogin(Context context) {
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(NotificationLogin);
	}
}

package fr.inria.arles.yarta.android.library;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

/**
 * This class contains the YartaLibrary dependency check functions.
 */
public class DependencyCheck {

	/**
	 * Checks if Yarta is installed. If not -> prompt user -> uninstall current
	 * app;
	 */
	public static void checkYartaInstallationAndPromptWithUninstall(
			Context context) {
		if (!isYartaInstalled(context)) {
			// promptUserAndUninstall(context);
		}
	}

	/**
	 * Checks if Yarta is installed on the current device.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isYartaInstalled(Context context) {
		PackageManager packageManager = context.getPackageManager();

		if (packageManager != null) {
			try {
				packageManager.getPackageInfo(
						"fr.inria.arles.yarta",
						PackageManager.GET_ACTIVITIES);
				return true;
			} catch (NameNotFoundException ex) {
				return false;
			}
		}
		return false;
	}

	/**
	 * Prompt user that Yarta is needed; upon notification click current app
	 * will be uninstalled.
	 * 
	 * @param context
	 */
	private static void promptUserAndUninstall(Context context) {
		NotificationManager manger = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(
				android.R.drawable.ic_dialog_alert, NOTIFICATION_MESSAGE,
				System.currentTimeMillis());

		PendingIntent contentIntent = PendingIntent.getActivity(
				context,
				0,
				new Intent(Intent.ACTION_DELETE, Uri.parse("package:"
						+ context.getPackageName())), 0);
		notification.setLatestEventInfo(context, NOTIFICATION_TITLE,
				NOTIFICATION_MESSAGE, contentIntent);
		manger.notify(0, notification);
	}

	private static final String NOTIFICATION_TITLE = "YartaLibrary missing";
	private static final String NOTIFICATION_MESSAGE = "Please uninstall current app and install YartaLibrary first.";
}

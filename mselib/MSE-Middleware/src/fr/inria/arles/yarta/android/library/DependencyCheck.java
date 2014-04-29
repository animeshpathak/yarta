package fr.inria.arles.yarta.android.library;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * This class contains the YartaLibrary dependency check functions.
 */
public class DependencyCheck {

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
				PackageInfo info = packageManager.getPackageInfo(
						"fr.inria.arles.iris", 0);
				if (info.versionName.startsWith("2")) {
					return true;
				}
			} catch (NameNotFoundException ex) {
				return false;
			}
		}
		return false;
	}
}

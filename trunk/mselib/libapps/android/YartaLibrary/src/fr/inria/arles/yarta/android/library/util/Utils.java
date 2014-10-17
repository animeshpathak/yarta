package fr.inria.arles.yarta.android.library.util;

import org.jsoup.Jsoup;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.ui.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class Utils {

	public static String getCurrentVersion(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (Exception ex) {
		}
		return null;
	}

	public static void checkForUpdate(final BaseActivity context) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				final String latestVersion = getLatestVersion(context);
				final String currentVersion = getCurrentVersion(context);

				context.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (value(latestVersion) > value(currentVersion)) {
							AlertDialog.show(
									context,
									context.getString(R.string.app_new_version_message),
									context.getString(R.string.app_new_version_title),
									context.getString(R.string.app_new_version_yes),
									context.getString(R.string.app_new_version_cancel),
									new AlertDialog.Handler() {

										@Override
										public void onOK() {
											try {
												final String appPackageName = "fr.inria.arles.iris";
												context.startActivity(new Intent(
														Intent.ACTION_VIEW,
														Uri.parse("market://details?id="
																+ appPackageName)));
											} catch (Exception ex) {
												Toast.makeText(
														context,
														R.string.app_market_required,
														Toast.LENGTH_LONG)
														.show();
											}
										}
									});
						}
					}
				});
			}
		});
		thread.start();
	}

	private static String getLatestVersion(Context context) {
		try {
			return Jsoup
					.connect(
							"https://play.google.com/store/apps/details?id="
									+ context.getPackageName() + "&hl=en")
					.timeout(30000)
					.userAgent(
							"Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
					.referrer("http://www.google.com").get()
					.select("div[itemprop=softwareVersion]").first().ownText();
		} catch (Exception ex) {
		}
		return null;
	}

	private static long value(String string) {
		string = string.trim();
		if (string.contains(".")) {
			string = string.replace(".", "");
		}
		return Long.valueOf(string);
	}
}

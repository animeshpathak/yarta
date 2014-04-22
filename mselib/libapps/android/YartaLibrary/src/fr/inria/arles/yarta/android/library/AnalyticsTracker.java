package fr.inria.arles.yarta.android.library;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import fr.inria.arles.yarta.R;
import fr.inria.arles.yarta.android.library.util.Settings;

import android.content.Context;

/**
 * This is the Google Analytics Tracker class. Contains helper functions to
 * track API Usage & also UI Usage
 */
public class AnalyticsTracker {

	/**
	 * Basic C-tor.
	 */
	public AnalyticsTracker() {
	}

	/**
	 * Starts the GA tracking session.
	 */
	public void start(Context context) {
		if (tracker != null) {
			return;
		}

		settings = new Settings(context);

		try {
			tracker = GoogleAnalytics.getInstance(context).getTracker(
					context.getString(R.string.ga_trackingId));
			tracker.setStartSession(true);
			tracker.setAppId(CATEGORY);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Ends the GA tracking session.
	 */
	public void stop() {
		if (tracker == null) {
			return;
		}
		try {
			tracker.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		tracker = null;
	}

	/**
	 * Called before sendAPIUsage
	 */
	public void beforeAPIUsage() {
		startTime = System.currentTimeMillis();
	}

	/**
	 * Tracks an API usage & its duration. Please call beforeAPIUsage before
	 * accessing the Yarta API to monitor its duration.
	 * 
	 * @param function
	 *            (function name)
	 * @param duration
	 *            (in milliseconds)
	 */
	public void sendAPIUsage(String function) {
		if (!trackerEnabled()) {
			return;
		}

		try {
			tracker.sendEvent(CATEGORY, ACTION_API, function,
					Math.min(startTime, System.currentTimeMillis() - startTime));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		startTime = 0;
	}

	/**
	 * Tracks an UI usage.
	 * 
	 * @param name
	 */
	public void trackUIUsage(String name) {
		if (!trackerEnabled()) {
			return;
		}
		try {
			tracker.sendEvent(CATEGORY, ACTION_UI, name, 0L);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Checks weather to submit or not reports
	 * 
	 * @return boolean
	 */
	private boolean trackerEnabled() {
		if (tracker == null) {
			return false;
		}

		if (!enabled) {
			enabled = settings.getBoolean(Settings.AUR_ACCEPTED);
		}

		return enabled;
	}

	/**
	 * The GA tracker.
	 */
	private Tracker tracker = null;

	/**
	 * The start time for an API call.
	 */
	private long startTime = 0;

	/**
	 * Tells if AUR is enabled or not.
	 */
	private boolean enabled = false;

	/**
	 * The constants.
	 */
	private static final String CATEGORY = "YartaLibrary";
	private static final String ACTION_API = "APIUsage";
	private static final String ACTION_UI = "UIUsage";
	private Settings settings;
}

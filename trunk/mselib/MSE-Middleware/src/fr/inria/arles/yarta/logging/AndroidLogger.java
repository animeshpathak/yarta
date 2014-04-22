package fr.inria.arles.yarta.logging;

import android.util.Log;

/**
 * Implements the methods described in YLogger using Android Logging
 */
public class AndroidLogger implements YLogger {

	/**
	 * @see fr.inria.arles.yarta.logging.YLogger#d(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public int d(String tag, String message) {
		if (YLoggerFactory.getLoglevel() <= YLoggerFactory.LOGLEVEL_DEBUG) {
			Log.d(tag, message);
		}
		return 0;
	}

	/**
	 * @see fr.inria.arles.yarta.logging.YLogger#d(java.lang.String,
	 *      java.lang.String, java.lang.Throwable)
	 */
	@Override
	public int d(String tag, String message, Throwable t) {
		if (YLoggerFactory.getLoglevel() <= YLoggerFactory.LOGLEVEL_DEBUG) {
			Log.d(tag, message, t);
		}
		return 0;
	}

	/**
	 * @see fr.inria.arles.yarta.logging.YLogger#e(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public int e(String tag, String message) {
		if (YLoggerFactory.getLoglevel() <= YLoggerFactory.LOGLEVEL_ERROR) {
			Log.e(tag, message);
		}
		return 0;
	}

	/**
	 * @see fr.inria.arles.yarta.logging.YLogger#e(java.lang.String,
	 *      java.lang.String, java.lang.Throwable)
	 */
	@Override
	public int e(String tag, String message, Throwable t) {
		if (YLoggerFactory.getLoglevel() <= YLoggerFactory.LOGLEVEL_ERROR) {
			Log.e(tag, message, t);
		}
		return 0;
	}

	/**
	 * @see fr.inria.arles.yarta.logging.YLogger#i(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public int i(String tag, String message) {
		if (YLoggerFactory.getLoglevel() <= YLoggerFactory.LOGLEVEL_INFO) {
			Log.i(tag, message);
		}
		return 0;
	}

	/**
	 * @see fr.inria.arles.yarta.logging.YLogger#i(java.lang.String,
	 *      java.lang.String, java.lang.Throwable)
	 */
	@Override
	public int i(String tag, String message, Throwable t) {
		if (YLoggerFactory.getLoglevel() <= YLoggerFactory.LOGLEVEL_INFO) {
			Log.i(tag, message, t);
		}
		return 0;
	}

	/**
	 * @see fr.inria.arles.yarta.logging.YLogger#w(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public int w(String tag, String message) {
		if (YLoggerFactory.getLoglevel() <= YLoggerFactory.LOGLEVEL_WARN) {
			Log.w(tag, message);
		}
		return 0;
	}

	/**
	 * @see fr.inria.arles.yarta.logging.YLogger#w(java.lang.String,
	 *      java.lang.String, java.lang.Throwable)
	 */
	@Override
	public int w(String tag, String message, Throwable t) {
		if (YLoggerFactory.getLoglevel() <= YLoggerFactory.LOGLEVEL_WARN) {
			Log.w(tag, message, t);
		}
		return 0;
	}
}

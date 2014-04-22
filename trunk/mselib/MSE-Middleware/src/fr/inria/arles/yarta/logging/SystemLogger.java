package fr.inria.arles.yarta.logging;

/**
 * Implements the methods described in YLogger using System.err
 */
public class SystemLogger implements YLogger {

	/**
	 * @see fr.inria.arles.yarta.logging.YLogger#d(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public int d(String tag, String message) {
		if (YLoggerFactory.getLoglevel() <= YLoggerFactory.LOGLEVEL_DEBUG) {
			System.out.println("DEBUG: " + tag + ": " + message);
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
			System.out.println("DEBUG: " + tag + ": " + message);
			t.printStackTrace(System.err);
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
			System.err.println("ERROR: " + tag + ": " + message);
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
			System.err.println("ERROR: " + tag + ": " + message);
			t.printStackTrace(System.err);
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
			System.err.println("INFO: " + tag + ": " + message);
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
			System.err.println("INFO: " + tag + ": " + message);
			t.printStackTrace(System.err);
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
			System.err.println("WARN: " + tag + ": " + message);
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
			System.err.println("WARN: " + tag + ": " + message);
			t.printStackTrace(System.err);
		}
		return 0;
	}
}

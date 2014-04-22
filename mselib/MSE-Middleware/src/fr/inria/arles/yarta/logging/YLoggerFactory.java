package fr.inria.arles.yarta.logging;

/**
 * The factory class for Yarta Loggers. This is what should be used by other
 * classes needing a logger.
 */
public class YLoggerFactory {

	private static YLogger ylogger = null;
	/** The lowest, most verbose log level */
	public static final int LOGLEVEL_DEBUG = 0;
	/** Log level for info messages */
	public static final int LOGLEVEL_INFO = 1;
	/** All went went, but you should see this maybe */
	public static final int LOGLEVEL_WARN = 2;
	/**
	 * Something went wrong! Used often when exceptions have been caught. Please
	 * check the code!
	 */
	public static final int LOGLEVEL_ERROR = 3;
	/** No more logs. Use this ONLY if you know what you are doing! */
	public static final int LOGLEVEL_QUIET = 100;

	private static int logLevel = LOGLEVEL_DEBUG;

	/**
	 * Returns a YLogger, which can then be used to report logs.
	 * 
	 * @return A class that implements the YLogger interface.
	 */
	public static YLogger getLogger() {
		if (ylogger == null) {
			// need to find a new logger. Let's check if we have Android running
			if (System.getProperty("java.vm.name").equalsIgnoreCase("Dalvik")) {
				ylogger = new AndroidLogger();
				ylogger.d("YLoggerFactory",
						"Instantiating Android-based logger");
			} else {
				// fallback option, system logger.
				ylogger = new SystemLogger();
				ylogger.d("YLoggerFactory", "Instantiating System-based logger");
			}
		}
		return ylogger;
	}

	/**
	 * Set the log level for any calls from now on.
	 * 
	 * @param newLogLevel
	 *            The new log level.
	 */
	public static void setLoglevel(int newLogLevel) {
		logLevel = newLogLevel;
	}

	/**
	 * Get the current log level
	 * 
	 * @return The current log level.
	 */
	public static int getLoglevel() {
		return logLevel;
	}
}

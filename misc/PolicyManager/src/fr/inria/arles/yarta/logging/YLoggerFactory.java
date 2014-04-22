/**
 * 
 */
package fr.inria.arles.yarta.logging;

/**
 * The factory class for Yarta Loggers. This is what should be 
 * used by other classes needing a logger. 
 * @author pathak
 *
 */
public class YLoggerFactory {
	
	private static YLogger ylogger = null;
	
	/**
	 * Returns a YLogger, which can then be used to report logs. 
	 * @return A class that implements the YLogger interface.
	 */
	public static YLogger getLogger(){
		if (ylogger == null)
			ylogger = new SystemLogger();

		return ylogger;
	}

}

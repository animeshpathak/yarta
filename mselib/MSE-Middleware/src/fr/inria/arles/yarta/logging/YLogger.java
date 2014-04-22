package fr.inria.arles.yarta.logging;

/**
 * Interface for loggers used in the Yarta middleware. The loglevels are debug,
 * info, warn, and error. Modeled after the Android Log class, but valid in
 * other contexts too.
 */
public interface YLogger {

	/**
	 * Log an error.
	 * 
	 * @param tag
	 *            The tag to indicate the source of the message
	 * @param message
	 *            The message itself
	 * @return -1 on failure. 0 on success
	 */
	public int e(String tag, String message);

	/**
	 * Log an error with a Throwable.
	 * 
	 * @param tag
	 *            The tag to indicate the source of the message
	 * @param message
	 *            The message itself
	 * @param t
	 * @return -1 on failure. 0 on success
	 */
	public int e(String tag, String message, Throwable t);

	/**
	 * Log a warning.
	 * 
	 * @param tag
	 *            The tag to indicate the source of the message
	 * @param message
	 *            The message itself
	 * @return -1 on failure. 0 on success
	 */
	public int w(String tag, String message);

	/**
	 * Log a warning with a Throwable.
	 * 
	 * @param tag
	 *            The tag to indicate the source of the message
	 * @param message
	 *            The message itself
	 * @param t
	 * @return -1 on failure. 0 on success
	 */
	public int w(String tag, String message, Throwable t);

	/**
	 * Log an info message.
	 * 
	 * @param tag
	 *            The tag to indicate the source of the message
	 * @param message
	 *            The message itself
	 * @return -1 on failure. 0 on success
	 */
	public int i(String tag, String message);

	/**
	 * Log an info message with a Throwable.
	 * 
	 * @param tag
	 *            The tag to indicate the source of the message
	 * @param message
	 *            The message itself
	 * @param t
	 * @return -1 on failure. 0 on success
	 */
	public int i(String tag, String message, Throwable t);

	/**
	 * Log a debug message.
	 * 
	 * @param tag
	 *            The tag to indicate the source of the message
	 * @param message
	 *            The message itself
	 * @return -1 on failure. 0 on success
	 */
	public int d(String tag, String message);

	/**
	 * Log a debug message with a Throwable.
	 * 
	 * @param tag
	 *            The tag to indicate the source of the message
	 * @param message
	 *            The message itself
	 * @param t
	 * @return -1 on failure. 0 on success
	 */
	public int d(String tag, String message, Throwable t);
}

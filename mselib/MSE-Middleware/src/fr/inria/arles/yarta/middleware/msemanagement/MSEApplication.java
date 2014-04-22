package fr.inria.arles.yarta.middleware.msemanagement;

/**
 * The interface which should be implemented by any Application wishing to use
 * the MSEManager etc. This provides for the appropriate listeners etc.
 */
public interface MSEApplication {

	/**
	 * This takes a notification, and hopefully displays it to the user. Should
	 * return quickly.
	 * 
	 * @param notification
	 *            The text which is to be shown to the user.
	 */
	public void handleNotification(String notification);

	/**
	 * This takes a question, and gets the user to respond. Perhaps by a dialog
	 * box.
	 * 
	 * @param query
	 *            The question that the user must answer in yes or no.
	 */
	public boolean handleQuery(String query);

	/**
	 * This event is fired when the knowledge has finished with the
	 * initialization. Moreover, it provides the userId, which then needs to be
	 * passed to MSEManager object.
	 */
	public void handleKBReady(String userId);

	/**
	 * Returns the application id used for multiplexing the communication
	 * messages. So, all the application messages sent through this app, will be
	 * handles by the same app. Make sure you return something unique.
	 */
	public String getAppId();
}

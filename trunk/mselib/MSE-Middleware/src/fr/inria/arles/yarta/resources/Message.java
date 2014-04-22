package fr.inria.arles.yarta.resources;

/**
 * The message interface.
 */
public interface Message extends Content {

	/** The unique "type" URI */
	public static final String typeURI = baseMSEURI + "#Message";

	/** the time stamp */
	public static final String PROPERTY_TIME_URI = baseMSEURI + "#time";

	/**
	 * Returns the time stamp associated with this message.
	 * 
	 * @return
	 */
	public Long getTime();

	/**
	 * Sets the time stamp associated with this message.
	 * 
	 * @param timestmap
	 */
	public void setTime(Long timestmap);
}

package fr.inria.arles.yarta.resources;

import java.util.Set;

/**
 * The message interface.
 */
public interface Message extends Content {

	/** The unique "type" URI */
	public static final String typeURI = baseMSEURI + "#Message";

	/** the time stamp */
	public static final String PROPERTY_TIME_URI = baseMSEURI + "#time";
	
	/** the content */
	public static final String PROPERTY_CONTENT_URI = baseMSEURI + "#content";

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
	
	/**
	 * Returns the content associated with this message.
	 * 
	 * @return
	 */
	public String getContent();

	/**
	 * Sets the content associated with this message.
	 * 
	 * @param content
	 */
	public void setContent(String content);
	
	/**
	 * The inverse property contains.
	 * @return
	 */
	public Set<Conversation> getContains_inverse();
}

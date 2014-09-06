package fr.inria.arles.yarta.resources;

import java.util.Set;

/**
 * A generic class representing all possible types of entities carrying some
 * information.
 */
public interface Content extends Resource {

	/** The unique "type" URI */
	public static final String typeURI = baseMSEURI + "#Content";

	/** the URI for the property format */
	public static final String PROPERTY_FORMAT_URI = baseMSEURI + "#format";

	/** the URI for the property identifier */
	public static final String PROPERTY_IDENTIFIER_URI = baseMSEURI
			+ "#identifier";

	/** the URI for the property source */
	public static final String PROPERTY_SOURCE_URI = baseMSEURI + "#source";

	/** the URI for the property title */
	public static final String PROPERTY_TITLE_URI = baseMSEURI + "#title";
	
	/** the time stamp */
	public static final String PROPERTY_TIME_URI = baseMSEURI + "#time";
	
	/** the content */
	public static final String PROPERTY_CONTENT_URI = baseMSEURI + "#content";

	/** the URI for the property title */
	public static final String PROPERTY_HASREPLY_URI = baseMSEURI + "#hasReply";

	/**
	 * @return the format
	 */
	public String getFormat();

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format);

	/**
	 * @return the identifier
	 */
	public String getIdentifier();

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	public void setIdentifier(String identifier);

	/**
	 * @return the source
	 */
	public String getSource();

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source);

	/**
	 * @return the title
	 */
	public String getTitle();

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String contentTitle);
	
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
	 * inverse of {@link Agent#getCreator()}
	 * 
	 * @return The list of agents who are a creator of this content. Empty list
	 *         if there are no such agents. null if there was an error
	 */
	public Set<Agent> getCreator_inverse();

	/**
	 * inverse of {@link Group#getHasContent()}
	 * 
	 * @return the list of groups in which this content is held.
	 */
	public Set<Group> getHasContent_inverse();

	/**
	 * Creates a "has reply" edge between this content and a content
	 * 
	 * @param c
	 *            The content which is the reply
	 * @return true if all went well, false otherwise
	 */
	public boolean addHasReply(Content c);

	/**
	 * 
	 * @return The list of contents which are replies.
	 */
	public Set<Content> getHasReply();

	/**
	 * deletes the "has reply" link between this content and a content
	 * 
	 * @param c
	 *            The content which is the reply
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteHasReply(Content c);
	
	/**
	 * inverse of {@link Content#getHasReply()}
	 * 
	 * @return the list of contents for which this is a reply.
	 */
	public Set<Content> getHasReply_inverse();
}

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
	public static final String PROPERTY_TITLE_URI = baseMSEURI
			+ "#title";

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
	 * inverse of {@link Agent#getCreator()}
	 * 
	 * @return The list of agents who are a creator of this content. Empty list
	 *         if there are no such agents. null if there was an error
	 */
	public Set<Agent> getCreator_inverse();
}

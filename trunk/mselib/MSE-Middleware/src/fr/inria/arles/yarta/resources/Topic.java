package fr.inria.arles.yarta.resources;

import java.util.Set;

/**
 * This class represents any topic that might be, for example, subject of
 * interest for a person or object of description in a document (content) or
 * many others. It was inspired by users' tagging activities. As such, it might
 * be mapped onto keywords, folksonomies or more structured taxonomies.
 */
public interface Topic extends Resource {

	/** The unique "type" URI */
	public static final String typeURI = baseMSEURI + "#Topic";

	/** the URI for the property topicTitle */
	public static final String PROPERTY_TITLE_URI = baseMSEURI
			+ "#title";

	/**
	 * @return the topicTitle
	 */
	public String getTitle();

	/**
	 * @param topicTitle
	 *            the topicTitle to set
	 */
	public void setTitle(String topicTitle);

	/**
	 * inverse of {@link Resource#addIsTagged(Topic)}
	 * 
	 * @return the list of resources tagged with this topic. Empty list if none.
	 *         Null if something went wrong.
	 */
	public Set<Resource> getIsTagged_inverse();
}

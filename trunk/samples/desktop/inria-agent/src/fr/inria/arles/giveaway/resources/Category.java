package fr.inria.arles.giveaway.resources;

import java.util.Set;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.yarta.resources.Topic;
import fr.inria.arles.giveaway.msemanagement.MSEManagerEx;

/**
 * 
 * Category interface definition.
 *
 */
public interface Category extends Resource, Topic {
	public static final String typeURI = MSEManagerEx.baseMSEURI + "#Category";

	/** the URI for property title */
	public static final String PROPERTY_TITLE_URI = baseMSEURI + "#title";

	/** the URI for property isTagged */
	public static final String PROPERTY_ISTAGGED_URI = baseMSEURI + "#isTagged";

	/**
	 * inverse of {@link #getCategory()}
	 */
	public Set<Announcement> getCategory_inverse();
}
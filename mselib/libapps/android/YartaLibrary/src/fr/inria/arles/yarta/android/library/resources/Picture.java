package fr.inria.arles.yarta.android.library.resources;

import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Resource;
import java.util.Set;
import fr.inria.arles.yarta.android.library.msemanagement.MSEManagerEx;

/**
 * 
 * Picture interface definition.
 *
 */
public interface Picture extends Content, Resource {
	public static final String typeURI = MSEManagerEx.baseMSEURI + "#Picture";

	/** the URI for property identifier */
	public static final String PROPERTY_IDENTIFIER_URI = baseMSEURI + "#identifier";

	/** the URI for property format */
	public static final String PROPERTY_FORMAT_URI = baseMSEURI + "#format";

	/** the URI for property time */
	public static final String PROPERTY_TIME_URI = baseMSEURI + "#time";

	/** the URI for property source */
	public static final String PROPERTY_SOURCE_URI = baseMSEURI + "#source";

	/** the URI for property title */
	public static final String PROPERTY_TITLE_URI = baseMSEURI + "#title";

	/** the URI for property content */
	public static final String PROPERTY_CONTENT_URI = baseMSEURI + "#content";

	/** the URI for property isTagged */
	public static final String PROPERTY_ISTAGGED_URI = baseMSEURI + "#isTagged";

	/** the URI for property hasReply */
	public static final String PROPERTY_HASREPLY_URI = baseMSEURI + "#hasReply";

	/**
	 * inverse of {@link #getPicture()}
	 */
	public Set<Agent> getPicture_inverse();
}
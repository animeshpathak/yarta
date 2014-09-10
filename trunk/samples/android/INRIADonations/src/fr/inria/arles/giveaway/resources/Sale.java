package fr.inria.arles.giveaway.resources;

import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.giveaway.msemanagement.MSEManagerEx;

/**
 * 
 * Sale interface definition.
 *
 */
public interface Sale extends Resource, Content, Announcement {
	public static final String typeURI = MSEManagerEx.baseMSEURI + "#Sale";

	/** the URI for property content */
	public static final String PROPERTY_CONTENT_URI = baseMSEURI + "#content";

	/** the URI for property title */
	public static final String PROPERTY_TITLE_URI = baseMSEURI + "#title";

	/** the URI for property time */
	public static final String PROPERTY_TIME_URI = baseMSEURI + "#time";

	/** the URI for property price */
	public static final String PROPERTY_PRICE_URI = MSEManagerEx.baseMSEURI + "#price";

	/** the URI for property source */
	public static final String PROPERTY_SOURCE_URI = baseMSEURI + "#source";

	/** the URI for property description */
	public static final String PROPERTY_DESCRIPTION_URI = MSEManagerEx.baseMSEURI + "#description";

	/** the URI for property format */
	public static final String PROPERTY_FORMAT_URI = baseMSEURI + "#format";

	/** the URI for property identifier */
	public static final String PROPERTY_IDENTIFIER_URI = baseMSEURI + "#identifier";

	/** the URI for property isTagged */
	public static final String PROPERTY_ISTAGGED_URI = baseMSEURI + "#isTagged";

	/** the URI for property picture */
	public static final String PROPERTY_PICTURE_URI = MSEManagerEx.baseMSEURI + "#picture";

	/** the URI for property category */
	public static final String PROPERTY_CATEGORY_URI = MSEManagerEx.baseMSEURI + "#category";

	/** the URI for property hasReply */
	public static final String PROPERTY_HASREPLY_URI = baseMSEURI + "#hasReply";

	/** the URI for property hiddenBy */
	public static final String PROPERTY_HIDDENBY_URI = MSEManagerEx.baseMSEURI + "#hiddenBy";
}
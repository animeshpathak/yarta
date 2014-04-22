package fr.inria.arles.yarta.conference.resources;

import fr.inria.arles.yarta.conference.msemanagement.MSEManagerEx;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Resource;

/**
 * 
 * Paper interface definition.
 *
 */
public interface Paper extends Resource, Content {
	public static final String typeURI = MSEManagerEx.baseMSEURI + "#Paper";

	/** the URI for property title */
	public static final String PROPERTY_TITLE_URI = baseMSEURI + "#title";

	/** the URI for property source */
	public static final String PROPERTY_SOURCE_URI = baseMSEURI + "#source";

	/** the URI for property format */
	public static final String PROPERTY_FORMAT_URI = baseMSEURI + "#format";

	/** the URI for property identifier */
	public static final String PROPERTY_IDENTIFIER_URI = baseMSEURI + "#identifier";

	/** the URI for property isTagged */
	public static final String PROPERTY_ISTAGGED_URI = baseMSEURI + "#isTagged";
}
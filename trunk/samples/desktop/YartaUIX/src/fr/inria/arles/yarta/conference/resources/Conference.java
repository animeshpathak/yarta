package fr.inria.arles.yarta.conference.resources;

import fr.inria.arles.yarta.conference.msemanagement.MSEManagerEx;
import java.util.Set;
import fr.inria.arles.yarta.resources.Resource;

/**
 * 
 * Conference interface definition.
 *
 */
public interface Conference extends Resource, Event {
	public static final String typeURI = MSEManagerEx.baseMSEURI + "#Conference";

	/** the URI for property title */
	public static final String PROPERTY_TITLE_URI = baseMSEURI + "#title";

	/** the URI for property description */
	public static final String PROPERTY_DESCRIPTION_URI = baseMSEURI + "#description";

	/** the URI for property isTagged */
	public static final String PROPERTY_ISTAGGED_URI = baseMSEURI + "#isTagged";

	/** the URI for property follows */
	public static final String PROPERTY_FOLLOWS_URI = MSEManagerEx.baseMSEURI + "#follows";

	/** the URI for property isLocated */
	public static final String PROPERTY_ISLOCATED_URI = baseMSEURI + "#isLocated";

	/**
	 * inverse of {@link #getIsIncluded()}
	 */
	public Set<Presentation> getIsIncluded_inverse();
}
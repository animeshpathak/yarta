package fr.inria.arles.giveaway.resources;

import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.giveaway.msemanagement.MSEManagerEx;

/**
 * 
 * DR interface definition.
 *
 */
public interface DR extends Resource {
	public static final String typeURI = MSEManagerEx.baseMSEURI + "#DR";

	/** the URI for property isTagged */
	public static final String PROPERTY_ISTAGGED_URI = baseMSEURI + "#isTagged";
}
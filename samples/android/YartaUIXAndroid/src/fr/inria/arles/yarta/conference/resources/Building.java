package fr.inria.arles.yarta.conference.resources;

import fr.inria.arles.yarta.conference.msemanagement.MSEManagerEx;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.Resource;

/**
 * 
 * Building interface definition.
 *
 */
public interface Building extends Place, Resource {
	public static final String typeURI = MSEManagerEx.baseMSEURI + "#Building";

	/** the URI for property name */
	public static final String PROPERTY_NAME_URI = baseMSEURI + "#name";

	/** the URI for property longitude */
	public static final String PROPERTY_LONGITUDE_URI = baseMSEURI + "#longitude";

	/** the URI for property number */
	public static final String PROPERTY_NUMBER_URI = MSEManagerEx.baseMSEURI + "#number";

	/** the URI for property latitude */
	public static final String PROPERTY_LATITUDE_URI = baseMSEURI + "#latitude";

	/** the URI for property type */
	public static final String PROPERTY_TYPE_URI = MSEManagerEx.baseMSEURI + "#type";

	/** the URI for property isTagged */
	public static final String PROPERTY_ISTAGGED_URI = baseMSEURI + "#isTagged";

	/** the URI for property isLocated */
	public static final String PROPERTY_ISLOCATED_URI = baseMSEURI + "#isLocated";

	/**
	 * @return the number
	 */
	public Float getNumber();

	/**
	 * @param	number
	 * 			the number to set
	 */
	public void setNumber(Float number);

	/**
	 * @return the type
	 */
	public String getType();

	/**
	 * @param	type
	 * 			the type to set
	 */
	public void setType(String type);
}
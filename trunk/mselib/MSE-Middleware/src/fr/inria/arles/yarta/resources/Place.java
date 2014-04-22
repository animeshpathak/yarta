package fr.inria.arles.yarta.resources;

import java.util.Set;

/**
 * This class represents a generic notion of place/location. Several subclasses
 * are possible, depending on the application needs. Place can include other
 * places, recursively.
 */
public interface Place extends Resource {
	/** The unique "type" URI */
	public static final String typeURI = baseMSEURI + "#Place";

	/** the URI for the property latitude */
	public static final String PROPERTY_LATITUDE_URI = baseMSEURI + "#latitude";

	/** the URI for the property longitude */
	public static final String PROPERTY_LONGITUDE_URI = baseMSEURI
			+ "#longitude";

	/** the URI for the property longitude */
	public static final String PROPERTY_NAME_URI = baseMSEURI + "#name";

	/** the URI for the property longitude */
	public static final String PROPERTY_ISLOCATED_URI = baseMSEURI
			+ "#isLocated";

	/**
	 * @return the latitude
	 */
	public Float getLatitude();

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(Float latitude);

	/**
	 * @return the longitude
	 */
	public Float getLongitude();

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(Float longitude);

	/**
	 * @return the name
	 */
	public String getName();

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name);

	/**
	 * Creates a "isLocated" edge from this place to a place
	 * 
	 * @param p
	 *            the place
	 * @return true if all went well, false otherwise
	 */
	public boolean addIsLocated(Place p);

	/**
	 * 
	 * @return The list of places this place is located in. Empty list if there
	 *         are no such places. null if there was an error
	 */
	public Set<Place> getIsLocated();

	/**
	 * deletes the "isLocated" edge from this place to a place
	 * 
	 * @param p
	 *            the place
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteIsLocated(Place p);

	/**
	 * inverse of {@link Agent#addIsLocated(Place)},
	 * {@link Place#addIsLocated(Place)}, and {@link Event#addIsLocated(Place)}
	 * 
	 * @return The list of {@link Resource}s which are located at this place.
	 *         Empty list if there are none. null if there was an error
	 */
	public Set<Resource> getIsLocated_inverse();
}

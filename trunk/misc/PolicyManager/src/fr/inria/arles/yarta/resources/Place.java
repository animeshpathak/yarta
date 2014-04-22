/**
 * 
 */
package fr.inria.arles.yarta.resources;

/**
 * This class represents a generic notion of place/location. 
 * Several subclasses are possible, depending on the application 
 * needs. Place can include other places, recursively.
 * @author pathak
 *
 */
public class Place extends Resource {
	private static String baseMSEURI = 
		"http://yarta.gforge.inria.fr/ontologies/mse.rdf";

	/** The unique "type" URI*/
	public static final String typeURI = 
		"http://yarta.gforge.inria.fr/ontologies/mse.rdf#Place"; 
	
	/** the URI for the property latitude */
	public static final String PROPERTY_LATITUDE_URI =
		baseMSEURI + "#latitude";

	/** latitude: Expressed in degrees */
	private float latitude;

	/** the URI for the property longitude */
	public static final String PROPERTY_LONGITUDE_URI =
		baseMSEURI + "#longitude";

	/** longitude: Expressed in degrees */
	private float longitude;
	
	private String name;

	/**
	 * @return the latitude
	 */
	public float getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public float getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}

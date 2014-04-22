package fr.inria.arles.yarta.resources;

import java.util.Set;

import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.middleware.msemanagement.ThinStorageAccessManager;

/**
 * This class represents a generic notion of place/location. Several subclasses
 * are possible, depending on the application needs. Place can include other
 * places, recursively.
 * 
 * @author pathak
 * 
 */
public class PlaceImpl extends YartaResource implements Place {

	/**
	 * Wraps a given node into a PlaceImpl object
	 * 
	 * @param sam
	 *            The storage and access manager
	 * @param n
	 *            The node to wrap
	 */
	public PlaceImpl(ThinStorageAccessManager sam, Node n) {
		super(sam, n);
	}

	@Override
	public Float getLatitude() {
		return Float.valueOf(sam.getDataProperty(kbNode,
				PROPERTY_LATITUDE_URI, String.class));
	}

	@Override
	public void setLatitude(Float latitude) {
		sam.setDataProperty(kbNode, PROPERTY_LATITUDE_URI, String.class,
				String.valueOf(latitude));
	}

	@Override
	public Float getLongitude() {
		return Float.valueOf(sam.getDataProperty(kbNode,
				PROPERTY_LONGITUDE_URI, String.class));
	}

	@Override
	public void setLongitude(Float longitude) {
		sam.setDataProperty(kbNode, PROPERTY_LONGITUDE_URI, String.class,
				String.valueOf(longitude));
	}

	@Override
	public String getName() {
		return sam.getDataProperty(kbNode, PROPERTY_NAME_URI, String.class);
	}

	@Override
	public void setName(String placeName) {
		sam.setDataProperty(kbNode, PROPERTY_NAME_URI, String.class,
				placeName);
	}

	@Override
	public boolean addIsLocated(Place p) {
		return sam.setObjectProperty(kbNode, PROPERTY_ISLOCATED_URI, p);
	}

	@Override
	public Set<Place> getIsLocated() {
		return sam.getObjectProperty(kbNode, PROPERTY_ISLOCATED_URI);
	}

	@Override
	public boolean deleteIsLocated(Place p) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_ISLOCATED_URI, p);
	}

	@Override
	public boolean addIsTagged(Topic t) {
		return sam.setObjectProperty(kbNode, PROPERTY_ISTAGGED_URI, t);
	}

	@Override
	public Set<Topic> getIsTagged() {
		return sam.getObjectProperty(kbNode, PROPERTY_ISTAGGED_URI);
	}

	@Override
	public boolean deleteIsTagged(Topic t) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_ISTAGGED_URI, t);
	}

	@Override
	public Set<Agent> getHasInterest_inverse() {
		return sam.getObjectProperty_inverse(kbNode,
				Agent.PROPERTY_HASINTEREST_URI);
	}

	@Override
	public Set<Resource> getIsLocated_inverse() {
		return sam
				.getObjectProperty_inverse(kbNode, PROPERTY_ISLOCATED_URI);
	}
}

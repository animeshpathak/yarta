package fr.inria.arles.yarta.conference.resources;

import fr.inria.arles.yarta.middleware.msemanagement.ThinStorageAccessManager;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.yarta.resources.YartaResource;
import fr.inria.arles.yarta.resources.Agent;
import java.util.Set;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.resources.Topic;

/**
 * 
 * Building class implementation.
 * 
 */
public class BuildingImpl extends YartaResource implements Building {

	/**
	 * Wraps a given node into a BuildingImpl object
	 * 
	 * @param sam
	 *            The storage and access manager
	 * @param n
	 *            The node to wrap
	 */
	public BuildingImpl(ThinStorageAccessManager sam, Node n) {
		super(sam, n);
	}

	/**
	 * @return the name. Null if value is not set.
	 */
	public String getName() {
		return sam.getDataProperty(kbNode, PROPERTY_NAME_URI, String.class);
	}

	/**
	 * Sets the name.
	 * 
	 * @param string
	 *            the name to be set
	 */
	public void setName(String string) {
		sam.setDataProperty(kbNode, PROPERTY_NAME_URI, String.class, string);
	}

	/**
	 * @return the longitude. Null if value is not set.
	 */
	public Float getLongitude() {
		return Float.valueOf(sam.getDataProperty(kbNode,
				PROPERTY_LONGITUDE_URI, String.class));
	}

	/**
	 * Sets the longitude.
	 * 
	 * @param float the longitude to be set
	 */
	public void setLongitude(Float longitude) {
		sam.setDataProperty(kbNode, PROPERTY_LONGITUDE_URI, String.class,
				String.valueOf(longitude));
	}

	/**
	 * @return the number. Null if value is not set.
	 */
	public Float getNumber() {
		return Float.valueOf(sam.getDataProperty(kbNode, PROPERTY_NUMBER_URI,
				String.class));
	}

	/**
	 * Sets the number.
	 * 
	 * @param float the number to be set
	 */
	public void setNumber(Float number) {
		sam.setDataProperty(kbNode, PROPERTY_NUMBER_URI, String.class,
				String.valueOf(number));
	}

	/**
	 * @return the latitude. Null if value is not set.
	 */
	public Float getLatitude() {
		return Float.valueOf(sam.getDataProperty(kbNode, PROPERTY_LATITUDE_URI,
				String.class));
	}

	/**
	 * Sets the latitude.
	 * 
	 * @param float the latitude to be set
	 */
	public void setLatitude(Float latitude) {
		sam.setDataProperty(kbNode, PROPERTY_LATITUDE_URI, String.class,
				String.valueOf(latitude));
	}

	/**
	 * @return the type. Null if value is not set.
	 */
	public String getType() {
		return sam.getDataProperty(kbNode, PROPERTY_TYPE_URI, String.class);
	}

	/**
	 * Sets the type.
	 * 
	 * @param string
	 *            the type to be set
	 */
	public void setType(String string) {
		sam.setDataProperty(kbNode, PROPERTY_TYPE_URI, String.class, string);
	}

	/**
	 * Creates a "istagged" edge between this building and topic
	 * 
	 * @param topic
	 *            the Topic
	 * 
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addIsTagged(Topic topic) {
		return sam.setObjectProperty(kbNode, PROPERTY_ISTAGGED_URI, topic);
	}

	/**
	 * deletes the "istagged" link between this building and topic
	 * 
	 * @param topic
	 *            the Topic
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteIsTagged(Topic topic) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_ISTAGGED_URI, topic);
	}

	/**
	 * 
	 * @return The list of resources linked by a "istagged" edge with the
	 *         current resource. Empty list if I know no one. null if there was
	 *         an error
	 * 
	 */
	@Override
	public Set<Topic> getIsTagged() {
		return sam.getObjectProperty(kbNode, PROPERTY_ISTAGGED_URI);
	}

	/**
	 * Creates a "islocated" edge between this building and place
	 * 
	 * @param place
	 *            the Place
	 * 
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addIsLocated(Place place) {
		return sam.setObjectProperty(kbNode, PROPERTY_ISLOCATED_URI, place);
	}

	/**
	 * deletes the "islocated" link between this building and place
	 * 
	 * @param place
	 *            the Place
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteIsLocated(Place place) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_ISLOCATED_URI, place);
	}

	/**
	 * 
	 * @return The list of resources linked by a "islocated" edge with the
	 *         current resource. Empty list if I know no one. null if there was
	 *         an error
	 * 
	 */
	@Override
	public Set<Place> getIsLocated() {
		return sam.getObjectProperty(kbNode, PROPERTY_ISLOCATED_URI);
	}

	/**
	 * inverse of {@link #getHasInterest()}
	 */
	@Override
	public Set<Agent> getHasInterest_inverse() {
		return sam.getObjectProperty_inverse(kbNode,
				Agent.PROPERTY_HASINTEREST_URI);
	}

	/**
	 * inverse of {@link #getIsLocated()}
	 */
	@Override
	public Set<Resource> getIsLocated_inverse() {
		return sam.getObjectProperty_inverse(kbNode,
				Agent.PROPERTY_ISLOCATED_URI);
	}
}
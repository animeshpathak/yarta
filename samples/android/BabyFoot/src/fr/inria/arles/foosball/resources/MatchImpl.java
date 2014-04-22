package fr.inria.arles.foosball.resources;

import fr.inria.arles.yarta.middleware.msemanagement.ThinStorageAccessManager;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.YartaResource;
import fr.inria.arles.yarta.resources.Agent;
import java.util.Set;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.resources.Topic;

/**
 * 
 * Match class implementation.
 *
 */
public class MatchImpl extends YartaResource implements Match {

	/**
	 * Wraps a given node into a MatchImpl object
	 * 
	 * @param	sam
	 * 			The storage and access manager
	 * @param	n
	 * 			The node to wrap
	 */
	public MatchImpl(ThinStorageAccessManager sam, Node n) {
		super(sam, n);
	}

	/**
	 * @return the title. Null if value is not set.
	 */
	public String getTitle() {
		return sam.getDataProperty(kbNode, PROPERTY_TITLE_URI,
				String.class);
	}
	
	/**
	 * Sets the title.
	 * 
	 * @param	string
	 *			the title to be set
	 */
	public void setTitle(String string) {
		sam.setDataProperty(kbNode, PROPERTY_TITLE_URI, String.class,
				string);
	}

	/**
	 * @return the time. Null if value is not set.
	 */
	public Long getTime() {
		return Long.valueOf(sam.getDataProperty(kbNode, PROPERTY_TIME_URI,
				String.class));
	}

	/**
	 * Sets the time.
	 * 
	 * @param	long
	 *			the time to be set
	 */
	public void setTime(Long time) {
		sam.setDataProperty(kbNode, PROPERTY_TIME_URI, String.class,
				String.valueOf(time));
	}

	/**
	 * @return the blueScore. Null if value is not set.
	 */
	public Integer getBlueScore() {
		return Integer.valueOf(sam.getDataProperty(kbNode, PROPERTY_BLUESCORE_URI,
				String.class));
	}

	/**
	 * Sets the blueScore.
	 * 
	 * @param	int
	 *			the blueScore to be set
	 */
	public void setBlueScore(Integer bluescore) {
		sam.setDataProperty(kbNode, PROPERTY_BLUESCORE_URI, String.class,
				String.valueOf(bluescore));
	}

	/**
	 * @return the description. Null if value is not set.
	 */
	public String getDescription() {
		return sam.getDataProperty(kbNode, PROPERTY_DESCRIPTION_URI,
				String.class);
	}
	
	/**
	 * Sets the description.
	 * 
	 * @param	string
	 *			the description to be set
	 */
	public void setDescription(String string) {
		sam.setDataProperty(kbNode, PROPERTY_DESCRIPTION_URI, String.class,
				string);
	}

	/**
	 * @return the redScore. Null if value is not set.
	 */
	public Integer getRedScore() {
		return Integer.valueOf(sam.getDataProperty(kbNode, PROPERTY_REDSCORE_URI,
				String.class));
	}

	/**
	 * Sets the redScore.
	 * 
	 * @param	int
	 *			the redScore to be set
	 */
	public void setRedScore(Integer redscore) {
		sam.setDataProperty(kbNode, PROPERTY_REDSCORE_URI, String.class,
				String.valueOf(redscore));
	}

	/**
	 * Creates a "istagged" edge between this match and topic
	 * 
	 * @param	topic
	 *			the Topic
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addIsTagged(Topic topic) {
		return sam.setObjectProperty(kbNode, PROPERTY_ISTAGGED_URI, topic);
	}

	/**
	 * deletes the "istagged" link between this match and topic
	 * 
	 * @param	topic
	 * 			the Topic
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteIsTagged(Topic topic) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_ISTAGGED_URI, topic);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "istagged" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Topic> getIsTagged() {
		return sam.getObjectProperty(kbNode, PROPERTY_ISTAGGED_URI);
	}

	/**
	 * Creates a "blueo" edge between this match and person
	 * 
	 * @param	person
	 *			the Person
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addBlueO(Person person) {
		return sam.setObjectProperty(kbNode, PROPERTY_BLUEO_URI, person);
	}

	/**
	 * deletes the "blueo" link between this match and person
	 * 
	 * @param	person
	 * 			the Person
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteBlueO(Person person) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_BLUEO_URI, person);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "blueo" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Person> getBlueO() {
		return sam.getObjectProperty(kbNode, PROPERTY_BLUEO_URI);
	}

	/**
	 * Creates a "redo" edge between this match and person
	 * 
	 * @param	person
	 *			the Person
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addRedO(Person person) {
		return sam.setObjectProperty(kbNode, PROPERTY_REDO_URI, person);
	}

	/**
	 * deletes the "redo" link between this match and person
	 * 
	 * @param	person
	 * 			the Person
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteRedO(Person person) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_REDO_URI, person);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "redo" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Person> getRedO() {
		return sam.getObjectProperty(kbNode, PROPERTY_REDO_URI);
	}

	/**
	 * Creates a "redd" edge between this match and person
	 * 
	 * @param	person
	 *			the Person
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addRedD(Person person) {
		return sam.setObjectProperty(kbNode, PROPERTY_REDD_URI, person);
	}

	/**
	 * deletes the "redd" link between this match and person
	 * 
	 * @param	person
	 * 			the Person
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteRedD(Person person) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_REDD_URI, person);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "redd" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Person> getRedD() {
		return sam.getObjectProperty(kbNode, PROPERTY_REDD_URI);
	}

	/**
	 * Creates a "blued" edge between this match and person
	 * 
	 * @param	person
	 *			the Person
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addBlueD(Person person) {
		return sam.setObjectProperty(kbNode, PROPERTY_BLUED_URI, person);
	}

	/**
	 * deletes the "blued" link between this match and person
	 * 
	 * @param	person
	 * 			the Person
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteBlueD(Person person) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_BLUED_URI, person);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "blued" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Person> getBlueD() {
		return sam.getObjectProperty(kbNode, PROPERTY_BLUED_URI);
	}

	/**
	 * Creates a "islocated" edge between this match and place
	 * 
	 * @param	place
	 *			the Place
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addIsLocated(Place place) {
		return sam.setObjectProperty(kbNode, PROPERTY_ISLOCATED_URI, place);
	}

	/**
	 * deletes the "islocated" link between this match and place
	 * 
	 * @param	place
	 * 			the Place
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteIsLocated(Place place) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_ISLOCATED_URI, place);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "islocated" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Place> getIsLocated() {
		return sam.getObjectProperty(kbNode, PROPERTY_ISLOCATED_URI);
	}

	/**
	 * inverse of {@link #getIsAttending()}
	 */
	@Override
	public Set<Agent> getIsAttending_inverse() {
		return sam.getObjectProperty_inverse(kbNode, Agent.PROPERTY_ISATTENDING_URI);
	}

	/**
	 * inverse of {@link #getHasInterest()}
	 */
	@Override
	public Set<Agent> getHasInterest_inverse() {
		return sam.getObjectProperty_inverse(kbNode, Agent.PROPERTY_HASINTEREST_URI);
	}
}
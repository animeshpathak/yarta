package fr.inria.arles.yarta.conference.resources;

import fr.inria.arles.yarta.middleware.msemanagement.ThinStorageAccessManager;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.YartaResource;
import fr.inria.arles.yarta.resources.Agent;
import java.util.Set;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.resources.Topic;

/**
 * 
 * Presentation class implementation.
 *
 */
public class PresentationImpl extends YartaResource implements Presentation {

	/**
	 * Wraps a given node into a PresentationImpl object
	 * 
	 * @param	sam
	 * 			The storage and access manager
	 * @param	n
	 * 			The node to wrap
	 */
	public PresentationImpl(ThinStorageAccessManager sam, Node n) {
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
	 * Creates a "istagged" edge between this presentation and topic
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
	 * deletes the "istagged" link between this presentation and topic
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
	 * Creates a "follows" edge between this presentation and event
	 * 
	 * @param	event
	 *			the Event
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addFollows(Event event) {
		return sam.setObjectProperty(kbNode, PROPERTY_FOLLOWS_URI, event);
	}

	/**
	 * deletes the "follows" link between this presentation and event
	 * 
	 * @param	event
	 * 			the Event
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteFollows(Event event) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_FOLLOWS_URI, event);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "follows" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Event> getFollows() {
		return sam.getObjectProperty(kbNode, PROPERTY_FOLLOWS_URI);
	}

	/**
	 * Creates a "contains" edge between this presentation and topic
	 * 
	 * @param	topic
	 *			the Topic
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addContains(Topic topic) {
		return sam.setObjectProperty(kbNode, PROPERTY_CONTAINS_URI, topic);
	}

	/**
	 * deletes the "contains" link between this presentation and topic
	 * 
	 * @param	topic
	 * 			the Topic
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteContains(Topic topic) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_CONTAINS_URI, topic);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "contains" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Topic> getContains() {
		return sam.getObjectProperty(kbNode, PROPERTY_CONTAINS_URI);
	}

	/**
	 * Creates a "islocated" edge between this presentation and place
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
	 * deletes the "islocated" link between this presentation and place
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
	 * Creates a "isincluded" edge between this presentation and conference
	 * 
	 * @param	conference
	 *			the Conference
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addIsIncluded(Conference conference) {
		return sam.setObjectProperty(kbNode, PROPERTY_ISINCLUDED_URI, conference);
	}

	/**
	 * deletes the "isincluded" link between this presentation and conference
	 * 
	 * @param	conference
	 * 			the Conference
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteIsIncluded(Conference conference) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_ISINCLUDED_URI, conference);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "isincluded" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Conference> getIsIncluded() {
		return sam.getObjectProperty(kbNode, PROPERTY_ISINCLUDED_URI);
	}

	/**
	 * inverse of {@link #getFollows()}
	 */
	@Override
	public Set<Event> getFollows_inverse() {
		return sam.getObjectProperty_inverse(kbNode, Event.PROPERTY_FOLLOWS_URI);
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
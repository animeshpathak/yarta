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
 * Conference class implementation.
 *
 */
public class ConferenceImpl extends YartaResource implements Conference {

	/**
	 * Wraps a given node into a ConferenceImpl object
	 * 
	 * @param	sam
	 * 			The storage and access manager
	 * @param	n
	 * 			The node to wrap
	 */
	public ConferenceImpl(ThinStorageAccessManager sam, Node n) {
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
	 * Creates a "istagged" edge between this conference and topic
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
	 * deletes the "istagged" link between this conference and topic
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
	 * Creates a "follows" edge between this conference and event
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
	 * deletes the "follows" link between this conference and event
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
	 * Creates a "islocated" edge between this conference and place
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
	 * deletes the "islocated" link between this conference and place
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

	/**
	 * inverse of {@link #getIsIncluded()}
	 */
	@Override
	public Set<Presentation> getIsIncluded_inverse() {
		return sam.getObjectProperty_inverse(kbNode, Presentation.PROPERTY_ISINCLUDED_URI);
	}
}
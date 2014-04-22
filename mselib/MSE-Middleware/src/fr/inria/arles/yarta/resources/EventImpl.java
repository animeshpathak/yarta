package fr.inria.arles.yarta.resources;

import java.util.Set;

import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.middleware.msemanagement.ThinStorageAccessManager;

/**
 * <p>
 * The notion of event is twofold. From a theoretical perspective, events
 * represent 4D occurrences, i.e., occurrences that happen both in space and
 * time. Differently from 3D occurrences (e.g., Person) events exist in time and
 * do not change, at least as a whole, over time. From a social application
 * perspective, events represent entities happening in time having some kind of
 * social meaning/impact. Social events, such as meetings, parties or talks, are
 * common examples of events, although they do not cover all the expressivity of
 * this class. For example, a location-based social application might model the
 * entering of a person in a room as an Event. Specialized subclasses are
 * therefore needed for each application.
 */
public class EventImpl extends YartaResource implements Event {

	/**
	 * Wraps a given node into a EventImpl object
	 * 
	 * @param sam
	 *            The storage and access manager
	 * @param n
	 *            The node to wrap
	 */
	public EventImpl(ThinStorageAccessManager sam, Node n) {
		super(sam, n);
	}

	@Override
	public String getDescription() {
		return sam.getDataProperty(kbNode, PROPERTY_DESCRIPTION_URI,
				String.class);
	}

	@Override
	public void setDescription(String description) {
		sam.setDataProperty(kbNode, PROPERTY_DESCRIPTION_URI, String.class,
				description);
	}

	@Override
	public String getTitle() {
		return sam
				.getDataProperty(kbNode, PROPERTY_TITLE_URI, String.class);
	}

	@Override
	public void setTitle(String eventTitle) {
		sam.setDataProperty(kbNode, PROPERTY_TITLE_URI, String.class,
				eventTitle);
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
	public Set<Agent> getHasInterest_inverse() {
		return sam.getObjectProperty_inverse(kbNode,
				Agent.PROPERTY_HASINTEREST_URI);
	}

	@Override
	public Set<Agent> getIsAttending_inverse() {
		return sam.getObjectProperty_inverse(kbNode,
				Agent.PROPERTY_ISATTENDING_URI);
	}
}

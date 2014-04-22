package fr.inria.arles.yarta.resources;

import java.util.Set;

import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.middleware.msemanagement.ThinStorageAccessManager;

/**
 * <p>
 * An event consisting of two or more single events, which may take place in
 * parallel or in sequence.
 * </p>
 * <p>
 * Composite events might be handled, for example, by means of RDF Containers
 * (Bag, Alt and Seq). It has to be verified if they are suitable for data
 * processing purposes. The property used to state that one SingleEvent belongs
 * to a CompositeEvent is the rdfs:member property.
 * </p>
 */
public class CompositeEventImpl extends YartaResource implements CompositeEvent {

	/**
	 * Wraps a given node into a CompositeEventImpl object
	 * 
	 * @param sam
	 *            The storage and access manager
	 * @param n
	 *            The node to wrap
	 */
	public CompositeEventImpl(ThinStorageAccessManager sam, Node n) {
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
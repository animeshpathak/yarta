package fr.inria.arles.yarta.resources;

import java.util.Set;

import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.middleware.msemanagement.ThinStorageAccessManager;

/**
 * <p>
 * A group is a set of people. Both groups and single persons have the same
 * first-class properties linking them to other first-class entities: therefore,
 * they are modeled as subclass of the same Agent class. A person is linked to a
 * group by the rdfs:member property. We reuse this existing property, whether
 * another more specific property is needed has to be seen.
 * </p>
 */
public class GroupImpl extends YartaResource implements Group {

	/**
	 * Dummy constructor. To help in Extending the Class.
	 */
	public GroupImpl() {
		// do nothing.
	}

	/**
	 * Wraps a given node into a GroupImpl object
	 * 
	 * @param sam
	 *            The storage and access manager
	 * @param n
	 *            The node to wrap
	 */
	public GroupImpl(ThinStorageAccessManager sam, Node n) {
		super(sam, n);
	}

	@Override
	public String getEmail() {
		return sam.getDataProperty(kbNode, PROPERTY_EMAIL_URI, String.class);
	}

	@Override
	public void setEmail(String email) {
		sam.setDataProperty(kbNode, PROPERTY_EMAIL_URI, String.class, email);
	}

	@Override
	public String getHomepage() {
		return sam.getDataProperty(kbNode, PROPERTY_HOMEPAGE_URI, String.class);
	}

	@Override
	public void setHomepage(String homepage) {
		sam.setDataProperty(kbNode, PROPERTY_HOMEPAGE_URI, String.class,
				homepage);
	}

	@Override
	public String getName() {
		return sam.getDataProperty(kbNode, PROPERTY_NAME_URI, String.class);
	}

	@Override
	public void setName(String agentName) {
		sam.setDataProperty(kbNode, PROPERTY_NAME_URI, String.class, agentName);
	}

	@Override
	public boolean addKnows(Agent a) {
		return sam.setObjectProperty(kbNode, PROPERTY_KNOWS_URI, a);
	}

	@Override
	public Set<Agent> getKnows() {
		return sam.getObjectProperty(kbNode, PROPERTY_KNOWS_URI);
	}

	@Override
	public Set<Agent> getKnows_inverse() {
		return sam.getObjectProperty_inverse(kbNode, PROPERTY_KNOWS_URI);
	}

	@Override
	public boolean deleteKnows(Agent a) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_KNOWS_URI, a);
	}

	@Override
	public boolean addIsMemberOf(Group g) {
		return sam.setObjectProperty(kbNode, Agent.PROPERTY_ISMEMBEROF_URI, g);
	}

	@Override
	public Set<Group> getIsMemberOf() {
		return sam.getObjectProperty(kbNode, Agent.PROPERTY_ISMEMBEROF_URI);
	}

	@Override
	public boolean deleteIsMemberOf(Group g) {
		return sam.deleteObjectProperty(kbNode, Agent.PROPERTY_ISMEMBEROF_URI,
				g);
	}

	@Override
	public boolean addIsAttending(Event e) {
		return sam.setObjectProperty(kbNode, PROPERTY_ISATTENDING_URI, e);
	}

	@Override
	public Set<Event> getIsAttending() {
		return sam.getObjectProperty(kbNode, PROPERTY_ISATTENDING_URI);
	}

	@Override
	public boolean deleteIsAttending(Event e) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_ISATTENDING_URI, e);
	}

	@Override
	public boolean addCreator(Content e) {
		return sam.setObjectProperty(kbNode, PROPERTY_CREATOR_URI, e);
	}

	@Override
	public Set<Content> getCreator() {
		return sam.getObjectProperty(kbNode, PROPERTY_CREATOR_URI);
	}

	@Override
	public boolean deleteCreator(Content c) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_CREATOR_URI, c);
	}

	@Override
	public boolean addHasInterest(Resource r) {
		return sam.setObjectProperty(kbNode, PROPERTY_HASINTEREST_URI, r);
	}

	@Override
	public Set<Resource> getHasInterest() {
		return sam.getObjectProperty(kbNode, PROPERTY_HASINTEREST_URI);
	}

	@Override
	public boolean deleteHasInterest(Resource r) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_HASINTEREST_URI, r);
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
	public Set<Agent> getIsMemberOf_inverse() {
		return sam.getObjectProperty_inverse(kbNode, PROPERTY_ISMEMBEROF_URI);
	}

	@Override
	public Set<Agent> getHasInterest_inverse() {
		return sam.getObjectProperty_inverse(kbNode,
				Agent.PROPERTY_HASINTEREST_URI);
	}

	@Override
	public boolean addParticipatesTo(Conversation c) {
		return sam.setObjectProperty(kbNode, PROPERTY_PARTICIPATESTO_URI, c);
	}

	@Override
	public Set<Conversation> getParticipatesTo() {
		return sam.getObjectProperty(kbNode, PROPERTY_PARTICIPATESTO_URI);
	}

	@Override
	public boolean deleteParticipatesTo(Conversation c) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_PARTICIPATESTO_URI, c);
	}

	@Override
	public boolean addHasContent(Content c) {
		return sam.setObjectProperty(kbNode, PROPERTY_HASCONTENT_URI, c);
	}

	@Override
	public Set<Content> getHasContent() {
		return sam.getObjectProperty(kbNode, PROPERTY_HASCONTENT_URI);
	}

	@Override
	public boolean deleteHasContent(Content c) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_HASCONTENT_URI, c);
	}
}

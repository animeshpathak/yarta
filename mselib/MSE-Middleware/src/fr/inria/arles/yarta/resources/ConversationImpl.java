package fr.inria.arles.yarta.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.middleware.msemanagement.ThinStorageAccessManager;

/**
 * Conversation implementation.
 * 
 */
public class ConversationImpl extends YartaResource implements Conversation {

	/**
	 * Wraps a given node into a Conversation implementation object
	 * 
	 * @param sam
	 *            The storage and access manager
	 * @param n
	 *            The node to wrap
	 */
	public ConversationImpl(ThinStorageAccessManager sam, Node n) {
		super(sam, n);
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
	public Set<Agent> getParticipatesTo_inverse() {
		return sam.getObjectProperty_inverse(kbNode,
				Agent.PROPERTY_PARTICIPATESTO_URI);
	}

	@Override
	public boolean addContains(Message message) {
		return sam.setObjectProperty(kbNode, PROPERTY_CONTAINS_URI, message);
	}

	@Override
	public boolean deleteContains(Message message) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_CONTAINS_URI, message);
	}

	@Override
	public Set<Message> getContains() {
		return sam.getObjectProperty(kbNode, PROPERTY_CONTAINS_URI);
	}
}

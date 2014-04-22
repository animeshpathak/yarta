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
	public boolean addMessage(Message message) {
		return sam.setObjectProperty(kbNode, "http://www.w3.org/1999/02/22-rdf-syntax-ns#li", message);
	}

	@Override
	public boolean deleteMessage(Message message) {
		return sam.deleteObjectProperty(kbNode, "http://www.w3.org/1999/02/22-rdf-syntax-ns#li", message);
	}

	@Override
	public List<Message> getMessages() {
		Set<Message> ms = sam.getObjectProperty(kbNode, "http://www.w3.org/1999/02/22-rdf-syntax-ns#li");
		List<Message> messages = new ArrayList<Message>();
		for (Object message : ms) {
			messages.add((Message) message);
		}
		return messages;
	}
}

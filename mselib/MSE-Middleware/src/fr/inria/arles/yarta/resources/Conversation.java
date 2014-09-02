package fr.inria.arles.yarta.resources;

import java.util.List;
import java.util.Set;

/**
 * Conversation interface.
 */
public interface Conversation extends Resource {

	/** The unique "type" URI */
	public static final String typeURI = baseMSEURI + "#Conversation";

	/** the URI for the property participatesTo */
	public static final String PROPERTY_CONTAINS_URI = baseMSEURI + "#contains";

	/**
	 * Returns the set of agents who participates to this conversation.
	 * 
	 * @return
	 */
	public Set<Agent> getParticipatesTo_inverse();

	/**
	 * Adds a message to this conversation. The creator of the message must be a
	 * participant of this conversation.
	 * 
	 * @param message
	 * @return
	 */
	public boolean addContains(Message message);

	/**
	 * Deletes a message from this conversation.
	 * 
	 * @param message
	 * @return
	 */
	public boolean deleteContains(Message message);

	/**
	 * Returns a list of messages associated with this Conversation.
	 * 
	 * @return
	 */
	public Set<Message> getContains();
}

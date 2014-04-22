package fr.inria.arles.yarta.resources;

import java.util.Set;

/**
 * The most general class for agentive entities. This is the class that
 * traditionally includes groups and single person. See also FoaF and
 * DOLCE-UltraLite.
 */
public interface Agent extends Resource {

	static final long serialVersionUID = 7905617684389242568L;

	/** The unique "type" URI */
	public static final String typeURI = baseMSEURI + "#Agent";

	/** the URI for the property email */
	public static final String PROPERTY_EMAIL_URI = baseMSEURI + "#email";

	/** the URI for the property homepage */
	public static final String PROPERTY_HOMEPAGE_URI = baseMSEURI + "#homepage";

	/** the URI for the property name */
	public static final String PROPERTY_NAME_URI = baseMSEURI + "#name";

	/** the URI for the property knows */
	public static final String PROPERTY_KNOWS_URI = baseMSEURI + "#knows";

	/** the URI for the property knows */
	public static final String PROPERTY_ISMEMBEROF_URI = baseMSEURI
			+ "#isMemberOf";

	/** the URI for the property isAttending */
	public static final String PROPERTY_ISATTENDING_URI = baseMSEURI
			+ "#isAttending";

	/** the URI for the property participatesTo */
	public static final String PROPERTY_PARTICIPATESTO_URI = baseMSEURI
			+ "#participatesTo";

	/** the URI for the property creator */
	public static final String PROPERTY_CREATOR_URI = baseMSEURI + "#creator";

	/** the URI for the property hasInterest */
	public static final String PROPERTY_HASINTEREST_URI = baseMSEURI
			+ "#hasInterest";

	/** the URI for the property isLocated */
	public static final String PROPERTY_ISLOCATED_URI = baseMSEURI
			+ "#isLocated";

	/**
	 * @return the email
	 */
	public String getEmail();

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email);

	/**
	 * @return the homepage
	 */
	public String getHomepage();

	/**
	 * @param homepage
	 *            the homepage to set
	 */
	public void setHomepage(String homepage);

	/**
	 * @return the name
	 */
	public String getName();

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String agentName);

	// // Resource properties //

	/**
	 * Creates a "knows" edge between this agent and another
	 * 
	 * @param a
	 *            The other agent
	 * @return true if all went well, false otherwise
	 */
	public boolean addKnows(Agent a);

	/**
	 * 
	 * @return The list of agents known. Empty list if I know no one. null if
	 *         there was an error
	 */
	public Set<Agent> getKnows();

	/**
	 * inverse of {@link #getKnows()}
	 * 
	 * @return The list of agents who "know" this one. Empty list if I there are
	 *         none. null if there was an error
	 */
	public Set<Agent> getKnows_inverse();

	/**
	 * deletes the "knows" link between this agent and another
	 * 
	 * @param a
	 *            The other agent
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteKnows(Agent a);

	/**
	 * Creates a "is member of" edge between this agent and a group
	 * 
	 * @param g
	 *            The group this agent is a member of
	 * @return true if all went well, false otherwise
	 */
	public boolean addIsMemberOf(Group g);

	/**
	 * 
	 * @return The list of groups this agent is a member of. Empty list if there
	 *         are no such groups. null if there was an error
	 */
	public Set<Group> getIsMemberOf();

	/**
	 * deletes the "is member of " link between this agent and a group
	 * 
	 * @param g
	 *            the group
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteIsMemberOf(Group g);

	/**
	 * Creates a "isAttending" edge between this agent and an event
	 * 
	 * @param e
	 *            the event this agent participates to
	 * @return true if all went well, false otherwise
	 */
	public boolean addIsAttending(Event e);

	/**
	 * 
	 * @return The list of events this agent participates to. Empty list if
	 *         there are no such events. null if there was an error
	 */
	public Set<Event> getIsAttending();

	/**
	 * deletes the "is attending" link between this agent and an event
	 * 
	 * @param e
	 *            the event
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteIsAttending(Event e);

	/**
	 * Creates a "creator" edge from this agent to a content
	 * 
	 * @param c
	 *            the content
	 * @return true if all went well, false otherwise
	 */
	public boolean addCreator(Content c);

	/**
	 * 
	 * @return The list of content this agent is a creator of. Empty list if
	 *         there are no such contents. null if there was an error
	 */
	public Set<Content> getCreator();

	/**
	 * deletes the "creator" edge from this agent to a content
	 * 
	 * @param c
	 *            the content
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteCreator(Content c);

	/**
	 * Creates a "hasInterest" edge from this agent to a resource
	 * 
	 * @param r
	 *            the resource
	 * @return true if all went well, false otherwise
	 */
	public boolean addHasInterest(Resource r);

	/**
	 * 
	 * @return The list of resources this agent has interest in. Empty list if
	 *         there are no such resources. null if there was an error
	 */
	public Set<Resource> getHasInterest();

	/**
	 * deletes the "hasInterest" edge from this agent to a resource
	 * 
	 * @param r
	 *            the resource
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteHasInterest(Resource r);

	/**
	 * Creates a "isLocated" edge from this agent to a place
	 * 
	 * @param p
	 *            the place
	 * @return true if all went well, false otherwise
	 */
	public boolean addIsLocated(Place p);

	/**
	 * 
	 * @return The list of places this agent is located in. Empty list if there
	 *         are no such places. null if there was an error
	 */
	public Set<Place> getIsLocated();

	/**
	 * deletes the "isLocated" edge from this agent to a place
	 * 
	 * @param p
	 *            the place
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteIsLocated(Place p);

	/**
	 * Creates a "participatesTo" edge between this agent and a conversation.
	 * 
	 * @param c
	 *            the conversation this agent participates to
	 * @return true if all went well, false otherwise
	 */
	public boolean addParticipatesTo(Conversation c);

	/**
	 * 
	 * @return The list of conversation this agent participates to. Empty list
	 *         if there are no such conversations. null if there was an error.
	 */
	public Set<Conversation> getParticipatesTo();

	/**
	 * deletes the "participates to" link between this agent and a conversation
	 * 
	 * @param c
	 *            the conversation
	 * @return true if success. false if something went wrong
	 */
	public boolean deleteParticipatesTo(Conversation c);
}

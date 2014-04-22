package fr.inria.arles.yarta.conference.resources;

import fr.inria.arles.yarta.middleware.msemanagement.ThinStorageAccessManager;
import fr.inria.arles.yarta.resources.Conversation;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.YartaResource;
import fr.inria.arles.yarta.resources.Agent;
import java.util.Set;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Group;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.resources.Topic;

/**
 * 
 * Mutant class implementation.
 *
 */
public class MutantImpl extends YartaResource implements Mutant {

	/**
	 * Wraps a given node into a MutantImpl object
	 * 
	 * @param	sam
	 * 			The storage and access manager
	 * @param	n
	 * 			The node to wrap
	 */
	public MutantImpl(ThinStorageAccessManager sam, Node n) {
		super(sam, n);
	}

	/**
	 * Constructor to store a node and its unique Id. Use this normally to
	 * create a new node
	 * 
	 * @param	sam
	 *			The storage and access manager
	 * @param	uniqueRequestorId
	 *			application unique identifier
	 *
	 * @throws	KBException
	 */
	public MutantImpl(ThinStorageAccessManager sam, String uniqueRequestorId) {
		super(sam, sam.createNewNode(Mutant.typeURI));
		this.setUserId(uniqueRequestorId);
	}

	/**
	 * @return the lastName. Null if value is not set.
	 */
	public String getLastName() {
		return sam.getDataProperty(kbNode, PROPERTY_LASTNAME_URI,
				String.class);
	}
	
	/**
	 * Sets the lastName.
	 * 
	 * @param	string
	 *			the lastName to be set
	 */
	public void setLastName(String string) {
		sam.setDataProperty(kbNode, PROPERTY_LASTNAME_URI, String.class,
				string);
	}

	/**
	 * @return the format. Null if value is not set.
	 */
	public String getFormat() {
		return sam.getDataProperty(kbNode, PROPERTY_FORMAT_URI,
				String.class);
	}
	
	/**
	 * Sets the format.
	 * 
	 * @param	string
	 *			the format to be set
	 */
	public void setFormat(String string) {
		sam.setDataProperty(kbNode, PROPERTY_FORMAT_URI, String.class,
				string);
	}

	/**
	 * @return the homepage. Null if value is not set.
	 */
	public String getHomepage() {
		return sam.getDataProperty(kbNode, PROPERTY_HOMEPAGE_URI,
				String.class);
	}
	
	/**
	 * Sets the homepage.
	 * 
	 * @param	string
	 *			the homepage to be set
	 */
	public void setHomepage(String string) {
		sam.setDataProperty(kbNode, PROPERTY_HOMEPAGE_URI, String.class,
				string);
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
	 * @return the source. Null if value is not set.
	 */
	public String getSource() {
		return sam.getDataProperty(kbNode, PROPERTY_SOURCE_URI,
				String.class);
	}
	
	/**
	 * Sets the source.
	 * 
	 * @param	string
	 *			the source to be set
	 */
	public void setSource(String string) {
		sam.setDataProperty(kbNode, PROPERTY_SOURCE_URI, String.class,
				string);
	}

	/**
	 * @return the email. Null if value is not set.
	 */
	public String getEmail() {
		return sam.getDataProperty(kbNode, PROPERTY_EMAIL_URI,
				String.class);
	}
	
	/**
	 * Sets the email.
	 * 
	 * @param	string
	 *			the email to be set
	 */
	public void setEmail(String string) {
		sam.setDataProperty(kbNode, PROPERTY_EMAIL_URI, String.class,
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
	 * @return the name. Null if value is not set.
	 */
	public String getName() {
		return sam.getDataProperty(kbNode, PROPERTY_NAME_URI,
				String.class);
	}
	
	/**
	 * Sets the name.
	 * 
	 * @param	string
	 *			the name to be set
	 */
	public void setName(String string) {
		sam.setDataProperty(kbNode, PROPERTY_NAME_URI, String.class,
				string);
	}

	/**
	 * @return the userId. Null if value is not set.
	 */
	public String getUserId() {
		return sam.getDataProperty(kbNode, PROPERTY_USERID_URI,
				String.class);
	}
	
	/**
	 * Sets the userId.
	 * 
	 * @param	string
	 *			the userId to be set
	 */
	public void setUserId(String string) {
		sam.setDataProperty(kbNode, PROPERTY_USERID_URI, String.class,
				string);
	}

	/**
	 * @return the gender. Null if value is not set.
	 */
	public String getGender() {
		return sam.getDataProperty(kbNode, PROPERTY_GENDER_URI,
				String.class);
	}
	
	/**
	 * Sets the gender.
	 * 
	 * @param	string
	 *			the gender to be set
	 */
	public void setGender(String string) {
		sam.setDataProperty(kbNode, PROPERTY_GENDER_URI, String.class,
				string);
	}

	/**
	 * @return the longitude. Null if value is not set.
	 */
	public Float getLongitude() {
		return Float.valueOf(sam.getDataProperty(kbNode, PROPERTY_LONGITUDE_URI,
				String.class));
	}

	/**
	 * Sets the longitude.
	 * 
	 * @param	float
	 *			the longitude to be set
	 */
	public void setLongitude(Float longitude) {
		sam.setDataProperty(kbNode, PROPERTY_LONGITUDE_URI, String.class,
				String.valueOf(longitude));
	}

	/**
	 * @return the latitude. Null if value is not set.
	 */
	public Float getLatitude() {
		return Float.valueOf(sam.getDataProperty(kbNode, PROPERTY_LATITUDE_URI,
				String.class));
	}

	/**
	 * Sets the latitude.
	 * 
	 * @param	float
	 *			the latitude to be set
	 */
	public void setLatitude(Float latitude) {
		sam.setDataProperty(kbNode, PROPERTY_LATITUDE_URI, String.class,
				String.valueOf(latitude));
	}

	/**
	 * @return the firstName. Null if value is not set.
	 */
	public String getFirstName() {
		return sam.getDataProperty(kbNode, PROPERTY_FIRSTNAME_URI,
				String.class);
	}
	
	/**
	 * Sets the firstName.
	 * 
	 * @param	string
	 *			the firstName to be set
	 */
	public void setFirstName(String string) {
		sam.setDataProperty(kbNode, PROPERTY_FIRSTNAME_URI, String.class,
				string);
	}

	/**
	 * @return the identifier. Null if value is not set.
	 */
	public String getIdentifier() {
		return sam.getDataProperty(kbNode, PROPERTY_IDENTIFIER_URI,
				String.class);
	}
	
	/**
	 * Sets the identifier.
	 * 
	 * @param	string
	 *			the identifier to be set
	 */
	public void setIdentifier(String string) {
		sam.setDataProperty(kbNode, PROPERTY_IDENTIFIER_URI, String.class,
				string);
	}

	/**
	 * Creates a "knows" edge between this mutant and agent
	 * 
	 * @param	agent
	 *			the Agent
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addKnows(Agent agent) {
		return sam.setObjectProperty(kbNode, PROPERTY_KNOWS_URI, agent);
	}

	/**
	 * deletes the "knows" link between this mutant and agent
	 * 
	 * @param	agent
	 * 			the Agent
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteKnows(Agent agent) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_KNOWS_URI, agent);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "knows" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Agent> getKnows() {
		return sam.getObjectProperty(kbNode, PROPERTY_KNOWS_URI);
	}

	/**
	 * Creates a "istagged" edge between this mutant and topic
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
	 * deletes the "istagged" link between this mutant and topic
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
	 * Creates a "follows" edge between this mutant and event
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
	 * deletes the "follows" link between this mutant and event
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
	 * Creates a "isattending" edge between this mutant and event
	 * 
	 * @param	event
	 *			the fr.inria.arles.yarta.resources.Event
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addIsAttending(fr.inria.arles.yarta.resources.Event event) {
		return sam.setObjectProperty(kbNode, PROPERTY_ISATTENDING_URI, event);
	}

	/**
	 * deletes the "isattending" link between this mutant and event
	 * 
	 * @param	event
	 * 			the fr.inria.arles.yarta.resources.Event
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteIsAttending(fr.inria.arles.yarta.resources.Event event) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_ISATTENDING_URI, event);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "isattending" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<fr.inria.arles.yarta.resources.Event> getIsAttending() {
		return sam.getObjectProperty(kbNode, PROPERTY_ISATTENDING_URI);
	}

	/**
	 * Creates a "hasinterest" edge between this mutant and resource
	 * 
	 * @param	resource
	 *			the Resource
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addHasInterest(Resource resource) {
		return sam.setObjectProperty(kbNode, PROPERTY_HASINTEREST_URI, resource);
	}

	/**
	 * deletes the "hasinterest" link between this mutant and resource
	 * 
	 * @param	resource
	 * 			the Resource
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteHasInterest(Resource resource) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_HASINTEREST_URI, resource);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "hasinterest" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Resource> getHasInterest() {
		return sam.getObjectProperty(kbNode, PROPERTY_HASINTEREST_URI);
	}

	/**
	 * Creates a "ismemberof" edge between this mutant and group
	 * 
	 * @param	group
	 *			the Group
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addIsMemberOf(Group group) {
		return sam.setObjectProperty(kbNode, PROPERTY_ISMEMBEROF_URI, group);
	}

	/**
	 * deletes the "ismemberof" link between this mutant and group
	 * 
	 * @param	group
	 * 			the Group
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteIsMemberOf(Group group) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_ISMEMBEROF_URI, group);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "ismemberof" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Group> getIsMemberOf() {
		return sam.getObjectProperty(kbNode, PROPERTY_ISMEMBEROF_URI);
	}

	/**
	 * Creates a "participatesto" edge between this mutant and conversation
	 * 
	 * @param	conversation
	 *			the Conversation
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addParticipatesTo(Conversation conversation) {
		return sam.setObjectProperty(kbNode, PROPERTY_PARTICIPATESTO_URI, conversation);
	}

	/**
	 * deletes the "participatesto" link between this mutant and conversation
	 * 
	 * @param	conversation
	 * 			the Conversation
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteParticipatesTo(Conversation conversation) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_PARTICIPATESTO_URI, conversation);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "participatesto" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Conversation> getParticipatesTo() {
		return sam.getObjectProperty(kbNode, PROPERTY_PARTICIPATESTO_URI);
	}

	/**
	 * Creates a "islocated" edge between this mutant and place
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
	 * deletes the "islocated" link between this mutant and place
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
	 * Creates a "creator" edge between this mutant and content
	 * 
	 * @param	content
	 *			the Content
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addCreator(Content content) {
		return sam.setObjectProperty(kbNode, PROPERTY_CREATOR_URI, content);
	}

	/**
	 * deletes the "creator" link between this mutant and content
	 * 
	 * @param	content
	 * 			the Content
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteCreator(Content content) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_CREATOR_URI, content);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "creator" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Content> getCreator() {
		return sam.getObjectProperty(kbNode, PROPERTY_CREATOR_URI);
	}

	/**
	 * inverse of {@link #getKnows()}
	 */
	@Override
	public Set<Agent> getKnows_inverse() {
		return sam.getObjectProperty_inverse(kbNode, Agent.PROPERTY_KNOWS_URI);
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
	 * inverse of {@link #getIsMemberOf()}
	 */
	@Override
	public Set<Agent> getIsMemberOf_inverse() {
		return sam.getObjectProperty_inverse(kbNode, Agent.PROPERTY_ISMEMBEROF_URI);
	}

	/**
	 * inverse of {@link #getIsLocated()}
	 */
	@Override
	public Set<Resource> getIsLocated_inverse() {
		return sam.getObjectProperty_inverse(kbNode, Agent.PROPERTY_ISLOCATED_URI);
	}

	/**
	 * inverse of {@link #getCreator()}
	 */
	@Override
	public Set<Agent> getCreator_inverse() {
		return sam.getObjectProperty_inverse(kbNode, Agent.PROPERTY_CREATOR_URI);
	}
}
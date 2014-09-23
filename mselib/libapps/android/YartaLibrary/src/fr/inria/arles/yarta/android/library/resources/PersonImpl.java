package fr.inria.arles.yarta.android.library.resources;

import fr.inria.arles.yarta.middleware.msemanagement.ThinStorageAccessManager;
import fr.inria.arles.yarta.resources.Event;
import fr.inria.arles.yarta.resources.Conversation;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.YartaResource;
import java.util.Set;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.resources.Topic;

/**
 * 
 * Person class implementation.
 *
 */
public class PersonImpl extends YartaResource implements Person {

	/**
	 * Wraps a given node into a PersonImpl object
	 * 
	 * @param	sam
	 * 			The storage and access manager
	 * @param	n
	 * 			The node to wrap
	 */
	public PersonImpl(ThinStorageAccessManager sam, Node n) {
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
	public PersonImpl(ThinStorageAccessManager sam, String uniqueRequestorId) {
		super(sam, sam.createNewNode(fr.inria.arles.yarta.resources.Person.typeURI));
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
	 * @return the phone. Null if value is not set.
	 */
	public String getPhone() {
		return sam.getDataProperty(kbNode, PROPERTY_PHONE_URI,
				String.class);
	}
	
	/**
	 * Sets the phone.
	 * 
	 * @param	string
	 *			the phone to be set
	 */
	public void setPhone(String string) {
		sam.setDataProperty(kbNode, PROPERTY_PHONE_URI, String.class,
				string);
	}

	/**
	 * @return the location. Null if value is not set.
	 */
	public String getLocation() {
		return sam.getDataProperty(kbNode, PROPERTY_LOCATION_URI,
				String.class);
	}
	
	/**
	 * Sets the location.
	 * 
	 * @param	string
	 *			the location to be set
	 */
	public void setLocation(String string) {
		sam.setDataProperty(kbNode, PROPERTY_LOCATION_URI, String.class,
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
	 * @return the room. Null if value is not set.
	 */
	public String getRoom() {
		return sam.getDataProperty(kbNode, PROPERTY_ROOM_URI,
				String.class);
	}
	
	/**
	 * Sets the room.
	 * 
	 * @param	string
	 *			the room to be set
	 */
	public void setRoom(String string) {
		sam.setDataProperty(kbNode, PROPERTY_ROOM_URI, String.class,
				string);
	}

	/**
	 * Creates a "picture" edge between this person and picture
	 * 
	 * @param	picture
	 *			the Picture
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addPicture(Picture picture) {
		return sam.setObjectProperty(kbNode, PROPERTY_PICTURE_URI, picture);
	}

	/**
	 * deletes the "picture" link between this person and picture
	 * 
	 * @param	picture
	 * 			the Picture
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deletePicture(Picture picture) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_PICTURE_URI, picture);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "picture" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Picture> getPicture() {
		return sam.getObjectProperty(kbNode, PROPERTY_PICTURE_URI);
	}

	/**
	 * Creates a "knows" edge between this person and agent
	 * 
	 * @param	agent
	 *			the fr.inria.arles.yarta.resources.Agent
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addKnows(fr.inria.arles.yarta.resources.Agent agent) {
		return sam.setObjectProperty(kbNode, PROPERTY_KNOWS_URI, agent);
	}

	/**
	 * deletes the "knows" link between this person and agent
	 * 
	 * @param	agent
	 * 			the fr.inria.arles.yarta.resources.Agent
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteKnows(fr.inria.arles.yarta.resources.Agent agent) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_KNOWS_URI, agent);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "knows" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<fr.inria.arles.yarta.resources.Agent> getKnows() {
		return sam.getObjectProperty(kbNode, PROPERTY_KNOWS_URI);
	}

	/**
	 * Creates a "istagged" edge between this person and topic
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
	 * deletes the "istagged" link between this person and topic
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
	 * Creates a "isattending" edge between this person and event
	 * 
	 * @param	event
	 *			the Event
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addIsAttending(Event event) {
		return sam.setObjectProperty(kbNode, PROPERTY_ISATTENDING_URI, event);
	}

	/**
	 * deletes the "isattending" link between this person and event
	 * 
	 * @param	event
	 * 			the Event
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteIsAttending(Event event) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_ISATTENDING_URI, event);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "isattending" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Event> getIsAttending() {
		return sam.getObjectProperty(kbNode, PROPERTY_ISATTENDING_URI);
	}

	/**
	 * Creates a "hasinterest" edge between this person and resource
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
	 * deletes the "hasinterest" link between this person and resource
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
	 * Creates a "ismemberof" edge between this person and group
	 * 
	 * @param	group
	 *			the fr.inria.arles.yarta.resources.Group
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addIsMemberOf(fr.inria.arles.yarta.resources.Group group) {
		return sam.setObjectProperty(kbNode, PROPERTY_ISMEMBEROF_URI, group);
	}

	/**
	 * deletes the "ismemberof" link between this person and group
	 * 
	 * @param	group
	 * 			the fr.inria.arles.yarta.resources.Group
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteIsMemberOf(fr.inria.arles.yarta.resources.Group group) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_ISMEMBEROF_URI, group);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "ismemberof" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<fr.inria.arles.yarta.resources.Group> getIsMemberOf() {
		return sam.getObjectProperty(kbNode, PROPERTY_ISMEMBEROF_URI);
	}

	/**
	 * Creates a "participatesto" edge between this person and conversation
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
	 * deletes the "participatesto" link between this person and conversation
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
	 * Creates a "islocated" edge between this person and place
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
	 * deletes the "islocated" link between this person and place
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
	 * Creates a "creator" edge between this person and content
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
	 * deletes the "creator" link between this person and content
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
	public Set<fr.inria.arles.yarta.resources.Agent> getKnows_inverse() {
		return sam.getObjectProperty_inverse(kbNode, fr.inria.arles.yarta.resources.Agent.PROPERTY_KNOWS_URI);
	}

	/**
	 * inverse of {@link #getHasInterest()}
	 */
	@Override
	public Set<fr.inria.arles.yarta.resources.Agent> getHasInterest_inverse() {
		return sam.getObjectProperty_inverse(kbNode, fr.inria.arles.yarta.resources.Agent.PROPERTY_HASINTEREST_URI);
	}
}
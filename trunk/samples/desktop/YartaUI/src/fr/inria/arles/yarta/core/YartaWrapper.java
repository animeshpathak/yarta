package fr.inria.arles.yarta.core;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.Receiver;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import fr.inria.arles.yarta.middleware.msemanagement.MSEManager;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Event;
import fr.inria.arles.yarta.resources.Group;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.yarta.resources.Topic;

public class YartaWrapper {

	public boolean init(MSEApplication app) {
		try {
			mseManager.initialize(baseOntologyFilePath, basePolicyFilePath,
					app, null);

			accessManager = mseManager.getStorageAccessManager();

		} catch (KBException ex) {
			System.err.println("initialize exception: " + ex.getMessage());
			return false;
		} catch (Exception ex) {
			System.err.println("unknown exception: " + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
		initialized = true;
		return true;
	}

	public void setOwnerId(String ownerId) {
		mseManager.setOwnerUID(ownerId);
	}

	public boolean uninit() {
		try {
			mseManager.shutDown();
		} catch (Exception ex) {
			System.err.println("Exception: " + ex.getMessage());
		}
		return false;
	}

	public boolean isInitialized() {
		return initialized;
	}

	// UI methods
	private Map<String, Image> icons = new HashMap<String, Image>();

	public Image getResourceImage(Object resource) {
		String classId = resource.getClass().getSimpleName().toLowerCase()
				.replace("impl", "");
		if (!icons.containsKey(classId)) {
			ImageIcon icon = new ImageIcon(getClass().getResource(
					"/fr/inria/arles/yarta/ui/images/" + classId + ".png"));
			icons.put(classId, icon.getImage());
		}

		return icons.get(classId);
	}

	public String getResourceName(Object resource) {
		if (resource instanceof Agent) {
			Agent agent = (Agent) resource;
			String email = agent.getEmail();
			if (email == null) {
				if (agent instanceof Person) {
					return ((Person) agent).getUserId();
				}
			}
			return email;
		} else if (resource instanceof Content) {
			Content content = (Content) resource;
			String title = content.getTitle();
			if (title != null) {
				return title;
			}
			return "Anonymous content";
		} else if (resource instanceof Topic) {
			Topic topic = (Topic) resource;
			String title = topic.getTitle();
			if (title != null) {
				return title;
			}
			return "Anonymous topic";
		} else if (resource instanceof Place) {
			Place place = (Place) resource;
			String name = place.getName();
			if (name != null) {
				return name;
			}
			return "Anonymous place";
		} else if (resource instanceof Event) {
			Event event = (Event) resource;
			String title = event.getTitle();
			if (title != null) {
				return title;
			}
			return "Anonymous event";
		}
		return "#UndefinedName";
	}

	// UI methods end

	// Person.C
	public void createPerson(String personId, String firstName,
			String lastName, String homePage) {
		Person person = accessManager.createPerson(personId);
		person.setEmail(personId);
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setHomepage(homePage);
	}

	// Person.R
	public Person readPerson(String personId) {
		Person person = null;
		try {
			person = accessManager.getPersonByUserId(personId);
		} catch (KBException ex) {
			System.err.println("getPerson error: " + ex.getMessage());
		}
		return person;
	}

	// Person.U
	public void updatePerson(String personId, String email, String name,
			String firstName, String lastName, String homePage) {
		Person person = readPerson(personId);
		person.setEmail(email);
		person.setName(name);
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setHomepage(homePage);
	}

	// Person.D
	public void deletePerson(String userId) {
		throw new RuntimeException("Not implemented");
	}

	// Group.C
	public void createGroup(String email, String homePage, String name) {
		Group group = accessManager.createGroup();
		group.setName(name);
		group.setEmail(email);
		group.setHomepage(homePage);
	}

	// Group.R
	public Group readGroup(String groupId) {
		Set<Group> groups = accessManager.getAllGroups();

		for (Group group : groups) {
			if (group.getUniqueId().equals(groupId)) {
				return group;
			}
		}
		return null;
	}

	// Group.U
	public void updateGroup(String uniqueId, String email, String name,
			String homePage) {
		Group group = readGroup(uniqueId);
		group.setName(name);
		group.setEmail(email);
		group.setHomepage(homePage);
	}

	// Group.D
	public void deleteGroup(String uniqueId) {
		throw new RuntimeException("Not implemented");
	}

	// Content.C
	public void createContent(String title, String source, String identifier,
			String format) {
		Content content = accessManager.createContent();
		content.setTitle(title);
		content.setSource(source);
		content.setIdentifier(identifier);
		content.setFormat(format);
	}

	// Content.R
	public Content readContent(String contentId) {
		Set<Content> lstContents = accessManager.getAllContents();
		for (Content content : lstContents) {
			if (content.getUniqueId().equals(contentId)) {
				return content;
			}
		}
		return null;
	}

	// Content.U
	public void updateContent(String contentId, String title, String source,
			String identifier, String format) {
		Content content = readContent(contentId);
		content.setTitle(title);
		content.setSource(source);
		content.setIdentifier(identifier);
		content.setFormat(format);
	}

	// Content.D
	public void deleteContent(String contentId) {
		throw new RuntimeException("Not implemented");
	}

	// Topic.C
	public void createTopic(String title) {
		Topic topic = accessManager.createTopic();
		topic.setTitle(title);
	}

	// Topic.R
	public Topic readTopic(String uniqueId) {
		Set<Topic> lstTopics = accessManager.getAllTopics();

		for (Topic topic : lstTopics) {
			if (topic.getUniqueId().equals(uniqueId)) {
				return topic;
			}
		}
		return null;
	}

	// Topic.U
	public void updateTopic(String uniqueId, String title) {
		Topic topic = readTopic(uniqueId);
		topic.setTitle(title);
	}

	// Topic.D
	public void deleteTopic(String uniqueId) {
		throw new RuntimeException("Not implemented");
	}

	// Place.C
	public void createPlace(String name, float latitude, float longitude) {
		Place place = accessManager.createPlace();
		place.setName(name);
		place.setLatitude(latitude);
		place.setLongitude(longitude);
	}

	// Place.R
	public Place readPlace(String uniqueId) {
		Set<Place> lstPlaces = accessManager.getAllPlaces();

		for (Place place : lstPlaces) {
			if (place.getUniqueId().equals(uniqueId)) {
				return place;
			}
		}
		return null;
	}

	// Place.U
	public void updatePlace(String uniqueId, String name, float latitude,
			float longitude) {
		Place place = readPlace(uniqueId);
		place.setName(name);
		place.setLatitude(latitude);
		place.setLongitude(longitude);
	}

	// Place.D
	public void deletePlace(String uniqueId) {
		throw new RuntimeException("Not implemented");
	}

	// Event.C
	public void createEvent(String title, String description) {
		Event event = accessManager.createEvent();
		event.setTitle(title);
		event.setDescription(description);
	}

	// Event.R
	public Event readEvent(String uniqueId) {
		Set<Event> lstEvents = accessManager.getAllEvents();

		for (Event event : lstEvents) {
			if (event.getUniqueId().equals(uniqueId)) {
				return event;
			}
		}

		return null;
	}

	// Event.U
	public void updateEvent(String uniqueId, String title, String description) {
		Event event = readEvent(uniqueId);
		event.setTitle(title);
		event.setDescription(description);
	}

	// Event.D
	public void deleteEvent(String uniqueId) {
		throw new RuntimeException("Not implemented");
	}

	public Set<Group> getAllGroups() {
		return accessManager.getAllGroups();
	}

	public Set<Agent> getAllAgents() {
		return accessManager.getAllAgents();
	}

	public Set<Content> getAllContents() {
		return accessManager.getAllContents();
	}

	public Set<Topic> getAllTopics() {
		return accessManager.getAllTopics();
	}

	public Set<Place> getAllPlaces() {
		return accessManager.getAllPlaces();
	}

	public Set<Event> getAllEvents() {
		return accessManager.getAllEvents();
	}

	public Set<Resource> getAllResources() {
		return accessManager.getAllResources();
	}

	Map<String, Map<Object, Map<Object, Boolean>>> linksMap = new HashMap<String, Map<Object, Map<Object, Boolean>>>();

	public Map<Object, Map<Object, Boolean>> getLinksMap(String link) {
		if (!linksMap.containsKey(link)) {
			linksMap.put(link, new HashMap<Object, Map<Object, Boolean>>());
		}
		return linksMap.get(link);
	}

	public void resetLinks() {
		linksMap = new HashMap<String, Map<Object, Map<Object, Boolean>>>();
	}

	public boolean knowsLink(Object a, Object b) {
		Map<Object, Map<Object, Boolean>> linkMap = getLinksMap("knows");

		if (linkMap.get(a) == null) {
			linkMap.put(a, new HashMap<Object, Boolean>());
		}

		if (linkMap.get(a).containsKey(b)) {
			return linkMap.get(a).get(b);
		}

		if (a instanceof Agent && b instanceof Agent) {
			Agent aAgent = (Agent) a;
			Agent bAgent = (Agent) b;
			Set<Agent> knownAgents = aAgent.getKnows();

			linkMap.get(a).put(b, knownAgents.contains(bAgent));
			return linkMap.get(a).get(b);
		}
		return false;
	}

	public boolean memberOfLink(Object a, Object b) {
		Map<Object, Map<Object, Boolean>> linkMap = getLinksMap("memberOf");

		if (linkMap.get(a) == null) {
			linkMap.put(a, new HashMap<Object, Boolean>());
		}

		if (linkMap.get(a).containsKey(b)) {
			return linkMap.get(a).get(b);
		}

		if (a instanceof Agent && b instanceof Group) {
			Agent aAgent = (Agent) a;
			Group bGroup = (Group) b;

			Set<Group> memberOfGroups = aAgent.getIsMemberOf();

			linkMap.get(a).put(b, memberOfGroups.contains(bGroup));
			return linkMap.get(a).get(b);
		}
		return false;
	}

	public boolean creatorLink(Object a, Object b) {
		Map<Object, Map<Object, Boolean>> linkMap = getLinksMap("creator");

		if (linkMap.get(a) == null) {
			linkMap.put(a, new HashMap<Object, Boolean>());
		}

		if (linkMap.get(a).containsKey(b)) {
			return linkMap.get(a).get(b);
		}

		if (a instanceof Agent && b instanceof Content) {
			Agent aAgent = (Agent) a;
			Content bContent = (Content) b;
			Set<Content> isCreatorContents = aAgent.getCreator();

			linkMap.get(a).put(b, isCreatorContents.contains(bContent));
			return linkMap.get(a).get(b);
		}
		return false;
	}

	public boolean hasInterestLink(Object a, Object b) {
		Map<Object, Map<Object, Boolean>> linkMap = getLinksMap("hasInterest");

		if (linkMap.get(a) == null) {
			linkMap.put(a, new HashMap<Object, Boolean>());
		}

		if (linkMap.get(a).containsKey(b)) {
			return linkMap.get(a).get(b);
		}

		if (a instanceof Agent && b instanceof Resource) {
			Agent aAgent = (Agent) a;
			Resource bResource = (Resource) b;
			Set<Resource> hasInterestResources = aAgent.getHasInterest();

			linkMap.get(a).put(b, hasInterestResources.contains(bResource));
			return linkMap.get(a).get(b);
		}
		return false;
	}

	public boolean isTaggedLink(Object a, Object b) {
		Map<Object, Map<Object, Boolean>> linkMap = getLinksMap("isTagged");

		if (linkMap.get(a) == null) {
			linkMap.put(a, new HashMap<Object, Boolean>());
		}

		if (linkMap.get(a).containsKey(b)) {
			return linkMap.get(a).get(b);
		}

		if (a instanceof Resource && b instanceof Topic) {
			Resource aResource = (Resource) a;
			Topic bTopic = (Topic) b;
			Set<Topic> isTaggedTopics = aResource.getIsTagged();

			linkMap.get(a).put(b, isTaggedTopics.contains(bTopic));
			return linkMap.get(a).get(b);
		}
		return false;
	}

	public boolean isLocatedLink(Object a, Object b) {
		Map<Object, Map<Object, Boolean>> linkMap = getLinksMap("isLocated");

		if (linkMap.get(a) == null) {
			linkMap.put(a, new HashMap<Object, Boolean>());
		}

		if (linkMap.get(a).containsKey(b)) {
			return linkMap.get(a).get(b);
		}

		if (!(b instanceof Place)) {
			return false;
		}

		if (!(a instanceof Agent) && !(a instanceof Place)
				&& !(a instanceof Event)) {
			return false;
		}

		Place bPlace = (Place) b;
		Set<Place> isLocatedPlaces = null;

		if (a instanceof Agent) {
			Agent aAgent = (Agent) a;
			isLocatedPlaces = aAgent.getIsLocated();
		} else if (a instanceof Place) {
			Place aPlace = (Place) a;
			isLocatedPlaces = aPlace.getIsLocated();
		} else if (a instanceof Event) {
			Event aEvent = (Event) a;
			isLocatedPlaces = aEvent.getIsLocated();
		}

		linkMap.get(a).put(b, isLocatedPlaces.contains(bPlace));

		return linkMap.get(a).get(b);
	}

	public boolean participatesLink(Object a, Object b) {
		Map<Object, Map<Object, Boolean>> linkMap = getLinksMap("participates");

		if (linkMap.get(a) == null) {
			linkMap.put(a, new HashMap<Object, Boolean>());
		}

		if (linkMap.get(a).containsKey(b)) {
			return linkMap.get(a).get(b);
		}

		if (a instanceof Agent && b instanceof Event) {
			Agent aAgent = (Agent) a;
			Event bEvent = (Event) b;

			Set<Event> lstEvents = aAgent.getIsAttending();

			linkMap.get(a).put(b, lstEvents.contains(bEvent));

			return linkMap.get(a).get(b);
		}
		return false;
	}

	public void sendHello(String userId) {
		try {
			mseManager.sayHelloTo(userId, null);
		} catch (KBException ex) {
			System.err.println("sendHello exception: " + ex.getMessage());
		}
	}

	public void sendUpdate(String userId) {
		try {
			mseManager.askUpdateTo(userId, null);
		} catch (KBException ex) {
			System.err.println("sendUpdate exception: " + ex.getMessage());
		}
	}

	public void sendMessage(String userId, String message) {
		CommunicationManager comMgr = mseManager.getCommunicationManager();
		comMgr.sendMessage(userId, new Message(message.getBytes()));
	}

	public void setReceiver(Receiver receiver) {
		CommunicationManager comMgr = mseManager.getCommunicationManager();
		comMgr.setMessageReceiver(receiver);
	}

	public void dumpKB(String fileName) {
		mseManager.writeKnowledge(fileName);
	}

	public void loadKB(String fileName) {
		mseManager.addKnowledge(fileName);
	}

	public static YartaWrapper getInstance() {
		return s_instance;
	}

	private boolean initialized;
	private MSEManager mseManager;
	private StorageAccessManager accessManager;

	private YartaWrapper() {
		mseManager = new MSEManager();
	}

	private static YartaWrapper s_instance = new YartaWrapper();
	private String baseOntologyFilePath = "mse-1.2.rdf";
	private String basePolicyFilePath = "policies";
}

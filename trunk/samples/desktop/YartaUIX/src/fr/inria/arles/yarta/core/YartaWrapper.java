package fr.inria.arles.yarta.core;

import java.awt.Image;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import fr.inria.arles.yarta.conference.msemanagement.MSEManagerEx;
import fr.inria.arles.yarta.conference.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.yarta.conference.resources.Conference;
import fr.inria.arles.yarta.conference.resources.Paper;
import fr.inria.arles.yarta.conference.resources.Presentation;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.Receiver;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Event;
import fr.inria.arles.yarta.resources.Group;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.yarta.resources.Topic;

public class YartaWrapper {

	public boolean init(String userId, MSEApplication application) {
		this.userId = userId;

		try {
			mseManager.initialize(confOntologyFilePath, basePolicyFilePath,
					application, null);

			accessManager = mseManager.getStorageAccessManagerEx();
		} catch (KBException ex) {
			System.err.println("initialize exception: " + ex.getMessage());
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		initialized = true;
		return true;
	}

	public boolean uninit() {
		try {
			mseManager.shutDown();
		} catch (Exception ex) {
			System.err.println("Exception: " + ex.getMessage());
		}
		return false;
	}

	public void setOwnerId(String userId) {
		mseManager.setOwnerUID(userId);
	}

	public boolean isInitialized() {
		return initialized;
	}

	public String getUserId() {
		return userId;
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
		} else {
			return false;
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

	public boolean isIncludedLink(Object a, Object b) {
		Map<Object, Map<Object, Boolean>> linkMap = getLinksMap("isIncluded");

		if (linkMap.get(a) == null) {
			linkMap.put(a, new HashMap<Object, Boolean>());
		}

		if (linkMap.get(a).containsKey(b)) {
			return linkMap.get(a).get(b);
		}

		if (a instanceof Presentation && b instanceof Conference) {
			Presentation aPresentation = (Presentation) a;
			Conference bConference = (Conference) b;

			Set<Conference> conferences = aPresentation.getIsIncluded();

			linkMap.get(a).put(b, conferences.contains(bConference));

			return linkMap.get(a).get(b);
		}

		return false;
	}

	public boolean containsLink(Object a, Object b) {
		Map<Object, Map<Object, Boolean>> linkMap = getLinksMap("contains");

		if (linkMap.get(a) == null) {
			linkMap.put(a, new HashMap<Object, Boolean>());
		}

		if (linkMap.get(a).containsKey(b)) {
			return linkMap.get(a).get(b);
		}

		if (a instanceof Presentation && b instanceof Topic) {
			Presentation aPresentation = (Presentation) a;
			Topic bTopic = (Topic) b;

			Set<Topic> topics = aPresentation.getContains();

			linkMap.get(a).put(b, topics.contains(bTopic));

			return linkMap.get(a).get(b);
		}

		return false;
	}

	public boolean followsLink(Object a, Object b) {
		Map<Object, Map<Object, Boolean>> linkMap = getLinksMap("follows");

		if (linkMap.get(a) == null) {
			linkMap.put(a, new HashMap<Object, Boolean>());
		}

		if (linkMap.get(a).containsKey(b)) {
			return linkMap.get(a).get(b);
		}

		if (a instanceof fr.inria.arles.yarta.conference.resources.Event
				&& b instanceof fr.inria.arles.yarta.conference.resources.Event) {
			fr.inria.arles.yarta.conference.resources.Event aEvent = (fr.inria.arles.yarta.conference.resources.Event) a;
			fr.inria.arles.yarta.conference.resources.Event bEvent = (fr.inria.arles.yarta.conference.resources.Event) b;

			Set<fr.inria.arles.yarta.conference.resources.Event> events = aEvent
					.getFollows();

			linkMap.get(a).put(b, events.contains(bEvent));

			return linkMap.get(a).get(b);
		}

		return false;
	}

	// UI specific methods
	private Map<String, Image> icons = new HashMap<String, Image>();

	public Image getResourceImage(Object resource) {
		String classId = resource.getClass().getSimpleName().toLowerCase()
				.replace("impl", "");
		if (!icons.containsKey(classId)) {
			System.out.println(classId);
			if (classId.equals("message")) {
				classId = "content";
			}
			ImageIcon icon = new ImageIcon(getClass().getResource(
					"/fr/inria/arles/yarta/ui/images/" + classId + ".png"));
			icons.put(classId, icon.getImage());
		}

		return icons.get(classId);
	}

	public String getResourceName(Object resource) {
		if (resource instanceof Person) {
			Person person = (Person) resource;
			return person.getUserId();
		} else if (resource instanceof Group) {
			Group group = (Group) resource;
			String email = group.getEmail();
			if (email != null)
				return email;
			return group.getUniqueId();
		} else if (resource instanceof Content) {
			Content content = (Content) resource;
			String title = content.getTitle();
			if (title != null) {
				return title;
			}
			return content.getUniqueId();
		} else if (resource instanceof Topic) {
			Topic topic = (Topic) resource;
			String title = topic.getTitle();
			if (title != null) {
				return title;
			}
			return topic.getUniqueId();
		} else if (resource instanceof Place) {
			Place place = (Place) resource;
			String name = place.getName();
			if (name != null) {
				return name;
			}
			return place.getUniqueId();
		} else if (resource instanceof Event) {
			Event event = (Event) resource;
			String title = event.getTitle();
			if (title != null) {
				return title;
			}
			return event.getUniqueId();
		}
		return "<#Unknown Resource#>";
	}

	// UI specific methods

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

	public Set<Conference> getAllConferences() {
		return accessManager.getAllConferences();
	}

	public Set<Presentation> getAllPresentations() {
		return accessManager.getAllPresentations();
	}

	public Set<Paper> getAllPapers() {
		return accessManager.getAllPapers();
	}

	public StorageAccessManagerEx getAccessManager() {
		return accessManager;
	}

	public Set<Resource> getAllResources() {
		Set<Resource> lstResources = new HashSet<Resource>();
		lstResources.addAll(getAllAgents());
		lstResources.addAll(getAllTopics());
		lstResources.addAll(getAllEvents());
		lstResources.addAll(getAllPlaces());
		lstResources.addAll(getAllContents());
		return lstResources;
	}

	public void dumpKB(String fileName) {
		mseManager.writeKnowledge(fileName);
	}

	public void loadKB(String fileName) {
		mseManager.addKnowledge(fileName);
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

	public static YartaWrapper getInstance() {
		return instance;
	}

	private String userId;
	private boolean initialized;
	private MSEManagerEx mseManager;
	private StorageAccessManagerEx accessManager;

	private static YartaWrapper instance = new YartaWrapper();

	private YartaWrapper() {
		mseManager = new MSEManagerEx();
	}

	private String confOntologyFilePath = "mse-conf.rdf";
	private String basePolicyFilePath = "policies";
}

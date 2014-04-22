package fr.inria.arles.yarta.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.res.AssetManager;

import fr.inria.arles.yarta.conference.AsyncRunner;
import fr.inria.arles.yarta.conference.R;
import fr.inria.arles.yarta.conference.ResourceActivity;
import fr.inria.arles.yarta.conference.msemanagement.MSEManagerEx;
import fr.inria.arles.yarta.conference.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.yarta.conference.resources.Conference;
import fr.inria.arles.yarta.conference.resources.Paper;
import fr.inria.arles.yarta.conference.resources.Presentation;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.interfaces.PolicyManager;
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

	public boolean init(String userId, Object object, MSEApplication application) {

		ensureBaseFiles((Context) object);

		this.userId = userId;

		try {
			mseManager.initialize(dataPath + "/" + confOntologyFilePath,
					dataPath + "/" + basePolicyFilePath, application, object);
			accessManager = mseManager.getStorageAccessManagerEx();
		} catch (KBException ex) {
			System.err.println("initialize exception: " + ex.getMessage());
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("unknown exception: " + ex.getMessage());
			return false;
		}
		initialized = true;
		return true;
	}

	public boolean uninit() {
		try {
			return mseManager.shutDown();
		} catch (Exception ex) {
			System.err.println("uninit exception: " + ex.getMessage());
		}
		return false;
	}

	public void setOwnerId(String userId) {
		mseManager.setOwnerUID(userId);
		accessManager.setOwnerID(userId);
	}

	/**
	 * In case it's the very first time, copy the base rdf & policy to the
	 * specified folder.
	 */
	private void ensureBaseFiles(Context context) {
		dataPath = context.getFilesDir().getAbsolutePath();

		File policyFile = context.getFileStreamPath(basePolicyFilePath);
		File rdfFile = context.getFileStreamPath(confOntologyFilePath);

		if (rdfFile.exists() && policyFile.exists()) {
			return;
		}

		dumpAsset(context, dataPath, confOntologyFilePath);
		dumpAsset(context, dataPath, basePolicyFilePath);
	}

	/**
	 * Dumps an asset in the specified folder.
	 */
	private void dumpAsset(Context context, String folder, String fileName) {
		try {
			InputStream fin = context.getAssets().open(fileName,
					AssetManager.ACCESS_RANDOM);
			FileOutputStream fout = new FileOutputStream(folder + "/"
					+ fileName);

			int count = 0;
			byte buffer[] = new byte[1024];

			while ((count = fin.read(buffer)) != -1) {
				fout.write(buffer, 0, count);
			}

			fin.close();
			fout.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean isInitialized() {
		return initialized;
	}

	public String getUserId() {
		return userId;
	}

	// UI methods

	public int getResIcon(Object currentObject) {
		if (currentObject instanceof Person) {
			return R.drawable.person;
		} else if (currentObject instanceof Group) {
			return R.drawable.group;
		} else if (currentObject instanceof Place) {
			return R.drawable.place;
		} else if (currentObject instanceof Event) {
			return R.drawable.event;
		} else if (currentObject instanceof Topic) {
			return R.drawable.topic;
		} else if (currentObject instanceof Content) {
			return R.drawable.content;
		}
		return R.drawable.person;
	}

	public String getResTitle(Object currentObject) {
		return currentObject.getClass().getSimpleName().replace("Impl", "");
	}

	public String getResName(Object resource) {
		if (resource instanceof Person) {
			Person p = (Person) resource;
			String uniqueId = p.getUniqueId();

			if (uniqueId.contains(userId)) {
				return String.format("%s (%s)", "Me", uniqueId);
			}

			String firstName = p.getFirstName();
			String lastName = p.getLastName();

			return String.format("%s %s (%s)", firstName, lastName, uniqueId);
		} else if (resource instanceof Group) {
			Group g = (Group) resource;
			String name = g.getName();
			String uniqueId = g.getUniqueId();

			return String.format("%s (%s)", name, uniqueId);
		} else if (resource instanceof Content) {
			Content c = (Content) resource;
			String title = c.getTitle();
			String uniqueId = c.getUniqueId();

			return String.format("%s (%s)", title, uniqueId);
		} else if (resource instanceof Topic) {
			Topic t = (Topic) resource;

			String title = t.getTitle();
			String uniqueId = t.getUniqueId();

			return String.format("%s (%s)", title, uniqueId);
		} else if (resource instanceof Place) {
			Place p = (Place) resource;
			String name = p.getName();
			String uniqueId = p.getUniqueId();

			return String.format("%s (%s)", name, uniqueId);
		} else if (resource instanceof Event) {
			Event e = (Event) resource;

			String title = e.getTitle();
			String uniqueId = e.getUniqueId();

			return String.format("%s (%s)", title, uniqueId);
		}

		return "<#Unknown Resource#>";
	}

	public String getResInfo(Object resource) {
		if (resource instanceof Person) {
			Person person = (Person) resource;
			String info = person.getEmail();
			if (info == null) {
				info = person.getUserId();
			} else if (info.length() == 0) {
				info = person.getUserId();
			}
			return info;
		} else if (resource instanceof Group) {
			Group group = (Group) resource;
			return group.getEmail();
		} else if (resource instanceof Agent) {
			Agent agent = (Agent) resource;
			return agent.getEmail();
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

			return "Anonumous place";
		} else if (resource instanceof Event) {
			Event event = (Event) resource;
			String title = event.getTitle();
			if (title != null) {
				return title;
			}

			return "Anonymouse event";
		}
		return "<#Unknown Resource#>";
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
	public void updatePerson(String personId, String email, String firstName,
			String lastName, String homePage) {
		Person person = readPerson(personId);
		person.setEmail(email);
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

	public Set<Conference> getAllConferences() {
		return accessManager.getAllConferences();
	}

	public Set<Presentation> getAllPresentations() {
		return accessManager.getAllPresentations();
	}

	public Set<Paper> getAllPapers() {
		return accessManager.getAllPapers();
	}

	public Person getMe() {
		try {
			return accessManager.getMe();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public Set<Resource> getAllResources() {
		Set<Resource> lstResources = new HashSet<Resource>();
		for (int i = 0; i < 5; i++) {
			try {
				lstResources.addAll(accessManager.getAllPersons());
				lstResources.addAll(accessManager.getAllPapers());
				lstResources.addAll(getAllConferences());
				lstResources.addAll(getAllPresentations());
				i = 5;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return lstResources;
	}

	public Set<Resource> getAllPushableResources() {
		Set<Resource> lstResources = new HashSet<Resource>();
		lstResources.addAll(accessManager.getAllGroups());
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

	public boolean sendHello(String userId) {
		try {
			return mseManager.sayHelloTo(userId, null);
		} catch (KBException ex) {
			System.err.println("sendHello exception: " + ex.getMessage());
			ex.printStackTrace();
		}
		return false;
	}

	public boolean sendUpdate(String userId) {
		try {
			return mseManager.askUpdateTo(userId, null);
		} catch (KBException ex) {
			System.err.println("sendUpdate exception: " + ex.getMessage());
			ex.printStackTrace();
		}
		return false;
	}

	public int sendMessage(String userId, String message) {
		CommunicationManager comMgr = mseManager.getCommunicationManager();
		return comMgr.sendMessage(userId, new Message(message.getBytes()));
	}

	public int sendPush(String userId, String resourceId) {
		CommunicationManager comMgr = mseManager.getCommunicationManager();
		return comMgr.sendResource(userId, resourceId);
	}

	public void setReceiver(Receiver receiver) {
		CommunicationManager comMgr = mseManager.getCommunicationManager();
		comMgr.setMessageReceiver(receiver);
	}

	public static YartaWrapper getInstance() {
		return s_instance;
	}

	public StorageAccessManagerEx getAccessManager() {
		return accessManager;
	}

	public PolicyManager getPolicyManager() {
		return mseManager.getPolicyManager();
	}

	private String userId;
	private boolean initialized;
	private MSEManagerEx mseManager;
	private StorageAccessManagerEx accessManager;

	private YartaWrapper() {
		mseManager = new MSEManagerEx();
	}

	private static YartaWrapper s_instance = new YartaWrapper();
	private String confOntologyFilePath = "mse-conf.rdf";
	private String basePolicyFilePath = "policies";
	private String dataPath;

	public ResourceActivity.Validator validator;
	public Class<?> currentResourceClass;
	public Object currentResource;
	public AsyncRunner runner;
}

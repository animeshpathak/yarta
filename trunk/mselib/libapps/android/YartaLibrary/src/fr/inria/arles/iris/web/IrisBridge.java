package fr.inria.arles.iris.web;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.inria.arles.yarta.android.library.ContentClientPictures;
import fr.inria.arles.yarta.android.library.msemanagement.MSEManagerEx;
import fr.inria.arles.yarta.android.library.resources.Agent;
import fr.inria.arles.yarta.android.library.resources.Group;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.resources.Picture;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import fr.inria.arles.yarta.resources.Conversation;
import fr.inria.arles.yarta.resources.Message;

/**
 * This class contains useful methods to fetch data into KB using the Iris web
 * client.
 */
public class IrisBridge {

	private static ElggClient client = ElggClient.getInstance();
	private MSEApplication app;
	private KnowledgeBase kb;
	private ContentClientPictures content;

	public IrisBridge(MSEApplication app, KnowledgeBase kb,
			ContentClientPictures content) {
		this.app = app;
		this.kb = kb;
		this.content = content;
	}

	/**
	 * Ensures a given p, o triple will exist.
	 * 
	 * @param predicate
	 * @param object
	 */
	public void ensureSubjectInformation(final Node predicate, final Node object) {
		final String p = predicate.getName();

		new Thread(new Runnable() {

			@Override
			public void run() {
				if (p.equals(Person.PROPERTY_USERID_URI)) {
					UserItem user = client.getUser(object.getName());

					if (user != null) {
						if (createPerson(user, true)) {
							app.handleNotification("person "
									+ user.getUsername() + " updated.");
						}
					}
				} else if (p.equals(Person.PROPERTY_KNOWS_URI)) {
					ensureFriends(object);
				} else if (p.equals(MSEKnowledgeBase.PROPERTY_TYPE_URI)) {
					if (object.getName().equals(Conversation.typeURI)) {
						ensureConversations();
					}
				}
			}
		}).start();
	}

	public void ensureObjectInformation(final Node subject, final Node predicate) {
		final String s = subject.getName();
		final String p = predicate.getName();
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (p.equals(Person.PROPERTY_KNOWS_URI)) {
					ensureFriends(subject);
				} else if (p.equals(Agent.PROPERTY_ISMEMBEROF_URI)) {
					ensureGroups(subject);
				} else if (s.contains(Group.typeURI)) {
					if (getSimpleLiteral(subject, p) == null) {
						String groupId = s.substring(s.indexOf('_') + 1);

						GroupItem group = client.getGroup(groupId);
						if (createGroup(group, true)) {
							app.handleNotification("group " + groupId
									+ " updated.");
						}
					}
				}
			}
		}).start();
	}

	private void ensureConversations() {
		boolean update = false;

		List<List<MessageItem>> conversations = client.getMessageThreads();

		for (List<MessageItem> conversation : conversations) {
			update |= createConversation(conversation);
		}

		if (update) {
			app.handleNotification("conversations updated.");
		}
	}

	private void ensureFriends(Node node) {
		String userId = getUserId(node);

		List<UserItem> users = client.getFriends(userId, 0);

		boolean update = false;
		for (UserItem user : users) {
			update |= createPerson(user, false);
			update |= addKnows(user);
		}

		if (update) {
			app.handleNotification("friend list updated");
		}
	}

	private void ensureGroups(Node node) {
		String userId = getUserId(node);

		List<GroupItem> groups = client.getGroups(userId);

		boolean update = false;
		for (GroupItem group : groups) {
			update |= createGroup(group, false);
			update |= addIsMemberOf(group);
		}

		if (update) {
			app.handleNotification("group list updated");
		}
	}

	private boolean createConversation(List<MessageItem> messages) {
		boolean update = false;

		String reqId = client.getUsername();
		Node contains = kb
				.getResourceByURINoPolicies(Conversation.PROPERTY_CONTAINS_URI);
		Node participatesTo = kb
				.getResourceByURINoPolicies(Person.PROPERTY_PARTICIPATESTO_URI);

		// to be able to pass it as ref
		Node conversation[] = new Node[1];
		List<Node> peers = new ArrayList<Node>();

		for (MessageItem item : messages) {
			update |= createMessage(item, conversation);
			peers.add(getNode(item.getFrom().getUsername()));
		}

		try {
			if (conversation[0] == null) {
				String conversationId = UUID.randomUUID().toString();
				conversation[0] = kb.addResource(MSEManagerEx.baseMSEURI + '#'
						+ conversationId, Conversation.typeURI, reqId);
				update = true;
			}

			for (Node peer : peers) {
				if (kb.getTriple(peer, participatesTo, conversation[0], reqId) == null) {
					kb.addTriple(peer, participatesTo, conversation[0], reqId);
					update = true;
				}
			}

			for (MessageItem item : messages) {
				String messageId = Message.typeURI + "_" + item.getGuid();
				Node message = kb.getResourceByURINoPolicies(messageId);

				if (kb.getTriple(conversation[0], contains, message, reqId) == null) {
					kb.addTriple(conversation[0], contains, message, reqId);
					update = true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return update;
	}

	private boolean createMessage(MessageItem item, Node conversation[]) {
		boolean update = false;

		Node contains = kb
				.getResourceByURINoPolicies(Conversation.PROPERTY_CONTAINS_URI);

		try {
			String messageId = Message.typeURI + "_" + item.getGuid();
			Node s = kb.getResourceByURINoPolicies(messageId);
			if (s == null) {
				update = true;
				s = kb.addResource(messageId, Message.typeURI,
						client.getUsername());
				log("createMessage<%s>", item.getGuid());
			}

			update |= ensureSimpleTriple(s, Message.PROPERTY_TITLE_URI,
					item.getSubject());
			update |= ensureSimpleTriple(s, Message.PROPERTY_CONTENT_URI,
					item.getDescription());
			update |= ensureSimpleTriple(s, Message.PROPERTY_TIME_URI,
					String.valueOf(item.getTimestamp()));
			update |= createPerson(item.getFrom(), false);

			String reqId = client.getUsername();

			// set creator
			Node u = getNode(item.isSent() ? client.getUsername() : item
					.getFrom().getUsername());
			Node p = kb.getResourceByURINoPolicies(Agent.PROPERTY_CREATOR_URI);
			if (kb.getTriple(u, p, s, client.getUsername()) == null) {
				kb.addTriple(u, p, s, reqId);
				update = true;
			}

			List<Triple> triples = kb.getPropertySubjectAsTriples(contains, s,
					reqId);
			for (Triple triple : triples) {
				conversation[0] = triple.getSubject();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return update;
	}

	/**
	 * Gets the username from a node's URI
	 * 
	 * @param node
	 */
	private String getUserId(Node node) {
		String userId = node.getName();
		userId = userId.substring(userId.indexOf('_') + 1);
		return userId;
	}

	/**
	 * Gets the node from a userId.
	 * 
	 * @param userId
	 * @return
	 */
	private Node getNode(String userId) {
		String ownerId = Person.typeURI + "_" + userId;
		Node owner = kb.getResourceByURINoPolicies(ownerId);
		return owner;
	}

	/**
	 * Add a bi-directional knows link between owner and this user.
	 * 
	 * @param item
	 * @return
	 */
	private boolean addKnows(UserItem item) {
		boolean update = false;

		String reqId = client.getUsername();

		Node owner = getNode(reqId);
		Node user = getNode(item.getUsername());

		Node p = kb.getResourceByURINoPolicies(Person.PROPERTY_KNOWS_URI);

		try {
			if (kb.getTriple(owner, p, user, reqId) == null) {
				kb.addTriple(owner, p, user, reqId);
				update = true;
			}
			if (kb.getTriple(user, p, owner, reqId) == null) {
				kb.addTriple(user, p, owner, reqId);
				update = true;
			}
		} catch (KBException ex) {
			ex.printStackTrace();
		}

		return update;
	}

	/**
	 * Adds a IsMemberOf link from owner to this group.
	 * 
	 * @param item
	 * @return
	 */
	private boolean addIsMemberOf(GroupItem item) {
		boolean update = false;

		String reqId = client.getUsername();

		Node owner = getNode(reqId);

		String groupId = Group.typeURI + "_" + item.getGuid();
		Node group = kb.getResourceByURINoPolicies(groupId);

		Node p = kb.getResourceByURINoPolicies(Agent.PROPERTY_ISMEMBEROF_URI);

		try {
			if (kb.getTriple(owner, p, group, reqId) == null) {
				kb.addTriple(owner, p, group, client.getUsername());
				update = true;
			}
		} catch (KBException ex) {
			ex.printStackTrace();
		}

		return update;
	}

	/**
	 * Creates a person with the given details.
	 * 
	 * @param kb
	 * @param item
	 * @param highres
	 *            should the image quality be high res?
	 * @return true if the person was updated
	 */
	private boolean createPerson(UserItem item, boolean highres) {
		boolean update = false;
		try {
			String userUniqueId = Person.typeURI + "_" + item.getUsername();
			Node s = kb.getResourceByURINoPolicies(userUniqueId);
			if (s == null) {
				update = true;
				s = kb.addResource(userUniqueId, Person.typeURI,
						client.getUsername());
				log("createPerson<%s>", item.getUsername());
			}

			update |= ensureSimpleTriple(s, Person.PROPERTY_USERID_URI,
					item.getUsername());
			update |= ensureSimpleTriple(s, Person.PROPERTY_NAME_URI,
					item.getName());
			update |= ensureSimpleTriple(s, Person.PROPERTY_ROOM_URI,
					item.getRoom());
			update |= ensureSimpleTriple(s, Person.PROPERTY_PHONE_URI,
					item.getPhone());
			update |= ensureSimpleTriple(s, Person.PROPERTY_LOCATION_URI,
					item.getLocation());

			String pictureId = getSimpleLiteral(s, Person.PROPERTY_PICTURE_URI);
			if (pictureId == null) {
				pictureId = UUID.randomUUID().toString();
				Node p = kb.addResource(MSEManagerEx.baseMSEURI + '#'
						+ pictureId, Picture.typeURI, client.getUsername());
				kb.addTriple(
						s,
						kb.getResourceByURINoPolicies(Person.PROPERTY_PICTURE_URI),
						p, client.getUsername());
				content.setBitmap(pictureId, item.getAvatarURL());
				update = true;
			} else {
				pictureId = pictureId.substring(pictureId.indexOf('#') + 1);

				if (content.getData(pictureId) == null) {
					content.setBitmap(pictureId, item.getAvatarURL());
				} else if (highres) {
					String avatarURL = item.getAvatarURL();
					if (avatarURL.endsWith("small")) {
						avatarURL = avatarURL.replace("small", "medium");
					}
					content.setBitmap(pictureId, item.getAvatarURL());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return update;
	}

	/**
	 * Creates a group with the given details.
	 * 
	 * @param item
	 * @param highres
	 *            should the image quality be high res?
	 * @return true if the group was updated.
	 */
	private boolean createGroup(GroupItem item, boolean highres) {
		boolean update = false;
		try {
			String groupId = Group.typeURI + "_" + item.getGuid();
			Node s = kb.getResourceByURINoPolicies(groupId);
			if (s == null) {
				update = true;
				s = kb.addResource(groupId, Group.typeURI, client.getUsername());
				log("createGroup<%s>", item.getGuid());
			}

			update |= ensureSimpleTriple(s, Group.PROPERTY_NAME_URI,
					item.getName());
			update |= ensureSimpleTriple(s, Group.PROPERTY_OWNERNAME_URI,
					item.getOwnerName());
			update |= ensureSimpleTriple(s, Group.PROPERTY_MEMBERS_URI,
					String.valueOf(item.getMembers()));

			String pictureId = getSimpleLiteral(s, Group.PROPERTY_PICTURE_URI);
			if (pictureId == null) {
				pictureId = UUID.randomUUID().toString();
				Node p = kb.addResource(MSEManagerEx.baseMSEURI + '#'
						+ pictureId, Picture.typeURI, client.getUsername());
				kb.addTriple(
						s,
						kb.getResourceByURINoPolicies(Group.PROPERTY_PICTURE_URI),
						p, client.getUsername());
				content.setBitmap(pictureId, item.getAvatarURL());
				update = true;
			} else {
				pictureId = pictureId.substring(pictureId.indexOf('#') + 1);
				
				if (content.getData(pictureId) == null) {
					content.setBitmap(pictureId, item.getAvatarURL());
				} else if (highres) {
					String avatarURL = item.getAvatarURL();
					if (avatarURL.endsWith("small")) {
						avatarURL = avatarURL.replace("small", "medium");
					}
					content.setBitmap(pictureId, item.getAvatarURL());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return update;
	}

	/**
	 * Makes sure the triple s p o exists in the KB.
	 * 
	 * Returns true if UI needs updates, false otherwise.
	 * 
	 * @throws KBException
	 */
	private boolean ensureSimpleTriple(Node s, String predicate, String object)
			throws KBException {
		if (object != null) {
			String value = getSimpleLiteral(s, predicate);

			if (!object.equals(value)) {
				addSimpleTriple(s, predicate, object);
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a simple triple.
	 * 
	 * @param s
	 * @param predicateURI
	 * @return
	 */
	private String getSimpleLiteral(Node s, String predicateURI) {
		String value = null;
		try {
			List<Triple> triples = kb.getPropertyObjectAsTriples(s,
					kb.getResourceByURINoPolicies(predicateURI),
					client.getUsername());
			for (Triple t : triples) {
				value = t.getObject().getName();
			}

		} catch (KBException ex) {
			ex.printStackTrace();
		}
		return value;
	}

	/**
	 * Adds a s, p, o triple where the object is a string.
	 * 
	 * @param s
	 * @param predicateURI
	 * @param literal
	 * @return
	 * @throws KBException
	 */
	private Triple addSimpleTriple(Node s, String predicateURI, String literal)
			throws KBException {
		return kb.addTriple(s, kb.getResourceByURINoPolicies(predicateURI),
				kb.addLiteral(literal, TYPE_STRING_URI, literal),
				client.getUsername());
	}

	private static void log(String format, Object... args) {
		YLoggerFactory.getLogger().d("IrisBridge", String.format(format, args));
	}

	private static final String TYPE_STRING_URI = "http://www.w3.org/2001/XMLSchema#string";
}

package fr.inria.arles.iris.bridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.graphics.Bitmap;

import fr.inria.arles.iris.web.ElggClient;
import fr.inria.arles.iris.web.GroupItem;
import fr.inria.arles.iris.web.MessageItem;
import fr.inria.arles.iris.web.PostItem;
import fr.inria.arles.iris.web.UserItem;
import fr.inria.arles.yarta.android.library.ContentClientPictures;
import fr.inria.arles.yarta.android.library.msemanagement.MSEManagerEx;
import fr.inria.arles.yarta.android.library.resources.Agent;
import fr.inria.arles.yarta.android.library.resources.Group;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.resources.Picture;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Conversation;
import fr.inria.arles.yarta.resources.Message;

/**
 * Contains utility functions which works with the KB or Content provider.
 */
public class YartaContentProxy {

	private static final String TYPE_STRING_URI = "http://www.w3.org/2001/XMLSchema#string";
	private KnowledgeBase kb;
	private ContentClientPictures content;
	/**
	 * Used for caching getResourceByURINoPolicies calls
	 */
	public Map<String, Node> nodesCache = new HashMap<String, Node>();

	public YartaContentProxy(KnowledgeBase kb, ContentClientPictures content) {
		this.kb = kb;
		this.content = content;
	}

	private String getUsername() {
		return ElggClient.getInstance().getUsername();
	}

	/**
	 * Fast returns a node by its URI using the cache.
	 * 
	 * @param uri
	 * @return
	 */
	public Node getNode(String uri) {
		Node result = nodesCache.get(uri);
		if (result == null) {
			result = kb.getResourceByURINoPolicies(uri);
			nodesCache.put(uri, result);
		}
		return result;
	}

	/**
	 * Gets the resource id from a node's URI
	 */
	public String getResourceId(Node node) {
		String resourceId = node.getName();
		resourceId = resourceId.substring(resourceId.indexOf('_') + 1);
		return resourceId;
	}

	/**
	 * Gets the node from a userId.
	 * 
	 * @param userId
	 * @return
	 */
	public Node getPersonNode(String userId) {
		String ownerId = Person.typeURI + "_" + userId;
		Node owner = getNode(ownerId);
		return owner;
	}

	/**
	 * Gets the node for a group.
	 * 
	 * @param group
	 *            uri
	 * @return
	 */
	public Node getGroupNode(String groupId) {
		String groupURI = Group.typeURI + "_" + groupId;
		return getNode(groupURI);
	}

	/**
	 * Ensures a triples s, p, o exists.
	 * 
	 * @param s
	 * @param p
	 * @param o
	 * @return
	 */
	public boolean ensureTriple(String s, String p, String o) {
		Node subject = getNode(s);
		Node predicate = getNode(p);
		Node object = getNode(o);

		try {
			if (kb.getTriple(subject, predicate, object, getUsername()) == null) {
				kb.addTriple(subject, predicate, object, getUsername());
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * Makes sure the triple s p o exists in the KB.
	 * 
	 * Returns true if UI needs updates, false otherwise.
	 * 
	 * @throws KBException
	 */
	public boolean ensureSimpleLiteral(Node s, String predicate, String object)
			throws KBException {
		if (object != null) {
			String value = getSimpleLiteral(s, predicate);

			if (!object.equals(value)) {
				// should remove previous literals
				List<Triple> prevProps = kb.getPropertyObjectAsTriples(s,
						getNode(predicate), getUsername());
				for (Triple t : prevProps) {
					kb.removeTriple(t.getSubject(), t.getProperty(),
							t.getObject(), getUsername());
				}

				addSimpleLiteral(s, predicate, object);
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the Object of this query as a String
	 * 
	 * @param s
	 * @param predicateURI
	 * @return
	 */
	public String getSimpleLiteral(Node s, String predicateURI) {
		String value = null;
		try {
			Node predicate = getNode(predicateURI);
			// predicate might not be in the KB
			if (predicate != null) {
				List<Triple> triples = kb.getPropertyObjectAsTriples(s,
						predicate, getUsername());
				for (Triple t : triples) {
					value = t.getObject().getName();
				}
			}
		} catch (KBException ex) {
			ex.printStackTrace();
		}
		return value;
	}

	/**
	 * Adds a s, p, o triple where the object is a literal.
	 * 
	 * @param s
	 * @param predicateURI
	 * @param literal
	 * @return
	 * @throws KBException
	 */
	private Triple addSimpleLiteral(Node s, String predicateURI, String literal)
			throws KBException {
		return kb
				.addTriple(s, getNode(predicateURI),
						kb.addLiteral(literal, TYPE_STRING_URI, literal),
						getUsername());
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
	public boolean createPerson(UserItem item, boolean highres) {
		boolean update = false;
		try {
			String userUniqueId = Person.typeURI + "_" + item.getUsername();
			Node s = getNode(userUniqueId);
			if (s == null) {
				update = true;
				s = kb.addResource(userUniqueId, Person.typeURI, getUsername());
				log("createPerson<%s>", item.getUsername());
			}

			update |= ensureSimpleLiteral(s, Person.PROPERTY_USERID_URI,
					item.getUsername());
			update |= ensureSimpleLiteral(s, Person.PROPERTY_NAME_URI,
					item.getName());
			update |= ensureSimpleLiteral(s, Person.PROPERTY_ROOM_URI,
					item.getRoom());
			update |= ensureSimpleLiteral(s, Person.PROPERTY_PHONE_URI,
					item.getPhone());
			update |= ensureSimpleLiteral(s, Person.PROPERTY_LOCATION_URI,
					item.getLocation());

			String pictureId = getSimpleLiteral(s, Person.PROPERTY_PICTURE_URI);
			if (pictureId == null) {
				pictureId = UUID.randomUUID().toString();
				Node p = kb.addResource(MSEManagerEx.baseMSEURI + '#'
						+ pictureId, Picture.typeURI, getUsername());
				kb.addTriple(s, getNode(Person.PROPERTY_PICTURE_URI), p,
						getUsername());
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
	public boolean createGroup(GroupItem item, boolean highres) {
		boolean update = false;
		try {
			String groupId = Group.typeURI + "_" + item.getGuid();
			Node s = getNode(groupId);
			if (s == null) {
				update = true;
				s = kb.addResource(groupId, Group.typeURI, getUsername());
				log("createGroup<%s>", item.getGuid());
			}

			update |= ensureSimpleLiteral(s, Group.PROPERTY_NAME_URI,
					item.getName());
			update |= ensureSimpleLiteral(s, Group.PROPERTY_OWNERNAME_URI,
					item.getOwnerName());
			update |= ensureSimpleLiteral(s, Group.PROPERTY_MEMBERS_URI,
					String.valueOf(item.getMembers()));
			update |= ensureSimpleLiteral(s, Group.PROPERTY_DESCRIPTION_URI,
					item.getDescription());

			String pictureId = getSimpleLiteral(s, Group.PROPERTY_PICTURE_URI);
			if (pictureId == null) {
				pictureId = UUID.randomUUID().toString();
				Node p = kb.addResource(MSEManagerEx.baseMSEURI + '#'
						+ pictureId, Picture.typeURI, getUsername());
				kb.addTriple(s, getNode(Group.PROPERTY_PICTURE_URI), p,
						getUsername());

				content.setBitmap(pictureId, getGroupIcon(item, highres));
				update = true;
			} else {
				pictureId = pictureId.substring(pictureId.indexOf('#') + 1);

				if (content.getData(pictureId) == null) {
					content.setBitmap(pictureId, getGroupIcon(item, highres));
				} else if (highres) {
					content.setBitmap(pictureId, getGroupIcon(item, true));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return update;
	}

	private Bitmap getGroupIcon(GroupItem group, boolean highres) {
		return ElggClient.getInstance().getGroupIcon(group.getGuid(),
				highres ? "large" : "medium");
	}

	/**
	 * Add a bi-directional knows link between owner and this user.
	 * 
	 * @param item
	 * @return
	 */
	public boolean addKnows(UserItem item) {
		boolean update = false;

		String reqId = getUsername();

		Node owner = getPersonNode(reqId);
		Node user = getPersonNode(item.getUsername());

		Node p = getNode(Person.PROPERTY_KNOWS_URI);

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
	public boolean addIsMemberOf(GroupItem item) {
		boolean update = false;

		String reqId = getUsername();

		Node owner = getPersonNode(reqId);
		Node group = getGroupNode(item.getGuid());
		Node isMemberOf = getNode(Agent.PROPERTY_ISMEMBEROF_URI);

		try {
			if (kb.getTriple(owner, isMemberOf, group, reqId) == null) {
				kb.addTriple(owner, isMemberOf, group, reqId);
				update = true;
			}
		} catch (KBException ex) {
			ex.printStackTrace();
		}

		return update;
	}

	/**
	 * Removes the IsMemberOf link between this users and existing groups which
	 * are not on the set.
	 * 
	 * @param user
	 * @param groupIds
	 * @return
	 */
	public boolean removeIsMemberOf(Node user, Set<String> groupIds) {
		Node memberOf = getNode(Agent.PROPERTY_ISMEMBEROF_URI);
		boolean update = false;

		try {
			List<Triple> triples = kb.getPropertyObjectAsTriples(user,
					memberOf, getUsername());

			for (Triple triple : triples) {
				String groupId = getResourceId(triple.getObject());

				// no longer or not yet a member
				if (!groupIds.contains(groupId)) {
					log("removeIsMemberOf <%s>", groupId);

					Node groupNode = getGroupNode(groupId);
					kb.removeTriple(user, memberOf, groupNode, getUsername());

					update |= true;
				}
			}
		} catch (Exception ex) {
		}
		return update;
	}

	public boolean createPost(PostItem item) {
		boolean update = false;

		String reqId = getUsername();
		String postId = Content.typeURI + "_" + item.getGuid();

		try {
			Node post = getNode(postId);
			if (post == null) {
				update = true;
				post = kb.addResource(postId, Content.typeURI, reqId);
				log("createPost<%s>", item.getGuid());
			}

			update |= ensureSimpleLiteral(post, Content.PROPERTY_TITLE_URI,
					item.getTitle());
			update |= ensureSimpleLiteral(post, Content.PROPERTY_CONTENT_URI,
					item.getDescription());
			update |= ensureSimpleLiteral(post, Content.PROPERTY_TIME_URI,
					String.valueOf(item.getTime()));

			// set creator, make sure it exists
			update |= createPerson(item.getOwner(), false);

			String userId = Person.typeURI + "_"
					+ item.getOwner().getUsername();
			update |= ensureTriple(userId, Agent.PROPERTY_CREATOR_URI, postId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return update;
	}

	public boolean createMessage(MessageItem item, Node conversation[]) {
		boolean update = false;

		Node contains = getNode(Conversation.PROPERTY_CONTAINS_URI);

		try {
			String messageId = Message.typeURI + "_" + item.getGuid();
			Node s = getNode(messageId);
			if (s == null) {
				update = true;
				s = kb.addResource(messageId, Message.typeURI, getUsername());
				log("createMessage<%s>", item.getGuid());
			}

			update |= ensureSimpleLiteral(s, Message.PROPERTY_TITLE_URI,
					item.getSubject());
			update |= ensureSimpleLiteral(s, Message.PROPERTY_CONTENT_URI,
					item.getDescription());
			update |= ensureSimpleLiteral(s, Message.PROPERTY_TIME_URI,
					String.valueOf(item.getTimestamp()));
			update |= createPerson(item.getFrom(), false);

			String reqId = getUsername();

			// set creator
			String userId = Person.typeURI + "_"
					+ (item.isSent() ? reqId : item.getFrom().getUsername());
			update |= ensureTriple(userId, Agent.PROPERTY_CREATOR_URI,
					messageId);

			// get linked conversation
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

	public boolean createConversation(List<MessageItem> messages) {
		boolean update = false;

		String reqId = getUsername();

		// to be able to pass it as ref
		Node conversation[] = new Node[1];
		List<Node> peers = new ArrayList<Node>();

		for (MessageItem item : messages) {
			update |= createMessage(item, conversation);
			peers.add(getPersonNode(item.getFrom().getUsername()));
		}

		try {
			if (conversation[0] == null) {
				String conversationId = UUID.randomUUID().toString();
				conversation[0] = kb.addResource(MSEManagerEx.baseMSEURI + '#'
						+ conversationId, Conversation.typeURI, reqId);
				update = true;
			}

			for (Node peer : peers) {
				update |= ensureTriple(peer.getName(),
						Person.PROPERTY_PARTICIPATESTO_URI,
						conversation[0].getName());
			}

			for (MessageItem item : messages) {
				String messageId = Message.typeURI + "_" + item.getGuid();
				update |= ensureTriple(conversation[0].getName(),
						Conversation.PROPERTY_CONTAINS_URI, messageId);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return update;
	}

	private void log(String format, Object... args) {
		YLoggerFactory.getLogger().d("YartaProxy", String.format(format, args));
	}
}
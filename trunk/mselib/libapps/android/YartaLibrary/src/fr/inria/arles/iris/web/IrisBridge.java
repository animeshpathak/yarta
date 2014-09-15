package fr.inria.arles.iris.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.content.Context;
import fr.inria.arles.iris.web.ElggClient.WebCallback;
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
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Conversation;
import fr.inria.arles.yarta.resources.Message;

/**
 * This class contains useful methods to fetch data into KB using the Iris web
 * client.
 */
public class IrisBridge implements WebCallback {

	private static ElggClient client = ElggClient.getInstance();
	private Context context;
	private MSEApplication app;
	private MSEKnowledgeBase kb;
	private ContentClientPictures content;
	private boolean loggedin = false;
	private boolean nointernet = false;
	private IrisBridgeUtils util;

	public IrisBridge(Context context, MSEApplication app, KnowledgeBase kb,
			ContentClientPictures content) {
		this.context = context;
		this.app = app;
		this.kb = (MSEKnowledgeBase) kb;
		this.content = content;

		util = new IrisBridgeUtils(kb);

		client.addCallback(this);

		queueThread.start();
	}

	@Override
	public void onAuthenticationFailed() {
		loggedin = false;
		Notification.showLogin(context);
	}

	@Override
	public void onAuthentication() {
		loggedin = true;
		Notification.hideLogin(context);
	}

	@Override
	public void onNetworkFailed() {
		// no internet
	}

	/********************** WRITE CALLBACKS ***************************************/

	/**
	 * Gets fired when a new resource is being added.
	 * 
	 * @param nodeURI
	 * @param typeURI
	 */
	public void onAddResource(String nodeURI, String typeURI) {
		lastWrite = System.currentTimeMillis();
		synchronized (resourceQueue) {
			if (typeURI.equals(Content.typeURI)
					|| typeURI.equals(Message.typeURI)) {
				resourceQueue.add(new QueueItem(nodeURI, typeURI));
			}
		}
	}

	/**
	 * Gets fired when a new triple is being added.
	 * 
	 * @param sub
	 * @param pre
	 * @param obj
	 */
	public void onAddTriple(Node sub, Node pre, Node obj) {
		lastWrite = System.currentTimeMillis();

		synchronized (resourceQueue) {
			if (pre.getName().equals(Agent.PROPERTY_ISMEMBEROF_URI)) {
				resourceQueue.add(new QueueItem(sub, pre, obj));
			}
		}
	}

	static class QueueItem {
		public String nodeURI;
		public String typeURI;

		public QueueItem(String nodeURI, String typeURI) {
			this.nodeURI = nodeURI;
			this.typeURI = typeURI;
		}

		public Node s;
		public Node p;
		public Node o;

		public QueueItem(Node s, Node p, Node o) {
			this.s = s;
			this.p = p;
			this.o = o;
		}

		public boolean isTriple() {
			return s != null && p != null && o != null;
		}
	}

	private static final long IdleTimeout = 1000;
	private long lastWrite = 0;
	private List<QueueItem> resourceQueue = new ArrayList<IrisBridge.QueueItem>();
	private Runnable queueTask = new Runnable() {

		@Override
		public void run() {
			boolean forever = true;
			while (forever) {
				try {
					while (!loggedin) {
						Thread.sleep(1000);
					}
					long now = System.currentTimeMillis();
					if (now - lastWrite >= IdleTimeout) {
						synchronized (resourceQueue) {
							dequeueItems();
						}
					}
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					break;
				}
			}
		}
	};
	private Thread queueThread = new Thread(queueTask);

	private void dequeueItems() {
		while (resourceQueue.size() > 0) {
			QueueItem resource = resourceQueue.get(0);
			try {
				nointernet = false;
				int result = ElggClient.RESULT_OK;

				if (resource.isTriple()) {
					result = processAddedTriple(resource.s, resource.p,
							resource.o);
				} else {
					result = processCreatedResource(resource.nodeURI,
							resource.typeURI);
				}

				switch (result) {
				case ElggClient.RESULT_AUTH_FAILED:
					loggedin = false;
					break;
				case ElggClient.RESULT_NO_NET:
					nointernet = true;
					break;
				case ElggClient.RESULT_OK:
				case ElggClient.RESULT_ERROR:
					resourceQueue.remove(0);
					break;
				}

				if (!loggedin || nointernet)
					break;
			} catch (Exception ex) {
				resourceQueue.remove(0);
				ex.printStackTrace();
			}
		}
	}

	// TODO: friends, groups, etc.
	private int processAddedTriple(Node s, Node p, Node o) throws Exception {
		log("processAddedTriple(%s, %s, %s)", s.getName(), p.getName(),
				o.getName());
		int result = ElggClient.RESULT_OK;

		if (p.getName().equals(Agent.PROPERTY_ISMEMBEROF_URI)) {
			// get the group
			String userId = util.getResourceId(s);
			String groupId = util.getResourceId(o);

			// not owner, remove triple
			if (!userId.equals(client.getUsername())) {
				kb.removeTriple(s, p, o, client.getUsername());
			} else {
				result = client.joinGroup(groupId);

				switch (result) {
				case ElggClient.RESULT_NO_NET:
				case ElggClient.RESULT_AUTH_FAILED:
					break;
				case ElggClient.RESULT_OK:
					app.handleNotification("group join sent.");
					break;
				default:
					app.handleNotification("group join error.");
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Processes a previously created resource. Returns an Elgg return code, OK
	 * if the resource is of no interest.
	 * 
	 * @param nodeURI
	 * @param typeURI
	 * @return
	 * @throws Exception
	 */
	private int processCreatedResource(String nodeURI, String typeURI)
			throws Exception {
		log("processCreatedResource(%s, %s)", nodeURI, typeURI);
		int result = ElggClient.RESULT_OK;

		if (typeURI.equals(Content.typeURI)) {
			Node hasReply = util.getNode(Content.PROPERTY_HASREPLY_URI);
			Node comment = util.getNode(nodeURI);
			List<Triple> triples = kb.getPropertySubjectAsTriples(hasReply,
					comment, client.getUsername());

			// is a comment
			if (triples.size() > 0) {
				log("processCreatedResource(%s, %s)", nodeURI, typeURI);

				String postId = triples.get(0).getSubject().getName();
				postId = postId.substring(postId.indexOf('_') + 1);

				String title = util.getSimpleLiteral(comment,
						Content.PROPERTY_TITLE_URI);

				result = client.addGroupPostComment(postId, title);

				switch (result) {
				case ElggClient.RESULT_OK:
					kb.removeResource(nodeURI);
					ensurePostComments(triples.get(0).getSubject());
					break;
				}
			} else {
				// is a content in a group
				Node hasContent = util.getNode(Group.PROPERTY_HASCONTENT_URI);
				triples = kb.getPropertySubjectAsTriples(hasContent, comment,
						client.getUsername());

				if (triples.size() > 0) {
					log("processCreatedResource(%s, %s)", nodeURI, typeURI);

					String groupId = triples.get(0).getSubject().getName();
					groupId = groupId.substring(groupId.indexOf('_') + 1);

					String title = util.getSimpleLiteral(comment,
							Content.PROPERTY_TITLE_URI);
					String description = util.getSimpleLiteral(comment,
							Content.PROPERTY_CONTENT_URI);

					result = client.addGroupPost(groupId, title, description);

					switch (result) {
					case ElggClient.RESULT_OK:
						kb.removeResource(nodeURI);
						app.handleNotification("post created.");
						break;
					}
				}
			}
		}

		return result;
	}

	/********************** READ CALLBACKS ****************************************/

	/**
	 * Ensures a given p, o triple will exist.
	 * 
	 * @param predicate
	 * @param object
	 */
	public void onGetPropertySubjectAsTriples(final Node predicate,
			final Node object) {
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

	public void onGetPropertyObjectAsTriples(final Node subject,
			final Node predicate) {
		final String s = subject.getName();
		final String p = predicate.getName();
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (p.equals(Person.PROPERTY_KNOWS_URI)) {
					ensureFriends(subject);
				} else if (p.equals(Agent.PROPERTY_ISMEMBEROF_URI)) {
					ensureGroups(subject);
				} else if (p.equals(Group.PROPERTY_HASCONTENT_URI)) {
					ensurePosts(subject);
				} else if (p.equals(Content.PROPERTY_HASREPLY_URI)) {
					ensurePostComments(subject);
				} else if (s.contains(Group.typeURI)) {
					if (util.getSimpleLiteral(subject, p) == null) {
						String groupId = s.substring(s.indexOf('_') + 1);

						GroupItem group = client.getGroup(groupId);
						if (createGroup(group, true)) {
							app.handleNotification("group " + groupId
									+ " updated.");
						}
					}
				} else if (s.contains(Content.typeURI)) {
					if (util.getSimpleLiteral(subject, p) == null) {
						String postId = s.substring(s.indexOf('_') + 1);

						PostItem post = client.getGroupPost(postId);
						if (post != null && createPost(post)) {
							app.handleNotification("post " + postId
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
		String userId = util.getResourceId(node);

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
		String userId = util.getResourceId(node);

		Set<String> groupIds = new HashSet<String>();
		List<GroupItem> groups = client.getGroups(userId);

		boolean update = false;
		for (GroupItem group : groups) {
			update |= createGroup(group, false);
			update |= addIsMemberOf(group);

			// add for re-check
			groupIds.add(group.getGuid());
		}

		// check if no longer member of existing groups
		Node memberOf = util.getNode(Agent.PROPERTY_ISMEMBEROF_URI);
		try {
			List<Triple> triples = kb.getPropertyObjectAsTriples(node,
					memberOf, client.getUsername());

			for (Triple triple : triples) {
				String groupId = util.getResourceId(triple.getObject());

				// no longer or not yet a member
				if (!groupIds.contains(groupId)) {
					log("ensureGroups removed %s", groupId);

					Node groupNode = util.getGroupNode(groupId);
					kb.removeTriple(node, memberOf, groupNode,
							client.getUsername());

					update |= true;
				}
			}
		} catch (Exception ex) {
		}

		if (update) {
			app.handleNotification("group list updated");
		}
	}

	/**
	 * Ensure the existence of posts in a group.
	 * 
	 * @param node
	 */
	private void ensurePosts(Node node) {
		String groupId = util.getResourceId(node);

		List<PostItem> posts = client.getGroupPosts(groupId);

		boolean update = false;

		// check if group does exist
		if (util.getNode(node.getName()) == null) {
			GroupItem group = client.getGroup(groupId);
			update |= createGroup(group, false);
		}

		for (PostItem post : posts) {
			update |= createPost(post);
			update |= ensureTriple(node.getName(),
					Group.PROPERTY_HASCONTENT_URI,
					Content.typeURI + "_" + post.getGuid());
		}

		if (update) {
			app.handleNotification("post list updated.");
		}
	}

	private boolean ensureTriple(String s, String p, String o) {
		Node subject = util.getNode(s);
		Node predicate = util.getNode(p);
		Node object = util.getNode(o);

		try {
			if (kb.getTriple(subject, predicate, object, client.getUsername()) == null) {
				kb.addTriple(subject, predicate, object, client.getUsername());
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private void ensurePostComments(Node node) {
		String postId = util.getResourceId(node);

		List<PostItem> posts = client.getGroupPostComments(postId);

		boolean update = false;
		for (PostItem post : posts) {
			update |= createPost(post);
			String replyId = Content.typeURI + "_" + post.getGuid();
			update |= ensureTriple(node.getName(),
					Content.PROPERTY_HASREPLY_URI, replyId);
		}

		if (update) {
			app.handleNotification("post comments updated.");
		}
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

		Node owner = util.getPersonNode(reqId);
		Node user = util.getPersonNode(item.getUsername());

		Node p = util.getNode(Person.PROPERTY_KNOWS_URI);

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

		Node owner = util.getPersonNode(reqId);
		Node group = util.getGroupNode(item.getGuid());
		Node isMemberOf = util.getNode(Agent.PROPERTY_ISMEMBEROF_URI);

		try {
			if (kb.getTriple(owner, isMemberOf, group, reqId) == null) {
				kb.addTriple(owner, isMemberOf, group, client.getUsername());
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
			Node s = util.getNode(userUniqueId);
			if (s == null) {
				update = true;
				s = kb.addResource(userUniqueId, Person.typeURI,
						client.getUsername());
				log("createPerson<%s>", item.getUsername());
			}

			update |= util.ensureSimpleLiteral(s, Person.PROPERTY_USERID_URI,
					item.getUsername());
			update |= util.ensureSimpleLiteral(s, Person.PROPERTY_NAME_URI,
					item.getName());
			update |= util.ensureSimpleLiteral(s, Person.PROPERTY_ROOM_URI,
					item.getRoom());
			update |= util.ensureSimpleLiteral(s, Person.PROPERTY_PHONE_URI,
					item.getPhone());
			update |= util.ensureSimpleLiteral(s, Person.PROPERTY_LOCATION_URI,
					item.getLocation());

			String pictureId = util.getSimpleLiteral(s,
					Person.PROPERTY_PICTURE_URI);
			if (pictureId == null) {
				pictureId = UUID.randomUUID().toString();
				Node p = kb.addResource(MSEManagerEx.baseMSEURI + '#'
						+ pictureId, Picture.typeURI, client.getUsername());
				kb.addTriple(s, util.getNode(Person.PROPERTY_PICTURE_URI), p,
						client.getUsername());
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
			Node s = util.getNode(groupId);
			if (s == null) {
				update = true;
				s = kb.addResource(groupId, Group.typeURI, client.getUsername());
				log("createGroup<%s>", item.getGuid());
			}

			update |= util.ensureSimpleLiteral(s, Group.PROPERTY_NAME_URI,
					item.getName());
			update |= util.ensureSimpleLiteral(s, Group.PROPERTY_OWNERNAME_URI,
					item.getOwnerName());
			update |= util.ensureSimpleLiteral(s, Group.PROPERTY_MEMBERS_URI,
					String.valueOf(item.getMembers()));

			String pictureId = util.getSimpleLiteral(s,
					Group.PROPERTY_PICTURE_URI);
			if (pictureId == null) {
				pictureId = UUID.randomUUID().toString();
				Node p = kb.addResource(MSEManagerEx.baseMSEURI + '#'
						+ pictureId, Picture.typeURI, client.getUsername());
				kb.addTriple(s, util.getNode(Group.PROPERTY_PICTURE_URI), p,
						client.getUsername());
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

	private boolean createConversation(List<MessageItem> messages) {
		boolean update = false;

		String reqId = client.getUsername();

		// to be able to pass it as ref
		Node conversation[] = new Node[1];
		List<Node> peers = new ArrayList<Node>();

		for (MessageItem item : messages) {
			update |= createMessage(item, conversation);
			peers.add(util.getPersonNode(item.getFrom().getUsername()));
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

	private boolean createPost(PostItem item) {
		boolean update = false;

		String reqId = client.getUsername();
		String postId = Content.typeURI + "_" + item.getGuid();

		try {
			Node post = util.getNode(postId);
			if (post == null) {
				update = true;
				post = kb.addResource(postId, Content.typeURI, reqId);
				log("createPost<%s>", item.getGuid());
			}

			update |= util.ensureSimpleLiteral(post,
					Content.PROPERTY_TITLE_URI, item.getTitle());
			update |= util.ensureSimpleLiteral(post,
					Content.PROPERTY_CONTENT_URI, item.getDescription());
			update |= util.ensureSimpleLiteral(post, Content.PROPERTY_TIME_URI,
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

	private boolean createMessage(MessageItem item, Node conversation[]) {
		boolean update = false;

		Node contains = util.getNode(Conversation.PROPERTY_CONTAINS_URI);

		try {
			String messageId = Message.typeURI + "_" + item.getGuid();
			Node s = util.getNode(messageId);
			if (s == null) {
				update = true;
				s = kb.addResource(messageId, Message.typeURI,
						client.getUsername());
				log("createMessage<%s>", item.getGuid());
			}

			update |= util.ensureSimpleLiteral(s, Message.PROPERTY_TITLE_URI,
					item.getSubject());
			update |= util.ensureSimpleLiteral(s, Message.PROPERTY_CONTENT_URI,
					item.getDescription());
			update |= util.ensureSimpleLiteral(s, Message.PROPERTY_TIME_URI,
					String.valueOf(item.getTimestamp()));
			update |= createPerson(item.getFrom(), false);

			String reqId = client.getUsername();

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

	private static void log(String format, Object... args) {
		YLoggerFactory.getLogger().d("IrisBridge", String.format(format, args));
	}
}
package fr.inria.arles.iris.bridge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import fr.inria.arles.iris.web.ElggClient;
import fr.inria.arles.iris.web.GroupItem;
import fr.inria.arles.iris.web.MessageItem;
import fr.inria.arles.iris.web.Notification;
import fr.inria.arles.iris.web.PostItem;
import fr.inria.arles.iris.web.UserItem;
import fr.inria.arles.iris.web.ElggClient.WebCallback;
import fr.inria.arles.yarta.android.library.ContentClientPictures;
import fr.inria.arles.yarta.android.library.resources.Agent;
import fr.inria.arles.yarta.android.library.resources.Group;
import fr.inria.arles.yarta.android.library.resources.Person;
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

	private MSEApplication app;
	private MSEKnowledgeBase kb;

	private boolean loggedin = false;
	private boolean nointernet = false;

	private YartaContentProxy util;
	private Notification notification;

	public IrisBridge(Context context, MSEApplication app, KnowledgeBase kb,
			ContentClientPictures content) {
		this.app = app;
		this.kb = (MSEKnowledgeBase) kb;

		util = new YartaContentProxy(kb, content);
		notification = new Notification(context);

		client.addCallback(this);

		queueThread.start();
	}

	@Override
	public void onAuthenticationFailed() {
		loggedin = false;
		notification.showLogin();
	}

	@Override
	public void onAuthentication() {
		loggedin = true;
		notification.hideLogin();
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
			result = ensureGroupJoin(s, o);
		}

		// add knows
		// add content to a Group (Iris)
		// add content to a Content (if Iris Content, reply)
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
						notifyApp("post created.");
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
					ensurePerson(object);
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
						ensureGroup(subject);
					}
				} else if (s.contains(Content.typeURI)) {
					if (util.getSimpleLiteral(subject, p) == null) {
						ensurePost(subject);
					}
				}
			}
		}).start();
	}

	/**
	 * Ensures that a join request between S and O is made, otherwise remove
	 * IsMemberOf
	 * 
	 * @param s
	 * @param o
	 * 
	 * @return ELGG error code
	 */
	private int ensureGroupJoin(Node s, Node o) {
		int result = ElggClient.RESULT_OK;

		String userId = util.getResourceId(s);
		String groupId = util.getResourceId(o);

		Node memberOf = util.getNode(Agent.PROPERTY_ISMEMBEROF_URI);

		// not owner, remove triple
		if (!userId.equals(client.getUsername())) {
			try {
				kb.removeTriple(s, memberOf, o, client.getUsername());
			} catch (Exception ex) {
				// TODO: move it to kb
			}
		} else {
			result = client.joinGroup(groupId);

			switch (result) {
			case ElggClient.RESULT_NO_NET:
			case ElggClient.RESULT_AUTH_FAILED:
				break;
			case ElggClient.RESULT_OK:
				notifyApp("group join sent.");
				break;
			default:
				notifyApp("group join error.");
				break;
			}
		}

		return result;
	}

	private void ensureConversations() {
		boolean update = false;

		List<List<MessageItem>> conversations = client.getMessageThreads();

		for (List<MessageItem> conversation : conversations) {
			update |= util.createConversation(conversation);
		}

		if (update) {
			notifyApp("conversations updated.");
		}
	}

	private void ensurePerson(Node node) {
		UserItem user = client.getUser(node.getName());

		if (user != null) {
			if (util.createPerson(user, true)) {
				notifyApp("person <%s> updated.", user.getUsername());
			}
		}
	}

	private void ensureGroup(Node node) {
		String s = node.getName();
		String groupId = s.substring(s.indexOf('_') + 1);

		GroupItem group = client.getGroup(groupId);
		if (util.createGroup(group, true)) {
			notifyApp("group <%s> updated.", groupId);
		}
	}

	private void ensurePost(Node node) {
		String s = node.getName();
		String postId = s.substring(s.indexOf('_') + 1);

		PostItem post = client.getGroupPost(postId);
		if (post != null && util.createPost(post)) {
			notifyApp("post <%s> updated.", postId);
		}
	}

	private void ensureFriends(Node node) {
		String userId = util.getResourceId(node);

		List<UserItem> users = client.getFriends(userId, 0);

		boolean update = false;
		for (UserItem user : users) {
			update |= util.createPerson(user, false);
			update |= util.addKnows(user);
		}

		if (update) {
			notifyApp("friend list updated");
		}
	}

	private void ensureGroups(Node node) {
		String userId = util.getResourceId(node);

		Set<String> groupIds = new HashSet<String>();
		List<GroupItem> groups = client.getGroups(userId);

		boolean update = false;
		for (GroupItem group : groups) {
			update |= util.createGroup(group, false);
			update |= util.addIsMemberOf(group);

			// add for re-check
			groupIds.add(group.getGuid());
		}

		update |= util.removeIsMemberOf(node, groupIds);

		if (update) {
			notifyApp("group list updated");
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
			update |= util.createGroup(group, false);
		}

		for (PostItem post : posts) {
			update |= util.createPost(post);
			update |= util.ensureTriple(node.getName(),
					Group.PROPERTY_HASCONTENT_URI,
					Content.typeURI + "_" + post.getGuid());
		}

		if (update) {
			notifyApp("post list updated.");
		}
	}

	private void ensurePostComments(Node node) {
		String postId = util.getResourceId(node);

		List<PostItem> posts = client.getGroupPostComments(postId);

		boolean update = false;
		for (PostItem post : posts) {
			update |= util.createPost(post);

			// TODO: add creator here

			String replyId = Content.typeURI + "_" + post.getGuid();
			update |= util.ensureTriple(node.getName(),
					Content.PROPERTY_HASREPLY_URI, replyId);
		}

		if (update) {
			notifyApp("post <%s> comments updated.", postId);
		}
	}

	/**
	 * Sends a notification to the registered MSEApplication.
	 * 
	 * @param format
	 * @param args
	 */
	private void notifyApp(String format, Object... args) {
		app.handleNotification(String.format(format, args));
	}

	private static void log(String format, Object... args) {
		YLoggerFactory.getLogger().d("IrisBridge", String.format(format, args));
	}
}
package fr.inria.arles.yarta.middleware.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.MSELiteral;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.knowledgebase.MSETriple;
import fr.inria.arles.yarta.knowledgebase.UpdateHelper;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.ThinKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.ContentClient;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import fr.inria.arles.yarta.middleware.msemanagement.MSEManager;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.yarta.resources.YartaResource;

/**
 * Provides interfaces to the MSE middle-ware for communicating with other
 * nodes.
 */
public class YCommunicationManager implements CommunicationManager, Receiver {

	private String userId;
	private Connection connection;
	private MSEApplication application;
	private KnowledgeBase knowledgeBase;

	/**
	 * This is only a copy of the original one. Used for its higher level
	 * capabilities to work with information.
	 */
	private StorageAccessManager accessManager;

	/**
	 * the programmer's message receiver
	 */
	private Receiver messageReceiver;

	/**
	 * Logger specific objects
	 */
	private static YLogger log = YLoggerFactory.getLogger();
	private static final String LOGTAG = "Yarta-CommManager";

	/** response codes for messages */
	public static final String UPDATE_SUCCESS = "UpdateSuccess";
	public static final String UPDATE_FAILED = "UpdateFailed";
	public static final String UPDATE_EMPTY = "UpdateEmtpy";

	/**
	 * Content client useful in updates;
	 */
	private ContentClient client;

	/**
	 * initialization code. Currently only sets up local IBIURL
	 * 
	 * @param userID
	 *            The user ID.
	 * @param kb
	 *            KnowledgeBase
	 * @param context
	 *            The context. Null for J2SE for PC. Android context for
	 *            Android.
	 */
	@Override
	public int initialize(String selfId, KnowledgeBase knowledgeBase,
			MSEApplication application, Object context) {
		log.d(LOGTAG, "initializing");

		this.userId = selfId;
		this.knowledgeBase = knowledgeBase;
		this.application = application;

		accessManager = new StorageAccessManager();
		accessManager.setOwnerID(selfId);
		accessManager.setKnowledgeBase(knowledgeBase);

		if (context != null) {
			connection = new fr.inria.arles.yarta.middleware.communication.CommPushConnection(
					selfId);
			client = new fr.inria.arles.yarta.android.library.ContentClientAndroid(
					context);
		} else {
			connection = new CommPollConnection(selfId);
			client = new fr.inria.arles.yarta.desktop.library.ContentClientDesktop();
		}

		synchronized (connection) {
			connection.setReceiver(this);
			connection.init(context);
		}

		return 0;
	}

	/**
	 * Does the proper initialization of the communication manager.
	 * 
	 * @return true if all goes well. False if not.
	 */
	@Override
	public int uninitialize() {
		log.d(LOGTAG, "uninitializing");

		synchronized (connection) {
			connection.uninit();
			connection = null;
		}

		return 0;
	}

	@Override
	public int clear() {
		return 0;
	}

	/**
	 * Sends HELLO to a peer.
	 * 
	 * @param partnerUID
	 *            the id of the peer where the data needs to be sent to
	 * @return connection id - returns -1 if unsuccessful
	 * 
	 * @throws KBException
	 */
	@Override
	public int sendHello(String partnerUID) throws KBException {
		log.d(LOGTAG, "send HELLO to " + partnerUID);

		List<Triple> selfTriples = getSelfTriples();
		Person me = accessManager.getMe();

		if (selfTriples == null || selfTriples.size() == 0) {
			return -1;
		}

		((MSEKnowledgeBase) knowledgeBase).createPerson(partnerUID);

		HelloMessage helloMessage = new HelloMessage(new BasicPersonInfo(
				me.getFirstName(), me.getLastName(), me.getUserId(),
				me.getEmail(), selfTriples.get(0), selfTriples.get(1)));

		Message message = new Message(Message.TYPE_HELLO,
				YCommunicationManagerUtils.toBytes(helloMessage));

		synchronized (connection) {
			return connection.postMessage(partnerUID, message);
		}
	}

	/**
	 * Sends PUSH to a peer containing a list of triples describing a resource.
	 * 
	 * @param peerId
	 *            the id of the peer where the data needs to be sent to
	 * @param uniqueId
	 *            the id of the resource which is going to be sent
	 * @return connection id; -1 if unsuccessful
	 */
	@Override
	public int sendResource(String peerId, String uniqueId) {
		log.d(LOGTAG, "send PUSH to " + peerId + " about " + uniqueId);

		List<Triple> triples = new ArrayList<Triple>();
		try {
			triples.addAll(knowledgeBase.getAllPropertiesAsTriples(
					knowledgeBase.getResourceByURINoPolicies(uniqueId), userId));
		} catch (KBException ex) {
			log.e(LOGTAG, "sendResource exception!");
			ex.printStackTrace();
		}

		if (triples.size() > 0) {
			Message message = new Message(Message.TYPE_PUSH,
					YCommunicationManagerUtils.toBytes(new TriplesMessage(
							triples)));
			synchronized (connection) {
				return connection.postMessage(peerId, message);
			}
		} else {
			log.d(LOGTAG, "0 triples about the requested resource.");
		}
		return -1;
	}

	/**
	 * Ask a peer to retrieve his triples
	 * 
	 * @param partnerUID
	 *            the id of the peer where the data needs to be read from
	 * @return connection id - returns -1 if unsuccessful
	 * 
	 * @throws KBException
	 */
	@Override
	public int sendUpdateRequest(String peerId) throws KBException {
		log.d(LOGTAG, "send UPDATE to " + peerId);

		Long lastUpdate = helper.getTime(peerId);

		Message message = new Message(Message.TYPE_UPDATE,
				YCommunicationManagerUtils.toBytes(lastUpdate));

		synchronized (connection) {
			return connection.postMessage(peerId, message);
		}
	}

	public int sendNotify(String peerId) {
		synchronized (connection) {
			// TODO: sync on a connection level
			return connection.postMessage(peerId, new NotifyMessage());
		}
	}

	@Override
	public int sendMessage(String peerId, Message message) {
		log.d(LOGTAG, "sending message to " + peerId);
		synchronized (connection) {
			return connection.postMessage(peerId, message);
		}
	}

	@Override
	public void setMessageReceiver(Receiver messageReceiver) {
		log.d(LOGTAG, "setting a message receiver: " + messageReceiver);
		this.messageReceiver = messageReceiver;
	}

	@Override
	public boolean handleMessage(String id, Message message) {
		log.d(LOGTAG,
				"handleMessage from " + id + " message.id = "
						+ message.getType());

		// check if client handles the message;
		if (message.getType() == Message.TYPE_HELLO && messageReceiver != null) {
			if (messageReceiver.handleMessage(id, message)) {
				return true;
			}
		}

		switch (message.getType()) {
		case Message.TYPE_HELLO: {
			handleHelloMessage(id, message);
			break;
		}

		case Message.TYPE_HELLO_REPLY: {
			handleHelloReplyMessage(id, message);
			break;
		}

		case Message.TYPE_UPDATE: {
			handleUpdateMessage(id, message);
			break;
		}

		case Message.TYPE_UPDATE_REPLY_MULTIPART:
			handleUpdateReplyMultipart(id, message);
			break;

		case Message.TYPE_UPDATE_REPLY: {
			handleUpdateReplyMessage(id, message);
			break;
		}

		case Message.TYPE_PUSH: {
			handlePushMessage(id, message);
			break;
		}

		case Message.TYPE_NOTIFY: {
			handleNotify(id, message);
			break;
		}

		default: {
			log.w(LOGTAG, "Message type " + message.getType()
					+ " not handled. Redirrecting to the user.");
			break;
		}
		}

		boolean result = false;
		// check if client handles the message;
		if (message.getType() != Message.TYPE_HELLO && messageReceiver != null) {
			result = messageReceiver.handleMessage(id, message);
		}

		return result;
	}

	/**
	 * Used to read multi-part update reply messages;
	 */
	private Map<String, byte[]> updateChunks = new HashMap<String, byte[]>();

	private void handleUpdateReplyMultipart(String id, Message message) {
		ChunkMessage chunk = (ChunkMessage) message;
		String updateId = chunk.getUpdateId();

		byte[] hugeData = null;

		// TODO: what if the hugeData can not fit into memory?
		synchronized (updateChunks) {
			hugeData = updateChunks.get(updateId);
			if (hugeData == null) {
				hugeData = new byte[chunk.getTotal()];
				updateChunks.put(updateId, hugeData);
			}
		}

		synchronized (hugeData) {
			System.arraycopy(chunk.getData(), 0, hugeData, chunk.getCurrent(),
					chunk.getData().length);

			log.d(LOGTAG, "chunk: " + updateId + "[" + chunk.getCurrent()
					+ ", " + chunk.getTotal() + "]");

			if (chunk.getData().length + chunk.getCurrent() >= hugeData.length) {
				UpdateResponseFullMessage update = (UpdateResponseFullMessage) YCommunicationManagerUtils
						.toObject(hugeData);

				Map<String, byte[]> contentData = update.getContentData();

				for (String contentName : contentData.keySet()) {
					byte[] data = contentData.get(contentName);
					if (data != null) {
						if (data.length > 0) {
							client.setData(contentName, data);
						}
					}
				}

				handleUpdateResponseMessage(id, update);

				synchronized (updateChunks) {
					updateChunks.remove(updateId);
				}
			}
		}
	}

	@Override
	public Message handleRequest(String id, Message message) {
		log.d(LOGTAG, "handleRequest from " + id);
		return null;
	}

	/**
	 * Handles a PUSH message from a remote peer.
	 * 
	 * @param id
	 * @param message
	 */
	private void handlePushMessage(String id, Message message) {
		log.d(LOGTAG, "handlePushMessage from " + id);

		TriplesMessage msgTriples = (TriplesMessage) YCommunicationManagerUtils
				.toObject(message.getData());
		List<Triple> triples = msgTriples.getTriples();

		for (Triple triple : triples) {
			try {
				if (knowledgeBase.getTriple(triple.getSubject(),
						triple.getProperty(), triple.getObject(), userId) == null) {
					/**
					 * if literal, remove previous props;
					 */
					if (triple.getObject().whichNode() == Node.RDF_LITERAL) {
						List<Triple> prevProps = knowledgeBase
								.getPropertyObjectAsTriples(
										triple.getSubject(),
										triple.getProperty(), id);
						for (Triple t : prevProps) {
							knowledgeBase.removeTriple(t.getSubject(),
									t.getProperty(), t.getObject(), id);
						}
					}
					knowledgeBase.addTriple(triple.getSubject(),
							triple.getProperty(), triple.getObject(), id);
				}
			} catch (KBException ex) {
				log.d(LOGTAG,
						"handlePushMessage: addTriple error: "
								+ ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	private void handleNotify(String peerId, Message message) {
		log.d(LOGTAG, "handleNotify " + peerId);
		try {
			sendUpdateRequest(peerId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Handles an UPDATE message request from a remote peer.
	 * 
	 * @param id
	 * @param message
	 */
	private void handleUpdateMessage(String id, Message message) {

		// the time from which all begins
		Long lastUpdate = (Long) YCommunicationManagerUtils.toObject(message
				.getData());

		String result = UPDATE_SUCCESS;
		List<Triple> allInfo = new ArrayList<Triple>();
		List<Long> allTimes = new ArrayList<Long>();
		Map<String, byte[]> contentData = new HashMap<String, byte[]>();

		try {
			Set<Resource> resources = accessManager.getAllResources();

			for (Resource resource : resources) {
				YartaResource res = (YartaResource) resource;
				Node node = res.getNode();

				// check if node is dirty
				// if (!helper.isDirty(node, lastUpdate)) {
				// continue;
				// }

				String typePredicate = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
				List<Triple> properties = knowledgeBase
						.getAllPropertiesAsTriples(node, id);

				for (int i = 0; i < properties.size(); i++) {
					if (properties.get(i).getProperty().getName()
							.equals(typePredicate)
							&& i != 0) {
						Triple t = properties.get(0);
						properties.set(0, properties.get(i));
						properties.set(i, t);
						break;
					}
				}

				boolean dirty = false;
				for (Triple triple : properties) {
					// TODO: rdf:Seq
					if (triple.getProperty().getName()
							.contains(ThinKnowledgeBase.RDF_NAMESPACE + "_")) {
						triple = new MSETriple(triple.getSubject(),
								new MSEResource(ThinKnowledgeBase.RDF_NAMESPACE
										+ "li", "grosca"), triple.getObject());
					}
					long time = helper.getTime(triple);
					if (time == 0) {
						time = System.currentTimeMillis();
						helper.setTime(triple, time);
					}

					time = helper.getTime(triple);
					if (time > lastUpdate) {
						dirty = true;
						allInfo.add(triple);
						allTimes.add(time);
					}
				}

				// TODO: remove dirty only if it is about a picture
				if (resource instanceof Content && dirty) {
					String longId = resource.getUniqueId();
					String shortId = longId.substring(longId.indexOf('#') + 1);
					byte[] data = client.getData(shortId);
					if (data != null) {
						if (data.length > 0) {
							contentData.put(shortId, data);
						}
					}
				}
			}
		} catch (KBException ex) {
			log.e(LOGTAG, "handleUpdateRequest got a KBException", ex);
			ex.printStackTrace();
		}

		if (allInfo.size() == 0) {
			result = UPDATE_EMPTY;
		}

		log.d(LOGTAG, "sending " + allInfo.size() + " triples.");

		// chunk update reply in multiple messages
		String updateId = UUID.randomUUID().toString();
		UpdateResponseFullMessage fullResponse = new UpdateResponseFullMessage(
				result, allInfo, allTimes, contentData);
		byte[] fullResponseBinary = YCommunicationManagerUtils
				.toBytes(fullResponse);

		int chunkSize = 102400;

		for (int i = 0; i < fullResponseBinary.length; i += chunkSize) {
			byte chunk[] = new byte[Math.min(chunkSize,
					fullResponseBinary.length - i)];
			System.arraycopy(fullResponseBinary, i, chunk, 0, chunk.length);

			synchronized (connection) {
				connection.postMessage(id, new ChunkMessage(updateId, i,
						fullResponseBinary.length, chunk));
			}
		}
	}

	private void handleUpdateReplyMessage(String id, Message message) {
		UpdateResponseMessage response = (UpdateResponseMessage) YCommunicationManagerUtils
				.toObject(message.getData());

		handleUpdateResponseMessage(id, response);
	}

	private void handleUpdateResponseMessage(String id,
			UpdateResponseMessage response) {
		boolean updateResponse = false;

		List<Triple> triples = response.getTriples();
		List<Long> times = response.getTimes();

		log.d(LOGTAG, "handleUpdateReplyMessage " + triples.size()
				+ " triples.");

		try {
			if (triples.size() == 0) {
				log.e(LOGTAG, "handleUpdateReplyMessage: Update from user "
						+ id + " is an empty graph");
			} else {
				for (int i = 0; i < triples.size(); i++) {
					Triple triple = triples.get(i);
					Long time = times.get(i);
					try {
						if (knowledgeBase.getTriple(triple.getSubject(),
								triple.getProperty(), triple.getObject(),
								userId) == null) {

							/**
							 * if literal, remove previous props;
							 */
							if (triple.getObject().whichNode() == Node.RDF_LITERAL) {
								List<Triple> prevProps = knowledgeBase
										.getPropertyObjectAsTriples(
												triple.getSubject(),
												triple.getProperty(), id);
								for (Triple t : prevProps) {
									knowledgeBase.removeTriple(t.getSubject(),
											t.getProperty(), t.getObject(), id);
								}
							}
							Triple t = knowledgeBase.addTriple(
									triple.getSubject(), triple.getProperty(),
									triple.getObject(), id);

							if (t != null) {
								helper.setTime(t, time);
							}
						}

					} catch (KBException ex) {
						log.d(LOGTAG, "addTriple ex: " + ex.getMessage());
					}
				}
				updateResponse = true;
			}
		} catch (Exception ex) {
			log.e(LOGTAG, "handleUpdateReplyMessage: "
					+ "Something went wrong trying to write the graph, "
					+ "maybe graph is null", ex);
			ex.printStackTrace();
			return;
		}

		if (updateResponse) {
			log.d(LOGTAG, "Graph received by user " + id
					+ " successfully written on user's KB");

			application
					.handleNotification("You have received information from user "
							+ id);

			helper.setTime(id, System.currentTimeMillis());
		} else {
			application.handleNotification("Something went wrong "
					+ "in receiving information from user " + id);
			log.e(LOGTAG, "handleUpdateReplyMessage: Something "
					+ "went wrong performing graph update from user" + id);
		}
	}

	/**
	 * Handles a HELLO message request.
	 * 
	 * @param id
	 * @param message
	 */
	public void handleHelloMessage(String id, Message message) {
		HelloMessageResponse response = handleHelloRequest((HelloMessage) YCommunicationManagerUtils
				.toObject(message.getData()));

		synchronized (connection) {
			connection.postMessage(id, new Message(Message.TYPE_HELLO_REPLY,
					YCommunicationManagerUtils.toBytes(response)));
		}
	}

	/**
	 * Handles a HELLO message request received from a remote peer by returning
	 * the response.
	 * 
	 * @param helloMessage
	 * @return HelloMessageResponse
	 */
	private HelloMessageResponse handleHelloRequest(HelloMessage helloMessage) {
		try {
			BasicPersonInfo info = helloMessage.getBasicPersonInfo();

			knowledgeBase.addTriple(info.getTypeTriple().getSubject(), info
					.getTypeTriple().getProperty(), info.getTypeTriple()
					.getObject(), userId);
			knowledgeBase.addTriple(info.getUserIdTriple().getSubject(), info
					.getUserIdTriple().getProperty(), info.getUserIdTriple()
					.getObject(), userId);

			Person other = accessManager.getPersonByUserId(info.getUserId());
			Person me = accessManager.getMe();

			other.setEmail(info.getEmail());
			other.setFirstName(info.getFName());
			other.setLastName(info.getLName());
			other.addKnows(me);

			if (application.handleQuery("Person says hello."
					+ "Want to reciprocate? (Yes/No)")) {

				me.addKnows(other);

				List<Triple> selfTriples = getSelfTriples();

				return new HelloMessageResponse(HelloMessageResponse.HELLO_YES,
						new BasicPersonInfo(me.getFirstName(),
								me.getLastName(), me.getUserId(),
								me.getEmail(), selfTriples.get(0),
								selfTriples.get(1)));
			} else {
				return new HelloMessageResponse(HelloMessageResponse.HELLO_NO,
						new BasicPersonInfo());
			}
		} catch (KBException ex) {
			log.e(LOGTAG, "handleHelloRequest ex: " + ex.getMessage());
			ex.printStackTrace();
		}

		return null;
	}

	/**
	 * Handles the HELLO message reply received from a remote peer.
	 * 
	 * @param id
	 * @param message
	 */
	private void handleHelloReplyMessage(String id, Message message) {
		HelloMessageResponse response = (HelloMessageResponse) YCommunicationManagerUtils
				.toObject(message.getData());

		boolean helloResponse = response.getStatus() == HelloMessageResponse.HELLO_YES;

		log.d(LOGTAG, "User " + id + " responded to my hello message with "
				+ helloResponse);

		if (helloResponse) {
			try {
				BasicPersonInfo info = response.getBasicPersonInfo();

				knowledgeBase.addTriple(info.getUserIdTriple().getSubject(),
						info.getUserIdTriple().getProperty(), info
								.getUserIdTriple().getObject(), userId);
				knowledgeBase.addTriple(info.getTypeTriple().getSubject(), info
						.getTypeTriple().getProperty(), info.getTypeTriple()
						.getObject(), userId);

				Person me = accessManager.getMe();
				Person other = accessManager
						.getPersonByUserId(info.getUserId());
				other.setFirstName(info.getFName());
				other.setLastName(info.getLName());
				other.setEmail(info.getEmail());

				me.addKnows(other);
				other.addKnows(me);
			} catch (KBException kbe) {
				log.e(MSEManager.LOGTAG,
						"HelloResponseHandler: Getting KBException!", kbe);
			}

			// Send a message to the UI saying all went well.
			application.handleNotification("User " + id
					+ " responded positively to your Hello! :)");

		} else {
			// Send a message to the UI saying the other chap rejected your
			// request
			application.handleNotification("User " + id
					+ " responded negatively to your :-(");
		}
	}

	/**
	 * Gets a list of basic information to be sent as triples.
	 * 
	 * @return List<Triple> the triples.
	 * @throws KBException
	 */
	private List<Triple> getSelfTriples() throws KBException {
		Node userIdNode = knowledgeBase
				.getResourceByURINoPolicies(Person.PROPERTY_USERID_URI);
		Node typeNode = knowledgeBase
				.getResourceByURINoPolicies(ThinKnowledgeBase.RDF_TYPE);
		Node personNode = knowledgeBase
				.getResourceByURINoPolicies(Person.typeURI);

		List<Triple> userTriples = knowledgeBase.getPropertySubjectAsTriples(
				userIdNode,
				new MSELiteral(userId, ThinKnowledgeBase.XSD_STRING), userId);

		if (userTriples.size() == 0) {
			return null;
		}

		Triple selfTriple = userTriples.get(0);

		Node userNode = knowledgeBase.getResourceByURINoPolicies(selfTriple
				.getSubject().getName());

		List<Triple> result = new ArrayList<Triple>();

		result.add(new MSETriple(userNode, userIdNode, selfTriple.getObject()));
		result.add(new MSETriple(userNode, typeNode, personNode));

		return result;
	}

	public void setUpdateHelper(UpdateHelper helper) {
		this.helper = helper;
	}

	private UpdateHelper helper;
}

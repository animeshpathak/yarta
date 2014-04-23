package fr.inria.arles.yarta.middleware.msemanagement;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.MSELiteral;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.ThinKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.AgentImpl;
import fr.inria.arles.yarta.resources.CompositeEvent;
import fr.inria.arles.yarta.resources.CompositeEventImpl;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.ContentImpl;
import fr.inria.arles.yarta.resources.Conversation;
import fr.inria.arles.yarta.resources.ConversationImpl;
import fr.inria.arles.yarta.resources.Event;
import fr.inria.arles.yarta.resources.EventImpl;
import fr.inria.arles.yarta.resources.Group;
import fr.inria.arles.yarta.resources.GroupImpl;
import fr.inria.arles.yarta.resources.Message;
import fr.inria.arles.yarta.resources.MessageImpl;
import fr.inria.arles.yarta.resources.ParEvent;
import fr.inria.arles.yarta.resources.ParEventImpl;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.PersonImpl;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.PlaceImpl;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.yarta.resources.SeqEvent;
import fr.inria.arles.yarta.resources.SeqEventImpl;
import fr.inria.arles.yarta.resources.SingleEvent;
import fr.inria.arles.yarta.resources.SingleEventImpl;
import fr.inria.arles.yarta.resources.Topic;
import fr.inria.arles.yarta.resources.TopicImpl;
import fr.inria.arles.yarta.resources.YartaResource;

/**
 * The StorageAccessManager class meant for app-level API only. Essentially it
 * uses only the Triple-based KB
 */
public class ThinStorageAccessManager implements ThinKnowledgeBaseManager {

	// static final strings for URIs taken from other namespaces.
	private static final String STRING_TYPE_URI = ThinKnowledgeBase.XSD_STRING;

	private static final String STORAGE_ACCESS_MGR_LOGTAG = "ThinStorageAccessManager";

	private ThinKnowledgeBase thinKnowledgeBase;
	private String kbNameSpace;

	private Person ownerPerson;

	protected String ownerId;
	protected YLogger logger;

	/* constants declared here. For memory efficiency */
	static String personTypeURI = Person.typeURI;
	static Node userIdPropertyNode = null;
	static Node firstNamePropertyNode = null;
	static Node lastNamePropertyNode = null;
	static Node emailPropertyNode = null;
	static Node namePropertyNode = null;
	static Node homePagePropertyNode = null;
	static Node groupMembershipPropertyNode = null;
	static Node propertyTypeNode = null;
	static Node knowsPropertyNode = null;

	/** the temporary list for queries, never read it without writing */
	private List<Triple> tempTriples;

	/**
	 * constructor. Sets the logger object.
	 */
	public ThinStorageAccessManager() {
		logger = YLoggerFactory.getLogger();
		if (!ThinKnowledgeBase.PERFORM_CHECKS) {
			logger.e(STORAGE_ACCESS_MGR_LOGTAG,
					"PERFORM_CHECKS is false. Not performing checks. Use at your own risk!");
		}
	}

	@Override
	public void setKnowledgeBase(ThinKnowledgeBase k) {
		thinKnowledgeBase = k;
		kbNameSpace = k.getMyNameSpace();
	}

	/**
	 * Sets the userIdForKb
	 * 
	 * @param uID
	 *            value of the userId property of the user to be set as the
	 *            owner of the mWare
	 */
	public void setOwnerID(String uID) {
		ownerId = uID;
	}

	/**
	 * @return The Person Object corresponding to the user.
	 */
	public Person getMe() throws KBException {
		if (ownerPerson == null) {
			try {
				ownerPerson = getPersonByUserId(ownerId);
			} catch (KBException e) {
				logger.e(STORAGE_ACCESS_MGR_LOGTAG,
						"setting of this.mePerson failed", e);
			}
		}
		return ownerPerson;
	}

	/**
	 * Returns a Person Object, based on the unique user ID passed during its
	 * construction.
	 * 
	 * @param userId
	 *            - The userId of the person
	 * @return - The person resource. Null if nothing is found.
	 * @throws MSEException
	 */
	public Person getPersonByUserId(String userId) throws KBException {
		// first see if a resource exists with this userId property
		if (userIdPropertyNode == null)
			userIdPropertyNode = thinKnowledgeBase
					.getResourceByURINoPolicies(Person.PROPERTY_USERID_URI);

		tempTriples = thinKnowledgeBase.getPropertySubjectAsTriples(
				userIdPropertyNode, new MSELiteral(userId, STRING_TYPE_URI),
				ownerId);

		Node retPersonNode;

		if (tempTriples.size() == 0) {
			// oops. Weird. No triples in this list.
			logger.e(STORAGE_ACCESS_MGR_LOGTAG,
					"getPersonByUserId resulted in graph with 0 triples for userId "
							+ userId);
			// How about throwing a KnowledgeBaseException?
			return null;
		} else {
			if (tempTriples.size() > 1) {
				// more than one person with same userID. Weird. Log error, but
				// proceed.
				logger.e(STORAGE_ACCESS_MGR_LOGTAG,
						"getPersonByUserId resulted in graph with more than 1 triple for userId "
								+ userId);
			}
			// in any case, get the 0th Triple, and move on.
			Triple personTriple = tempTriples.get(0);
			// TODO Assert that personTriple is of type Person?
			retPersonNode = personTriple.getSubject();
			if (retPersonNode == null) {
				logger.e(STORAGE_ACCESS_MGR_LOGTAG,
						"getPersonByUniqueId resulted in null node for uid "
								+ userId);
				return null;
			} else {
				// convert this person Node into a PersonImpl object
				return new PersonImpl(this,
						thinKnowledgeBase
								.getResourceByURINoPolicies(retPersonNode
										.getName()));
			}
		}
	}

	/**
	 * @param requestor
	 *            - The person requesting to read all triples
	 * @return - A List of all triples retrieved from the KB (subject to valid
	 *         policies)
	 * @throws KBException
	 */
	public List<Triple> getAllInfoAsTriples(Person requestor)
			throws KBException {
		return getInfoAsTriples(requestor.getUserId());
	}

	/**
	 * Returns all the KB as a List of Triples.
	 * 
	 * @param partnerUID
	 * @return List<Triple>
	 * @throws KBException
	 */
	public List<Triple> getInfoAsTriples(String partnerUID) throws KBException {
		return thinKnowledgeBase.getKBAsTriples(partnerUID);
	}

	// //////////////// Utility Methods //////////////////////////

	/**
	 * get a data property directly from the KB
	 * 
	 * @param resourceNode
	 *            The node whose property we want
	 * @param propertyUri
	 *            The URI of the property needed
	 * @param c
	 *            The Java class which the property maps to
	 */
	public String getDataProperty(Node resourceNode, String propertyUri,
			Class<?> c) {
		try {
			tempTriples = thinKnowledgeBase.getPropertyObjectAsTriples(
					resourceNode, getPropertyNode(propertyUri), ownerId);
		} catch (KBException e) {
			logger.d(STORAGE_ACCESS_MGR_LOGTAG, "KBException getting property "
					+ propertyUri + " from KB.");
			return null;
		}
		if (tempTriples.size() == 0) {
			return null;
		} else {
			if (c == String.class) {
				return (tempTriples.get(0).getObject().getName());
			} else {
				// TODO check for other classes. Prepare a proper object
				logger.e(STORAGE_ACCESS_MGR_LOGTAG, "Non-string class " + c
						+ " asked for to get property.");
				return null;
			}
		}
	}

	/**
	 * Stores data properties directly into the KB
	 * 
	 * @param resourceNode
	 *            The node whose property we want to set
	 * @param propertyUri
	 *            The URI of the property needed
	 * @param c
	 *            The Java class which the property maps to
	 * @param property
	 *            The property itself
	 */
	public void setDataProperty(Node resourceNode, String propertyUri,
			Class<?> c, Object property) {

		if (property == null) {
			logger.e(STORAGE_ACCESS_MGR_LOGTAG,
					"Null property passed. Returning.");
			return;
		}
		// check if the Object is the proper class.

		// if (!(property instanceof c)){
		// logger.e(STORAGE_ACCESS_MGR_LOGTAG, "Oops. property "+ property
		// +" not an instance of class " + c);
		// return;
		// }

		// TODO do we handle non-string classes?
		if (c == String.class) {

			try {
				// removing all existing triples with this property [should be
				// only one]

				Node propertyNode = getPropertyNode(propertyUri);

				// do this only if performing checks.
				if (ThinKnowledgeBase.PERFORM_CHECKS) {
					tempTriples = thinKnowledgeBase.getPropertyObjectAsTriples(
							resourceNode, propertyNode, ownerId);

					if (tempTriples.size() != 0) {
						for (Triple tempTriple : tempTriples) {
							logger.d(STORAGE_ACCESS_MGR_LOGTAG,
									"removing triple " + tempTriple);
							thinKnowledgeBase.removeTriple(resourceNode,
									propertyNode, tempTriple.getObject(),
									ownerId);
						}
					}
				}

				this.thinKnowledgeBase.addTriple(resourceNode, propertyNode,
						thinKnowledgeBase.addLiteral((String) property,
								STRING_TYPE_URI, ownerId), ownerId);
			} catch (KBException e) {
				logger.d(STORAGE_ACCESS_MGR_LOGTAG,
						"KBException setting property " + propertyUri
								+ " in KB.", e);
			}
		} else {
			logger.e(STORAGE_ACCESS_MGR_LOGTAG,
					"Why am I trying to set a non String resource as an object of property "
							+ propertyUri + " ?");
		}
	}

	/** The cache of property nodes used for {@link #getPropertyNode(String)} */
	HashMap<String, Node> propertyNodes = new HashMap<String, Node>();

	/**
	 * gets a KB node corresponding to a particular property URI. This
	 * implementation uses a cache stored in {@link #propertyNodes}.
	 * 
	 * @param propertyUri
	 *            The URI for the property
	 * @return
	 */
	protected Node getPropertyNode(String propertyUri) {
		Node retNode = propertyNodes.get(propertyUri);

		if (retNode == null) {
			// get the node, store in cache
			retNode = this.thinKnowledgeBase
					.getResourceByURINoPolicies(propertyUri);
			propertyNodes.put(propertyUri, retNode);
		}

		return retNode;
	}

	/**
	 * Stores object properties directly into the KB
	 * 
	 * @param resourceNode
	 *            The node whose property we want to set
	 * @param propertyUri
	 *            The URI of the property needed
	 * @param typeuri
	 *            The URL of the type of the target resource -- do we need this?
	 * @param property
	 *            The property itself -- should be an instance of YartaResource
	 * @return true if all goes well, false otherwise
	 */
	public boolean setObjectProperty(Node resourceNode, String propertyUri,
			Object o) {
		if (o instanceof YartaResource) {

			try {
				if (thinKnowledgeBase.addTriple(resourceNode,
						getPropertyNode(propertyUri),
						((YartaResource) o).getNode(), ownerId) != null) {
					return true;
				} else {
					logger.d(STORAGE_ACCESS_MGR_LOGTAG,
							"Null triple returned in addTriple during setObjectProperty "
									+ propertyUri + " in KB.");
					return false;
				}
			} catch (KBException e) {
				logger.d(STORAGE_ACCESS_MGR_LOGTAG,
						"KBException setting property " + propertyUri
								+ " in KB.", e);
				return true;
			}
		} else {
			logger.e(STORAGE_ACCESS_MGR_LOGTAG,
					"Why am I trying to set a non YartaResource resource as an object of property "
							+ propertyUri + "?");
			return false;
		}
	}

	/**
	 * Deletes object properties from the KB
	 * 
	 * @param resourceNode
	 *            The node whose property we want to set
	 * @param propertyUri
	 *            The URI of the property needed
	 * @param property
	 *            The property itself -- should be an instance of YartaResource
	 * @return true if all goes well. False if there is an error.
	 */
	public boolean deleteObjectProperty(Node resourceNode, String propertyUri,
			Object o) {
		if (o instanceof YartaResource) {

			try {
				if (thinKnowledgeBase.removeTriple(resourceNode,
						getPropertyNode(propertyUri),
						((YartaResource) o).getNode(), ownerId) != null) {
					// non-null deleted triple returned. All went well.
					return true;
				} else {
					logger.d(STORAGE_ACCESS_MGR_LOGTAG,
							"Got null triple returned in deleting property "
									+ propertyUri + " in KB.");
					return false;
				}

			} catch (KBException e) {
				logger.d(STORAGE_ACCESS_MGR_LOGTAG,
						"KBException deletingproperty " + propertyUri
								+ " in KB.", e);
				return false;
			}
		} else {
			logger.e(STORAGE_ACCESS_MGR_LOGTAG,
					"Why am I deleting a non YartaResource resource as an object of property "
							+ propertyUri + "?");
			return false;
		}
	}

	/**
	 * Gets a List of Objects which match the given property of the given node
	 * 
	 * @param resourceNode
	 *            The node whose property we want to get
	 * @param propertyUri
	 *            The URI of the property needed
	 * @param c
	 *            The class to cast the
	 * @return the list of objects. Null if something went wrong.
	 */
	public <K> Set<K> getObjectProperty(Node resourceNode, String propertyUri) {
		try {
			tempTriples = thinKnowledgeBase.getPropertyObjectAsTriples(
					resourceNode, getPropertyNode(propertyUri), this.ownerId);

			// create return ArrayList
			Set<K> retSet = new HashSet<K>();

			if (tempTriples.size() == 0) {
				logger.e(
						STORAGE_ACCESS_MGR_LOGTAG,
						"Graph with zero triples returned in getObjectProperty for "
								+ propertyUri + " for Node "
								+ resourceNode.toString());
				return retSet;
			}

			for (Triple tempTriple : tempTriples) {
				try {
					Node object = thinKnowledgeBase
							.getResourceByURINoPolicies(tempTriple.getObject()
									.getName());
					String className = getJavaClassNameFor(object.getType());

					Object instance = getCached(className, object.getName());

					if (instance == null) {
						Class<? extends YartaResource> c = (Class<? extends YartaResource>) Class
								.forName(className);

						Constructor<? extends YartaResource> cons = c
								.getConstructor(new Class[] {
										ThinStorageAccessManager.class,
										Node.class });

						instance = cons.newInstance(this, object);

						setCached(className, object.getName(), instance);
					}

					retSet.add((K) instance);
				} catch (Exception e) {
					logger.e(STORAGE_ACCESS_MGR_LOGTAG,
							"Exception in getObjectProperty for " + propertyUri
									+ " for Node " + resourceNode.toString(), e);
				}
			}// end for
			return retSet;

		} catch (KBException e) {
			// TODO write a log message
			logger.e(STORAGE_ACCESS_MGR_LOGTAG, "KBException getting property "
					+ propertyUri + " from KB.");
			return null;
		}
	}

	/**
	 * Inverse of {@link #getObjectProperty(Node, String)}. Given an node and a
	 * property, it finds all nodes which are linked to this node via this
	 * property. Useful for finding out "who knows me" etc.
	 * 
	 * @param resourceNode
	 *            The node who is the "range" of this property
	 * @param propertyUri
	 *            The URI of the property needed
	 * @return the list of objects who are linked to this node by this property.
	 *         Null if something went wrong.
	 */
	public <K> Set<K> getObjectProperty_inverse(Node resourceNode,
			String propertyUri) {
		return getObjectProperty_inverse(resourceNode, propertyUri, null);
	}

	/**
	 * Inverse of {@link #getObjectProperty(Node, String)}. Given an node and a
	 * property, it finds all nodes which are linked to this node via this
	 * property. Useful for finding out "who knows me" etc.
	 * 
	 * @param resourceNode
	 *            The node who is the "range" of this property
	 * @param propertyUri
	 *            The URI of the property needed
	 * @param c
	 *            the class upon which the resources needs to be casted
	 * @return the list of objects who are linked to this node by this property.
	 *         Null if something went wrong.
	 */
	public <K> Set<K> getObjectProperty_inverse(Node resourceNode,
			String propertyUri, Class<K> c) {
		try {
			tempTriples = thinKnowledgeBase.getPropertySubjectAsTriples(
					getPropertyNode(propertyUri), resourceNode, this.ownerId);
			// TODO refactor the code below to a method that converts a graph of
			// this form to a List.
			// Move code from getObjectProperty also.

			// create return ArrayList
			Set<K> retSet = new HashSet<K>();

			if (tempTriples.size() == 0) {
				logger.e(
						STORAGE_ACCESS_MGR_LOGTAG,
						"Graph with zero triples returned in getObjectProperty for "
								+ propertyUri + " for Node "
								+ resourceNode.toString());
				return retSet;
			}

			for (Triple tempTriple : tempTriples) {
				try {
					Node subject = thinKnowledgeBase
							.getResourceByURINoPolicies(tempTriple.getSubject()
									.getName());
					String className = getJavaClassNameFor(subject.getType());

					if (c != null) {
						// TODO: hacky
						className = c.getCanonicalName() + "Impl";
					}

					Object instance = getCached(className, subject.getName());

					if (instance == null) {
						Class<? extends YartaResource> cls = (Class<? extends YartaResource>) Class
								.forName(className);

						Constructor<? extends YartaResource> cons = cls
								.getConstructor(new Class[] {
										ThinStorageAccessManager.class,
										Node.class });

						instance = cons.newInstance(this, subject);

						setCached(className, subject.getName(), instance);
					}
					retSet.add((K) instance);
				} catch (Exception e) {
					logger.e(STORAGE_ACCESS_MGR_LOGTAG,
							"Exception in getObjectProperty for " + propertyUri
									+ " for Node " + resourceNode.toString(), e);
				}
			}
			return retSet;
		} catch (KBException e) {
			logger.e(STORAGE_ACCESS_MGR_LOGTAG, "KBException getting property "
					+ propertyUri + " from KB.");
			return null;
		}
	}

	/**
	 * The map which contains the list of implementation class names. used for
	 * {@link #getJavaClassNameFor(String)}
	 */
	private HashMap<String, String> classTable = new HashMap<String, String>();
	{
		classTable.put(fr.inria.arles.yarta.resources.Person.typeURI,
				"fr.inria.arles.yarta.resources.PersonImpl");
		classTable.put(fr.inria.arles.yarta.resources.Group.typeURI,
				"fr.inria.arles.yarta.resources.GroupImpl");
		classTable.put(fr.inria.arles.yarta.resources.Agent.typeURI,
				"fr.inria.arles.yarta.resources.AgentImpl");
		classTable.put(fr.inria.arles.yarta.resources.Content.typeURI,
				"fr.inria.arles.yarta.resources.ContentImpl");
		classTable.put(fr.inria.arles.yarta.resources.Message.typeURI,
				"fr.inria.arles.yarta.resources.MessageImpl");
		classTable.put(fr.inria.arles.yarta.resources.Conversation.typeURI,
				"fr.inria.arles.yarta.resources.ConversationImpl");
		classTable.put(fr.inria.arles.yarta.resources.Event.typeURI,
				"fr.inria.arles.yarta.resources.EventImpl");
		classTable.put(fr.inria.arles.yarta.resources.CompositeEvent.typeURI,
				"fr.inria.arles.yarta.resources.CompositeEventImpl");
		classTable.put(fr.inria.arles.yarta.resources.SeqEvent.typeURI,
				"fr.inria.arles.yarta.resources.SeqEventImpl");
		classTable.put(fr.inria.arles.yarta.resources.ParEvent.typeURI,
				"fr.inria.arles.yarta.resources.ParEventImpl");
		classTable.put(fr.inria.arles.yarta.resources.SingleEvent.typeURI,
				"fr.inria.arles.yarta.resources.SingleEventImpl");
		classTable.put(fr.inria.arles.yarta.resources.Place.typeURI,
				"fr.inria.arles.yarta.resources.PlaceImpl");
		classTable.put(fr.inria.arles.yarta.resources.Topic.typeURI,
				"fr.inria.arles.yarta.resources.TopicImpl");
	}

	/**
	 * Binds an RDF resource's URI to a Java class path containing the
	 * implementation.
	 * 
	 * @param typeURI
	 *            the uri
	 * @param classPath
	 *            the class path
	 */
	protected void bindInterfacetoImplementation(String typeURI,
			String classPath) {
		classTable.put(typeURI, classPath);
	}

	/**
	 * Gets the Java class name for the class that implements a particular
	 * datatype. Used {@link #classTable} as interal store
	 * 
	 * @param type
	 *            The Yarta type -- full URI
	 * @return The class name -- fully qualified
	 */
	private String getJavaClassNameFor(String type) {
		return classTable.get(type);
	}

	/**
	 * Creates a new node of a given type in the KB, giving it a UUID generated
	 * locally
	 * 
	 * @param typeuri
	 *            The URI of the type
	 * @return The KB Node created.
	 */
	public Node createNewNode(String typeuri) {
		String newID = kbNameSpace + UUID.randomUUID().toString();
		try {
			Node newNode = thinKnowledgeBase.addResource(newID, typeuri,
					ownerId);
			return newNode;
		} catch (KBException e) {
			logger.e(STORAGE_ACCESS_MGR_LOGTAG,
					"KBException crating new node of type " + typeuri, e);
			return null;
		}
	}

	/**
	 * Resource instances cache
	 */
	private Map<String, Map<String, Object>> resourceCache = new HashMap<String, Map<String, Object>>();

	/**
	 * Retrieving cached resource instance.
	 * 
	 * @param className
	 * @param uri
	 * @return
	 */
	private Object getCached(String className, String uri) {
		Object result = null;
		synchronized (resourceCache) {
			Map<String, Object> uris = resourceCache.get(className);
			if (uris != null) {
				result = uris.get(uri);
			}
		}
		return result;
	}

	/**
	 * Storing resource instance in cache.
	 * 
	 * @param className
	 * @param uri
	 * @param resource
	 */
	private void setCached(String className, String uri, Object resource) {
		synchronized (resourceCache) {
			Map<String, Object> uris = resourceCache.get(className);
			if (uris == null) {
				uris = new HashMap<String, Object>();
				resourceCache.put(className, uris);
			}
			uris.put(uri, resource);
		}
	}

	/**
	 * Returns objects for all resources of a given type found in the KB.
	 * WARNING: Use carefully, it returns an untyped List
	 * 
	 * @param resourceTypeNode
	 *            - A Node pointing to the type of resource we are looking for.
	 * @return a List containing the resources.
	 */
	protected <K> Set<K> getAllResourcesOfType(Node resourceTypeNode) {
		try {
			tempTriples = thinKnowledgeBase.getPropertySubjectAsTriples(
					getPropertyNode(MSEKnowledgeBase.PROPERTY_TYPE_URI),
					resourceTypeNode, this.ownerId);

			Set<K> retList = new HashSet<K>();
			Node tempresourceNode;

			if (tempTriples.size() == 0) {
				// oops. Weird. No triples in this list.
				logger.e(STORAGE_ACCESS_MGR_LOGTAG,
						"getAllPersons resulted in graph with 0 triples");
				return retList;
			} else {
				// for each triple here
				for (Triple resourceTriple : tempTriples) {
					// this node does not have the proper type attribute set
					tempresourceNode = resourceTriple.getSubject();
					if (tempresourceNode == null) {
						logger.e(STORAGE_ACCESS_MGR_LOGTAG,
								"getAllResources resulted in triple with null subject. Craziness!");
						return null;
					} else {
						try {
							Node properResourceNode = thinKnowledgeBase
									.getResourceByURINoPolicies(tempresourceNode
											.getName());
							String type = properResourceNode.getType();
							String className = getJavaClassNameFor(type);

							// it's a new class for which the current storage
							// manager does not have any bound implementation
							if (className == null) {
								// TODO: get the lowest node.
								className = getJavaClassNameFor(resourceTypeNode
										.getName());
							}

							Object instance = getCached(className,
									properResourceNode.getName());

							if (instance == null) {
								logger.d(STORAGE_ACCESS_MGR_LOGTAG,
										"going to create class " + className
												+ " for resource type " + type);
								// convert this node into a proper object
								Class<? extends YartaResource> c = (Class<? extends YartaResource>) Class
										.forName(className); // #person, or
																// #group

								Constructor<? extends YartaResource> cons = c
										.getConstructor(new Class[] {
												ThinStorageAccessManager.class,
												Node.class });
								instance = cons.newInstance(this,
										properResourceNode);

								setCached(className,
										properResourceNode.getName(), instance);
							}

							retList.add((K) instance);
						} catch (Exception e) {
							logger.e(STORAGE_ACCESS_MGR_LOGTAG,
									"Exception in getAllResourcesOfType for type "
											+ resourceTypeNode.toString(), e);
						}
					}
				}
			}
			return retList;
		} catch (KBException e) {
			logger.e(STORAGE_ACCESS_MGR_LOGTAG, "KBException in getAllPersons",
					e);
			return null;
		}
	}

	// //////////////////////////////////////////////////////////////
	// //////////////////// Factory Methods /////////////////////////
	// //////////////////////////////////////////////////////////////

	/**
	 * Creates and return a new instance of the Person Interface
	 * 
	 * @param uniqueId
	 *            - A unique ID used when requests are made to the KB on behalf
	 *            of this user. Note that it is the application developer's job
	 *            to ensure its uniqueness. We suggest using an email address
	 *            :).
	 * @return New person. Null if something goes wrong.
	 */
	public Person createPerson(String uniqueId) {
		return (Person) new PersonImpl(this, uniqueId);
	}

	/**
	 * Creates and return a new instance of the Group Interface. The URI is
	 * auto-generated
	 * 
	 * @return New group. Null if something goes wrong.
	 */
	public Group createGroup() {
		return (Group) new GroupImpl(this, createNewNode(Group.typeURI));
	}

	/**
	 * Creates and return a new instance of the Agent Interface. The URI is
	 * auto-generated
	 * 
	 * @return New agent. Null if something goes wrong.
	 */
	public Agent createAgent() {
		return (Agent) new AgentImpl(this, createNewNode(Agent.typeURI));
	}

	/**
	 * Creates and return a new instance of the Content Interface. The URI is
	 * auto-generated
	 * 
	 * @return
	 * @return New Content. Null if something goes wrong.
	 */
	public Content createContent() {
		return (Content) new ContentImpl(this, createNewNode(Content.typeURI));
	}

	/**
	 * Creates and returns a new instance of the Message Interface. The URI is
	 * auto-generated.
	 * 
	 * @return New Message. Null if something goes wrong.
	 */
	public Message createMessage() {
		return (Message) new MessageImpl(this, createNewNode(Message.typeURI));
	}

	public Conversation createConversation() {
		return (Conversation) new ConversationImpl(this,
				createNewNode(Conversation.typeURI));
	}

	/**
	 * Creates and return a new instance of the Event Interface. The URI is
	 * auto-generated
	 * 
	 * @return New Event. Null if something goes wrong.
	 */
	public Event createEvent() {
		return (Event) new EventImpl(this, createNewNode(Event.typeURI));
	}

	/**
	 * Creates and return a new instance of the CompositeEvent Interface. The
	 * URI is auto-generated
	 * 
	 * @return New CompositeEvent. Null if something goes wrong.
	 */
	public CompositeEvent createCompositeEvent() {
		return (CompositeEvent) new CompositeEventImpl(this,
				createNewNode(CompositeEvent.typeURI));
	}

	/**
	 * Creates and return a new instance of the ParEvent Interface. The URI is
	 * auto-generated
	 * 
	 * @return New ParEvent. Null if something goes wrong.
	 */
	public ParEvent createParEvent() {
		return (ParEvent) new ParEventImpl(this,
				createNewNode(ParEvent.typeURI));
	}

	/**
	 * Creates and return a new instance of the SeqEvent Interface. The URI is
	 * auto-generated
	 * 
	 * @return New SeqEvent. Null if something goes wrong.
	 */
	public SeqEvent createSeqEvent() {
		return (SeqEvent) new SeqEventImpl(this,
				createNewNode(SeqEvent.typeURI));
	}

	/**
	 * Creates and return a new instance of the SingleEvent Interface. The URI
	 * is auto-generated
	 * 
	 * @return New SingleEvent. Null if something goes wrong.
	 */
	public SingleEvent createSingleEvent() {
		return (SingleEvent) new SingleEventImpl(this,
				createNewNode(SingleEvent.typeURI));
	}

	/**
	 * Creates and return a new instance of the Place Interface. The URI is
	 * auto-generated
	 * 
	 * @return New Place. Null if something goes wrong.
	 */
	public Place createPlace() {
		return (Place) new PlaceImpl(this, createNewNode(Place.typeURI));
	}

	/**
	 * Creates and return a new instance of the Topic Interface. The URI is
	 * auto-generated
	 * 
	 * @return New Topic. Null if something goes wrong.
	 */
	public Topic createTopic() {
		return (Topic) new TopicImpl(this, createNewNode(Topic.typeURI));
	}

	/**
	 * Gets a list of all agents in the KB
	 * 
	 * @return list of agents. Empty if there is no one is there. null if
	 *         something went wrong.
	 */
	public Set<Agent> getAllAgents() {
		return getAllResourcesOfType(getPropertyNode(Agent.typeURI));
	}

	/**
	 * Gets a list of all groups in the KB
	 * 
	 * @return list of groups. Empty if there is no one is there. null if
	 *         something went wrong.
	 */
	public Set<Group> getAllGroups() {
		return getAllResourcesOfType(getPropertyNode(Group.typeURI));
	}

	/**
	 * Gets a list of all persons in the KB
	 * 
	 * @return list of persons. Emptuy if there is no one is there. null if
	 *         something went wrong.
	 */
	public Set<Person> getAllPersons() {
		return getAllResourcesOfType(getPropertyNode(Person.typeURI));
	}

	/**
	 * Gets a list of all contents in the KB
	 * 
	 * @return list of contents. Empty if there is no one is there. null if
	 *         something went wrong.
	 */
	public Set<Content> getAllContents() {
		return getAllResourcesOfType(getPropertyNode(Content.typeURI));
	}

	/**
	 * Returns a set of all messages in the KB
	 * 
	 * @return list of messages. Empty if there are none. null if something went
	 *         wrong.
	 */
	public Set<Message> getAllMessages() {
		return getAllResourcesOfType(getPropertyNode(Message.typeURI));
	}

	/**
	 * Returns a set of all conversations in the KB
	 * 
	 * @return list of conversations. Empty if there are none. null if something
	 *         went wrong.
	 */
	public Set<Conversation> getAllConversations() {
		return getAllResourcesOfType(getPropertyNode(Conversation.typeURI));
	}

	/**
	 * Gets a list of all events in the KB
	 * 
	 * @return list of events. Empty if there is no one is there. null if
	 *         something went wrong.
	 */
	public Set<Event> getAllEvents() {
		return getAllResourcesOfType(getPropertyNode(Event.typeURI));
	}

	/**
	 * Gets a list of all compositeevents in the KB
	 * 
	 * @return list of compositeevents. Empty if there is no one is there. null
	 *         if something went wrong.
	 */
	public Set<CompositeEvent> getAllCompositeEvents() {
		return getAllResourcesOfType(getPropertyNode(CompositeEvent.typeURI));
	}

	/**
	 * Gets a list of all parevents in the KB
	 * 
	 * @return list of parevents. Empty if there is no one is there. null if
	 *         something went wrong.
	 */
	public Set<ParEvent> getAllParEvents() {
		return getAllResourcesOfType(getPropertyNode(ParEvent.typeURI));
	}

	/**
	 * Gets a list of all seqevents in the KB
	 * 
	 * @return list of seqevents. Empty if there is no one is there. null if
	 *         something went wrong.
	 */
	public Set<SeqEvent> getAllSeqEvents() {
		return getAllResourcesOfType(getPropertyNode(SeqEvent.typeURI));
	}

	/**
	 * Gets a list of all singleevents in the KB
	 * 
	 * @return list of singleevents. Empty if there is no one is there. null if
	 *         something went wrong.
	 */
	public Set<SingleEvent> getAllSingleEvents() {
		return getAllResourcesOfType(getPropertyNode(SingleEvent.typeURI));
	}

	/**
	 * Gets a list of all places in the KB
	 * 
	 * @return list of places. Empty if there is no one is there. null if
	 *         something went wrong.
	 */
	public Set<Place> getAllPlaces() {
		return getAllResourcesOfType(getPropertyNode(Place.typeURI));
	}

	/**
	 * Gets a list of all topics in the KB
	 * 
	 * @return list of topics. Empty if there is no one is there. null if
	 *         something went wrong.
	 */
	public Set<Topic> getAllTopics() {
		return getAllResourcesOfType(getPropertyNode(Topic.typeURI));
	}

	/**
	 * Gets a list of all resources in the KB
	 * 
	 * @return list of resources. Empty if there is no one in there. null if
	 *         something went wrong;
	 */
	public Set<Resource> getAllResources() {
		Set<Resource> resources = new HashSet<Resource>();
		resources.addAll(getAllAgents());
		resources.addAll(getAllConversations());
		resources.addAll(getAllEvents());
		resources.addAll(getAllPlaces());
		resources.addAll(getAllContents());
		resources.addAll(getAllTopics());
		return resources;
	}

	/**
	 * Returns a YartaResource by its URI.
	 * 
	 * @param uri
	 * @return YartaResource
	 */
	public YartaResource getResourceByURI(String uri) {
		try {

			Node subject = thinKnowledgeBase.getResourceByURINoPolicies(uri);

			String className = getJavaClassNameFor(subject.getType());
			Object instance = getCached(className, subject.getName());

			if (instance == null) {
				Class<? extends YartaResource> c = (Class<? extends YartaResource>) Class
						.forName(className);
				Constructor<? extends YartaResource> cons = c
						.getConstructor(new Class[] {
								ThinStorageAccessManager.class, Node.class });

				instance = cons.newInstance(this, subject);

				setCached(className, subject.getName(), instance);
			}
			return (YartaResource) instance;
		} catch (Exception e) {
			logger.e(STORAGE_ACCESS_MGR_LOGTAG,
					"Exception while getting the resource from it's uri for "
							+ uri);
			return null;
		}
	}
}

package fr.inria.arles.iris.web;

import java.util.List;
import java.util.UUID;

import fr.inria.arles.yarta.android.library.ContentClientPictures;
import fr.inria.arles.yarta.android.library.msemanagement.MSEManagerEx;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.resources.Picture;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;

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
						if (createPerson(user)) {
							app.handleNotification("person updated "
									+ user.getUsername());
						}
					}
				} else if (p.equals(Person.PROPERTY_KNOWS_URI)) {
					ensureFriends(object);
				}
			}
		}).start();
	}

	private void ensureFriends(Node node) {
		String userId = node.getName();
		userId = userId.substring(userId.indexOf('_') + 1);

		List<UserItem> users = client.getFriends(userId, 0);

		boolean update = false;
		for (UserItem user : users) {
			update |= createPerson(user);
			update |= addKnows(user);
		}

		if (update) {
			app.handleNotification("friend list updated");
		}
	}

	public void ensureObjectInformation(final Node subject,
			final Node predicate) {
		final String p = predicate.getName();
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (p.equals(Person.PROPERTY_KNOWS_URI)) {
					ensureFriends(subject);
				}
			}
		}).start();
	}

	private boolean addKnows(UserItem item) {
		boolean update = false;

		String reqId = client.getUsername();

		String ownerId = Person.typeURI + "_" + client.getUsername();
		Node owner = kb.getResourceByURINoPolicies(ownerId);

		String userId = Person.typeURI + "_" + item.getUsername();
		Node user = kb.getResourceByURINoPolicies(userId);

		Node p = kb.getResourceByURINoPolicies(Person.PROPERTY_KNOWS_URI);

		try {
			if (kb.getTriple(owner, p, user, reqId) == null) {
				kb.addTriple(owner, p, user, client.getUsername());
				update = true;
			}
			if (kb.getTriple(user, p, owner, reqId) == null) {
				kb.addTriple(user, p, owner, client.getUsername());
				update = true;
			}
		} catch (KBException ex) {
			ex.printStackTrace();
		}

		return update;
	}

	/**
	 * Creates a person with the given details and returns the node.
	 * 
	 * @param kb
	 * @param item
	 * @return true if the person was updated
	 */
	private boolean createPerson(UserItem item) {
		boolean update = false;
		try {
			String userUniqueId = Person.typeURI + "_" + item.getUsername();
			Node s = kb.getResourceByURINoPolicies(userUniqueId);
			if (s == null) {
				update = true;
				s = kb.addResource(userUniqueId, Person.typeURI,
						client.getUsername());
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
				update = true;
			} else {
				pictureId = pictureId.substring(pictureId.indexOf('#') + 1);
			}

			content.setBitmap(pictureId, item.getAvatarURL());
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

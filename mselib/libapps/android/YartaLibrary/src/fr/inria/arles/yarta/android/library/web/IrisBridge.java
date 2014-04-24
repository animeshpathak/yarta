package fr.inria.arles.yarta.android.library.web;

import java.util.List;

import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Person;

/**
 * This class contains useful methods to fetch data into KB using the Iris web
 * client.
 */
public class IrisBridge {

	/**
	 * Queries specified information from Iris web services into the
	 * knowledgeBase;
	 * 
	 * @param kb
	 * @param subject
	 * @param predicate
	 */
	public static void fetchPropertyObject(final KnowledgeBase kb,
			final Node subject, Node predicate) {
		String s = subject.getName();
		String p = predicate.getName();
		log("fetchPropertyObject (%s, %s)", s, p);

		if (s.contains("Person") && p.equals(Agent.PROPERTY_KNOWS_URI)) {
			runAndWait(new Runnable() {

				@Override
				public void run() {
					fetchFriends(kb, subject);
				}
			});
		}
	}

	/**
	 * runAndWait
	 * @param runnable
	 */
	private static void runAndWait(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.start();
		try {
			thread.join();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Fetches the friends of a user into the KB.
	 * 
	 * @param kb
	 * @param user
	 */
	private static void fetchFriends(KnowledgeBase kb, Node userNode) {
		String uid = userNode.getName();
		String username = uid.substring(uid.indexOf('_') + 1, uid.indexOf('@'));
		log("fetchFriends(%s)", username);

		List<UserItem> users = client.getFriends(username);
		Node knowsNode = kb
				.getResourceByURINoPolicies(Agent.PROPERTY_KNOWS_URI);

		for (UserItem user : users) {
			Node person = createPerson(kb, user);
			try {
				kb.addTriple(userNode, knowsNode, person, client.getUsername()
						+ "@inria.fr");
				kb.addTriple(person, knowsNode, userNode, client.getUsername()
						+ "@inria.fr");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Creates a person with the given details and returns the node.
	 * 
	 * @param kb
	 * @param item
	 * @return
	 */
	private static Node createPerson(KnowledgeBase kb, UserItem item) {
		String username = item.getUsername() + "@inria.fr";
		String owner = client.getUsername() + "@inria.fr";

		Node result = null;
		try {
			result = kb.addResource(Person.typeURI + "_" + username,
					Person.typeURI, owner);
			kb.addTriple(result,
					kb.getResourceByURINoPolicies(Person.PROPERTY_USERID_URI),
					kb.addLiteral(username, TYPE_STRING_URI, owner), owner);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private static void log(String format, Object... args) {
		YLoggerFactory.getLogger().d("IrisBridge", String.format(format, args));
	}

	private static final String TYPE_STRING_URI = "http://www.w3.org/2001/XMLSchema#string";
	private static WebClient client = WebClient.getInstance();
}

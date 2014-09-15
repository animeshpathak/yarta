package fr.inria.arles.iris.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.arles.yarta.android.library.resources.Group;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;

/**
 * Contains utility functions which works with the KB.
 */
public class IrisBridgeUtils {

	private KnowledgeBase kb;

	public IrisBridgeUtils(KnowledgeBase kb) {
		this.kb = kb;
	}

	private String getUsername() {
		return ElggClient.getInstance().getUsername();
	}

	/**
	 * Used for caching getResourceByURINoPolicies calls
	 */
	public Map<String, Node> nodesCache = new HashMap<String, Node>();

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

	// literal functions
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
	 * Returns a simple triple.
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

	private static final String TYPE_STRING_URI = "http://www.w3.org/2001/XMLSchema#string";
}

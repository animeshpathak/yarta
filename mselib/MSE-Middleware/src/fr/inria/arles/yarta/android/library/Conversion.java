package fr.inria.arles.yarta.android.library;

import android.os.Bundle;
import fr.inria.arles.yarta.knowledgebase.interfaces.Graph;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;
import fr.inria.arles.yarta.middleware.communication.Message;

/**
 * Used in the {@link KBClient} to talk with the actual service through AIDL. It
 * contains useful functions to wrap/unwrap {@link Graph}s, {@link Triple}s and
 * {@link Node} into/from Bundle.
 */
public class Conversion {

	/**
	 * Converts a Node into a Bundle.
	 * 
	 * @param node
	 *            the node
	 * @return Bundle
	 */
	public static Bundle toBundle(Node node) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("Node", node);
		return bundle;
	}

	/**
	 * Converts a Triple into a Bundle.
	 * 
	 * @param triple
	 *            the triple
	 * @return Bundle
	 */
	public static Bundle toBundle(Triple triple) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("Triple", triple);
		return bundle;
	}

	/**
	 * Converts a Graph into a Bundle.
	 * 
	 * @param graph
	 *            the graph
	 * @return Bundle
	 */
	public static Bundle toBundle(Graph graph) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("Graph", graph);
		return bundle;
	}

	/**
	 * Converts a message to a Bundle.
	 * 
	 * @param message
	 *            the message
	 * @return Bundle
	 */
	public static Bundle toBundle(Message message) {
		Bundle bundle = new Bundle();
		bundle.putInt("type", message.getType());
		bundle.putByteArray("data", message.getData());
		bundle.putString("appid", message.getAppId());
		return bundle;
	}

	/**
	 * Converts a Bundle into a Node.
	 * 
	 * @param bundle
	 *            the bundle
	 * @return Node
	 */
	public static Node toNode(Bundle bundle) {
		return (Node) bundle.getSerializable("Node");
	}

	/**
	 * Converts a Bundle into a Triple.
	 * 
	 * @param bundle
	 *            the bundle
	 * @return Triple
	 */
	public static Triple toTriple(Bundle bundle) {
		return (Triple) bundle.getSerializable("Triple");
	}

	/**
	 * Converts a Bundle into a Graph.
	 * 
	 * @param bundle
	 *            the bundle
	 * @return Graph
	 */
	public static Graph toGraph(Bundle bundle) {
		return (Graph) bundle.getSerializable("Graph");
	}

	/**
	 * Converts a Bundle into a Message.
	 * 
	 * @param bundle
	 *            the bundle
	 * @return Message
	 */
	public static Message toMessage(Bundle bundle) {
		return new Message(bundle.getInt("type"), bundle.getByteArray("data"),
				bundle.getString("appid"));
	}
}

package fr.inria.arles.iris.bridge;

import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;

/**
 * Implements a synchronized queue to support synchronized writes to Yarta KB
 * with Elgg.
 */
public class Queue {

	public void push(Item object) {
		synchronized (operations) {
			operations.add(object);
			lastWrite = System.currentTimeMillis();
		}
	}

	public Item pop() {
		synchronized (operations) {
			return operations.remove(0);
		}
	}

	public Item head() {
		synchronized (operations) {
			return operations.get(0);
		}
	}

	public int size() {
		synchronized (operations) {
			return operations.size();
		}
	}

	/**
	 * Replaces existing writes with newly fetched URI.
	 * 
	 * @param oldURI
	 * @param newURI
	 */
	public void replace(String oldURI, String newURI) {
		synchronized (operations) {
			for (Item item : operations) {
				if (item.isTriple()) {
					replaceNode(item.s, oldURI, newURI);
					replaceNode(item.o, oldURI, newURI);
				} else {
					if (item.nodeURI.equals(oldURI)) {
						item.nodeURI = newURI;
					}
				}
			}
		}
	}

	/**
	 * Replaces a new node with a new one.
	 * 
	 * @param node
	 * @param oldURI
	 * @param newURI
	 * @return
	 */
	private void replaceNode(Node node, String oldURI, String newURI) {
		if (node.getName().equals(oldURI)) {
			MSEResource resource = (MSEResource) node;
			resource.setURI(newURI);
		}
	}

	public long getLastWrite() {
		return lastWrite;
	}

	/**
	 * Represents a write operation to the KB. It can be either a created
	 * resource or an added triple.
	 */
	public static class Item {
		public String nodeURI;
		public String typeURI;

		public Item(String nodeURI, String typeURI) {
			this.nodeURI = nodeURI;
			this.typeURI = typeURI;
		}

		public Node s;
		public Node p;
		public Node o;

		public Item(Node s, Node p, Node o) {
			this.s = s;
			this.p = p;
			this.o = o;
		}

		public boolean isTriple() {
			return s != null && p != null && o != null;
		}
	}

	private long lastWrite = 0;
	private List<Queue.Item> operations = new ArrayList<Queue.Item>();
}

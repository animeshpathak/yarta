package fr.inria.arles.yarta.knowledgebase;

import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;

public class MSETriple implements Triple {

	/**
	 * Serial id for serialization.
	 */
	private static final long serialVersionUID = 1L;
	private Node subject;
	private Node property;
	private Node object;

	public MSETriple(Node s, Node p, Node o) {
		this.subject = s;
		this.property = p;
		this.object = o;

	}

	/**
	 * @see fr.inria.arles.mse.knowledgebase.interfaces.Triple#getObject()
	 */
	public Node getObject() {
		return this.object;
	}

	/**
	 * @see fr.inria.arles.mse.knowledgebase.interfaces.Triple#getProperty()
	 */
	public Node getProperty() {
		return this.property;
	}

	/**
	 * @see fr.inria.arles.mse.knowledgebase.interfaces.Triple#getSubject()
	 */
	public Node getSubject() {
		return this.subject;
	}

	/**
	 * @return the triple in the form (subject, predicate, object)
	 */
	public String toString() {
		String s = "(" + this.subject.toString() + " , "
				+ this.property.toString() + " , " + this.object.toString()
				+ ")\n";
		return s;

	}
}

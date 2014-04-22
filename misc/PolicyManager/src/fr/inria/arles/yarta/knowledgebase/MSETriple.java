/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase;

import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;

/**
 * @author alessandra
 *
 */
public class MSETriple implements Triple {
	
	private Node subject;
	private Node property;
	private Node object;

	public MSETriple(Node s, Node p, Node o){
		this.subject = s;
		this.property = p;
		this.object = o;
		
	}
	/* (non-Javadoc)
	 * @see fr.inria.arles.mse.knowledgebase.interfaces.Triple#getObject()
	 */
	public Node getObject() {
		return this.object;
	}

	/* (non-Javadoc)
	 * @see fr.inria.arles.mse.knowledgebase.interfaces.Triple#getProperty()
	 */
	public Node getProperty() {
		
		return this.property;
	}

	/* (non-Javadoc)
	 * @see fr.inria.arles.mse.knowledgebase.interfaces.Triple#getSubject()
	 */
	public Node getSubject() {
		
		return this.subject;
	}
	
	/**
	 * @return the triple in the form (subject, predicate, object)
	 */
	public String toString()	{
		
		String s = "(" + this.subject.toString() + " , " + this.property.toString() + " , " + this.object.toString() + ")\n";
		return s;
		
		
		
	}

}

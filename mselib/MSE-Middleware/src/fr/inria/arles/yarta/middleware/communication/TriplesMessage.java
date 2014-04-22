package fr.inria.arles.yarta.middleware.communication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;

/**
 * Basic message holding a list of triples. Using this since a cast from Object
 * to List is not safe.
 */
public class TriplesMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Triple> triples = new ArrayList<Triple>();

	public TriplesMessage(List<Triple> triples) {
		this.triples.clear();
		this.triples.addAll(triples);
	}

	public List<Triple> getTriples() {
		return triples;
	}
}

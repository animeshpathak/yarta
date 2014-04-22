package fr.inria.arles.yarta.middleware.communication;

import java.io.Serializable;
import java.util.List;

import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;

/**
 * Message sent in response to an update request. It contains a status and the
 * graph of all information the requested user is willing to share with the
 * requesting user
 */
public class UpdateResponseMessage implements Serializable {

	private static final long serialVersionUID = -5706929719358901152L;

	private List<Long> times;
	private List<Triple> triples;
	private String result;

	public UpdateResponseMessage(String r, List<Triple> triples,
			List<Long> times) {
		this.result = r;
		this.triples = triples;
		this.times = times;
	}

	public String getResult() {
		return this.result;
	}

	public List<Triple> getTriples() {
		return triples;
	}

	public List<Long> getTimes() {
		return times;
	}
}

package fr.inria.arles.yarta.middleware.communication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;

public class UpdateResponseFullMessage extends UpdateResponseMessage {
	private static final long serialVersionUID = 1;

	private Map<String, byte[]> contentData = new HashMap<String, byte[]>();

	public UpdateResponseFullMessage(String r, List<Triple> triples,
			List<Long> times, Map<String, byte[]> contentData) {
		super(r, triples, times);
		this.contentData = contentData;
	}

	public Map<String, byte[]> getContentData() {
		return contentData;
	}

	public void setContentData(Map<String, byte[]> contentData) {
		this.contentData = contentData;
	}

}

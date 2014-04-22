package fr.inria.arles.yarta.resources;

import java.util.Set;

import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.middleware.msemanagement.ThinStorageAccessManager;

/**
 * This class represents any topic that might be, for example, subject of
 * interest for a person or object of description in a document (content) or
 * many others. It was inspired by users' tagging activities. As such, it might
 * be mapped onto keywords, folksonomies or more structured taxonomies.
 */
public class TopicImpl extends YartaResource implements Topic {

	/**
	 * Wraps a given node into a TopicImpl object
	 * 
	 * @param sam
	 *            The storage and access manager
	 * @param n
	 *            The node to wrap
	 */
	public TopicImpl(ThinStorageAccessManager sam, Node n) {
		super(sam, n);
	}

	@Override
	public boolean addIsTagged(Topic t) {
		return sam.setObjectProperty(kbNode, PROPERTY_ISTAGGED_URI, t);
	}

	@Override
	public Set<Topic> getIsTagged() {
		return sam.getObjectProperty(kbNode, PROPERTY_ISTAGGED_URI);
	}

	@Override
	public boolean deleteIsTagged(Topic t) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_ISTAGGED_URI, t);
	}

	@Override
	public String getTitle() {
		return sam.getDataProperty(kbNode, PROPERTY_TITLE_URI,
				String.class);
	}

	@Override
	public void setTitle(String topicTitle) {
		sam.setDataProperty(kbNode, PROPERTY_TITLE_URI, String.class,
				topicTitle);
	}

	@Override
	public Set<Agent> getHasInterest_inverse() {
		return sam.getObjectProperty_inverse(kbNode,
				Agent.PROPERTY_HASINTEREST_URI);
	}

	@Override
	public Set<Resource> getIsTagged_inverse() {
		return sam.getObjectProperty_inverse(kbNode,
				Resource.PROPERTY_ISTAGGED_URI);
	}
}

package fr.inria.arles.yarta.resources;

import java.util.Set;

import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.middleware.msemanagement.ThinStorageAccessManager;

/**
 * A generic class representing all possible types of entities carrying some
 * information.
 */
public class ContentImpl extends YartaResource implements Content {

	/**
	 * Wraps a given node into a ContentImpl object
	 * 
	 * @param sam
	 *            The storage and access manager
	 * @param n
	 *            The node to wrap
	 */
	public ContentImpl(ThinStorageAccessManager sam, Node n) {
		super(sam, n);
	}

	@Override
	public String getFormat() {
		return sam.getDataProperty(kbNode, PROPERTY_FORMAT_URI, String.class);
	}

	@Override
	public void setFormat(String format) {
		sam.setDataProperty(kbNode, PROPERTY_FORMAT_URI, String.class, format);
	}

	@Override
	public String getIdentifier() {
		return sam.getDataProperty(kbNode, PROPERTY_IDENTIFIER_URI,
				String.class);
	}

	@Override
	public void setIdentifier(String identifier) {
		sam.setDataProperty(kbNode, PROPERTY_IDENTIFIER_URI, String.class,
				identifier);
	}

	@Override
	public String getSource() {
		return sam.getDataProperty(kbNode, PROPERTY_SOURCE_URI, String.class);
	}

	@Override
	public void setSource(String source) {
		sam.setDataProperty(kbNode, PROPERTY_SOURCE_URI, String.class, source);
	}

	@Override
	public String getTitle() {
		return sam.getDataProperty(kbNode, PROPERTY_TITLE_URI, String.class);
	}

	@Override
	public void setTitle(String contentTitle) {
		sam.setDataProperty(kbNode, PROPERTY_TITLE_URI, String.class,
				contentTitle);
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
	public Set<Agent> getCreator_inverse() {
		return sam
				.getObjectProperty_inverse(kbNode, Agent.PROPERTY_CREATOR_URI);
	}

	@Override
	public Set<Agent> getHasInterest_inverse() {
		return sam.getObjectProperty_inverse(kbNode,
				Agent.PROPERTY_HASINTEREST_URI);
	}

	@Override
	public Set<Group> getHasContent_inverse() {
		return sam.getObjectProperty_inverse(kbNode,
				Group.PROPERTY_HASCONTENT_URI);
	}

	@Override
	public boolean addHasReply(Content c) {
		return sam.setObjectProperty(kbNode, PROPERTY_HASREPLY_URI, c);
	}

	@Override
	public Set<Content> getHasReply() {
		return sam.getObjectProperty(kbNode, PROPERTY_HASREPLY_URI);
	}

	@Override
	public boolean deleteHasReply(Content c) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_HASREPLY_URI, c);
	}
	
	@Override
	public Set<Content> getHasReply_inverse() {
		return sam.getObjectProperty_inverse(kbNode,
				Content.PROPERTY_HASREPLY_URI);
	}
	
	@Override
	public Long getTime() {
		return Long.valueOf(sam.getDataProperty(kbNode,
				Message.PROPERTY_TIME_URI, String.class));
	}

	@Override
	public void setTime(Long timestamp) {
		sam.setDataProperty(kbNode, Message.PROPERTY_TIME_URI, String.class,
				String.valueOf(timestamp));
	}

	@Override
	public String getContent() {
		return sam.getDataProperty(kbNode, Message.PROPERTY_CONTENT_URI,
				String.class);
	}

	@Override
	public void setContent(String content) {
		sam.setDataProperty(kbNode, Message.PROPERTY_CONTENT_URI, String.class,
				content);
	}
}

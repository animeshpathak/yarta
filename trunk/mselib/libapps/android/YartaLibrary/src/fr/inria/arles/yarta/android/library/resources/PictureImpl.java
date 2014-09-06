package fr.inria.arles.yarta.android.library.resources;

import fr.inria.arles.yarta.middleware.msemanagement.ThinStorageAccessManager;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Group;
import fr.inria.arles.yarta.resources.Message;
import fr.inria.arles.yarta.resources.YartaResource;

import java.util.Set;

import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.resources.Topic;

/**
 * 
 * Picture class implementation.
 * 
 */
public class PictureImpl extends YartaResource implements Picture {

	/**
	 * Wraps a given node into a PictureImpl object
	 * 
	 * @param sam
	 *            The storage and access manager
	 * @param n
	 *            The node to wrap
	 */
	public PictureImpl(ThinStorageAccessManager sam, Node n) {
		super(sam, n);
	}

	/**
	 * @return the title. Null if value is not set.
	 */
	public String getTitle() {
		return sam.getDataProperty(kbNode, PROPERTY_TITLE_URI, String.class);
	}

	/**
	 * Sets the title.
	 * 
	 * @param string
	 *            the title to be set
	 */
	public void setTitle(String string) {
		sam.setDataProperty(kbNode, PROPERTY_TITLE_URI, String.class, string);
	}

	/**
	 * @return the source. Null if value is not set.
	 */
	public String getSource() {
		return sam.getDataProperty(kbNode, PROPERTY_SOURCE_URI, String.class);
	}

	/**
	 * Sets the source.
	 * 
	 * @param string
	 *            the source to be set
	 */
	public void setSource(String string) {
		sam.setDataProperty(kbNode, PROPERTY_SOURCE_URI, String.class, string);
	}

	/**
	 * @return the format. Null if value is not set.
	 */
	public String getFormat() {
		return sam.getDataProperty(kbNode, PROPERTY_FORMAT_URI, String.class);
	}

	/**
	 * Sets the format.
	 * 
	 * @param string
	 *            the format to be set
	 */
	public void setFormat(String string) {
		sam.setDataProperty(kbNode, PROPERTY_FORMAT_URI, String.class, string);
	}

	/**
	 * @return the identifier. Null if value is not set.
	 */
	public String getIdentifier() {
		return sam.getDataProperty(kbNode, PROPERTY_IDENTIFIER_URI,
				String.class);
	}

	/**
	 * Sets the identifier.
	 * 
	 * @param string
	 *            the identifier to be set
	 */
	public void setIdentifier(String string) {
		sam.setDataProperty(kbNode, PROPERTY_IDENTIFIER_URI, String.class,
				string);
	}

	/**
	 * Creates a "istagged" edge between this picture and topic
	 * 
	 * @param topic
	 *            the Topic
	 * 
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addIsTagged(Topic topic) {
		return sam.setObjectProperty(kbNode, PROPERTY_ISTAGGED_URI, topic);
	}

	/**
	 * deletes the "istagged" link between this picture and topic
	 * 
	 * @param topic
	 *            the Topic
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteIsTagged(Topic topic) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_ISTAGGED_URI, topic);
	}

	/**
	 * 
	 * @return The list of resources linked by a "istagged" edge with the
	 *         current resource. Empty list if I know no one. null if there was
	 *         an error
	 * 
	 */
	@Override
	public Set<Topic> getIsTagged() {
		return sam.getObjectProperty(kbNode, PROPERTY_ISTAGGED_URI);
	}

	/**
	 * inverse of {@link #getPicture()}
	 */
	@Override
	public Set<Agent> getPicture_inverse() {
		return sam
				.getObjectProperty_inverse(kbNode, Agent.PROPERTY_PICTURE_URI);
	}

	/**
	 * inverse of {@link #getHasInterest()}
	 */
	@Override
	public Set<fr.inria.arles.yarta.resources.Agent> getHasInterest_inverse() {
		return sam.getObjectProperty_inverse(kbNode,
				fr.inria.arles.yarta.resources.Agent.PROPERTY_HASINTEREST_URI);
	}

	/**
	 * inverse of {@link #getCreator()}
	 */
	@Override
	public Set<fr.inria.arles.yarta.resources.Agent> getCreator_inverse() {
		return sam.getObjectProperty_inverse(kbNode,
				fr.inria.arles.yarta.resources.Agent.PROPERTY_CREATOR_URI);
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
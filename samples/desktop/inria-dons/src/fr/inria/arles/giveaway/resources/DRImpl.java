package fr.inria.arles.giveaway.resources;

import fr.inria.arles.yarta.middleware.msemanagement.ThinStorageAccessManager;
import fr.inria.arles.yarta.resources.YartaResource;
import fr.inria.arles.yarta.resources.Agent;
import java.util.Set;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.resources.Topic;

/**
 * 
 * DR class implementation.
 *
 */
public class DRImpl extends YartaResource implements DR {

	/**
	 * Wraps a given node into a DRImpl object
	 * 
	 * @param	sam
	 * 			The storage and access manager
	 * @param	n
	 * 			The node to wrap
	 */
	public DRImpl(ThinStorageAccessManager sam, Node n) {
		super(sam, n);
	}

	/**
	 * Creates a "istagged" edge between this dr and topic
	 * 
	 * @param	topic
	 *			the Topic
	 *
	 * @return true if all went well, false otherwise
	 */
	@Override
	public boolean addIsTagged(Topic topic) {
		return sam.setObjectProperty(kbNode, PROPERTY_ISTAGGED_URI, topic);
	}

	/**
	 * deletes the "istagged" link between this dr and topic
	 * 
	 * @param	topic
	 * 			the Topic
	 * @return true if success. false is something went wrong
	 */
	@Override
	public boolean deleteIsTagged(Topic topic) {
		return sam.deleteObjectProperty(kbNode, PROPERTY_ISTAGGED_URI, topic);
	}

	/**
	 * 
	 * @return	The list of resources linked by a "istagged" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	@Override
	public Set<Topic> getIsTagged() {
		return sam.getObjectProperty(kbNode, PROPERTY_ISTAGGED_URI);
	}

	/**
	 * inverse of {@link #getHasInterest()}
	 */
	@Override
	public Set<Agent> getHasInterest_inverse() {
		return sam.getObjectProperty_inverse(kbNode, Agent.PROPERTY_HASINTEREST_URI);
	}
}
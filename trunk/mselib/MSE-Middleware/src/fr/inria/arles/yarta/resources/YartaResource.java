package fr.inria.arles.yarta.resources;

import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.middleware.msemanagement.ThinStorageAccessManager;

/**
 * This class represents a resource as it is stored in the Yarta DB. The main
 * purpose is to define the constructor, which sets the pointers to the storage
 * and access manager, and the KBNode
 */
public class YartaResource {

	/**
	 * Explicit dummy constructor. Mainly for allowing it to be extended.
	 */
	public YartaResource() {
		// does nothing
	}

	/**
	 * Points to the {@link StorageAccessManager} handling this Resource. Note
	 * that we are not using a KB, since that would mean passing userIDs when
	 * making calls. The SAM auto-stores the ownerID Protected [as opposed to
	 * private] since we want the subclasses to read this variable
	 */
	protected ThinStorageAccessManager sam;

	/**
	 * Node that stores the pointer to the KB node. This is what is used by all
	 * other getter and setter methods to point to the relevant resource in the
	 * KB. Protected [as opposed to private] since we want the subclasses to
	 * read this variable
	 */
	protected Node kbNode;

	/**
	 * Constructor which sets the internal variables
	 */
	public YartaResource(ThinStorageAccessManager sam, Node kbNode) {
		this.sam = sam;
		this.kbNode = kbNode;
	}

	/**
	 * Get the KB Node associated with this {@link YartaResource}.
	 * <b>CAREFUL!</b> This should be used only by the
	 * {@link ThinStorageAccessManager}
	 * 
	 * @return the {@link Node} associated with this {@link YartaResource}
	 */
	public Node getNode() {
		return this.kbNode;
	}

	/**
	 * Returns the unique id of this resource.
	 * 
	 * @return String the id
	 */
	public String getUniqueId() {
		return this.kbNode.getName();
	}

	/**
	 * Override Object.equals in order to distinguish between different
	 * resources in a HashSet.
	 */
	@Override
	public boolean equals(Object object) {
		if (object != null && object instanceof Resource) {
			Resource r = (Resource) object;
			return getUniqueId().equals(r.getUniqueId());
		}
		return super.equals(object);
	}

	/**
	 * Hash code used for comparisons.
	 */
	@Override
	public int hashCode() {
		return getUniqueId().hashCode();
	}
}

package fr.inria.arles.yarta.resources;

import java.util.Set;

/**
 * Generic High-level Resource Class. Not functional except to use in methods
 * where the domain or range is not specified.
 */
public interface Resource {

	static final long serialVersionUID = 1975308196771829115L;

	static final String baseMSEURI = "http://yarta.gforge.inria.fr/ontologies/mse.rdf";

	/** the URI for the property isLocated */
	public static final String PROPERTY_ISTAGGED_URI = baseMSEURI + "#isTagged";

	/**
	 * Gets the unique ID of resource. Implemented in the {@link YartaResource}
	 * class.
	 * 
	 * @return The unique ID of the resource. Null if things go wrong.
	 */
	public String getUniqueId();

	/**
	 * To check if a resource is equal to another.
	 * 
	 * @param r
	 *            The other resource
	 * @return true if they are, false if not.
	 */
	public boolean equals(Object r);

	/**
	 * Hash code needed for method equals.
	 * 
	 * @return int (the actual hash code)
	 */
	public int hashCode();

	/**
	 * Creates a "isTagged" edge from this resource to a topic
	 * 
	 * @param t
	 *            the topic
	 * @return true if all went well, false otherwise
	 */
	public boolean addIsTagged(Topic t);

	/**
	 * 
	 * @return The list of topics this resource is tagged with. Empty list if
	 *         there are no such topics. null if there was an error
	 */
	public Set<Topic> getIsTagged();

	/**
	 * deletes the "isTagged" edge from this resource to a topic
	 * 
	 * @param t
	 *            the topic
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteIsTagged(Topic t);

	/**
	 * inverse of {@link Agent#getHasInterest()}
	 * 
	 * @return The list of agents who have interest in this resource. Empty list
	 *         if there are no such agents. null if there was an error
	 */
	public Set<Agent> getHasInterest_inverse();
}

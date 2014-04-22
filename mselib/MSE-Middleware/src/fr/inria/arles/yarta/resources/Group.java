package fr.inria.arles.yarta.resources;

import java.util.Set;

/**
 * <p>
 * A group is a set of people. Both groups and single persons have the same
 * first-class properties linking them to other first-class entities: therefore,
 * they are modeled as subclass of the same Agent class. A person is linked to a
 * group by the rdfs:member property. We reuse this existing property, whether
 * another more specific property is needed has to be seen.
 * </p>
 */
public interface Group extends Agent {

	/** The unique "type" URI */
	public static final String typeURI = baseMSEURI + "#Group";

	/**
	 * inverse of {@link Agent#getIsMemberOf()}
	 * 
	 * @return The list of agents who are a member of this group. Empty list if
	 *         there are no such agents. null if there was an error
	 */
	public Set<Agent> getIsMemberOf_inverse();
}

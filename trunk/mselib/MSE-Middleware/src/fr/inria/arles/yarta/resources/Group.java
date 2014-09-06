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
	
	/** the URI for the property hasContent */
	public static final String PROPERTY_HASCONTENT_URI = baseMSEURI + "#hasContent";

	/**
	 * inverse of {@link Agent#getIsMemberOf()}
	 * 
	 * @return The list of agents who are a member of this group. Empty list if
	 *         there are no such agents. null if there was an error
	 */
	public Set<Agent> getIsMemberOf_inverse();
	
	/**
	 * Creates a "has content" edge between this group and a content
	 * 
	 * @param c
	 *            The content this group holds
	 * @return true if all went well, false otherwise
	 */
	public boolean addHasContent(Content c);

	/**
	 * 
	 * @return The list of contents this group holds. Empty list if there
	 *         are no such contents. null if there was an error
	 */
	public Set<Content> getHasContent();

	/**
	 * deletes the "has content " link between this group and a content
	 * 
	 * @param c
	 *            the content
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteHasContent(Content c);
}

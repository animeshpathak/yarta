package fr.inria.arles.yarta.resources;

import java.util.Set;

/**
 * <p>
 * The notion of event is twofold. From a theoretical perspective, events
 * represent 4D occurrences, i.e., occurrences that happen both in space and
 * time. Differently from 3D occurrences (e.g., Person) events exist in time and
 * do not change, at least as a whole, over time. From a social application
 * perspective, events represent entities happening in time having some kind of
 * social meaning/impact. Social events, such as meetings, parties or talks, are
 * common examples of events, although they do not cover all the expressivity of
 * this class. For example, a location-based social application might model the
 * entering of a person in a room as an Event. Specialized subclasses are
 * therefore needed for each application.
 */
public interface Event extends Resource {

	static final long serialVersionUID = 1L;

	/** The unique "type" URI */
	public static final String typeURI = baseMSEURI + "#Event";

	/** the URI for the property description */
	public static final String PROPERTY_DESCRIPTION_URI = baseMSEURI
			+ "#description";

	/** the URI for the property title */
	public static final String PROPERTY_TITLE_URI = baseMSEURI + "#title";

	/** the URI for the property isLocated */
	public static final String PROPERTY_ISLOCATED_URI = baseMSEURI
			+ "#isLocated";

	/**
	 * @return the description
	 */
	public String getDescription();

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description);

	/**
	 * @return the title
	 */
	public String getTitle();

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String eventTitle);

	/**
	 * Creates a "isLocated" edge from this event to a place
	 * 
	 * @param p
	 *            the place
	 * @return true if all went well, false otherwise
	 */
	public boolean addIsLocated(Place p);

	/**
	 * 
	 * @return The list of places this event is located in. Empty list if there
	 *         are no such places. null if there was an error
	 */
	public Set<Place> getIsLocated();

	/**
	 * deletes the "isLocated" edge from this event to a place
	 * 
	 * @param p
	 *            the place
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteIsLocated(Place p);

	/**
	 * inverse of {@link Agent#getIsAttending()}
	 * 
	 * @return the List of agents who participate to this event. Empty list of
	 *         there are none. Null if there was an error.
	 */
	public Set<Agent> getIsAttending_inverse();
}

package fr.inria.arles.yarta.conference.resources;

import fr.inria.arles.yarta.conference.msemanagement.MSEManagerEx;
import java.util.Set;
import fr.inria.arles.yarta.resources.Resource;

/**
 * 
 * Event interface definition.
 *
 */
public interface Event extends Resource, fr.inria.arles.yarta.resources.Event {

	/** the URI for property title */
	public static final String PROPERTY_TITLE_URI = baseMSEURI + "#title";

	/** the URI for property description */
	public static final String PROPERTY_DESCRIPTION_URI = baseMSEURI + "#description";

	/** the URI for property isTagged */
	public static final String PROPERTY_ISTAGGED_URI = baseMSEURI + "#isTagged";

	/** the URI for property follows */
	public static final String PROPERTY_FOLLOWS_URI = MSEManagerEx.baseMSEURI + "#follows";

	/** the URI for property isLocated */
	public static final String PROPERTY_ISLOCATED_URI = baseMSEURI + "#isLocated";

	/**
	 * Creates a "follows" edge between this event and event
	 * 
	 * @param	event
	 *			the Event
	 *
	 * @return true if all went well, false otherwise
	 */
	public boolean addFollows(Event event);
	
	/**
	 * deletes the "follows" link between this event and event
	 * 
	 * @param	event
	 * 			the Event
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteFollows(Event event);
	
	/**
	 * 
	 * @return	The list of resources linked by a "follows" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	public Set<Event> getFollows();

	/**
	 * inverse of {@link #getFollows()}
	 */
	public Set<Event> getFollows_inverse();
}
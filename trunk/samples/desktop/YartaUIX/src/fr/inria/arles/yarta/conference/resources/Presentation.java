package fr.inria.arles.yarta.conference.resources;

import fr.inria.arles.yarta.conference.msemanagement.MSEManagerEx;
import java.util.Set;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.yarta.resources.Topic;

/**
 * 
 * Presentation interface definition.
 *
 */
public interface Presentation extends Resource, Event {
	public static final String typeURI = MSEManagerEx.baseMSEURI + "#Presentation";

	/** the URI for property title */
	public static final String PROPERTY_TITLE_URI = baseMSEURI + "#title";

	/** the URI for property description */
	public static final String PROPERTY_DESCRIPTION_URI = baseMSEURI + "#description";

	/** the URI for property isTagged */
	public static final String PROPERTY_ISTAGGED_URI = baseMSEURI + "#isTagged";

	/** the URI for property follows */
	public static final String PROPERTY_FOLLOWS_URI = MSEManagerEx.baseMSEURI + "#follows";

	/** the URI for property contains */
	public static final String PROPERTY_CONTAINS_URI = MSEManagerEx.baseMSEURI + "#contains";

	/** the URI for property isLocated */
	public static final String PROPERTY_ISLOCATED_URI = baseMSEURI + "#isLocated";

	/** the URI for property isIncluded */
	public static final String PROPERTY_ISINCLUDED_URI = MSEManagerEx.baseMSEURI + "#isIncluded";

	/**
	 * Creates a "contains" edge between this presentation and topic
	 * 
	 * @param	topic
	 *			the Topic
	 *
	 * @return true if all went well, false otherwise
	 */
	public boolean addContains(Topic topic);
	
	/**
	 * deletes the "contains" link between this presentation and topic
	 * 
	 * @param	topic
	 * 			the Topic
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteContains(Topic topic);
	
	/**
	 * 
	 * @return	The list of resources linked by a "contains" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	public Set<Topic> getContains();

	/**
	 * Creates a "isincluded" edge between this presentation and conference
	 * 
	 * @param	conference
	 *			the Conference
	 *
	 * @return true if all went well, false otherwise
	 */
	public boolean addIsIncluded(Conference conference);
	
	/**
	 * deletes the "isincluded" link between this presentation and conference
	 * 
	 * @param	conference
	 * 			the Conference
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteIsIncluded(Conference conference);
	
	/**
	 * 
	 * @return	The list of resources linked by a "isincluded" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	public Set<Conference> getIsIncluded();
}
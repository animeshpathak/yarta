package fr.inria.arles.foosball.resources;

import fr.inria.arles.foosball.msemanagement.MSEManagerEx;
import fr.inria.arles.yarta.resources.Event;
import java.util.Set;
import fr.inria.arles.yarta.resources.Resource;

/**
 * 
 * Match interface definition.
 *
 */
public interface Match extends Resource, Event {
	public static final String typeURI = MSEManagerEx.baseMSEURI + "#Match";

	/** the URI for property title */
	public static final String PROPERTY_TITLE_URI = baseMSEURI + "#title";

	/** the URI for property time */
	public static final String PROPERTY_TIME_URI = MSEManagerEx.baseMSEURI + "#time";

	/** the URI for property blueScore */
	public static final String PROPERTY_BLUESCORE_URI = MSEManagerEx.baseMSEURI + "#blueScore";

	/** the URI for property description */
	public static final String PROPERTY_DESCRIPTION_URI = baseMSEURI + "#description";

	/** the URI for property redScore */
	public static final String PROPERTY_REDSCORE_URI = MSEManagerEx.baseMSEURI + "#redScore";

	/** the URI for property isTagged */
	public static final String PROPERTY_ISTAGGED_URI = baseMSEURI + "#isTagged";

	/** the URI for property blueO */
	public static final String PROPERTY_BLUEO_URI = MSEManagerEx.baseMSEURI + "#blueO";

	/** the URI for property redO */
	public static final String PROPERTY_REDO_URI = MSEManagerEx.baseMSEURI + "#redO";

	/** the URI for property redD */
	public static final String PROPERTY_REDD_URI = MSEManagerEx.baseMSEURI + "#redD";

	/** the URI for property blueD */
	public static final String PROPERTY_BLUED_URI = MSEManagerEx.baseMSEURI + "#blueD";

	/** the URI for property isLocated */
	public static final String PROPERTY_ISLOCATED_URI = baseMSEURI + "#isLocated";

	/**
	 * @return the time
	 */
	public Long getTime();

	/**
	 * @param	time
	 * 			the time to set
	 */
	public void setTime(Long time);

	/**
	 * @return the bluescore
	 */
	public Integer getBlueScore();

	/**
	 * @param	bluescore
	 * 			the bluescore to set
	 */
	public void setBlueScore(Integer bluescore);

	/**
	 * @return the redscore
	 */
	public Integer getRedScore();

	/**
	 * @param	redscore
	 * 			the redscore to set
	 */
	public void setRedScore(Integer redscore);

	/**
	 * Creates a "blueo" edge between this match and person
	 * 
	 * @param	person
	 *			the Person
	 *
	 * @return true if all went well, false otherwise
	 */
	public boolean addBlueO(Person person);
	
	/**
	 * deletes the "blueo" link between this match and person
	 * 
	 * @param	person
	 * 			the Person
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteBlueO(Person person);
	
	/**
	 * 
	 * @return	The list of resources linked by a "blueo" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	public Set<Person> getBlueO();

	/**
	 * Creates a "redo" edge between this match and person
	 * 
	 * @param	person
	 *			the Person
	 *
	 * @return true if all went well, false otherwise
	 */
	public boolean addRedO(Person person);
	
	/**
	 * deletes the "redo" link between this match and person
	 * 
	 * @param	person
	 * 			the Person
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteRedO(Person person);
	
	/**
	 * 
	 * @return	The list of resources linked by a "redo" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	public Set<Person> getRedO();

	/**
	 * Creates a "redd" edge between this match and person
	 * 
	 * @param	person
	 *			the Person
	 *
	 * @return true if all went well, false otherwise
	 */
	public boolean addRedD(Person person);
	
	/**
	 * deletes the "redd" link between this match and person
	 * 
	 * @param	person
	 * 			the Person
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteRedD(Person person);
	
	/**
	 * 
	 * @return	The list of resources linked by a "redd" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	public Set<Person> getRedD();

	/**
	 * Creates a "blued" edge between this match and person
	 * 
	 * @param	person
	 *			the Person
	 *
	 * @return true if all went well, false otherwise
	 */
	public boolean addBlueD(Person person);
	
	/**
	 * deletes the "blued" link between this match and person
	 * 
	 * @param	person
	 * 			the Person
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteBlueD(Person person);
	
	/**
	 * 
	 * @return	The list of resources linked by a "blued" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	public Set<Person> getBlueD();
}
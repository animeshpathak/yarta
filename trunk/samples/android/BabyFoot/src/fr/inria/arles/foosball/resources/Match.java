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
	 * inverse of {@link #getBlueO()}
	 */
	public Set<Player> getBlueO_inverse();

	/**
	 * inverse of {@link #getRedO()}
	 */
	public Set<Player> getRedO_inverse();

	/**
	 * inverse of {@link #getRedD()}
	 */
	public Set<Player> getRedD_inverse();

	/**
	 * inverse of {@link #getBlueD()}
	 */
	public Set<Player> getBlueD_inverse();
}
package fr.inria.arles.foosball.resources;

import fr.inria.arles.foosball.msemanagement.MSEManagerEx;
import fr.inria.arles.yarta.resources.Agent;
import java.util.Set;
import fr.inria.arles.yarta.resources.Resource;

/**
 * 
 * Person interface definition.
 *
 */
public interface Person extends Resource, fr.inria.arles.yarta.resources.Person {

	/** the URI for property lastName */
	public static final String PROPERTY_LASTNAME_URI = baseMSEURI + "#lastName";

	/** the URI for property totalGames */
	public static final String PROPERTY_TOTALGAMES_URI = MSEManagerEx.baseMSEURI + "#totalGames";

	/** the URI for property scorePoints */
	public static final String PROPERTY_SCOREPOINTS_URI = MSEManagerEx.baseMSEURI + "#scorePoints";

	/** the URI for property email */
	public static final String PROPERTY_EMAIL_URI = baseMSEURI + "#email";

	/** the URI for property nickName */
	public static final String PROPERTY_NICKNAME_URI = MSEManagerEx.baseMSEURI + "#nickName";

	/** the URI for property userId */
	public static final String PROPERTY_USERID_URI = baseMSEURI + "#userId";

	/** the URI for property name */
	public static final String PROPERTY_NAME_URI = baseMSEURI + "#name";

	/** the URI for property winRate */
	public static final String PROPERTY_WINRATE_URI = MSEManagerEx.baseMSEURI + "#winRate";

	/** the URI for property lastPlayed */
	public static final String PROPERTY_LASTPLAYED_URI = MSEManagerEx.baseMSEURI + "#lastPlayed";

	/** the URI for property firstName */
	public static final String PROPERTY_FIRSTNAME_URI = baseMSEURI + "#firstName";

	/** the URI for property homepage */
	public static final String PROPERTY_HOMEPAGE_URI = baseMSEURI + "#homepage";

	/** the URI for property knows */
	public static final String PROPERTY_KNOWS_URI = baseMSEURI + "#knows";

	/** the URI for property isTagged */
	public static final String PROPERTY_ISTAGGED_URI = baseMSEURI + "#isTagged";

	/** the URI for property isAttending */
	public static final String PROPERTY_ISATTENDING_URI = baseMSEURI + "#isAttending";

	/** the URI for property hasInterest */
	public static final String PROPERTY_HASINTEREST_URI = baseMSEURI + "#hasInterest";

	/** the URI for property isMemberOf */
	public static final String PROPERTY_ISMEMBEROF_URI = baseMSEURI + "#isMemberOf";

	/** the URI for property participatesTo */
	public static final String PROPERTY_PARTICIPATESTO_URI = baseMSEURI + "#participatesTo";

	/** the URI for property isLocated */
	public static final String PROPERTY_ISLOCATED_URI = baseMSEURI + "#isLocated";

	/** the URI for property creator */
	public static final String PROPERTY_CREATOR_URI = baseMSEURI + "#creator";

	/**
	 * @return the totalgames
	 */
	public Integer getTotalGames();

	/**
	 * @param	totalgames
	 * 			the totalgames to set
	 */
	public void setTotalGames(Integer totalgames);

	/**
	 * @return the scorepoints
	 */
	public Integer getScorePoints();

	/**
	 * @param	scorepoints
	 * 			the scorepoints to set
	 */
	public void setScorePoints(Integer scorepoints);

	/**
	 * @return the nickname
	 */
	public String getNickName();

	/**
	 * @param	nickname
	 * 			the nickname to set
	 */
	public void setNickName(String nickname);

	/**
	 * @return the winrate
	 */
	public Integer getWinRate();

	/**
	 * @param	winrate
	 * 			the winrate to set
	 */
	public void setWinRate(Integer winrate);

	/**
	 * @return the lastplayed
	 */
	public Long getLastPlayed();

	/**
	 * @param	lastplayed
	 * 			the lastplayed to set
	 */
	public void setLastPlayed(Long lastplayed);

	/**
	 * inverse of {@link #getBlueO()}
	 */
	public Set<Match> getBlueO_inverse();

	/**
	 * inverse of {@link #getRedO()}
	 */
	public Set<Match> getRedO_inverse();

	/**
	 * inverse of {@link #getRedD()}
	 */
	public Set<Match> getRedD_inverse();

	/**
	 * inverse of {@link #getBlueD()}
	 */
	public Set<Match> getBlueD_inverse();
}
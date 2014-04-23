package fr.inria.arles.foosball.resources;

import fr.inria.arles.foosball.msemanagement.MSEManagerEx;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.Agent;
import java.util.Set;
import fr.inria.arles.yarta.resources.Resource;

/**
 * 
 * Player interface definition.
 *
 */
public interface Player extends Resource, Agent, Person {
	public static final String typeURI = MSEManagerEx.baseMSEURI + "#Player";

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

	/** the URI for property blueO */
	public static final String PROPERTY_BLUEO_URI = MSEManagerEx.baseMSEURI + "#blueO";

	/** the URI for property isMemberOf */
	public static final String PROPERTY_ISMEMBEROF_URI = baseMSEURI + "#isMemberOf";

	/** the URI for property redO */
	public static final String PROPERTY_REDO_URI = MSEManagerEx.baseMSEURI + "#redO";

	/** the URI for property participatesTo */
	public static final String PROPERTY_PARTICIPATESTO_URI = baseMSEURI + "#participatesTo";

	/** the URI for property redD */
	public static final String PROPERTY_REDD_URI = MSEManagerEx.baseMSEURI + "#redD";

	/** the URI for property blueD */
	public static final String PROPERTY_BLUED_URI = MSEManagerEx.baseMSEURI + "#blueD";

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
	 * Creates a "blueo" edge between this player and match
	 * 
	 * @param	match
	 *			the Match
	 *
	 * @return true if all went well, false otherwise
	 */
	public boolean addBlueO(Match match);
	
	/**
	 * deletes the "blueo" link between this player and match
	 * 
	 * @param	match
	 * 			the Match
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteBlueO(Match match);
	
	/**
	 * 
	 * @return	The list of resources linked by a "blueo" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	public Set<Match> getBlueO();

	/**
	 * Creates a "redo" edge between this player and match
	 * 
	 * @param	match
	 *			the Match
	 *
	 * @return true if all went well, false otherwise
	 */
	public boolean addRedO(Match match);
	
	/**
	 * deletes the "redo" link between this player and match
	 * 
	 * @param	match
	 * 			the Match
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteRedO(Match match);
	
	/**
	 * 
	 * @return	The list of resources linked by a "redo" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	public Set<Match> getRedO();

	/**
	 * Creates a "redd" edge between this player and match
	 * 
	 * @param	match
	 *			the Match
	 *
	 * @return true if all went well, false otherwise
	 */
	public boolean addRedD(Match match);
	
	/**
	 * deletes the "redd" link between this player and match
	 * 
	 * @param	match
	 * 			the Match
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteRedD(Match match);
	
	/**
	 * 
	 * @return	The list of resources linked by a "redd" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	public Set<Match> getRedD();

	/**
	 * Creates a "blued" edge between this player and match
	 * 
	 * @param	match
	 *			the Match
	 *
	 * @return true if all went well, false otherwise
	 */
	public boolean addBlueD(Match match);
	
	/**
	 * deletes the "blued" link between this player and match
	 * 
	 * @param	match
	 * 			the Match
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteBlueD(Match match);
	
	/**
	 * 
	 * @return	The list of resources linked by a "blued" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	public Set<Match> getBlueD();
}
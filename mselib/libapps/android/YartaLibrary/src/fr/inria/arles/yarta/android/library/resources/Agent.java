package fr.inria.arles.yarta.android.library.resources;

import java.util.Set;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.yarta.android.library.msemanagement.MSEManagerEx;

/**
 * 
 * Agent interface definition.
 *
 */
public interface Agent extends Resource, fr.inria.arles.yarta.resources.Agent {

	/** the URI for property email */
	public static final String PROPERTY_EMAIL_URI = baseMSEURI + "#email";

	/** the URI for property name */
	public static final String PROPERTY_NAME_URI = baseMSEURI + "#name";

	/** the URI for property homepage */
	public static final String PROPERTY_HOMEPAGE_URI = baseMSEURI + "#homepage";

	/** the URI for property picture */
	public static final String PROPERTY_PICTURE_URI = MSEManagerEx.baseMSEURI + "#picture";

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
	 * Creates a "picture" edge between this agent and picture
	 * 
	 * @param	picture
	 *			the Picture
	 *
	 * @return true if all went well, false otherwise
	 */
	public boolean addPicture(Picture picture);
	
	/**
	 * deletes the "picture" link between this agent and picture
	 * 
	 * @param	picture
	 * 			the Picture
	 * @return true if success. false is something went wrong
	 */
	public boolean deletePicture(Picture picture);
	
	/**
	 * 
	 * @return	The list of resources linked by a "picture" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	public Set<Picture> getPicture();
}
package fr.inria.arles.yarta.android.library.resources;

import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.yarta.android.library.msemanagement.MSEManagerEx;

/**
 * 
 * Group interface definition.
 *
 */
public interface Group extends Resource, Agent, fr.inria.arles.yarta.resources.Group {

	/** The unique "type" URI */
	public static final String typeURI = baseMSEURI + "#Group";
	
	/** the URI for property ownerName */
	public static final String PROPERTY_OWNERNAME_URI = MSEManagerEx.baseMSEURI + "#ownerName";

	/** the URI for property email */
	public static final String PROPERTY_EMAIL_URI = baseMSEURI + "#email";

	/** the URI for property description */
	public static final String PROPERTY_DESCRIPTION_URI = MSEManagerEx.baseMSEURI + "#description";

	/** the URI for property name */
	public static final String PROPERTY_NAME_URI = baseMSEURI + "#name";

	/** the URI for property homepage */
	public static final String PROPERTY_HOMEPAGE_URI = baseMSEURI + "#homepage";

	/** the URI for property members */
	public static final String PROPERTY_MEMBERS_URI = MSEManagerEx.baseMSEURI + "#members";

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

	/** the URI for property hasContent */
	public static final String PROPERTY_HASCONTENT_URI = baseMSEURI + "#hasContent";

	/** the URI for property creator */
	public static final String PROPERTY_CREATOR_URI = baseMSEURI + "#creator";

	/**
	 * @return the ownername
	 */
	public String getOwnerName();

	/**
	 * @param	ownername
	 * 			the ownername to set
	 */
	public void setOwnerName(String ownername);

	/**
	 * @return the description
	 */
	public String getDescription();

	/**
	 * @param	description
	 * 			the description to set
	 */
	public void setDescription(String description);

	/**
	 * @return the members
	 */
	public Long getMembers();

	/**
	 * @param	members
	 * 			the members to set
	 */
	public void setMembers(Long members);
}
package fr.inria.arles.giveaway.resources;

import fr.inria.arles.yarta.resources.Agent;
import java.util.Set;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.giveaway.msemanagement.MSEManagerEx;

/**
 * 
 * Person interface definition.
 *
 */
public interface Person extends Resource, Agent, fr.inria.arles.yarta.resources.Person {

	/** the URI for property lastName */
	public static final String PROPERTY_LASTNAME_URI = baseMSEURI + "#lastName";

	/** the URI for property phone */
	public static final String PROPERTY_PHONE_URI = MSEManagerEx.baseMSEURI + "#phone";

	/** the URI for property email */
	public static final String PROPERTY_EMAIL_URI = baseMSEURI + "#email";

	/** the URI for property userId */
	public static final String PROPERTY_USERID_URI = baseMSEURI + "#userId";

	/** the URI for property name */
	public static final String PROPERTY_NAME_URI = baseMSEURI + "#name";

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
	 * @return the phone
	 */
	public String getPhone();

	/**
	 * @param	phone
	 * 			the phone to set
	 */
	public void setPhone(String phone);

	/**
	 * inverse of {@link #getHiddenBy()}
	 */
	public Set<Announcement> getHiddenBy_inverse();
}
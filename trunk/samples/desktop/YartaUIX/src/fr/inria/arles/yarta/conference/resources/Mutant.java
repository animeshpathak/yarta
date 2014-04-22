package fr.inria.arles.yarta.conference.resources;

import fr.inria.arles.yarta.conference.msemanagement.MSEManagerEx;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Group;
import fr.inria.arles.yarta.resources.Resource;

/**
 * 
 * Mutant interface definition.
 *
 */
public interface Mutant extends Place, Resource, Event, Agent, Content, Speaker, Person, Group {
	public static final String typeURI = MSEManagerEx.baseMSEURI + "#Mutant";

	/** the URI for property lastName */
	public static final String PROPERTY_LASTNAME_URI = baseMSEURI + "#lastName";

	/** the URI for property format */
	public static final String PROPERTY_FORMAT_URI = baseMSEURI + "#format";

	/** the URI for property homepage */
	public static final String PROPERTY_HOMEPAGE_URI = baseMSEURI + "#homepage";

	/** the URI for property title */
	public static final String PROPERTY_TITLE_URI = baseMSEURI + "#title";

	/** the URI for property source */
	public static final String PROPERTY_SOURCE_URI = baseMSEURI + "#source";

	/** the URI for property email */
	public static final String PROPERTY_EMAIL_URI = baseMSEURI + "#email";

	/** the URI for property description */
	public static final String PROPERTY_DESCRIPTION_URI = baseMSEURI + "#description";

	/** the URI for property name */
	public static final String PROPERTY_NAME_URI = baseMSEURI + "#name";

	/** the URI for property userId */
	public static final String PROPERTY_USERID_URI = baseMSEURI + "#userId";

	/** the URI for property gender */
	public static final String PROPERTY_GENDER_URI = MSEManagerEx.baseMSEURI + "#gender";

	/** the URI for property longitude */
	public static final String PROPERTY_LONGITUDE_URI = baseMSEURI + "#longitude";

	/** the URI for property latitude */
	public static final String PROPERTY_LATITUDE_URI = baseMSEURI + "#latitude";

	/** the URI for property firstName */
	public static final String PROPERTY_FIRSTNAME_URI = baseMSEURI + "#firstName";

	/** the URI for property identifier */
	public static final String PROPERTY_IDENTIFIER_URI = baseMSEURI + "#identifier";

	/** the URI for property knows */
	public static final String PROPERTY_KNOWS_URI = baseMSEURI + "#knows";

	/** the URI for property isTagged */
	public static final String PROPERTY_ISTAGGED_URI = baseMSEURI + "#isTagged";

	/** the URI for property follows */
	public static final String PROPERTY_FOLLOWS_URI = MSEManagerEx.baseMSEURI + "#follows";

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
}
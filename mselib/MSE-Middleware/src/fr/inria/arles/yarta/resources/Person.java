package fr.inria.arles.yarta.resources;

/**
 * <p>
 * Person can be mapped onto existing social ontologies, such
 * Friend-of-A-Friend.
 * </p>
 * <p>
 * Person has a number of datatype properties (i.e., properties whose value is a
 * literal, such as a string or an integer) + some object properties (e.g.,
 * knows). These properties are inspired by FoaF, and thus set equivalent to
 * them. The MSE ontology does not directly import the properties because the
 * current version of FoaF is OWL-Full (due to DL axioms + mixed use of OWL
 * Datatype and Object property). To avoid this complexity, we keep our ontology
 * as OWL Lite (RDFS-like), but we model properties so that the mapping onto
 * FoaF is made easy to perform. Also, existing FoaF profiles can be directly
 * imported and used if needed.
 * </p>
 * <p>
 * This class actually refer to a human being, which is the main actor of any
 * social situation.
 * </p>
 */
public interface Person extends Agent {

	/** The unique "type" URI */
	public static final String typeURI = baseMSEURI + "#Person";

	/** the URI for the property firstName */
	public static final String PROPERTY_FIRSTNAME_URI = baseMSEURI
			+ "#firstName";

	/** the URI for the property lastName */
	public static final String PROPERTY_LASTNAME_URI = baseMSEURI + "#lastName";

	/** the URI for the property uniqueRequestorId */
	public static final String PROPERTY_USERID_URI = baseMSEURI + "#userId";

	/**
	 * @return the firstName
	 */
	public String getFirstName();

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName);

	/**
	 * @return the lastName
	 */
	public String getLastName();

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName);

	/**
	 * @return the userId
	 */
	public String getUserId();

	/**
	 * @param userId
	 *            the userId to set. Make sure it is unique. We suggest using
	 *            emails.
	 */
	public void setUserId(String userId);
}

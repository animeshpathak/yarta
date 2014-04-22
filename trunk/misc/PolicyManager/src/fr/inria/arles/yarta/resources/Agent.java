/**
 * 
 */
package fr.inria.arles.yarta.resources;

/**
 * The most general class for agentive entities. This is the class that traditionally 
 * includes groups and single person. See also FoaF and DOLCE-UltraLite.
 * @author pathak
 *
 */
public class Agent extends Resource implements Uniqueable{
	
	private static String baseMSEURI = 
		"http://yarta.gforge.inria.fr/ontologies/mse.rdf";
	
	/** The unique "type" URI*/
	public static final String typeURI = 
		baseMSEURI + "#Agent"; 

	/** the URI for the property email */
	public static final String PROPERTY_EMAIL_URI =
		baseMSEURI + "#email";
	
	private String email;
	
	/** the URI for the property homepage */
	public static final String PROPERTY_HOMEPAGE_URI =
		baseMSEURI + "#homepage";
	private String homepage;
	
	/** the URI for the property name */
	public static final String PROPERTY_NAME_URI =
		baseMSEURI + "#name";
	private String name;
	
	/** Dummy constructor */
	public Agent(){
		//do nothing
	}
	
	
	/**
	 * Constructor to set the uniqueId manually. 
	 * Pre-pends a string and saves it as the unique ID.
	 * @param uniqueId
	 */
	public Agent(String uniqueString){
		this.uniqueId = "Agent_" + uniqueString;
	}
	

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the homepage
	 */
	public String getHomepage() {
		return homepage;
	}

	/**
	 * @param homepage the homepage to set
	 */
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}

/**
 * 
 */
package fr.inria.arles.yarta.resources;

/**
 * <p>Person can be mapped onto existing social ontologies, 
 * such Friend-of-A-Friend.</p>
 * <p>Person has a number of datatype properties (i.e., properties 
 * whose value is a literal, such as a string or an integer) + 
 * some object properties (e.g., knows). These properties are 
 * inspired by FoaF, and thus set equivalent to them. The MSE 
 * ontology does not directly import the properties because the 
 * current version of FoaF is OWL-Full (due to DL axioms + mixed 
 * use of OWL Datatype and Object property). To avoid this 
 * complexity, we keep our ontology as OWL Lite (RDFS-like), 
 * but we model properties so that the mapping onto FoaF is made 
 * easy to perform. Also, existing FoaF profiles can be directly 
 * imported and used if needed.</p>
 * <p>This class actually refer to a human being, which is the 
 * main actor of any social situation.</p>
 * @author pathak
 *
 */
public class Person extends Agent implements Uniqueable{
	private static String baseMSEURI = 
		"http://yarta.gforge.inria.fr/ontologies/mse.rdf";
	
	/** The unique "type" URI*/
	public static final String typeURI = 
		baseMSEURI + "#Person"; 

	/** the URI for the property firstName */
	public static final String PROPERTY_FIRSTNAME_URI =
		baseMSEURI + "#firstName";
	/** Can be mapped onto FOAF firstName */
	private String firstName;
	
	/** the URI for the property lastName */
	public static final String PROPERTY_LASTNAME_URI =
		baseMSEURI + "#lastName";
	/** Can be mapped on foaf:lastName */
	private String lastName;
	
	
	/**
	 * Dummy constructor. To help in Extending the Class.
	 */
	public Person(){
		//do nothing.
	}
	
	/**
	 * Constructor to set the uniqueId manually. 
	 * Pre-pends a string and saves it as the unique ID.
	 * @param uniqueId
	 */
	public Person(String uniqueString){
		this.uniqueId = "Person_" + uniqueString;
	}
	
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
}

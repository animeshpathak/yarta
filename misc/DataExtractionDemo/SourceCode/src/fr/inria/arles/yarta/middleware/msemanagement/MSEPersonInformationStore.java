/**
* <p>This package contains classes that extract information from
 * various social data source.
 * </p>
 * <p>The classes of this package forms the functional unit of Data
 * Extraction Manager</p>
 
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement;


/**
 * 
 * The base class for the Information storage.
 * This class is extended by other class for 
 * information store
 * 
 * @author Kumar Nishant
 * 
 *
 */
public class MSEPersonInformationStore {

	/**
	 * The first name of a MSE person
	 */
	public String firstName;
	/**
	 * The last name of a MSE person
	 */
	
	public String lastName;
	/**
	 * The full name of a MSE person
	 */
	
	public String fullName;
	/**
	 * The affiliation of a MSE person
	 */
	
	public String affiliation;
	/**
	 * The profile URL of a MSE person
	 */
	
	public String websiteURL;
	
	public EmailStore [] emailStore;
	//ArrayList<MSEPersonInformationStore> friends;
	

	
	public MSEPersonInformationStore(){
		firstName=null;
		lastName=null;
		fullName=null;
	    affiliation=null;
		websiteURL=null;
	//	friends=null;
	}
	
}

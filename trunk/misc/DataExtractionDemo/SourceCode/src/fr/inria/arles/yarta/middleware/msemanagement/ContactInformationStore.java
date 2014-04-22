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
 * Stores the various information for a contact 
 *  in the contact
 * list of the Android phone user
 * 
 * 
 *  
 * @author Kumar Nishant
 *
 */
public class ContactInformationStore extends MSEPersonInformationStore
										{
	/**
	 * The storage for more that one phoneNumber for the contact found
	 */
	public PhoneNumberStore[] phoneNumberStore;
	
	

	
	public ContactInformationStore(){
		
	

	}
	

	
}

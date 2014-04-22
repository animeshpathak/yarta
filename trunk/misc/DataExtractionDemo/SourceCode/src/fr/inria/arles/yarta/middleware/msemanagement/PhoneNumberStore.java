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
 * Store the phone number and its type for a particular user.
 * 
 * Phone number type may include Mobile, Home, Work, Other  etc.
 * 
 * @author Kumar Nishant
 *
 */
public class PhoneNumberStore {

	public 	String phoneNumberType;
	public String phoneNumber;
	
	public PhoneNumberStore(){

		phoneNumberType = new String();
		phoneNumber= new String();
	}
	
}

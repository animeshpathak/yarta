/**
<p>This package contains classes that extract information from
 * various social data source.
 * </p>
 * <p>The classes of this package forms the functional unit of Data
 * Extraction Manager</p>
 
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement;

/**
 * Stores the extracted email
 * of a user.
 * 
 * Email Type may be work, home, office
 * 
 * 
 * @author Kumar Nishant 
 *
 */
public class EmailStore {
	public  String emailType;
	public  String emailAddress;

	public EmailStore(){
			emailType=new String();
			emailAddress=new String();
	 }
}

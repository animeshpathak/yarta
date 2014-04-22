/**
* <p>This package contains the classes that extract information from
 * various social data source.
 * </p>
 * <p>The classes of this package forms the functional unit of Data
 * Extraction Manager</p>
 
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement;

/**
 * 
 * Stores the information about the SMS  extracted from user 
 * android mobile phone
 * 
 * @author Nishant Kumar
 *
 */
public class SMSInformationStore extends MSEPersonInformationStore {

	/**
	 * The date when the sms was sent or received
	 */
	public String smsDate;
	/**
	 * The time of the day  when the sms was sent or received
	 */
	
	public String smsTime;
	/**
	 * The type (incoming/ outgoing )of the sms 
	 */
	
	public 	String smsType;
	/**
	 * The number from which the sms was received or to which 
	 * the sms was sent
	 */
	public String smsNumber;
	/**
	 *  Set to true if the number to/from which the sms was sent/recieved
	 *  is also in contact list
	 */
	boolean isFriend;
	
	/**
	 * If the number from/to which the sms is received/sent is in contact list,
	 * then the contact id of the contact is stored in this variable, otherwise null
	 */
	public String contactID;

	/**
	 * Constructore initializing the variables to stor ethe information for sms
	 */
	public SMSInformationStore(){
	
		 smsDate = null;
		 smsTime= null;
		 smsType= null;
		 smsNumber= null;
		 isFriend= false;
		 
	}
	
	
}

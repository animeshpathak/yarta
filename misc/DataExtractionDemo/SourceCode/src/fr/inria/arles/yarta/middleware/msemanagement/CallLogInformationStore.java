/**
 * <p>This package contains classes for extraction of information from
 * various social data source.
 * </p>
 * <p>The classes of this package forms the functional unit of Data
 * Extraction Manager</p>
 
 */
package fr.inria.arles.yarta.middleware.msemanagement;

/**
 * Store information extracted from the call log 
 * of the Android phone.
 * 
 * @author Nishant Kumar
 * 
 *
 */
public class CallLogInformationStore extends MSEPersonInformationStore {

	/**
	 * The date of the call as shown in an entry in Call log of Android phone
	 */	
	public String callDate;
	
	/**
	 * The duration of the call as shown in an entry in the Call log of Android phone
	 */
	public String callDuration;
	
	/**
	 * The type of the call (Incoming, Outgoing, Missed) as shown in an entry
	 * in the Call log of Android phone
	 */
	public String callType;
	
	/**
	 * The mobile number of the call as shown in an entry in the Call log of Android phone
	 */
	public String mobileNumber;
	
	/**
	 * The time(in 24 Hrs format) of the call present in the Call log of Android phone
	 */
	public String callTime;

	
	public CallLogInformationStore(){
		
		mobileNumber=null;
		callTime=null;
		callDate=null;
		callDuration=null;
		callType=null;	
	}
}

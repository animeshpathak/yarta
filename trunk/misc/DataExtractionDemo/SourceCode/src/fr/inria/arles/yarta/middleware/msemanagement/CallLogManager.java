/**
* <p>This package contains classes that extract information from
 * various social data source.
 * </p>
 * <p>The classes of this package forms the functional unit of Data
 * Extraction Manager</p>
 
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement;


import java.util.ArrayList;

import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.text.format.Time;
import android.content.Context;

import fr.inria.arles.yarta.logging.*;

/**
 * Provides method to extract information
 * from Call Log of an Android phone. The extracted
 * information from the call history are stored in the 
 * object of CallLogInformationStore class and the 
 * object is then stored in an arraylist.
 * 
 * @author  Kumar Nishant
 * @since   June 6, 2010
 *
 */
public class CallLogManager extends PhoneProcessor{
	
	
	String [] columnNames;
	Uri callLogURI;
	
	public ArrayList<CallLogInformationStore> callLogStore;
	/**
	 * Constructor for the class
	 */
	public CallLogManager(){
				
	        callLogURI =CallLog.Calls.CONTENT_URI;
			callLogStore = null;
	        
		    columnNames = new String []{
				   CallLog.Calls.CACHED_NAME,
				   CallLog.Calls.NUMBER,
				   CallLog.Calls.TYPE ,
				   CallLog.Calls.DURATION,
				   CallLog.Calls.DATE,
	              			       };
		  
		
	}
	
	   /**
	    * Extract the information from the  
	    * call log and store the extracted information in an 
	    * object of CallLogInformationStore class. Then the  object is 
	    * stored in an arraylist
	    *  
	    * The calling function must pass the application context 
	    * ( getApplicationContext() )
	    * 
	    * <p>The following information are extracted by this method</p>
	    * <ui>
	    *   <li>Name</li>
	    *   <li>MobileNumber</li>
	    *   <li>CallType</li>
	    *   <li>Duration</li>
	    *   <li>Date</li>
	    *   <li>Time</li>
	    *   <li>Email</li>
	    * </ui>
	    * 
	    * @param context  The application context of the Android activity  
	    *           from where this function is called. 
	    *         
	    * 
	    * @return The arrayList containing the objects of CallLogInformationStore
	    *         class.
	    *         <ui>
	    *         <li>Each object in the arraylist represents a call log in 
	    *         CallLog of the phone</li> 
	    *          </ui>
	    */
	   public ArrayList<CallLogInformationStore> getCallHistory(Context context){
		  
		   try{
			   YLogger Log = YLoggerFactory.getLogger();
			   Log.d("CallLogManager.getCallHistory", "Start extrating Call Log");			
				 
				/*
			    * Make a query to CallLog Content and obtain the result
			    */
			   Cursor callLogResult =   queryProcessor( context,
					   												callLogURI, 
					   												columnNames, 
					   												null, 
					   												null,
					   												null);
			   
			   Log.d("CallLogManager.getCallHistory", "Query for Call Log extraction over");
			   
			   /*
			    * Process of Storing call log info:-
			    * 
			    * If there is any Call Log record, then move to each record,
			    * get the column Index of the column, and extract the value 
			    * of that column
			    * 
			    * Form a CallLogInformationStore object and store the data in it.
			    * Add the object in the arraylist
			    * 
			    */
			   if(callLogResult!=null && callLogResult.getCount()>0)
			   {   
				   callLogStore =  extractInfoFromQueryResult( context,callLogResult);
				   return callLogStore;
			   }
			 
	  		   Log.d("CallLogManager.getCallHistory", "No call log found");
			   callLogResult.close();
			   messageString="No CallLog Found\n1.Check your Call Log Record"+
			                  "2.View Log file for more Information";
			   return null;
		   }
		   catch(Exception ex){
			    String  debugString ="CallLogManager.getCallHistory: ";
			    debugString +=	ex.getMessage();
			    YLogger Log = YLoggerFactory.getLogger();
			    Log.d("CallLogManager.getCallHistory", debugString);
	    		 messageString=debugString;
  
	    		return null;
		   }
	   }   

	   
	   
	   /**
	    * Extract all entries from a particular name in 
	    * Call Log. The extracted information is stored in an object of 
	    * CallLogInformationStore class.
	    * Then this object is added to an Arraylist	     
	    * 
	    * <p>The following information are extracted by this method</p>
	    * <ui>
	    *   <li>Name</li>
	    *   <li>MobileNumber</li>
	    *   <li>CallType</li>
	    *   <li>Duration</li>
	    *   <li>Date</li>
	    *   <li>Time</li>
	    *   <li>Email</li>
	    * </ui>
	    * 
	    *   
	    * @param context  The application context of the Android activity
	    * from where this function is called. 
	    * 
	    * @param name The name for which the entry in call log
	    *             will be extracted
	    * 
	    * @return The arrayList containing the objects of CallLogInformationStore class.
	    * 
	    *         An object in the arraylist has various information for an entry
	    *          in the CallLog. 
	    */
	   public ArrayList<CallLogInformationStore> getCallHistoryByName(Context context,String name){
		   
		   try{
			   
			   YLogger Log = YLoggerFactory.getLogger();		   
			   
	  		   Log.d("CallLogManager.getCallHistoryByName","Inside getCallHistoryByName");
	  		   
			     
			   /*
			    * Make a query to CallLog Content and obtain the result
			    */
			   Cursor callLogResult =  queryProcessor( context,callLogURI, 
							columnNames, 
							Calls.CACHED_NAME + "=?", 
							new String[]{String.valueOf(name)},
							null); 
			   
	  		   Log.d("CallLogManager.getCallHistoryByName",
	  				   "Query over and call log extracted for the name");
			   
			   /*
			    * Process of extracting info from Call logs:- 
			    * 
			    */
			   if(callLogResult!=null && callLogResult.getCount()>0)
			   {
				   callLogStore =   extractInfoFromQueryResult( context,callLogResult);
				   return callLogStore;
			  
			   }
			  
	  		   Log.d("CallLogManager.getCallHistoryByName", "No call log found for the name");
			   callLogResult.close();
			   messageString="No call log found for the name";
			   return null;
		   }
		   catch(Exception ex){
			   String  debugString ="CallLOgManager.CallHistoryByName: ";
			    debugString +=	ex.getMessage(); 
			    YLogger Log = YLoggerFactory.getLogger();		   
				   
			    Log.d("CallLogManager.getCallHistoryByName", debugString);
		  		messageString=debugString;
	    		return null;
		   }
	   }   

       

	   /**
	    * Extract all entries in 
	    * call log for a given mobile number and store the extracted 
	    * information in a CallLogInformationStore class object.
	    * 
	     * <p>The following information are extracted by this method</p>
	    * <ui>
	    *   <li>Name</li>
	    *   <li>MobileNumber</li>
	    *   <li>CallType</li>
	    *   <li>Duration</li>
	    *   <li>Date</li>
	    *   <li>Time</li>
	    *   <li>Email</li>
	    * </ui>
	    * 
	    *
	    * @param context  The application context of the Android activity   
	    *        from where this function is called.
	    * 
	    * @param mobileNumber The mobile number for which the entry 
	    *                      in call log will be extracted
	    *                      
	    * 
	    * @return The arrayList containing the objects of CallLogInformationStore class.
	    * 
	    *  An object in the arraylist has various information for an entry
	    *          in the CallLog. 
	    *          
	    */
	   public ArrayList<CallLogInformationStore> getCallHistoryByNumber(Context context,
			   															String mobileNumber){
		   
		   
		   try{
			  /*
			    * Make a query to CallLog Content and obtain the result
			    */
			   YLogger Log = YLoggerFactory.getLogger();
				  
			   Cursor callLogResult =  queryProcessor( context, callLogURI, 
							columnNames, 
							Calls.NUMBER + "=?", 
							new String[]{String.valueOf(mobileNumber)},
							null); 
	
	           Log.d("CallLogManager.getCallHistoryByNumber",
	        		   "Query Over for Getting callLog by Number");
			   /*
			    * If there is any Call Log recored,  then move to each record,
			    */
			   if(callLogResult!=null && callLogResult.getCount()>0)
			   {
				 callLogStore = extractInfoFromQueryResult(context,callLogResult);
				   return callLogStore;
			  
				}
		
	           Log.d("CallLogManager.getCallHistoryByNumber","No call Log found for the number");
	           messageString="No call Log found for the number";
			   return null;
		   }
		   catch(Exception ex){
			   String  debugString = "CallLogManager.CallLogByNumber: ";
	            debugString +=	ex.getMessage();
	            YLogger Log = YLoggerFactory.getLogger();
	  		    Log.d("CallLogManager.getCallHistoryByNumber",debugString);
	    		messageString=debugString;
	    		return null;
		   }
	   }   

	   

	   /**
	    * Extract entries in 
	    * call log after a given time and store the extracted 
	    * information in object of CallLogInformationStore class .
	    * 
	    * <p>The following information are extracted by this method</p>
	    * <ui>
	    *   <li>Name</li>
	    *   <li>MobileNumber</li>
	    *   <li>CallType</li>
	    *   <li>Duration</li>
	    *   <li>Date</li>
	    *   <li>Time</li>
	    *   <li>Email</li>
	    * </ui>
	    * 
	    *
	    * @param context  The application context of the Android  activity  
	    * 		          from where this function is called. 
	    *           
	    * @param day The day of the month of the call (1 - 31)
	    * @param month The  month of the call (1 - 12) 
	    * @param year The year when the call took place 
	    * @param hour The hour of the day in same format as(00 - 24) 
	    *             of the clock of phone 
	    *             
	    *                      
	    * 
	    * @return The arraylist containing the objects of CallLogInformationStore 
	    * class.
	    
	    *  An object in the arraylist has various information for an entry
	    *          in the CallLog. 
	    * 
	    */
	   public ArrayList<CallLogInformationStore> getCallHistorySinceTime(Context context,
			    														int day,
			    														int month,
			    														int year,
			    														int hour){
		   
		  
		   try{
			   /*
			    * Convert the given time parameter to the Time object
			    * 
			    * In the time object , the month is represented in 
			    * range (0-11). So, in the month field, subtracting by 1
			    * 
			    * 
			    */

			   YLogger Log = YLoggerFactory.getLogger();
			     
			   Log.d("CallLogManager.getCallHistorySinceTime",
					   "Going to set time in Time object");
			     
			   Time setTime = new Time();
		       setTime.set(0, 0, hour, day, month - 1,year);
		       
		       
		       Long sinceTime = setTime.toMillis(false);
		       
			   Log.d("CallLogManager.getCallHistorySinceTime",
					   "Time set and value of sec obtained");
			     

			   /*
			    * Make a query to CallLog Content and obtain the result
			    */
			   Cursor callLogResult =   queryProcessor(context,
					   		callLogURI, 
							columnNames, 
							Calls.DATE + ">?", 
							new String[]{String.valueOf(sinceTime)},
							null); 
			   
			
	           Log.d("CallLogManager.getCallHistorySinceTime",
	        		   	"Query Over for Getting callLog by Number");
			   /*
			    * If there is any Call Log recored,  then move to each record,
			    * get the column Index of the column, and extract the text of that column
			    * 
			    * Form a CallLogInformationStore object and store the data in it.
			    * Add the object in the arraylist
			    * 
			    */
			   if(callLogResult!=null && callLogResult.getCount()>0)
			   {    
				   callLogStore =  extractInfoFromQueryResult(context,callLogResult);
				   return callLogStore;
			  
			   }
			   
	           Log.d("CallLogManager.getCallHistorySinceTime","No call Log found");
	           messageString = "No Call Log found after the given time";
			   return null;
		   }
		   catch(Exception ex){
			  String  debugString = "CallLogManager.CallLogSinceTime: ";
	            debugString +=	ex.getMessage();

	 		   YLogger Log = YLoggerFactory.getLogger();
	 		   
	            Log.d("CallLogManager.getCallHistorySinceTime",debugString);
	    		messageString = debugString;
	    		return null;
		   }
	   }  

	   
	   /**
	    * Extract all entries in 
	    * call log between a given time and store the extracted 
	    * information in an object of CallLogInformationStore class.
	    * 
	    * 
	    * <p>The following information are extracted by this method</p>
	    * <ui>
	    *   <li>Name</li>
	    *   <li>MobileNumber</li>
	    *   <li>CallType</li>
	    *   <li>Duration</li>
	    *   <li>Date</li>
	    *   <li>Time</li>
	    *   <li>Email</li>
	    * </ui>
	    * 
	    * @param context  The application context of Android activity 
	    *           from where this function is called. It is used in 
	    *           making query to Call Log Content
	    * 
	    * <p>The parameters take the two time between which the call logs will
	    * be extracted.</p> 
	    * 
	    *   @param   fromDay    The date after which the call log is to be extracted
			@param	 fromMonth  The month after which the call log is to be extracted 
			@param	 fromYear   The year after which the call log is to be extracted
            @param   fromHour   The hour of the day after which the call los is to be extracted
     		@param	 toDay      The day till when the call log is to be extracted
			@param	 toMonth    The month till when the call log need to be extracted
			@param	 toYear     The year till when the call log is to be extracted
			@param	 toHour     The hour till when the call log is to be extracted
			
			
	   	*                      
	    * 
	    * @return The ArrayList containing the objects of CallLogInformationStore
	    * class.
	    *  An object in the arraylist has various information for an entry
	    *          in the CallLog. 
	    
	    * 
	    * 
	    */
	   public ArrayList<CallLogInformationStore> getCallHistoryBetweenTime(Context context,
			    														int fromDay,
			    														int fromMonth,
			    														int fromYear,
			    														int fromHour,
			    														int toDay,
			    														int toMonth,
			    														int toYear,
			    														int toHour){
		    
		   try{
			   /*
			    * Convert the given time parameter to the Time object
			    * 
			    * In the time object , the month is represented in 
			    * range (0-11). So, in the month field, subtracting by 1
			    * 
			    * 
			    */
			   YLogger Log = YLoggerFactory.getLogger();
				
				   
			   Time setTime = new Time();
		       setTime.set(0, 0, fromHour,fromDay,fromMonth - 1,fromYear);
		       
		       Long fromTime = setTime.toMillis(false);
		       
		       // setting the fields of time object
		       setTime.set(0, 0, toHour,toDay,toMonth - 1,toYear);
	           // Converting the given date to milli sec.
		       Long toTime = setTime.toMillis(false);
		      
			  /*
			    * Make a query to CallLog Content and obtain the result
			    */
			   Cursor callLogResult =   queryProcessor( context,
					   							callLogURI, 
					   							columnNames, 
					   							Calls.DATE + ">? AND " + Calls.DATE + " < ?", 
					   							new String[]{String.valueOf(fromTime),String.valueOf(toTime)},
					   							null); 
			   
		
	           Log.d("CallLogManager.getCallHistoryBetweenTime",
	        		   "Query Over for Getting callLog between time");
			   /*
			    * If there is any Call Log recored,  then move to each record,
			    * get the column Index of the column, and extract the text of that column
			    * 
			    * Form a CallLogInformationStore object and store the data in it.
			    * Add the object in the arraylist
			    * 
			    */
			   if(callLogResult!=null && callLogResult.getCount()>0)
			   {    
				   callLogStore =  extractInfoFromQueryResult(context,callLogResult);
				   return callLogStore;
			  
			   }
		
	           Log.d("CallLogManager.getCallHistoryBetweenTime",
	        		   "No call Log found between the specified period");
	           
	           messageString ="No call Log found between the specified Period";
	           return null;
		   }
		   catch(Exception ex){
			    String debugString = "CallLOgManager.CallLogBetweenTime: ";
	            debugString +=	ex.getMessage();
	            YLogger Log = YLoggerFactory.getLogger();
	            Log.d("CallLogManager.getCallHistoryBetweenTime",debugString);
	    		messageString = debugString;
	    		return null;
		   }
	   }  

	   
	   
	  /**
	   *  Find the friend of the Android phone user on basis of call log.
	   *  If the mobile number of a call log entry is also in 
	   *  the contact list then the user whose mobile number is 
	   *  found in the call log is considered as a friend.
	   *  
	   *  It extracts all entries in call log. 
	   *   If the user found in call log entry is also in contact list
	   *   then he is declared a friend.
	   * 
	   * @param context  The application context of the Android activity from 
	   * where this function is called.
	   *  
	   * @return  The arraylist storing the object of CallLogInformationStore. 
	   * These objects store information about the contact in contact list.
	   * that is the friend of user.
	   * 
	   */
	  public ArrayList<CallLogInformationStore>findFriendFromCallLog(Context context){
        
		  String displayName="";
		  String mobileNumber="";
		
		  ArrayList<CallLogInformationStore> callLogHistory = new ArrayList<CallLogInformationStore>();
		  ArrayList<CallLogInformationStore> friendsList = new ArrayList<CallLogInformationStore>();
		  ArrayList<String> contactIDs = new ArrayList<String>();
          String contactID=null; 
		 
          try{
			  YLogger Log = YLoggerFactory.getLogger();
		  
			  Log.d("CallLogManager.findFriendFromCallLog","Going to find All Call Log History");
		  
			  callLogHistory = getCallHistory(context);
		  
			  Log.d("CallLogManager.findFriendFromCallLog","Call Log History received");
		  
			  if(callLogHistory !=null && callLogHistory.size()>0){
			
				  Log.d("CallLogManager.findFriendFromCallLog","At Least One Call Log found");
			  
			  /*
			   * Checking one by one if the phone number
			   * in the call log is also in contact list
			   * if so, then it is a friend
			   * 
			   */
			  for(int i =0; i<callLogHistory.size();i++){
			 	 
				 
				  Log.d("CallLogManager.findFriendFromCallLog",
						  "Going to search contact list for this call log ");
				  
	
				  /*
				   * obtainig the display name of an entry in call log
				   */
				   displayName=callLogHistory.get(i).fullName;
				   /*
				    * Obtaining the mobile number of an entry in call log
				    */
				   mobileNumber=callLogHistory.get(i).mobileNumber;
					
				   contactIDs = getContactIDByNumber(context, mobileNumber);
	               
		             if(contactIDs!=null && contactIDs.size()>0){
		           /*
		            * The following line is important .
		            * If more than one entry are found in the 
		            * contact list for the given mobile number,
		            * then only one of them will be taken into account for 
		            * displaying result as a friend.
		            * 
		            *  This is because I am not getting the contact ID for all 
		            *  values returned, nut I am only
		            *  getting contactIDs.get(0)
		            * 
		            *  	 
		            */
		               contactID = contactIDs.get(0);
	                   if(contactID!=null){
	         		      		                 
		            	  
		        	       Log.d("CallLogManager.findFriendFromCallLog",
		        	    		   "CallLog user is in Contact "+contactID);
		        	       
		        	       CallLogInformationStore callLogInfo = new CallLogInformationStore();
		        	       callLogInfo.affiliation=null;
		        	       callLogInfo.firstName=displayName;
		        	       callLogInfo.lastName="";
		        	       callLogInfo.fullName=displayName;
		        	       callLogInfo.websiteURL=null;
		        	       callLogInfo.emailStore=getEmailByContactID(context,contactID);
		        	       callLogInfo.mobileNumber=mobileNumber;
				 			  /*
					 		   * Checking if the found friend already exsist in the list
					 		   * or not.  For doing this, first filter out the contact book
					 		   * by matching the Display name of the contact in call log and 
					 		   * contact book.
					 		   * 
					 		   * After that, take out the phone number of the 
					 		   * 
					 		   */
		        	       boolean isFound =false;
					 		
		        	       for(int k=0;k<friendsList.size();k++){
					 		  			 			  
					 		  if(friendsList.get(k).fullName.equalsIgnoreCase(displayName)){
					 				  isFound=true;
					 			      break;
					 		  }
					  	   }
					 		  if(!isFound){
					 		       friendsList.add(callLogInfo);
					 		 
						           Log.d("CallLogManager.findFriendFromCallLog","Friend found");
					 		  }
	                   }
	                   
	                   
		            }
				   
				 Log.d("CallLogManager.findFriendFromCallLog","Extraction for this name over");
			  }
			 if(friendsList!=null && friendsList.size()>0){
			  Log.d("CallLogManager.findFriendFromCallLog","All call Log Checked");			  
			  return friendsList;
			 }else{
				 Log.d("CallLogManager.findFriendFromCallLog","You Have no Friend");
				 messageString = "You have no friend";
				  return null;
			 }
			 
		  }
		  messageString = "No Call Log found on the Phone";
		  Log.d("CallLogManager.findFriendFromCallLog","No Call Log Found");
 		  return null;
 		  
         }catch(Exception ex){
        	     String debugString = "CallLOgmanager.findFriendFromCallLog: ";
	            debugString +=	ex.getMessage();
	            YLogger Log = YLoggerFactory.getLogger();
	    		
	            Log.d("CallLogManager.findFriendFromCallLOg",debugString);
	    		messageString = debugString;
	    		return null;
         }
	  }
	  
	  
	 /**
	  * Called by other functions of this class to extract information 
	  * from the result obtained after the query to Android phone.
	  * 
	  * 
	  * @param context the application context that was passed to the function that 
	  * wants to extract information after the result from the Android phone query.
	  * 
	  *  
	  * @param cursor  The cursor object obtained after querying the Android phone.
	  *                This query is done by the phone processor class functions.
 	  
 	  * @return The arraylist storing the objects of CallLogInformationStore class
	  * These objects store information about the contact in contact list.
	   * The contact is the friend of user
	   * 
	  */
	  
	  private ArrayList<CallLogInformationStore> extractInfoFromQueryResult(Context context,
			  															Cursor cursor){
		 try{ 
		   YLogger Log = YLoggerFactory.getLogger();
		 
		   Log.d("CallLogManager.extractInfoFromQueryResult", "Inside ExtractInfor From Query");
		   
		  if(cursor==null){
			
			   Log.d("CallLogManager.extractInfoFromQueryResult", "result after  Query is null");
			   messageString = "In CallLogManager.extractInfoFromQueryResult:"+
			   						" No Result obtainedfrom query";
			   return null; 
		  }
			  
		   int columnIndex=-1;           //    columnIndex of the result table after query
		   long timeInResult=-1;         //    The time of call in resulting Table
		  
		   String callName="";           //    Name of phone number user
		   String callNumber="";         //    Mobile number of call
		   String callType="";           //    Incoming,Outgoing,Missed
		   String callDuration="";       //    Duration of call
		   String callDate="";           //    Date of Call
		   String callTime="";           //    Time of Call
		  
		  /**
		    * ArrayList to store the object containing the call log information
		   */
		     ArrayList<CallLogInformationStore> callLogStore= 
		    	 new ArrayList<CallLogInformationStore>();
		     
		     // Store the email address for the phone number obtained from call log
		     EmailStore[] emailStore;
		     
	
			 Log.d("CallLogManager.extractInfoFromQueryResult", "At least one call log found");
		     
			 while(cursor.moveToNext())
		     {   
				
				 Log.d("CallLogManager.extractInfoFromQueryResult",
						 "Extracting info for a call log");
				 
		    	 columnIndex = cursor.getColumnIndex(Calls.CACHED_NAME);
		    	 callName = cursor.getString(columnIndex);
		    	 
		    	 columnIndex = cursor.getColumnIndex(Calls.NUMBER);
	             callNumber = cursor.getString(columnIndex);
	             //callNumber = removeDashFromNumber(callNumber);
	             
	             columnIndex = cursor.getColumnIndex(Calls.TYPE);
	             callType = cursor.getString(columnIndex);
	             
	             if(Integer.parseInt(callType)==1)
	            	 callType="Incoming";
	             else if(Integer.parseInt(callType)==2)
	            	 callType="Outgoing";
	             else if(Integer.parseInt(callType)==3)
	            	 callType="Missed";
	             else if(Integer.parseInt(callType)>=4)
	            	 callType="Other";
	      
	             
	         
	  		     Log.d("CallLogManager.extractInfoFromQueryResult", 
	  		    		 "Going to find email for this call log");
	  		     
	             emailStore = getEmailByDisplayName( context,callName);
	             
	  		     Log.d("CallLogManager.extractInfoFromQueryResult", 
	  		    		 "Going to find PhoneNumber for this call log");
	  		     
	            
	  		     Log.d("CallLogManager.extractInfoFromQueryResult",
	  		    		 "Going to find call duration");
	  		     
	             columnIndex = cursor.getColumnIndex(Calls.DURATION);
	             callDuration = cursor.getString(columnIndex);
	         
	  		     Log.d("CallLogManager.extractInfoFromQueryResult", 
	  		    		 "Going to find  call date");
	  		     
	             columnIndex = cursor.getColumnIndex(Calls.DATE);
	             timeInResult = cursor.getLong(columnIndex);
       	 
	             Time time = new Time();
	             time.toMillis(true);
	             time.set(timeInResult);
	            
                callDate= time.monthDay+"-"+(time.month+1)+"-"+time.year;
                
                callTime = time.hour+":"+time.minute;
              
	  		     Log.d("CallLogManager.extractInfoFromQueryResult", 
	  		    		 "All info for a Call log obtained");
                
	  		  
	  		     Log.d("CallLogManager.extractInfoFromQueryResult",
	  		    		 "Storing the phone number of the user in array");
                
	  		     // Storing the call log information in an object
	  		    CallLogInformationStore callLogInfo = new CallLogInformationStore();
                callLogInfo.fullName = callName;
                callLogInfo.mobileNumber=callNumber;
                callLogInfo.callDate=callDate;
                callLogInfo.callTime=callTime;
                callLogInfo.callDuration=callDuration;
                callLogInfo.callType=callType;
                callLogInfo.emailStore=emailStore;
                
          
	  		     Log.d("CallLogManager.extractInfoFromQueryResult", 
	  		    		 "Call Log info stored");
	  		     // Storing the object for a particular call log
	  		     // to the main arrayList
                callLogStore.add(callLogInfo);
            
		     }
	
	  		 Log.d("CallLogManager.extractInfoFromQueryResult", "All Call Log stored");
	  		 
	  		cursor.close();
		    return callLogStore;    
         
		  
	  } catch(Exception ex){
		  YLogger Log = YLoggerFactory.getLogger();
			 
		  String debugString = "CallLogManager.extractInfoFromQueryResult: ";
          debugString +=	ex.getMessage();
  		  Log.d("CallLogManager.extractInfoFromQueryResult",debugString);
  		   messageString = debugString;
  		  return null;
	  }
	}
}

/**
 * * <p>This package contains classes that extract information from
 * various social data source.
 * </p>
 * <p>The classes of this package forms the functional unit of Data
 * Extraction Manager</p>
 
 */
package fr.inria.arles.yarta.middleware.msemanagement;

import java.util.ArrayList;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.Time;
import android.widget.Toast;



/**
 * 
 * Extracts information from the message store of the Android Phone 
 * 
 * @author Kumar Nishant
 *
 */
public class SMSManager extends PhoneProcessor{

	private   Uri SMS_CONTENT_URI;
	private   Uri SMS_INBOX_CONTENT_URI;
	private   Uri SMS_SENT_CONTENT_URI;
    
	private String SMS_MOBILENUMBER_COLUMN;
	private String SMS_CONTACTID_COLUMN;
	private String SMS_DATE_COLUMN;
	private String SMS_TYPE_COLUMN;
	private String SORT_ORDER;
	
	public ArrayList<SMSInformationStore> smsInformationArray;
     /**
      *   The column names that will be selected from the SMS Store
      *   to be displayed in output.
      */
   private String [] columnNames;
   /*
    * These columns that are to be extracted from the query
    * 
    * IMP:  If any of the columns are removed from this array, then the 
    * extractInfoFromQueryResult method of this class must be rechecked
    * because it extracts information from the result table obtained 
    * after query. 
    *   If the table obtained after query doesnt contain
    * any of the  mentioned column and the function will try to 
    * access the column,it will lead to run time error.
    * 
    */
  
   /**
     * Constructor initializing all the class variables
     * 
     */
	public SMSManager(){

		 SMS_CONTENT_URI        = Uri.parse("content://sms");
	     SMS_INBOX_CONTENT_URI  = Uri.withAppendedPath(SMS_CONTENT_URI, "inbox");
	     SMS_SENT_CONTENT_URI   = Uri.withAppendedPath(SMS_CONTENT_URI, "sent");
	     SMS_MOBILENUMBER_COLUMN= "address";
	     SMS_CONTACTID_COLUMN   ="person";
	     SMS_TYPE_COLUMN        ="type";
	     SMS_DATE_COLUMN        ="date";
	     SORT_ORDER             = "date DESC";
         
	     columnNames            = new String []{
        		   					SMS_MOBILENUMBER_COLUMN,
									SMS_CONTACTID_COLUMN,
									SMS_DATE_COLUMN,
									SMS_TYPE_COLUMN
         						  };
	     smsInformationArray   = new ArrayList<SMSInformationStore>(); 
         
    }
		 

	
	/**
	 * Extract information from all sms in the 
	 * user Android Phone.
	 *
	 * @param context The application context of the Android Activity from where
	 * this method is called.
	 * 
	 * @return ArrayList containing objects of SMSInformationStore class
	 * An object store all the required information for one sms.
	 * 
	 */
	public ArrayList<SMSInformationStore> extractAllSMS(Context context){
		 
		YLogger Log = YLoggerFactory.getLogger();
	
		Log.d("SMSManager.extractAllSMS", "Inside extractAllSMS ");
        /*
         * Forming the columns that are to be extracted from the query
         * 
         */

		Cursor cursor =null;
		
		try{
			Log.d("SMSManager.extractAllSMS", "Going for query ");
          
          /*
           * Query according to the need.
           */
			cursor = queryProcessor( context,
    			  SMS_CONTENT_URI,
    			  columnNames,
    			  null,
    			  null,
    			  SORT_ORDER);
		  
			
			Log.d("SMSManager.extractAllSMS", "Query Over");      		
          /*
           * If any result is obtained from the query
           */
          	if(cursor!=null && cursor.getCount()>0){
          	
              	Log.d("SMSManager.extractAllSMS", "Going to extract info from result obtained");
              	/*
              	 * Going to extract all the inforamation for the sms
              	 * from te result obtained
              	 */
          		smsInformationArray = extractInfoFromQueryResult(context, cursor);
          		return(smsInformationArray);
          												
          	}else{       	   
          	
             	Log.d("SMSManager.extractAllSMS","No SMS Found");
             	messageString="No SMS Found";
             	return null;
          	}
		}catch(Exception ex){
			String debugString="CATCH: extractAllSMS : ";
			debugString+=ex.toString();
			Log.d("SMSManager.extractAllSMS", debugString);
			//Toast.makeText(context,debugString,Toast.LENGTH_LONG).show();
			messageString="CATCH:SMSManager.extractAllSMS:  "+ex.toString();
         	return null;
		}		
          
	}
	
	
	/**
	 * Extract information from the inbox sms of the 
	 * user of Android Phone.
	 *
	 * @param context The application context of the Android activity 
	 * from where this method is called.
	 * 
	 * @return ArrayList containing objects of SMSInformationStore class
	 * An object stores all the required information for one sms.
	 * 
	 */
	public ArrayList<SMSInformationStore> extractAllInboxSMS(Context context){
		 
		YLogger Log = YLoggerFactory.getLogger();
		
        Log.d("SMSManager.extractAllInboxSMS", "Inside extractAllInboxSMS ");
    
		Cursor cursor =null;
		
		try{
		
			Log.d("SMSManager.extractAllInboxSMS", "Going for query Inbox ");
          
          /*
           * Query according to the need.
           */
			cursor = queryProcessor(context,
    			  SMS_INBOX_CONTENT_URI,
    			  columnNames,
    			  null,
    			  null,
    			  SORT_ORDER);
		  
			
			Log.d("SMSManager.extractAllInboxSMS","Query Over");      		
          /*
           * If any result is obtained from the query
           */
          	if(cursor !=null && cursor.getCount()>0){
          		
              	Log.d("SMSManager.extractAllInboxSMS", "Going to extract info from result obtained");
              	/*
              	 * Going to extract all the inforamation for the sms
              	 * from te result obtained
              	 */
          		smsInformationArray = extractInfoFromQueryResult(context, cursor);
          		return(smsInformationArray);
          												
          	}else{       	   
          		
             	Log.d("SMSManager.extractAllInboxSMS","No SMS Found");
             	messageString="No Inbox SMS Found";
             	return null;
          	}
		}catch(Exception ex){
			String debugString="CATCH: extractAllInboxSMS : ";
			debugString+=ex.toString();
			Log.d("SMSManager.extractAllInboxSMS", debugString);
			messageString="SMSManager.extractAllInboxSMS:  "+ex.toString();
         	return null;
		}		
          
	}
		
	/**
	 * Extract information from sent sms of the 
	 * user of Android Phone.
	 *
	 * @param context The application context of the Android activity from where
	 * this method is called.
	 * 
	 * @return ArrayList containing objects of SMSInformationStore class
	 * An object has all the required information for a sms.
	 * 
	 */
	public ArrayList<SMSInformationStore> extractAllSentSMS(Context context){
		 
		YLogger Log = YLoggerFactory.getLogger();

        Log.d("SMSManager.extractAllSentSMS", "Inside extractAllInboxSMS ");
      
		Cursor cursor =null;
		
		try{
		
			Log.d("SMSManager.extractAllSentSMS", "Going for query Inbox ");
          
          /*
           * Query according to the need.
           */
			cursor = queryProcessor(context,
    			  SMS_SENT_CONTENT_URI,
    			  columnNames,
    			  null,
    			  null,
    			  SORT_ORDER);
		  
			
			Log.d("SMSManager.extractAllSentSMS", "Query Over");      		
          
          //  If any result is obtained from the query
           
          	if(cursor!=null && cursor.getCount()>0){
          		Log.d("SMSManager.extractAllSentSMS",
              			"Going to extract info from result obtained");
              	/*
              	 * Going to extract all inforamation for the sms
              	 * from the result obtained 
              	 */
           		smsInformationArray = extractInfoFromQueryResult(context, cursor);
          		return(smsInformationArray);
          												
          	}else{       	   
          		Log.d("SMSManager.extractAllSentSMS", "No SMS Found");
          		messageString="No Sent SMS Found";
             	return null;
          	}
		}catch(Exception ex){
			String debugString="CATCH: extractAllSentSMS : ";
			debugString+=ex.toString();
			Log.d("SMSManager.extractAllSentSMS", debugString);
			messageString="CATCH:SMSManager.extractAllSentSMS:  "+ex.toString();
         	return null;
		}		
          
	}
	
	
	/**
	 * Extract information from all sms in the 
	 * user Android Phone from/to a particular number.
	 *
	 * @param context The application context of the Android activity from where
	 * this method is called.
	 * 
	 * @return ArrayList containing objects of SMSInformationStore class
	 * An object store all the required information for a sms.
	 * 
	 */
	public ArrayList<SMSInformationStore> extractAllSMSByNumber(Context context, 
																	String mobileNumber){
		 
		YLogger Log = YLoggerFactory.getLogger();
		
        Log.d("SMSManager.extractAllSMSByNumber","Inside extractAllSMSByNumber ");
        /*
         * Forming the columns that are to be extracted from the query
         * 
         */

		Cursor cursor =null;
		String queryString=null;
		try{
		
			Log.d("SMSManager.extractAllSMSByNumber", "Going for query String formation");
         
			queryString = "address"+"=?";
			
			
			Log.d("SMSManager.extractAllSMSByNumber", "Going for query phone for sms");
          /*
           * Query according to the need.
           */
			cursor = queryProcessor(context,
    			  SMS_CONTENT_URI,
    			  columnNames,
    			  queryString,
    			  new String[] {String.valueOf(mobileNumber)},
    			  SORT_ORDER);
		  
			
			Log.d("SMSManager.extractAllSMSByNumber", "Query Over");      		
          /*
           * If any result is obtained from the query
           */
          	if(cursor!=null && cursor.getCount()>0){
          		
              	Log.d("SMSManager.extractAllSMSByNumber", "Going to extract info from result obtained");
              	/*
              	 * Going to extract all the inforamation for the sms
              	 * from te result obtained
              	 */
          		smsInformationArray = extractInfoFromQueryResult(context, cursor);
          		return(smsInformationArray);
          												
          	}else{       	   
          		
             	Log.d("SMSManager.extractAllSMSByNumber", "No SMS Found");
             	messageString="No SMS for the given Mobile Number";
             	return null;
          	}
		}catch(Exception ex){
			String debugString="CATCH: extractAllSMSByNumber : ";
			debugString+=ex.toString();
			Log.d("SMSManager.extractAllSMSByNumber", debugString);
			messageString="CATCH:SMSManager.extractAllSMSByNumber:  "+ex.toString();
         	return null;
		}		
          
	}
	
	/**
	 * Extract information from Inbox sms by a particular number
	 * in the 
	 * user Android Phone .
	 *
	 * @param context The context of the Android Activity from where
	 * this method is called.
	 * 
	 * @return ArrayList containing objects of SMSInformationStore class
	 * An object stores all the required information from one sms.
	 * 
	 */
	public ArrayList<SMSInformationStore> extractInboxSMSByNumber(Context context, 
																	String mobileNumber){
		 
		YLogger Log = YLoggerFactory.getLogger();
		
        Log.d("SMSManager.extractInboxSMSByNumber","Inside extractInboxSMSByNumber ");
        /*
         * Forming the columns that are to be extracted from the query
         * 
         */

		Cursor cursor =null;
		String queryString=null;
		try{
			
			Log.d("SMSManager.extractInboxSMSByNumber", 
					"Going for query String formation");
         
			queryString = "address"+"=?";
			
			
			Log.d("SMSManager.extractInboxSMSByNumber","Going for query phone for sms");
          /*
           * Query according to the need.
           */
			cursor = queryProcessor(context,
    			  SMS_INBOX_CONTENT_URI,
    			  columnNames,
    			  queryString,
    			  new String[] {String.valueOf(mobileNumber)},
    			  SORT_ORDER);
		  
			
			Log.d("SMSManager.extractInboxSMSByNumber", "Query Over");      		
          /*
           * If any result is obtained from the query
           */
          	if(cursor!=null && cursor.getCount()>0){
          		
              	Log.d("SMSManager.extractInboxSMSByNumber",
              			"Going to extract info from result obtained");
              	/*
              	 * Going to extract all the inforamation for the sms
              	 * from te result obtained
              	 */
          		smsInformationArray = extractInfoFromQueryResult(context, cursor);
          		return(smsInformationArray);
          												
          	}else{       	   
          		
             	Log.d("SMSManager.extractInboxSMSByNumber", "No SMS Found");
             	messageString="No Inbox SMS from given Mobile Number";
             	return null;
          	}
		}catch(Exception ex){
			String debugString="CATCH: extractInboxSMSByNumber : ";
			debugString+=ex.toString();
			Log.d("SMSManager.extractInboxSMSByNumber", debugString);
			messageString="CATCH:SMSManager.extractInboxSMSByNumber:  "+ex.toString();
         	return null;
		}		
          
	}
	
	/**
	 * Extract information from all the sent sms in the 
	 * user Android Phone to a particular number.
	 *
	 * @param context The context of the Android Activity from where
	 * this method is called.
	 * 
	 * @return ArrayList containing objects of SMSInformationStore class
	 * An object stores all the required information from one sms.
	 * 
	 */
	public ArrayList<SMSInformationStore> extractSentSMSByNumber(Context context, 
																	String mobileNumber){
		 
		YLogger Log = YLoggerFactory.getLogger();
		
        Log.d("SMSManager.extractSentSMSByNumber", "Inside extractSentSMSByNumber ");
        /*
         * Forming the columns that are to be extracted from the query
         * 
         */

		Cursor cursor =null;
		String queryString=null;
		try{
		
			Log.d("SMSManager.extractSentSMSByNumber", "Going for query String formation");
         
			queryString = "address"+"=?";
			
			
			Log.d("SMSManager.extractSentSMSByNumber", "Going for query phone for sms");
          /*
           * Query according to the need.
           */
			cursor = queryProcessor(context,
    			  SMS_SENT_CONTENT_URI,
    			  columnNames,
    			  queryString,
    			  new String[] {String.valueOf(mobileNumber)},
    			  SORT_ORDER);
		  
			
			Log.d("SMSManager.extractSentSMSByNumber", "Query Over");      		
          /*
           * If any result is obtained from the query
           */
          	if(cursor!=null && cursor.getCount()>0){
          		
              	Log.d("SMSManager.extractSentSMSByNumber",
              			"Going to extract info from result obtained");
              	/*
              	 * Going to extract all the inforamation for the sms
              	 * from te result obtained
              	 */
           		smsInformationArray = extractInfoFromQueryResult(context, cursor);
          		return(smsInformationArray);
          												
          	}else{       	   
          		
             	Log.d("SMSManager.extractSentSMSByNumber", "No SMS Found");
             	messageString="No sent Message by the given Number";
             	return null;
          	}
		}catch(Exception ex){
			String debugString="CATCH: extractSentSMSByNumber : ";
			debugString+=ex.toString();
			Log.d("SMSManager.extractSentSMSByNumber", debugString);
			messageString="CATCH:SMSManager.extractSentSMSByNumber:  "+ex.toString();
         	return null;
		}		
          
	}
	
	
	/**
	 * Extract information from all the sms 
	 *  after a particular time till now in the 
	 *  Android Phone.
	 *
	 * @param context The context of the Android Activity from where
	 * this method is called.
	 * 
	 * @return ArrayList containing objects of SMSInformationStore class
	 * An object stores all the required information from one sms.
	 * 
	 */
	public ArrayList<SMSInformationStore> extractAllSMSSinceTime(Context context, 
																int day,
																int month,
																int year,
																int hour){	
		 
		YLogger Log = YLoggerFactory.getLogger();
	
        Log.d("SMSManager.extractAllSMSSinceTime", 
        		"Inside extractAllSMSSinceTime ");

               
        /*
		    * Convert the given time parameter to the Time object
		    * 
		    * In the time object , the month is represented in 
		    * range (0-11). So, in the month field, subtracting by 1
		    * 
		    * 
		    */

		   Log.d("SMSManager.extractAllSMSSinceTime",
				   "Going to set time in Time object");
		
		   try{  
		    Cursor cursor =null;
			String queryString=null;
			Time setTime = new Time();
	        setTime.set(0, 0, hour,day,month - 1,year);
	       
	       
	       Long sinceTime = setTime.toMillis(false);
	       

		   Log.d("SMSManager.extractAllSMSSinceTime",
				   "Time set and value of sec obtained");

			Log.d("SMSManager.extractAllSMSSinceTime",
					"Going for query String formation");
         
			queryString = "date"+">?";
			
	
			Log.d("SMSManager.extractAllSMSSinceTime",
					"Going for query phone for sms");
          /*
           * Query according to the need.
           */
			cursor =queryProcessor(context,
    			  SMS_CONTENT_URI,
    			  columnNames,
    			  queryString,
    			  new String[] {String.valueOf(sinceTime)},
    			  SORT_ORDER);
		  
			
			Log.d("SMSManager.extractAllSMSSinceTime", "Query Over");      		
          /*
           * If any result is obtained from the query
           */
          	if(cursor!=null && cursor.getCount()>0){
          	
              	Log.d("SMSManager.extractAllSMSSinceTime",
              			"Going to extract info from result obtained");
              	/*
              	 * Going to extract all the information for the sms
              	 * from the result obtained
              	 */
         		smsInformationArray = extractInfoFromQueryResult(context, cursor);
          		return(smsInformationArray);
          												
          	}else{       	   
          		
             	Log.d("SMSManager.extractAllSMSSinceTime", "No SMS Found");
             	messageString="No SMS after the given time";
             	return null;
          	}
		}catch(Exception ex){
			String debugString="CATCH: extractAllSMSSinceTime : ";
			debugString+=ex.toString();
			Log.d("SMSManager.extractAllSMSSinceTime", debugString);
			messageString="CATCH:SMSManager.extractAllSMSSinceTime:  "+ex.toString();
         	return null;
		}		
          
	}
	
	/**
	 * Extract information from inbox sms 
	 *  after a particular time till now in the 
	 *  Android Phone.
	 *
	 * @param context The context of the Android Activity from where
	 * this method is called.
	 * 
	 * @return ArrayList containing objects of SMSInformationStore class
	 * An object stores all the required information from one sms.
	 * 
	 */
	public ArrayList<SMSInformationStore> extractInboxSMSSinceTime(Context context, 
																int day,
																int month,
																int year,
																int hour){	
		 
		YLogger Log = YLoggerFactory.getLogger();
		
        Log.d("SMSManager.extractInboxSMSSinceTime",
        		"Inside extractInboxSMSSinceTime ");

           /*
		    * Convert the given time parameter to the Time object
		    * 
		    * In the time object , the month is represented in 
		    * range (0-11). So, in the month field, subtracting by 1
		    * 
		    * 
		    */
		 
		   Log.d("SMSManager.extractInboxSMSSinceTime",
				   "Going to set time in Time object");
		   try{ 
			   
				Cursor cursor =null;
				String queryString=null;
		   Time setTime = new Time();
		  
	       setTime.set(0, 0, hour, day, month - 1, year);
	       
	       
	       Long sinceTime = setTime.toMillis(false);
	       
	    
		   Log.d("SMSManager.extractInboxSMSSinceTime",
				   "Time set and value of sec obtained");


		
			
			Log.d("SMSManager.extractInboxSMSSinceTime",
					"Going for query String formation");
         
			queryString = "date"+">?";
			
	
			Log.d("SMSManager.extractInboxSMSSinceTime", 
					"Going for query phone for sms");
          /*
           * Query according to the need.
           */
			cursor = queryProcessor(context,
    			  SMS_INBOX_CONTENT_URI,
    			  columnNames,
    			  queryString,
    			  new String[] {String.valueOf(sinceTime)},
    			  SORT_ORDER);
		  
			
			Log.d("SMSManager.extractInboxSMSSinceTime", "Query Over");      		
          /*
           * If any result is obtained from the query
           */
          	if(cursor!=null && cursor.getCount()>0){
          		
              	Log.d("SMSManager.extractInboxSMSSinceTime", 
              			"Going to extract info from result obtained");
              	/*
              	 * Going to extract all the information for the sms
              	 * from the result obtained
              	 */
          		smsInformationArray = extractInfoFromQueryResult(context, cursor);
          		return(smsInformationArray);
          												
          	}else{       	   
          	
             	Log.d("SMSManager.extractInboxSMSSinceTime", "No SMS Found");
             	return null;
          	}
		}catch(Exception ex){
		String 	debugString="CATCH: extractAllSMSSinceTime : ";
			debugString+=ex.toString();
			Log.d("SMSManager.extractInboxSMSSinceTime", debugString);
			Toast.makeText(context,debugString,Toast.LENGTH_LONG).show();

         	return null;
		}		
          
	}
	
	
	/**
	 *  Extract information from sent sms 
	 *  after a particular time till now in the 
	 *  Android Phone.
	 *
	 * @param context The application context of the Android activity from where
	 * this method is called.
	 * 
	 * @return ArrayList containing objects of SMSInformationStore class
	 * An object has all the required information for one sms.
	 * 
	 */
	public ArrayList<SMSInformationStore> extractSentSMSSinceTime(Context context, 
			 														int day,
			 														int month,
			 														int year,
			 														int hour){	
		 
		YLogger Log = YLoggerFactory.getLogger();
		
        Log.d("SMSManager.extractSentSMSSinceTime",
        		"Inside extractInboxSMSSinceTime ");
       
           /*
		    * Convert the given time parameter to the Time object
		    * 
		    * In the time object , the month is represented in 
		    * range (0-11). So, in the month field, subtracting by 1
		    * 
		    * 
		    */
		 
		   Log.d("SMSManager.extractSentSMSSinceTime","Going to set time in Time object");
		   try{  
		      Time setTime = new Time();
	          setTime.set(0, 0, hour,day,month - 1,year);
	       
	       
	          Long sinceTime = setTime.toMillis(false);
	       
	          
		      Log.d("SMSManager.extractSentSMSSinceTime","Time set and value of sec obtained");

		      Cursor cursor =null;
		      String queryString=null;
		
			
			Log.d("SMSManager.extractSentSMSSinceTime","Going for query String formation");
         
			queryString = "date"+">?";
			
			
			Log.d("SMSManager.extractSentSMSSinceTime","Going for query phone for sms");
          /*
           * Query according to the need.
           */
			cursor = queryProcessor(context,
    			  SMS_INBOX_CONTENT_URI,
    			  columnNames,
    			  queryString,
    			  new String[] {String.valueOf(sinceTime)},
    			  SORT_ORDER);
		  
			
			Log.d("SMSManager.extractSentSMSSinceTime","Query Over");      		
          /*
           * If any result is obtained from the query
           */
          	if(cursor!=null && cursor.getCount()>0){
          		
              	Log.d("SMSManager.extractSentSMSSinceTime",
              			"Going to extract info from result obtained");
              	/*
              	 * Going to extract all the information for the sms
              	 * from the result obtained
              	 */
          		
          		smsInformationArray = extractInfoFromQueryResult(context, cursor);
          		return(smsInformationArray);
          												
          	}else{       	   
          		
             	Log.d("SMSManager.extractSentSMSSinceTime", "No SMS Found");
             	return null;
          	}
		}catch(Exception ex){
			String debugString="CATCH ";
			debugString+=ex.toString();
			Log.d("SMSManager.extractSentSMSSinceTime", debugString);
			Toast.makeText(context,debugString,Toast.LENGTH_LONG).show();

         	return null;
		}		
          
	}
	/**
	 *  Extract information from all the sms 
	 *  between a particular time interval till now in the 
	 *  Android Phone.
	 *
	 * @param context The application context of the Android activity from where
	 * this method is called.
	 * 
	 * @return ArrayList containing objects of SMSInformationStore class
	 * An object has all information for one sms.
	 * 
	 */
	public ArrayList<SMSInformationStore> extractAllSMSBetweenTime(Context context, 
																	int fromDay,
																	int fromMonth,
																	int fromYear,
																	int fromHour,
																	int toDay,
																	int toMonth,
																	int toYear,
																	int toHour){	
		 
		YLogger Log = YLoggerFactory.getLogger();
		
        Log.d("SMSManager.extractAllSMSBetweenTime",
        		"Inside extractAllSMSBetweenNumber ");
        try{     
           /*
		    * Convert the given time parameter to the Time object
		    * 
		    * In the time object , the month is represented in 
		    * range (0-11). So, in the month field, subtracting by 1
		    * 
		    * 
		    */
		  
		   Log.d("SMSManager.extractAllSMSBetweenTime","Going to set time in Time object");
		     
		   Time setTime = new Time();
	       setTime.set(0, 0, fromHour,  fromDay, fromMonth - 1, fromYear);
	       
	       Long fromTime = setTime.toMillis(false);
	       
	       // setting the fields of time object
	       setTime.set(0, 0, toHour,toDay,toMonth - 1,toYear);
	       
           // Converting the given date to milli sec.
	       Long toTime = setTime.toMillis(false);
		   
	       
	      
		   Log.d("SMSManager.extractAllSMSBetweenTime",
				   "Time set and value of sec obtained");

		   Cursor cursor =null;
		   String queryString=null;
		
			
			Log.d("SMSManager.extractAllSMSBetweenTime", 
					"Going for query String formation");
         
			queryString = "date"+">? AND date"+ "<? ";
			
			
			Log.d("SMSManager.extractAllSMSBetweenTime","Going for query phone for sms");
          /*
           * Query according to the need.
           */
			cursor = queryProcessor(context,
										SMS_CONTENT_URI,
										columnNames,
										queryString,
										new String[] {String.valueOf(fromTime),
														String.valueOf(toTime)},
										SORT_ORDER);
		  
			
			Log.d("SMSManager.extractAllSMSBetweenTime","Query Over");      		
          /*
           * If any result is obtained from the query
           */
          	if(cursor!=null && cursor.getCount()>0){
          		
              	Log.d("SMSManager.extractAllSMSBetweenTime",
              			"Going to extract info from result obtained");
              	/*
              	 * Going to extract all the information for the sms
              	 * from the result obtained
              	 */
          		smsInformationArray = extractInfoFromQueryResult(context, cursor);
          		return(smsInformationArray);
          												
          	}else{       	   
          		
             	Log.d("SMSManager.extractAllSMSBetweenTime","No SMS Found");
             	return null;
          	}
		}catch(Exception ex){
			String debugString="CATCH: extractAllSMSBetweenTime : ";
			debugString+=ex.toString();
			Log.d("SMSManager.extractAllSMSBetweenTime", debugString);
			Toast.makeText(context,debugString,Toast.LENGTH_LONG).show();

			return null;
		}		
          
	}
	
	/**
	 * Extract information from Inbox of the sms 
	 *  between a particular time interval till now in the 
	 *  Android Phone.
	 *
	 * @param context The application context of the Android Activity from where
	 * this method is called.
	 * 
	 * @return ArrayList containing objects of SMSInformationStore class
	 * An object stores all the required information from one sms.
	 * 
	 */
	public ArrayList<SMSInformationStore> extractInboxSMSBetweenTime(Context context, 
																		int fromDay,
																		int fromMonth,
																		int fromYear,
																		int fromHour,
																		int toDay,
																		int toMonth,
																		int toYear,
																		int toHour){
		 
		Long fromTime=null;
		Long toTime=null;
		Cursor cursor =null;
		String queryString=null;
		    		
		YLogger Log = YLoggerFactory.getLogger();
		
        Log.d("SMSManager.extractInboxSMSBetweenTime",
        		"Inside extractInboxSMSBetweenTime ");

        try{
           /*
		    * Convert the given time parameter to the Time object
		    * 
		    * In the time object , the month is represented in 
		    * range (0-11). So, in the month field, subtracting by 1
		    * 
		    * 
		    */
		   Log.d("SMSManager.extractInboxSMSBetweenTime",
				   "Going to set time in Time object");
		   
		   Time setTime = new Time();
	       setTime.set(0, 0, fromHour,  fromDay, fromMonth - 1, fromYear);
	       
	       fromTime = setTime.toMillis(false);
	       
	       // setting the fields of time object
	       setTime.set(0, 0, toHour,toDay,toMonth - 1,toYear);
	       
          
	       // Converting the given date to milli sec.
	       toTime = setTime.toMillis(false);
		    
	       Log.d("SMSManager.extractInboxSMSBetweenTime",
	    		   "Time set and value of sec obtained ");

			Log.d("SMSManager.extractInboxSMSBetweenTime", 
					"Going for query String formation");
         
			queryString = "date"+">? AND date"+ "<? ";
			
			Log.d("SMSManager.extractInboxSMSBetweenTime",
					"Going for query phone for sms");
          /*
           * Query according to the need.
           */
			cursor = queryProcessor(context,
    			  SMS_INBOX_CONTENT_URI,
    			  columnNames,
    			  queryString,
    			  new String[] {String.valueOf(fromTime),String.valueOf(toTime)},
    			  SORT_ORDER);
		  
			Log.d("SMSManager.extractInboxSMSBetweenTime","Query Over");      		
          /*
           * If any result is obtained from the query
           */
          	if(cursor!=null && cursor.getCount()>0){
          	 	Log.d("SMSManager.extractInboxSMSBetweenTime",
          	 			"Going to extract info from result obtained");
              	/*
              	 * Going to extract all the information for the sms
              	 * from the result obtained
              	 */
          		smsInformationArray = extractInfoFromQueryResult(context, cursor);
          		return(smsInformationArray);
          												
          	}else{       	   
          		Log.d("SMSManager.extractInboxSMSBetweenTime","No SMS Found");
             	return null;
          	}
		}catch(Exception ex){
			String debugString="CATCH: extractInboxSMSBetween : ";
			debugString+=ex.toString();
			Log.d("SMSManager.extractInboxSMSBetweenTime", debugString);
			Toast.makeText(context,debugString,Toast.LENGTH_LONG).show();

         	return null;
		}		
          
	}
	
	/**
	 * Extract information from Sent sms 
	 *  between a particular time interval till now in the 
	 *  Android Phone.
	 *
	 * @param context The context of the Android activity from where
	 * this method is called.
	 * 
	 * @return ArrayList containing objects of SMSInformationStore class
	 * An object stores all the required information from one sms.
	 * 
	 */
	public ArrayList<SMSInformationStore> extractSentSMSBetweenTime(Context context, 
																		int fromDay,
																		int fromMonth,
																		int fromYear,
																		int fromHour,
																		int toDay,
																		int toMonth,
																		int toYear,
																		int toHour){

		
		Long fromTime=null;
		Long toTime=null;
		Cursor cursor =null;
		String queryString=null;
		
		YLogger Log = YLoggerFactory.getLogger();
		
        Log.d("SMSManager.extractSentSMSBetweenTime", "Inside extractSentSMSBetweenTime");
        try{
           /*
		    * Convert the given time parameter to the Time object
		    * 
		    * In the time object , the month is represented in 
		    * range (0-11). So, in the month field, subtracting by 1
		    * 
		    * 
		    */
		   
		   Log.d("SMSManager.extractSentSMSBetweenTime","Going to set time in Time object");
		     
		   Time setTime = new Time();
	       setTime.set(0, 0, fromHour,  fromDay, fromMonth - 1, fromYear);
	       
	       fromTime = setTime.toMillis(false);
	       
	       // setting the fields of time object
	       setTime.set(0, 0, toHour,toDay,toMonth - 1,toYear);
	       
          
	       // Converting the given date to milli sec.
	       toTime = setTime.toMillis(false);
		   
	       Log.d("SMSManager.extractSentSMSBetweenTime","Time set and value of sec obtained");

		   
	       Log.d("SMSManager.extractSentSMSBetweenTime","Going for query String formation");
	         
		   queryString = "date"+">? AND date"+ "<? ";
				
		   Log.d("SMSManager.extractSentSMSBetweenTime","Going for query phone for sms");
	          /*
	           * Query according to the need.
	           */
		   cursor = queryProcessor(context,
	    			  SMS_SENT_CONTENT_URI,
	    			  columnNames,
	    			  queryString,
	    			  new String[] {String.valueOf(fromTime),String.valueOf(toTime)},
	    			  SORT_ORDER);
			  
	
		   Log.d("SMSManager.extractSentSMSBetweenTime", "Query Over");      		
	          /*
	           * If any result is obtained from the query
	           */
	       if(cursor!=null && cursor.getCount()>0){
	          		
	           	Log.d("SMSManager.extractSentSMSBetweenTime",
	             			"Going to extract info from result obtained");
	              	/*
	              	 * Going to extract all the information for the sms
	              	 * from the result obtained
	              	 */
	           	smsInformationArray = extractInfoFromQueryResult(context, cursor);
	          	return(smsInformationArray);
	          												
	        }else{       	   
	          
	            Log.d("SMSManager.extractSentSMSBetweenTime","No SMS Found");
	            return null;
	        }
		}catch(Exception ex){
			String debugString="CATCH: extractSentSMSBetweenTime : ";
			debugString+=ex.toString();
			Log.d("SMSManager.extractSentSMSBetweenTime", debugString);
			Toast.makeText(context,debugString,Toast.LENGTH_LONG).show();

         	return null;
		}		
          
	}
	
	/**
	 * Extract friends from the sms present in the Android phone. 
	 * 
	 * <p>Then if the sender/recipient of the incoming / outgoing sms is also in 
	 * contact list, he is declared as a friend.</p>
	 * 
	 * 
	 * <p>The resulting information for the contact is stored in the object of
	 *  SMSInformationStore class.</p> 
	 * 
	 * <ui><li>The function first extracts all sent and inbox sms in the phone by
	 *          calling extractAllSMS function of this class. This function sets
	 *          the variable isFriend to true or false depending on whether the 
	 *          contact is found or not. 
	 *     </li>
	 *     <li>Then the function scans all the sms and checks that if the isFriend
	 *         variable of the sms information object is set to true. If its true,
	 *         then it checks if the contact is already present in friend list.
	 *         If no, then it adds this contact to friend list  
	 *     </li>
	 * </ui>
	 * 
	 * @param context The application context of the android activity from where this 
	 * method is called.
	 * 
	 * @return ArrayList containing the object of SMSInformationStore class. Each
	 * object of the arraylist represents a friend
	 */
	public ArrayList<SMSInformationStore> extractAllSMSFriend(Context context){
		
		
		try{
			YLogger Log = YLoggerFactory.getLogger();
  		 	Log.d("SMSManager.extractAllSMSFriend","Inside extractAllSMSFriend");
		 
         	ArrayList<SMSInformationStore> friendList =new ArrayList<SMSInformationStore>();

          	Log.d("SMSManager.extractAllSMSFriend", "Going to extract AllSms ");
         	
          	ArrayList<SMSInformationStore> allSMSInfo = extractAllSMS(context);
		
        
          	Log.d("SMSManager.extractAllSMSFriend",
          			"All info from Sms extracted, going to filter for friend");
          	/*
          	 * The Method to find friend from the extracted information:
          	 * 
          	 * 1.  Check if the variable isFriend is set to true
          	 * 2.  Check if this friend has already been stored in the friendList
          	 *        This is done by matching the mobile number of current with
          	 *        the mobile number of all the other friends.
          	 * 3.  If not stored, then store the current object in friendList
          	 *        
          	 */
         	if(allSMSInfo!=null && allSMSInfo.size()>0){
			    boolean hasFriend =false;
			    boolean isStored=false;
			    
         		for(int i = 0 ; i<allSMSInfo.size();i++){
         		
         			if(allSMSInfo.get(i).isFriend){ 
         			
         				 isStored= false;
         				 
         				 for(int k=0;k<friendList.size();k++){
         					if(friendList.get(k).smsNumber.
         											equals(allSMSInfo.get(i).smsNumber)){
         				        isStored = true;
         					    break;
         				    }
         				 }
         				 if(!isStored){
         					  friendList.add(allSMSInfo.get(i));
            		          hasFriend = true;
            				
         				 }
         			}
         				
         		}
         		if(hasFriend){
         			
                  	 Log.d("SMSManager.extractAllSMSFriend","All friend extracted");
                  	 return friendList;
         			
         		}
         		else{
         			
                  	 Log.d("SMSManager.extractAllSMSFriend", "You have no friend");
                  	 messageString="SMSManager.extractAllSMSFriend: No Friend Found From SMS";
                  	 return null;
         		}
         	}
         	else{
         		
              	 Log.d("SMSManager.extractAllSMSFriend","recieved Null from extract ALL SMS ");
              	 messageString="SMSManager.extractAllSMSFriend: No SMS was found ";
              	 return null;
         	}
	    }
		catch(Exception ex){
			    YLogger Log = YLoggerFactory.getLogger();
  		        String  debugString="CATCH: ExtractAllSMSFriend: ";
	         	Log.d("SMSManager.extractAllSMSFriend", debugString+ex.toString());
	         	 messageString="CATCH:SMSManager.extractAllSMSFriend: "+ex.toString();
              	 
	         	return null;
		}
	}
	
	/**
	 * Extract friends from the sms present in the Inbox of phone. 
	 * Then if the sender of the incoming sms is also in 
	 * contact list, he is declared as a friend.
	 * <p>The resulting information for the contact is stored in theobject of
	 *  SMSInformationStore class.</p> 
	 * 
	 * @param context The application context of the android activity from where
	 * this 
	 * method is called.
	 * 
	 * @return ArrayList containing the object of SMSInformationStore class. Each
	 * object of the arraylist represents a friend
	 */
	public ArrayList<SMSInformationStore> extractInboxSMSFriend(Context context){
		
		
		try{
			YLogger Log = YLoggerFactory.getLogger();
  		
			Log.d("SMSManager.extractInboxSMSFriend","Inside extractInboxSMSFriend");
		 
         	ArrayList<SMSInformationStore> friendList =new ArrayList<SMSInformationStore>();

         	
          	Log.d("SMSManager.extractInboxSMSFriend", "Going to extract InboxSms ");
         	
          	ArrayList<SMSInformationStore> allSMSInfo = extractAllInboxSMS(context);
		
          	Log.d("SMSManager.extractInboxSMSFriend", 
          			"All info from Sms extracted, going to filter for friend");
          	
         	if(allSMSInfo!=null && allSMSInfo.size()>0){
			    boolean hasFriend =false;
			    boolean isStored=false;
			    
			    /*
	          	 * The Method to find friend from the extracted information:
	          	 * 
	          	 * 1.  Check if the variable isFriend is set to true
	          	 * 2.  Check if this friend has already been stored in the friendList
	          	 *        This is done by matching the mobile number of current with
	          	 *        the mobile number of all the other friends.
	          	 * 3.  If not stored, then store the current object in friendList
	          	 *        
	          	 */
			    for(int i = 0 ; i<allSMSInfo.size();i++){
	         		
         			if(allSMSInfo.get(i).isFriend){ 
         			
         				 isStored= false;
         				 
         				 for(int k=0;k<friendList.size();k++){
         					if(friendList.get(k).smsNumber.
         											equals(allSMSInfo.get(i).smsNumber)){
         				        isStored = true;
         					    break;
         				    }
         				 }
         				 if(!isStored){
         					  friendList.add(allSMSInfo.get(i));
            		          hasFriend = true;
            				
         				 }
         			}
         				
         		}
         		if(hasFriend){
         			 Log.d("SMSManager.extractInboxSMSFriend","All friend extracted");
                  	 return friendList;
         			
         		}
         		else{
         			 Log.d("SMSManager.extractInboxSMSFriend","You have no friend");
         			 messageString="SMSManager.extractInboxSMSFriend: No Friend Found from SMS";
                  	 
                  	 return null;
         		}
         	}
         	else{
         	     	Log.d("SMSManager.extractInboxSMSFriend", 
         				"recieved Null from extract ALL Inbox SMS ");
         	     	 messageString="SMSManager.extractInboxSMSFriend: No Inbox SMS Found ";
                  	 
         	     	return null;
         	}
	    }
		catch(Exception ex){
			    YLogger Log = YLoggerFactory.getLogger();
  		        String  debugString="CATCH: ExtractInboxSMSFriend: ";
	         	Log.d("SMSManager.extractInboxSMSFriend", debugString+ex.toString());
	         	messageString="CATCH: SMSManager.extractInboxSMSFriend: "+ex.toString();
              	return null;
		}
	}
	
	/**
	 * Extract friends from the sent sms  of phone. 
	 * Then if the sender of the incoming sms is also in 
	 * contact list, He is declared as a friend.
	 * <p>The resulting information for the contact is stored in the object of
	 *  SMSInformationStore class.</p> 
	 * 
	 * @param context The context of the android activity from where this 
	 * method is called.
	 * 
	 * @return ArrayList containg the object of SMSInformationStore class. Each
	 * object of the arraylist represents a friend
	 */
	public ArrayList<SMSInformationStore> extractSentSMSFriend(Context context){
		
		
		try{
			YLogger Log = YLoggerFactory.getLogger();
  		  
         	Log.d("SMSManager.extractSentSMSFriend", "Inside extractSentSMSFriend");
		 
         	ArrayList<SMSInformationStore> friendList =new ArrayList<SMSInformationStore>();

          	Log.d("SMSManager.extractSentSMSFriend","Going to extract SentSms ");
         	
          	ArrayList<SMSInformationStore> allSMSInfo = extractAllSentSMS(context);
		
          	Log.d("SMSManager.extractSentSMSFriend","All info from Sms extracted, going to filter for friend");
          	
         	if(allSMSInfo!=null && allSMSInfo.size()>0){
			    boolean hasFriend =false;
			    boolean isStored=false;
			    /*
	          	 * The Method to find friend from the extracted information:
	          	 * 
	          	 * 1.  Check if the variable isFriend is set to true
	          	 * 2.  Check if this friend has already been stored in the friendList
	          	 *        This is done by matching the mobile number of current with
	          	 *        the mobile number of all the other friends.
	          	 * 3.  If not stored, then store the current object in friendList
	          	 *        
	          	 */
			    for(int i = 0 ; i<allSMSInfo.size();i++){
	         		
         			if(allSMSInfo.get(i).isFriend){ 
         			
         				 isStored= false;
         				 
         				 for(int k=0;k<friendList.size();k++){
         					if(friendList.get(k).smsNumber.
         											equals(allSMSInfo.get(i).smsNumber)){
         				        isStored = true;
         					    break;
         				    }
         				 }
         				 if(!isStored){
         					  friendList.add(allSMSInfo.get(i));
            		          hasFriend = true;
            				
         				 }
         			}
         				
         		}
         		if(hasFriend){
         			 Log.d("SMSManager.extractSentSMSFriend", "All friend extracted");
                  	 return friendList;
         			
         		}
         		else{
         			
                  	 Log.d("SMSManager.extractSentSMSFriend", "You have no friend");
                  	 messageString="SMSManager.extractSentSMSFriend: No Friend Found from SMS";
                  	 return null;
         		}
         	}
         	else{
         		 Log.d("SMSManager.extractSentSMSFriend","recieved Null from extract ALL Sent SMS ");
         		 messageString="SMSManager.extractAllSMSFriend: No Sent SMS Found ";
              	 
         		 return null;
         	}
	    }
		catch(Exception ex){
			    YLogger Log = YLoggerFactory.getLogger();
  		        String debugString = "CATCH: ExtractInboxSMSFriend: "+ex.toString();
	         	Log.d("SMSManager.extractSentSMSFriend",debugString );
	         	messageString="CATCH: SMSManager.extractSentSMSFriend: No Friend Found ";
             	return null;
		}
	}
		
	
	/**
	 * Used by  other functions of this class to extract the
	 * information from the data obtained after the query to android phone.
	 * 
	 * @param context The application context passed as a parameter to the calling 
	 *                 function
	 *                 
	 * @param cursor  The result obtained after the query to the Android phone, from which the 
	 * necessary information is to be extracted.
	 * 
	 * @return  The arraylist storing the object of SMSInformationStore class. An object
	 * stores information for an sms
	 *          
	 *   
	 */
	private ArrayList<SMSInformationStore> extractInfoFromQueryResult(Context context,
															Cursor cursor){
		
		  YLogger Log = YLoggerFactory.getLogger();
		   Log.d("SMSManager.extractInfoFromQueryResult",
        		  "Inside extractSMSInfo,extracting from query result ");          

          try{            	 
              if(cursor !=null && cursor.getCount()>0){
            	   
            	    Log.d("SMSManager.extractInfoFromQueryResult",
                		   "At least one SMS found");
                   
                   int columnIndex=0;                   
                   
                   String smsDate=null;                   
	               String smsTime=null;
	               String smsType=null;
	               String mobileNumber=null;
                   String contactID=null;
                   String displayName=null;
                   
                   boolean isFriend =false;
                   
                   Long dateInTable=null;
                   EmailStore []emailStore =null;
                 ArrayList<String> contactIDs = new ArrayList<String>();
                   
                   ArrayList<SMSInformationStore> smsStoreArray 
                   									= new ArrayList<SMSInformationStore>();
            	   
                   while(cursor.moveToNext()){
            		   
                	   mobileNumber=null;
		        	   smsDate=null;
		        	   smsTime=null;		        	   
		        	   contactID=null;
		        	   isFriend=false;
		        	   smsType=null;
		        	   emailStore=null;
			           displayName=null;
			           displayName=null;
			           emailStore = null;
		               contactID = null;
		              
                	   
            		   columnIndex = cursor.getColumnIndex(SMS_MOBILENUMBER_COLUMN);
	        	       mobileNumber= cursor.getString(columnIndex);
	        	       // If The sms sender/receiver is not in contact list then the 
	        	       //  display name is same as the mobile number.
	        	       displayName = mobileNumber;  
	        	     
	        	       
	        	    
	        	       Log.d("SMSManager.extractInfoFromQueryResult",
	        	    		   "SMS Mobile number extracted"+mobileNumber);   
	        	            
	  //      	       columnIndex = cursor.getColumnIndex(SMS_CONTACTID_COLUMN);
	  //      	       contactID = cursor.getString(columnIndex);
	        	      
	  //      	       Log.d("SMSManager.extractInfoFromQueryResult", "ContactID extracted");   
	        	       
	        	       columnIndex = cursor.getColumnIndex(SMS_TYPE_COLUMN);
		        	   smsType = cursor.getString(columnIndex);
		        	  
		        	   if(Integer.parseInt(smsType)==1)
		        		   smsType="Incoming";
		        	   else if(Integer.parseInt(smsType)==2)
		        		   smsType="Outgoing"; 
		        	   else if(Integer.parseInt(smsType)==3)
		        		   smsType="Draft";
		        	   else if(Integer.parseInt(smsType)>=4 ||Integer.parseInt(smsType)==0)
		        		   smsType="Other";
		        	   
		        	   Log.d("SMSManager.extractInfoFromQueryResult","SMS Type extracted");  
		        	   
	        	       columnIndex = cursor.getColumnIndex(SMS_DATE_COLUMN);
	        	       dateInTable = cursor.getLong(columnIndex);
	        	   
	        	       Log.d("SMSManager.extractInfoFromQueryResult", "SMS date extracted");   
	        	            
	        	       Time time = new Time();
				       time.toMillis(true);
				       time.set(dateInTable);
				             
		               smsDate= time.monthDay+"-"+(time.month+1)+"-"+time.year;		                     
		               smsTime = time.hour+":"+time.minute;
		               
		              
	        	       Log.d("SMSManager.extractInfoFromQueryResult","SMS date processed");
	        	       
	        	       Log.d("SMSManager.extractInfoFromQueryResult",
	        	    		   "Going to Check if the number exist in contact book");
	        	   
	        	       /*
	        	        * To check if the person is in contact List:
	        	        * 
	        	        * Find the number from which the sms was recieved
	        	        * check this number of the sms sender/reciever in contact list.
	        	        * 
	        	        * 
	        	        * Another way to check if the person is in contact list is: 
	        	        * The sms table contains a column named as person. If the 
	        	        * person of the sms is in contact list then this column has
	        	        * the contact id as its value.
	        	        * 
	        	        * 
	        	        */
		             contactIDs = getContactIDByNumber(context, mobileNumber);
		               
		             if(contactIDs!=null && contactIDs.size()>0){
		            	 
		               contactID = contactIDs.get(0);
	                   if(contactID!=null){
	         		      		                 
		            	  
		        	       Log.d("SMSManager.extractInfoFromQueryResult",
		        	    		   "SMS contact is also in contact book "+contactID);
		            	   
		        	       isFriend=true;
			            
		        	       Log.d("SMSManager.extractInfoFromQueryResult", "going to find Display Name");
		            	   
		        	       displayName = getDisplayNameByContactID(context, 
		        	    		   									contactID);
		                
		        	       Log.d("SMSManager.extractInfoFromQueryResult",
		        	    		   "Going to find Emailaddress for the contact");
		            	   emailStore = getEmailByContactID(context,
		            			   									contactID);   

		               }
		             }  
		               Log.d("SMSManager.extractInfoFromQueryResult", 
	        	    		   "All information extracted for one sms");
		            	   
		        	   SMSInformationStore smsInformation = new SMSInformationStore();
		        	   
		        	   smsInformation.smsNumber=mobileNumber;
		        	   smsInformation.smsDate=smsDate;
		        	   smsInformation.smsTime=smsTime;		        	   
		        	   smsInformation.contactID=contactID;
		        	   smsInformation.isFriend=isFriend;
		        	   smsInformation.smsType=smsType;
		        	   smsInformation.emailStore=emailStore;
			           smsInformation.fullName=displayName;
			           smsInformation.firstName=displayName;
			           smsInformation.lastName=null;
		               
		               Log.d("SMSManager.extractInfoFromQueryResult",
	        	    		   "SMS info stored in the object ");
		               smsStoreArray.add(smsInformation);
            	   }
            	 
                   
        	       Log.d("SMSManager.extractInfoFromQueryResult", 
        	    		   "ALL SMS processed, returning");
        	       return (smsStoreArray);
        	       
               }else{
            	   
            	 
                   Log.d("SMSManager.extractInfoFromQueryResult", "No SMS Found");
                   messageString="SMSManager.extractAllSMSFriend: No  SMS Found ";
                 	
            	   return null;
               }
        	 
          }catch(Exception ex){
        	 String  debugString="CATCH: extractSMSInfo : ";
        	 debugString+=ex.toString();
             Log.d("SMSManager.extractInfoFromQueryResult", debugString);
             messageString="SMSManager.extractInfoFromQueryResult:  "+ex.toString();
           	
             return null;
         }		
	}
	
	
	
	
	
}

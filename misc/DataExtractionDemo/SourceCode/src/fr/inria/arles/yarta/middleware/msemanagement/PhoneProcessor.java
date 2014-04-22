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

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;


/**
 * Performs the common functions that are required for extraction 
 * of information from an Android Phone
 * 
 * @author Kumar Nishant
 *
 */
public class PhoneProcessor {
	
	public String messageString = "";
	public PhoneProcessor(){
		
	}
	
	/**
	 * 
	 * Makes a query to the store of the android that is uniquely 
	 * defined by the Uri given as the parameter.
	 *  
	 * @param context  The application context of the Android activity from where this 
	 *  method is called. Querying the android content requires the Context
	 *  
	 * @param uri The URI of the container that stores the required data
	 * 
	 * @param columns The array of String that contains the names of the column
	 *  that are to be extracted from the Information container of android
	 *  
	 * @param queryString The search condition for the query, according to which
	 * the data will be filtered from the container. <p>e.g.phoneNumber=?</p>
	 * <p>The equivalent of the SQL WHERE syntax</p>
 	 * 
 	 * @param parameterValue  The value of the criteria mentioned in the 
 	 * query string. This value will be replaced at the symbol ? of queryString
	 * 
	 * @param sortOrder The default sort order in which the result will be displayed
	 * 
	 * @return The cursor object that contains the result. 
	 * The result is in the tabular form in this object.
	 * 
	 */
	public Cursor queryProcessor(Context context, Uri uri, 
									String[] columns,String queryString,
									String[] parameterValue,String sortOrder){
		
		YLogger Log = YLoggerFactory.getLogger();
		String debugString="Inside PhoneProcessor.queryProcessor ";
        Log.d("PhoneProcessor.queryProcessor", debugString);
        
		Cursor cursor = null;
		try{
			debugString="Going to make query for folowing : ";
	        Log.d("PhoneProcessor.queryProcessor", debugString);
	        
	        debugString="URI : "+uri;
	        Log.d("PhoneProcessor.queryProcessor", debugString);
	        
	        debugString="queryString :"+queryString;
	        Log.d("PhoneProcessor.queryProcessor", debugString);
	        
	        cursor = context.getContentResolver().query( uri, 
				   										columns, 
				   										queryString, 
				   										parameterValue, 
				   										sortOrder);
		   
		    debugString="Query Successfull Over , Returnnig cursor";
	        Log.d("PhoneProcessor.queryProcessor", debugString);
	        
		    return cursor;
	
		}catch(Exception ex){
			debugString="CATCH Inside PhoneProcsessor.queryProcessor: ";
	        Log.d("PhoneProcessor.queryProcessor", debugString);
	        messageString=  debugString;
	        return null;
		}
	}
	
	/**
	 * Extracts the Display Name as stored in the Contact
	 * list for the contact represented by ContactID
	 * 
	 * @param  context  The application context of the Android Activity 
	 *  from 
	 *  where this method is called. 
	 * 
	 * @param contactID  The contact ID of the contact for which the Display Name
	 *                   is required
	 *
	 * @return           The display name of the contact represented by contactID
	 */
	
	public String getDisplayNameByContactID(Context context,String contactID){
	
		  /**
		   *  The index of the column in the result table
		   *  obtained after a query
		   */
		  int columnIndex=-1;
			
		  /**
		   * The query string for making query for a contact id
		   * 
		   */
	      String queryString="";
	      /**
	       * Error message to be displayed if any error occurs
	       */
	      YLogger Log = YLoggerFactory.getLogger();
	      String debugString="Inside getDisplayNameBycontactId";
		  Log.d("PhoneProcessor.getDisplayNameByContactID",debugString);
		  String displayName="";
		 
		  String [] columnNames = {
				  ContactsContract.Contacts._ID,
				  ContactsContract.Contacts.DISPLAY_NAME
				  
		    };
		  debugString="column made";
		  Log.d("PhoneProcessor.getDisplayNameByContactID",debugString);
		  
		  queryString = ContactsContract.Contacts._ID + "=?";
		  
          try{  
        	  debugString="Going to query for Contact URI";
    		  Log.d("PhoneProcessor.getDisplayNameByContactID",debugString);
			  Cursor resultDisplayName = queryProcessor(context,
					    						ContactsContract.Contacts.CONTENT_URI,
					    						columnNames,
					    						queryString,
					    						new String[] {String.valueOf(contactID)},
					    						null);
			  debugString="Query Over for getting Contact name and ID";
			  Log.d("PhoneProcessor.getDisplayNameByContactID",debugString);
			  
			   if(resultDisplayName!=null && resultDisplayName.getCount()>0){
				   debugString="At Least a contact found";
				   Log.d("PhoneProcessor.getDisplayNameByContactID",debugString);
					  
				   resultDisplayName.moveToFirst();
				 		   
				   columnIndex = resultDisplayName.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				   displayName = resultDisplayName.getString(columnIndex);
				   
				   debugString="One displayName extracted";
				   Log.d("PhoneProcessor.getDisplayNameByContactID",debugString);
				 
				   resultDisplayName.close();
				   return displayName;
			   }
			   debugString="No display Name found";
		       Log.d("PhoneProcessor.getDisplayNameByContactID",debugString);
		       messageString=debugString;
			   resultDisplayName.close();
			   return null;
			   
          }catch(Exception ex){
        		debugString="In catch in getdisplayName by ID";
    			debugString+=ex.getMessage();
    			Log.d("PhoneProcessor.getDisplayNameByContactID",debugString);
    			messageString=  debugString;
    		    return null;   
        	     
          }
		   
	}
		
	
	 /**
     * Extract the Email Address of the Contact represented 
     * by contactID. There may be more that one email
     * address for a contact.
     * The email address are stored in the array of 
     * objects of type EmailStore.  Each email entry is
     * associated with a email type e.g.  Home, Office
     * 
     *  @param  context  The application context of the Android Activity
     *   from 
	 *  where this method is called. 
     * 
     * @param contactID  The contact ID of the contact for which the email
	 *                   address is required
	 *                   
	 * @return           The list of email address of the contact 
	 *                   represented by contactID
     * 
     * 
     */
	public EmailStore[] getEmailByContactID(Context context,String contactID){
		
		  /*
		   * This method search for the given contactID in the 
		   * Email Store of the Android Memory.  Then if any email
		   * address is found for this contact ID , then it traverse 
		   * through each row of the ressulting table obtained
		   * and extract the email address and email type
		   * 
		   * This extracted information is stored in an array of 
		   * objects of type EmailStore.  the object has two strings
		   * for storing the corresponding two values.
		   * 
		   * Finally, the array of object is returned.
		   */
		  
		  
		  /**
		   *  The index of the column in the result table
		   *  obtained after a query
		   */
		  int columnIndex=-1;
			
		  /**
		   * The query string for making query for a contact id
		   * 
		   */
	      String queryString="";
	      /**
	       * Error message to be displayed if any error occurs
	       */
	      String debugString="";
		  
 		  String emailAddress="";
		  String emailType="";
		  YLogger Log = YLoggerFactory.getLogger();
		  
		  try{
		
			  /*
			   *  Column names that will be in output table after the
			   *  query to CallLog 
			   */
				   
			  String [] columnNames = {
					  ContactsContract.CommonDataKinds.Email.CONTACT_ID,
					  ContactsContract.CommonDataKinds.Email.DATA1,
					  ContactsContract.CommonDataKinds.Email.TYPE,
					  
			    };
	
			  // query String for selecting Contact ID from Stored Email Table in Memory
	          queryString = ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?";
	    
	          Cursor emailResult = queryProcessor( context,
	        		                               ContactsContract.CommonDataKinds.Email.CONTENT_URI ,
	                                               columnNames,
	                                               queryString, 
	                                               new String[] {String.valueOf(contactID)},
	                                               null);
	          
	    
	          // If there is any email address for this contactID
	          if(emailResult!=null && emailResult.getCount()>0){
	        	 
	        	 debugString="At least One email for contactID found";
			     Log.d("PhoneProcessor.getEmailByContactID",debugString);
				        
	        	 int index=0;
	        	   
	        	 EmailStore[] emailStore = new EmailStore[emailResult.getCount()];
	       
	     
	        	   // move to all the rows containing email for this contactID
	        	   while(emailResult.moveToNext()){
	     		
	        		   
	        		   columnIndex = emailResult.getColumnIndex(Email.DATA1);  
	        		   emailAddress=emailResult.getString(columnIndex);
	        		   
	         		   columnIndex = emailResult.getColumnIndex(Email.TYPE);  
	        		   emailType=emailResult.getString(columnIndex);
	 	        		 
	        		   if(Integer.parseInt(emailType)==1)
	        			   emailType="Home";
	        		   else if(Integer.parseInt(emailType)==2)
	        			   emailType="Work";
	        		   else if(Integer.parseInt(emailType)==3)
	        			   emailType="Other";
	        		   else if(Integer.parseInt(emailType)>=4)
	        			   emailType="Mobile";
	        		   
	        		   debugString="Email processed,Storing in object";
	    		       Log.d("DEBUG",debugString);
	    			   
	        		   EmailStore tempObject = new EmailStore();        		  
	        		   tempObject.emailAddress=emailAddress;        		  
	        		   tempObject.emailType=emailType;
	        		   
	        		    		  
	        		   emailStore[index] = tempObject;
	  
	        		   debugString="Email Object STored in Array";
	    		       Log.d("PhoneProcessor.getEmailByContactID",debugString);
	    			   
	        		   index++;
	           	   }
	        	   debugString="All Email Stored in Array";
    		       Log.d("PhoneProcessor.getEmailByContactID",debugString);
    			   
	 	           return emailStore;
	          }
	          debugString="No email found";
		      Log.d("PhoneProcessor.getEmailByContactID",debugString);
		      messageString=debugString;
		      return null;
	          
	      }catch(Exception ex){
	    	  debugString="In catch in getemail by ID";
  			  debugString+=ex.getMessage();
  			  Log.d("PhoneProcessor.getEmailByContactID",debugString); 
  			  messageString=debugString;
  		      return null;   
      	      
	      }
	  }

	  /**
       * Extract the phone number of the contact represented by ContactID.
       * There may be more that one phone Number for a contact  
       * The phoneNumbers are stored in the array of 
       * objects of type PhoneNumberStore.  Each phone number 
       * is associated with a phone type e.g.  Home, Office
       * 
       *  @param  context  The application context of the Android Activity of 
       *   from 
	   *                    where this method is called. 
       *
       *  @param contactID  The contact ID of the contact for which the phone
	   *                    number is required
	   *                   
	   *  @return           The list of Phone Numbers of the contact 
	   *                   represented by contactID
	   *                   
       */
	public PhoneNumberStore[] getPhoneNumberByContactID(Context context,String contactID){
		  /*
		   * This method search for the given contactID in the 
		   * Phone Store of the Android Memory.  Then if any phone
		   * number is found for this contact ID , then it traverse 
		   * through each row of the resulting table obtained
		   * and extract the phone number and phone type
		   * 
		   * This extracted information is stored in an array of 
		   * objects of type Phone Number Store  
		   * the object has two strings
		   * for storing the corresponding two values.
		   * 
		   * Finally, the array of object is returned.
		   */
		
		  /**
		   *  The index of the column in the result table
		   *  obtained after a query
		   */
		  int columnIndex=-1;
			
		  /**
		   * The query string for making query for a contact id
		   * 
		   */
	      String queryString="";
	      /**
	       * Error message to be displayed if any error occurs
	       */
	      String debugString="";
		  
 		  String phoneNumberType="";
		  String phoneNumber="";
		  YLogger Log = YLoggerFactory.getLogger();
     	  debugString="Inside PhoneNumber by ContactId";
		  Log.d("PhoneProcessor.getPhoneNumberByContactID",debugString);
			 
		  try{
			  /*
			   *  Column names that will be in output table after the
			   *  query to CallLog 
			   */
				   
			  String [] columnNames = {
					  ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
					  ContactsContract.CommonDataKinds.Phone.NUMBER,
					  ContactsContract.CommonDataKinds.Phone.TYPE,
					  
			    };

	          debugString="Column made for query";
			  Log.d("PhoneProcessor.getPhoneNumberByContactID",debugString);
				 
			  queryString = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?";
			

	          debugString="Going for query phone";
			  Log.d("PhoneProcessor.getPhoneNumberByContactID",debugString);
				 
			  Cursor phoneResult = queryProcessor(context, 
        		                       ContactsContract.CommonDataKinds.Phone.CONTENT_URI ,
        		                       columnNames,
        		                       queryString,  
                                       new String[] {String.valueOf(contactID)},
                                       null);
			

	           debugString="Query Over for phone";
			   Log.d("PhoneProcessor.getPhoneNumberByContactID",debugString);
				 
			  if(phoneResult!=null && phoneResult.getCount()>0){
				  debugString="At least 1 phone for this ID";
				  Log.d("PhoneProcessor.getPhoneNumberByContactID",debugString);
					
			
				  PhoneNumberStore []phoneNumberStore= new PhoneNumberStore[phoneResult.getCount()];
				  debugString="PhoneNumberStore made";
				  Log.d("PhoneProcessor.getPhoneNumberByContactID",debugString);
					
				  int index=0;
				  debugString="Processing each phone number obtained";
				  Log.d("PhoneProcessor.getPhoneNumberByContactID",debugString);
					
				  while(phoneResult.moveToNext()){
					
				
					  columnIndex = phoneResult.getColumnIndex(Phone.NUMBER);
					  phoneNumber = phoneResult.getString(columnIndex);
          			  phoneNumber = removeDashFromNumber(phoneNumber);
				
          			  columnIndex = phoneResult.getColumnIndex(Phone.TYPE);
					  phoneNumberType = phoneResult.getString(columnIndex);
                
					  if(Integer.parseInt(phoneNumberType)==1)
						  phoneNumberType="Home";
					  else if(Integer.parseInt(phoneNumberType)==2)
						  phoneNumberType="Mobile";
					  else if(Integer.parseInt(phoneNumberType)==3)
						  phoneNumberType="Work";
					  else if(Integer.parseInt(phoneNumberType)>=4)
						  phoneNumberType="Other/FAX";
					  
						
					  PhoneNumberStore tempObject = new PhoneNumberStore();
					  tempObject.phoneNumber=phoneNumber;
					  tempObject.phoneNumberType=phoneNumberType;
					
					  debugString="A phone number stored in object";
					  Log.d("PhoneProcessor.getPhoneNumberByContactID",debugString);
					  
					  phoneNumberStore[index]= tempObject;
					  debugString="The phone number object stored in array";
					  Log.d("PhoneProcessor.getPhoneNumberByContactID",debugString);
					
			
					  index++;
				  }
				  debugString="All phone number stored";
				  Log.d("PhoneProcessor.getPhoneNumberByContactID",debugString);
				
				  phoneResult.close();
				  return phoneNumberStore;
			  }
			  debugString="No phone Number for this ID";
			  Log.d("PhoneProcessor.getPhoneNumberByContactID",debugString);
			  messageString=debugString;
			  phoneResult.close();
			  return null;
			  
		  }catch(Exception ex){
			debugString="In catch in getPhone by ID";
  			debugString+=ex.getMessage();
  			Log.d("PhoneProcessor.getPhoneNumberByContactID",debugString); 
  			messageString=debugString;
  		    return null;   
      	     
	      }
	  }
	  
	  
	  /**
		 * Extracts the contactsID for a given name in the contact list
		 *
		 *  
         * @param  context  The application context of the Android Activity
         *  of the class from where this method is called. 
         *       
		 * @param displayName  The name of the contact displayed 
		 *                      in the contact list for which  the 
		 *                      contactID is required
		 *                      
		 * @return The arraylist containing all the contact Id for a given name
		 *        
		 *                 
		 */
	public ArrayList<String> getContactIDByDisplayName(Context context,String displayName){
			/*
			 * Form the columns to be extracted
			 * 
			 * Query the contact container for all the contacts by given display name
			 * 
			 * traverse the result to extract contactId
			 * 
			 * store the id in arraylist
			 * 
			 * 	
			 */
			/**
			 *  The index of the column in the result table
			 *  obtained after a query
			 */
			int columnIndex=-1;
			
			/**
			 * The contact id of the record of a contact
			 */
			String contactId="";

	        /**
			 * The query string for making query for a contact id
			 * 
			 */
		     String queryString="";
		     YLogger Log = YLoggerFactory.getLogger();
	         String debugString = "Inside getContactIDby Name";
	         Log.d("PhoneProcessor.getContactIDByDisplayName",debugString);
	         	    
	         
			try{
				 /**
				  *  Column names that will be in output table after the
				  *  query to Contacts
				  */
				   
				   
				  String [] columnNames = {
						  ContactsContract.Contacts._ID,
						  ContactsContract.Contacts.DISPLAY_NAME,
							  
				    };
				  
				 	debugString="Column declared for fetching";
					Log.d("PhoneProcessor.getContactIDByDisplayName",debugString);
			    
				  // query String for selecting Contact ID from Stored Contacts in Memory
		          queryString = ContactsContract.Contacts.DISPLAY_NAME + "=?";
		       	  
		          debugString="String made for matching name,going for query";
		       	  Log.d("PhoneProcessor.getContactIDByDisplayName",debugString);
		    
		          Cursor nameResult = queryProcessor(  context,		        		                               ContactsContract.Contacts.CONTENT_URI ,
		                                               columnNames,
		                                               queryString, 
		                                               new String[] {String.valueOf(displayName)},
		                                               null);
		          
		       	debugString="Query over for contact name";
				Log.d("PhoneProcessor.getContactIDByDisplayName",debugString);
		    
		        if(nameResult!=null && nameResult.getCount()>0){
		         	debugString="at least 1 contact exsist";
					Log.d("PhoneProcessor.getContactIDByDisplayName",debugString);
			        
	         		  /*
	         		   *  Entering the loop means there is at least one contact in 
	         		   *  contact list for which Information is to be stored.
	         		   *   
	         		   *  Hence defining an arrayList to store it
	         		   *  
	         		   */
		        	 ArrayList<String> contactIDStore = new ArrayList<String>();
		        	 	debugString="Array List formed for storing ID";
		    			Log.d("PhoneProcessor.getContactIDByDisplayName",debugString);
		    	    
		         	 while(nameResult.moveToNext())
		          	{        		  
		         		  /*
		         		   *  Get the contact Id from the result obtained after query for 
		         		   *  all contacts present in Contact list. 
		         		   */
		          	      columnIndex = nameResult.getColumnIndex(ContactsContract.Contacts._ID);
		         		  contactId = nameResult.getString(columnIndex);
		         		  contactIDStore.add(contactId);
		         		  
		         		  debugString="a contact Id stored";
		        		  Log.d("PhoneProcessor.getContactIDByDisplayName",debugString);
		        	           		                  
		             } // while  ends here
		          	
		         	debugString="All contact stored";
					Log.d("PhoneProcessor.getContactIDByDisplayName",debugString);
			    
		         	 nameResult.close();   
		             return contactIDStore;
		             
		        }// if ends here 	
		     	debugString="No contact found for name";
				Log.d("PhoneProcessor.getContactIDByDisplayName",debugString);
				messageString=debugString;
		        nameResult.close();   
	            return null;
		     
			}catch(Exception ex){
				debugString="In catch in getContactIDbyDisplayName";
	  			debugString+=ex.getMessage();
	  			Log.d("PhoneProcessor.getContactIDByDisplayName",debugString); 
	  			messageString=debugString;
	  		    return null;   
	      	     }
		        
		}
		
	  
		/**
		 * Extracts the contactsID for a given email in the contact list
		 *
		 *   
         * @param  context  The application context of the Android Activity
         *   from 
	     *                    where this method is called. 
         *       
		 *
		 * @param email  The email address of the contact displayed 
		 *                      in the contact list for which  the 
		 *                      contactID is required
		 *                      
		 * @return The arraylist containing all the contact Id for a given name
		 *        
		 *                 
		 */
	public ArrayList<String> getContactIDByEmail(Context context,String email){
			
			/**
			 *  The index of the column in the result table
			 *  obtained after a query
			 */
			int columnIndex=-1;
			
			/**
			 * The contact id of the record of a contact
			 */
			String contactId="";

	        /**
	         * Error message to be displayed if any error occurs
	         */
	        String debugString="";

	        /**
			 * The query string for making query for a contact id
			 * 
			 */
		     String queryString="";
		     YLogger Log = YLoggerFactory.getLogger();
		  	debugString="Inside ContactId by email";
			Log.d("PhoneProcessor.getContactIDByEmail",debugString);
	    
			try{
				 /**
				  *  Column names that will be in output table after the
				  *  query to Contacts
				  */
				   
				 String [] columnNames = {
						  ContactsContract.CommonDataKinds.Email.CONTACT_ID,
						  ContactsContract.CommonDataKinds.Email.DATA1,
							  
				    };
				  // query String for selecting Contact ID from Stored Email Table in Memory
		          queryString = ContactsContract.CommonDataKinds.Email.DATA1 + "=?";
		          
		       	  debugString="Going for query for Email";
				  Log.d("PhoneProcessor.getContactIDByEmail",debugString);
		    
		          Cursor emailResult = queryProcessor( context,
		        		                               ContactsContract.CommonDataKinds.Email.CONTENT_URI ,
		                                               columnNames,
		                                               queryString, 
		                                               new String[] {String.valueOf(email)},
		                                               null);
		          
		       	debugString="Query Over for email";
				Log.d("PhoneProcessor.getContactIDByEmail",debugString);
		    
		        if(emailResult!=null && emailResult.getCount()>0){		        
		        	  /*
	         		   *  Entering the loop means there is at least one contact in 
	         		   *  contact list for which Information is to be stored.
	         		   *   
	         		   *  Hence defining an arrayList to store it
	         		   *  
	         		   */
		        	 ArrayList<String> contactIDStore = new ArrayList<String>();
		        	 
		        	 debugString="At least one email found";
					 Log.d("PhoneProcessor.getContactIDByEmail",debugString);
				    
		         	  while(emailResult.moveToNext()){
		          	
		         		  
		         		  /*
		         		   *  Get the contact Id from the result obtained after query for 
		         		   *  all contacts present in Contact list. 
		         		   */
		          	      columnIndex = emailResult.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID);
		         		  contactId = emailResult.getString(columnIndex);
		         		  contactIDStore.add(contactId);
		         		  
		         		  debugString="Contact id for email stored";
						  Log.d("PhoneProcessor.getContactIDByEmail",debugString);
					    
		         	  }
		         	  debugString="All contactID stored";
					  Log.d("PhoneProcessor.getContactIDByEmail",debugString);
				    
		         	  emailResult.close();
		         	  return contactIDStore;
		        }
		     	debugString="No contactId found";
				Log.d("PhoneProcessor.getContactIDByEmail",debugString);
				messageString=debugString;
                emailResult.close();
                return null;
		     
			}catch(Exception ex){
				debugString="In catch in getContactId by email";
	  			debugString+=ex.getMessage();
	  			Log.d("PhoneProcessor.getContactIDByEmail",debugString); 
	  			//Toast.makeText(context,debugString,Toast.LENGTH_LONG).show();
	  			messageString=debugString;
	  		    return null;   
	      	       
			 }
		        
		}
		
		
		
		/**
		 * Extracts the contactsID for a given number in the contact list
		 * 
		 *    
         * @param  context  The application context of the Android Activity 
         *  from 
	     *                    where this method is called. 
         *       
		 *
		 * @param phoneNumber  The contact number of the contact displayed 
		 *                      in the contact list for which  the 
		 *                      contactID is required
		 *                      
		 * @return The arrayList containing all the contact Id for a given number
		 *        
		 *                 
		 */
	public ArrayList<String> getContactIDByNumber(Context context,String phoneNumber){
			/*
			 * Form the column to be extracted
			 * 
			 * query the contact container and filter by number
			 * 
			 * extract the contact id obtained from result
			 * 
			 * store it in arraylist
			 * 
			 */
			
			/**
			 *  The index of the column in the result table
			 *  obtained after a query
			 */
			int columnIndex=-1;
			
			/**
			 * The contact id of the record of a contact
			 * 
			 */
			String contactId="";

	        /**
	         *  message to be displayed if any error occurs
	         */
			YLogger Log = YLoggerFactory.getLogger();
	        String debugString="Inside contactIdbyNumber";
	    	Log.d("PhoneProcessor.getContactIDByNumber",debugString);
	    
	        /**
	         * 
			 * The query string for making query for a contact id
			 * 
			 */
		    String queryString="";

	        
			try{
				 /*
				  *  Column names that will be in output table after the
				  *  query to CallLog 
				  */
				debugString="Finding contactId by number "+phoneNumber;
		    	Log.d("PhoneProcessor.getContactIDByNumber",debugString);	  
		    	
				  String [] columnNames = {
						  ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
						  ContactsContract.CommonDataKinds.Phone.NUMBER,
							  
				    };
				  // query String for selecting Contact ID from Stored Email Table in Memory
		          queryString = ContactsContract.CommonDataKinds.Phone.NUMBER + "=?";
		 
		          debugString="Going to query contact book for the number";
				  Log.d("PhoneProcessor.getContactIDByNumber",debugString);
			    
		          Cursor phoneResult = queryProcessor( context,
		        		                               ContactsContract.CommonDataKinds.Phone.CONTENT_URI ,
		                                               columnNames,
		                                               queryString, 
		                                               new String[] {String.valueOf(phoneNumber)},
		                                               null);
		          
		          debugString="Query for phone number over";
				  Log.d("PhoneProcessor.getContactIDByNumber",debugString);
				  
		        if(phoneResult!=null && phoneResult.getCount()>0){
		        
		        	  /*
	         		   *  Entering the loop means there is at least one contact in 
	         		   *  contact list for which Information is to be stored.
	         		   *   
	         		   *  Hence defining an arrayList to store it
	         		   *  
	         		   */
		        	 ArrayList<String> contactIDStore = new ArrayList<String>();
		        	 
		        	 debugString="At least one phone number found";
					 Log.d("PhoneProcessor.getContactIDByNumber",debugString);
		         	 
					 while(phoneResult.moveToNext())
		          	 {
		         		  /*
		         		   *  Get the contact Id from the result obtained after query for 
		         		   *  all contacts present in phone book list. 
		         		   */
		          	      columnIndex = phoneResult.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
		         		  contactId = phoneResult.getString(columnIndex);
		         		  contactIDStore.add(contactId);
		         		  
		         		  debugString="Storing the contactId for number";
						  Log.d("PhoneProcessor.getContactIDByNumber",debugString);
		          	 }
					debugString="All the contactId for number saved";
					Log.d("PhoneProcessor.getContactIDByNumber",debugString);
		         	phoneResult.close();
		         	return contactIDStore;
		        }
		        debugString="No contactid for phonenumber found";
				Log.d("PhoneProcessor.getContactIDByNumber",debugString);
		        phoneResult.close();
		        messageString=debugString;
	         	return null;
		       
			}catch(Exception ex){
				debugString="In catch in getContactId by email";
	  			debugString+=ex.getMessage();
	  			Log.d("PhoneProcessor.getContactIDByNumber",debugString); 
	  			messageString=debugString;
	  		    return null;   
	      	     
			 }
		        
		}
		

		/**
	     * Extract the Email Address of the Contact represented 
	     * by phoneNumber. There may be more that one email
	     * address for the contact.
	     * 
	     * The email address are stored in the array of 
	     * objects of type EmailStore.  Each email entry is
	     * associated with a email type e.g.  Home, Office
	     *
	     *    
         * @param  context  The Context of the Android Activity of the class from 
	     *                    where this method is called. 
         *       
		 *
	     * @param phoneNumber  The mobile number of the contact for which 
	     *                     the email address is required
		 *                   
		 * @return           The list of email address of the contact 
		 *                   represented by that phone Number
	     * 
	     * 
	     */
	public EmailStore[] getEmailByPhoneNumber(Context context,String phoneNumber){
			  
			  /*
			   *   
			   *   First get the contact id for the phone number
			   *   
			   *   get the email for this obtained contact id
			   *   
			   */
		      /**
		       * message to be displayed if any error occurs
		       */
		      String debugString="";

			  String contactId="";
			  YLogger Log = YLoggerFactory.getLogger();
			
			  Log.d("ContactManager.getEmailByPhoneNumber","Inside getEmailByPhoneNumber");
		        
			  try{
				
				  ArrayList<String> contactIDStore = new ArrayList<String>();
				  EmailStore [] emailStore;
				
				  Log.d("PhoneProcessor.getEmailByPhoneNumber","Going to find ContactId by Number");
			        
				  contactIDStore = getContactIDByNumber(context,phoneNumber);
				  
				
				  Log.d("PhoneProcessor.getEmailByPhoneNumber","Contact Id from number found");
			        
				  if((contactIDStore!=null) && (contactIDStore.size()>0)){
					
					  Log.d("PhoneProcessor.getEmailByPhoneNumber","At least ContactID found");
				          
					  contactId = contactIDStore.get(0);

					  Log.d("PhoneProcessor.getEmailByPhoneNumber",
							  "Going to get Email from ContactID");
				      
					  emailStore = getEmailByContactID(context,contactId);
					
					  Log.d("PhoneProcessor.getEmailByPhoneNumber","returning Email");
				      
					  return emailStore;
				  }
				  debugString="No contactId found for this number";
				  Log.d("PhoneProcessor.getEmailByPhoneNumber",debugString);
				  messageString=debugString;
                  return null;		         
		          
		      }catch(Exception ex){
		    	    debugString="In catch ";
		  			debugString+=ex.getMessage();
		  			Log.d("PhoneProcessor.getEmailByPhoneNumber",debugString); 
		  			messageString=debugString;
		  		    return null;   
		      	      
		      }
		  }
		

		   /**
		     * Extract the Email Address of the Contact represented 
		     * by DisplayName. There may be more that one email
		     *  address for the contact.
		     * The email address are stored in the array of 
		     * objects of type EmailStore.  Each email entry is
		     * associated with a email type e.g.  Home, Office
		     * 
		     *
             * @param  context  The application context of the Android Activity
             *  from 
	         *                    where this method is called. 
             *       
		     * 
		     * 
		     * @param displayName  The name of the contact as displayed in 
		     *                     contact list for which 
		     *                     the email address is required
			 *                   
			 * @return           The list of email address of the contact 
			 *                   represented by the displayName
		     * 
		     * 
		     */
	public EmailStore[] getEmailByDisplayName(Context context,String displayName){
				  /*
				   *   Get the ContactId for the DisplayName
				   *   
				   *   Get the Email for the obtained Contact ID
				   *   
				   * 
				   */
				  

				  String contactId="";
				  YLogger Log = YLoggerFactory.getLogger();
				
				  Log.d("PhoneProcessor.getEmailByDisplayName","Inside get Email by DisplayName");
			      
				  try{
					
					  ArrayList<String> contactIDStore = new ArrayList<String>();
					  EmailStore [] emailStore;

					  
					  Log.d("PhoneProcessor.getEmailByDisplayName","Going to find contactId for this name");
				      
					  contactIDStore = getContactIDByDisplayName(context,displayName);

					  Log.d("PhoneProcessor.getEmailByDisplayName","ContactId for this name over");
				      
					  if((contactIDStore!=null) && (contactIDStore.size()>0)){
						
						  Log.d("PhoneProcessor.getEmailByDisplayName",
								  "At least One contactId for this name found");
					      
						  contactId = contactIDStore.get(0);

						  Log.d("PhoneProcessor.getEmailByDisplayName",
								  "going for email for this contactId");
					      
						  emailStore = getEmailByContactID(context,contactId);

						  Log.d("PhoneProcessor.getEmailByDisplayName",
								  "got emailStore for this conatctId");
					      
						  return emailStore;
					  }

					  Log.d("PhoneProcessor.getEmailByDisplayName",
							  "No email found for this name" );	
					  messageString="No email found for this name";
	                  return null;		         
			          
			      }catch(Exception ex){
			    	    String debugString="In catch in getemailByDisplayName";
			  			debugString+=ex.getMessage();
			  			Log.d("PhoneProcessor.getEmailByDisplayName",debugString); 
			  		    messageString=debugString;
			  		    return null;   
			      		  
			      }
			  }
			

		  
		    /**
		     * 
		     * Extract the Phone Number of the Contact represented 
		     * by emailAddress. There may be more that one number
		     * for the contact.
		     * The email address are stored in the array of 
		     * objects of type EmailStore.  Each email entry is
		     * associated with a email type e.g.  Home, Office
		     *
		     * 
             * @param  context  The Context of the Android Activity of the class from 
	         *                    where this method is called. 
             *
		     * 
		     * @param emailAddress  The email of the contact for which 
		     *                     the phoneNumber is required
			 *                   
			 * @return           The list of Phone number of the contact 
			 *                   represented by that email
		     * 
		     * 
		     */
	public PhoneNumberStore[] getPhoneNumberByEmail(Context context,String emailAddress){
				  /*
				   * Get the Contact Id for the email
				   * 
				   * Get the phone number for the obtained contact id
				   * 
				   */
			
				  String contactId="";
				  YLogger Log = YLoggerFactory.getLogger();
				 
				  Log.d("PhoneProcessor.getPhoneNumberByEmail","Inside PhoneNumber By email");				      

				  try{
					  
					ArrayList<String> contactIDs = new ArrayList<String>();
			        PhoneNumberStore [] phoneNumberStore;

					Log.d("PhoneProcessor.getPhoneNumberByEmail",
							"Going to get contactId for this mail");				      

						
			        contactIDs = getContactIDByEmail(context,emailAddress);

					Log.d("PhoneProcessor.getPhoneNumberByEmail","contactIds over for mail");				      

			        if((contactIDs!=null) && (contactIDs.size()>0)){
									
			        	contactId = contactIDs.get(0);

						Log.d("PhoneProcessor.getPhoneNumberByEmail",
								"Going to find phone number by id");				      
			        
			        	phoneNumberStore = getPhoneNumberByContactID(context,contactId);

						Log.d("PhoneProcessor.getPhoneNumberByEmail","returning phone NumberStore");				      

			        	return phoneNumberStore;
			        }
			      
					Log.d("PhoneProcessor.getPhoneNumberByEmail","No number for this email");				      
					messageString="No number for this email";
					return null;		         
			        
			        
			      }catch(Exception ex){
			    	    String  debugString="In catch ";
			  			debugString+=ex.getMessage();
			  			Log.d("PhoneProcessor.getPhoneNumberByEmail",debugString);
			  			messageString=debugString;
			  		    return null;   
			      		
			      }
			  }
			

			  
			  /**
			     * Extract the Phone Number of the Contact represented 
			     * by displayName. There may be more that one number
			     * for the contact.
			     * 
			     * The phone number are stored in the array of 
			     * objects of type PhoneNumberStore.  Each phone number is
			     * associated with a phone type e.g.  Home, Office
			     * 
		         * 
                 * @param  context  The Context of the Android Activity of the class from 
	             *                    where this method is called. 
                 *
			     * @param displayName  The name of the contact for which 
			     *                     the phoneNumber is required
				 *                   
				 * @return           The list of Phone number of the contact 
				 *                   represented by that display name
			     * 
			     * 
			     */
	public PhoneNumberStore[] getPhoneNumberByName(Context context,String displayName){
				/*
				 *
				 *  Get the contact id for the name
				 *  
				 *  get the phonenumber for the obtained contact id
				 * 
				 * 
				 */

					  String contactId="";
					  YLogger Log = YLoggerFactory.getLogger();
					  
					  Log.d("PhoneProcessor.getPhoneNumberByName","Inside getPhoneNumberByName");
					  
					  try{
						ArrayList<String> contactIDs = new ArrayList<String>();
				        PhoneNumberStore [] phoneNumberStore;

						Log.d("PhoneProcessor.getPhoneNumberByName",
								"going to call for Id for this name");
				        
						contactIDs = getContactIDByDisplayName(context,displayName);
				        
						Log.d("PhoneProcessor.getPhoneNumberByName","ID received for this name");
				        
				        if(contactIDs!=null){
				        	
							Log.d("PhoneProcessor.getPhoneNumberByName",
									"Contact IDs received size:"+contactIDs.size());
					       
							Log.d("PhoneProcessor.getPhoneNumberByName",
									"Getting the number only for first ID");
							
					       if(contactIDs.size()>0){ 
					    	   contactId = contactIDs.get(0);
					    	   phoneNumberStore = getPhoneNumberByContactID(context,
					    			   										contactId);
					    	   return phoneNumberStore;
					       }
					   	  
						   Log.d("PhoneProcessor.getPhoneNumberByName",
								   "No contact ID for this name");
						   
				           return null;
					    }

						Log.d("PhoneProcessor.getPhoneNumberByName",
								"No contact ID found for this name");
				        return null;
				    
					  }catch(Exception ex){
				    	   String  debugString="In catch ";
				  			debugString+=ex.getMessage();
				  			Log.d("PhoneProcessor.getPhoneNumberByName",debugString); 
				  		    return null;   
				      		  
				      }
				  }
				  
				  /**
				     * Extract the DisplayName of the Contact represented 
				     * by phoneNumber. 
				     *   
				     * @param  context    The Context of the Android Activity of the class from 
	                 *                    where this method is called.
	                 *                     
				     * @param phoneNumber  The phoneNumber of the contact for which 
				     *                     the Name is required
					 *                   
					 * @return           The list of Phone number of the contact 
					 *                   represented by that phone Number
				     * 
				     * 
				     */
				  public String getDisplayNameByNumber(Context context,String phoneNumber){
					/*
					 * 
					 * Get the contact id for the number
					 * 
					 * get the name from the obtained contact id
					 * 	  
					 */
						  String displayName=null;
						  String contactId="";
						  YLogger Log = YLoggerFactory.getLogger();
						 
						  Log.d("PhoneProcessor.getDisplayNameByNumber","Inside getDisplayNameByNumber");
					          
						  try{
							ArrayList<String> contactIDs = new ArrayList<String>();
					  		
							Log.d("PhoneProcessor.getDisplayNameByNumber",
									"going for contactId for this number");
					        
					        contactIDs = getContactIDByNumber(context,phoneNumber);
						
							Log.d("PhoneProcessor.getDisplayNameByNumber",
									"Contact id for number over");
					        
					        if(contactIDs!=null && contactIDs.size()>0){
					        	
								Log.d("PhoneProcessor.getDisplayNameByNumber",
										"At least a contact for this number");
						        
					        	contactId = contactIDs.get(0);
		
								Log.d("PhoneProcessor.getDisplayNameByNumber",
										"Going for getting display name for the contact id");
						        
						        displayName = getDisplayNameByContactID(context,contactId);
						    
								Log.d("PhoneProcessor.getDisplayNameByNumber",
										"returning display name for the contact id");
						        
						        return displayName;
						    }

					    
							Log.d("PhoneProcessor.getDisplayNameByNumber",
									"No name found for this number");
							messageString="No name found for this number";
					        return null;
					       
							
					      }catch(Exception ex){
					   	   String  debugString="In catch in getphoneNUmber by email";
				  			debugString+=ex.getMessage();
				  			Log.d("PhoneProcessor.getDisplayNameByNumber",debugString); 
				  			messageString=debugString;
				  			return null;   
				     
					      }
					  }
					  
					  /**
					     * Extract the DisplayName of the Contact represented 
					     * by emailAddress. 
					     *   
					     * @param emailAddress  The emailAddress of the contact for 
					     *                     which 
					     *                     the Name is required
						 *                   
						 * @return           The display name of the contact 
						 *                   represented by that email address
					     * 
					     * 
					     */
					  public String getDisplayNameByEmail(Context context,String emailAddress){
							  
						/*
						 *  Get the contact id for the email
						 *  
						 *  get the display name for the contact id obtained
						 * 
						 */
					
					      String displayName="";
						  String contactId="";
						  YLogger Log = YLoggerFactory.getLogger();
						 
						  Log.d("PhoneProcessor.getDisplayNameByEmail",
								  "Inside displayName by Email");
						          
						  try{
							  ArrayList<String> contactIDs = new ArrayList<String>();
							 
							  Log.d("PhoneProcessor.getDisplayNameByEmail",
									  "Going to get contactId for the email");
							        
						      contactIDs = getContactIDByEmail(context,emailAddress);
								   
						      if(contactIDs!=null && contactIDs.size()>0){
						          contactId = contactIDs.get(0);
							        
						          Log.d("PhoneProcessor.getDisplayNameByEmail",
						        		  "going to find name by the contactId");
							        
						          displayName = getDisplayNameByContactID(context,contactId);
									
						          Log.d("PhoneProcessor.getDisplayNameByEmail",
						        		  "returning name for the email this number");
							        
						          return displayName;
						      }
						      
					          Log.d("PhoneProcessor.getDisplayNameByEmail",
					        		  "No Name for this email found");
					          messageString="No Name for this email found";
					          return null;
						      
						   }catch(Exception ex){
					     	   String  debugString="In catch ";
					  			debugString+=ex.getMessage();
					  			Log.d("PhoneProcessor.getDisplayNameByEmail",debugString); 
					  			messageString=debugString;
					  			return null;   
						     	    	  
					      }
					  }
						  
		
		
		/**
		 * 
		 * Called whenever a mobile number is read from 
		 * android device. This methods makes sure that there is no dash
		 *  signt (-) in the mobile number obtained.
		 *
		 *  For e.g. If the number read is 988-988-0912 , 
		 *  it gets converted to 9889880912			 *  
		 *  
		 * @param mobileNumber
		 * @return The modified phone number without any dash sign ( - )
		 */
		public String removeDashFromNumber(String mobileNumber){
			
			String finalString ="";
		
			
			YLogger Log = YLoggerFactory.getLogger();
			String debugString = "Inside Replace Dash of Mobile Number"+mobileNumber;
			Log.d("PhoneProcessor.removeDashFromNumber", debugString);

			if(mobileNumber!=null){
				finalString = mobileNumber.replaceAll("-","");
				
				debugString = "Replaced all dash "+finalString;
				Log.d("PhoneProcessor.removeDashFromNumber", debugString);
				return finalString;
			}
			debugString = "Mobile number received is null";
			Log.d("PhoneProcessor.removeDashFromNumber", debugString);
			return null;
		}
		
		
		
		
		
		
		
}

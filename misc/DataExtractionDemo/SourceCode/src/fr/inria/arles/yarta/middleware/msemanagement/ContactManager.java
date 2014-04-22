/**
* <p>This package contains  classes that extract information from
 * various social data source.
 * </p>
 * <p>The classes of this package forms the functional unit of Data
 * Extraction Manager</p>
 
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import fr.inria.arles.yarta.logging.*;

/**
 * 
 * Extracts the information from the contact list 
 * of the Android phone
 * 
 * 
 * @author Kumar Nishant
 * @since June 5,2010
 *
 */
public class ContactManager extends PhoneProcessor{

	
	/**
     *   Stores the object of ContactInformationStore.
         An object in this arraylist stores information
         for a contact in contact list
     */
    public ArrayList<ContactInformationStore> contactInfoArrayList = null;
    
    
	public ContactManager(){
		contactInfoArrayList =  new ArrayList<ContactInformationStore>();
	}
	
	
	/**
	 * 
	 * Extract full contact list.
	 * 
	 *  * <p>The following information are extracted for a contact in contact list</p>
	    * <ui>
	    *   <li>DisplayName</li>
	    *   <li>MobileNumber</li>
	    *   <li>MobileType</li>
	    *   <li>Email Address</li>
	    *   <li>Email Type </li>
	      </ui>
	      
	 * @param  context  The application context of the Android activity from 
	 *  where this method is called. 
	 * 
	 * @return The arraylist that has objects of ContactInformationStore 
	 * class. An object in the arraylist stores information of a contact
	 * in the contact list.
	 *                 
	 */
	public ArrayList<ContactInformationStore> findContact(Context context){
	
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
         * The displayName of the Contact
         */
        String displayName="";

		try{
			 /*
			  *  Column names that will be in output table after the
			  *  query to contact
		
			  */
			YLogger Log = YLoggerFactory.getLogger();
		      
		    String [] columnNames = {
		    		ContactsContract.Contacts._ID,
		            ContactsContract.Contacts.DISPLAY_NAME
		    };
		    

		    Log.d("ContactManager.findContact", "Columns made, GOing Quering Contact");
		    /*
		     * Querying the content for contact
		     */
			Cursor resultContactTable = queryProcessor( context,
	                                                ContactsContract.Contacts.CONTENT_URI,
	               								    columnNames,
	               								    null,
	               								    null,
	               									null);
			
			
			Log.d("ContactManager.findContact","Query Over for COntact");
			 /*
		      * Traversing through the column for the table obtained
		      */
			
	        if(resultContactTable!=null && resultContactTable.getCount()>0)
	        {
	        	
	    	    Log.d("ContactManager.findContact","At least one contact found");
	    		
	        	while(resultContactTable.moveToNext())
	          	{
	        		
	        	    Log.d("ContactManager.findContact", "Processing a contact");
	        		  
	         	  /*
	         	   *  Entering the loop means there is a contact in contact list
	         	   *  for which Information is to be stored. 
	         	   *  Hence defining an object for ContactInforamtionStore
	         	   *  
	         	   */
	        			  
	        	    ContactInformationStore contactInfoStore = new ContactInformationStore();
	         		  
	         		  
	         	  /*
	         	   *  Get the contact Id from the result obtained after query for 
	         	   *  all contacts present in Contact list. 
	         	   */
	         	 
	        	     Log.d("ContactManager.findContact", "Getting contact Id for the contact");
		        		
	         		  
	        	     columnIndex = resultContactTable.getColumnIndex(ContactsContract.Contacts._ID);
	        	     contactId = resultContactTable.getString(columnIndex);
	         		  	         	  
	         		  
	         		/*
	         		 * Extract the name of the contact for the Contact ID
	         		 */
	         	
		        	  Log.d("ContactManager.findContact", "Getting the name of the contact");
		        		
	         		  columnIndex = resultContactTable.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
	         		  displayName = resultContactTable.getString(columnIndex);
	         		  
	         		  contactInfoStore.fullName=displayName;    		  
	         		  contactInfoStore.firstName=displayName;    		  
	         		  contactInfoStore.lastName="";    		  
	         
		        	  Log.d("ContactManager.findContact", "getting the email Store for the contact");
		        		
	         		  contactInfoStore.emailStore = getEmailByContactID(context,contactId);
	         		 
		        	  Log.d("ContactManager.findContact", "Going to get phone store for contact");
		        		
	         		  contactInfoStore.phoneNumberStore = getPhoneNumberByContactID(context,contactId);
	                
		        	  Log.d("ContactManager.findContact", "Going to get phone store for contact");
		        	  
	                  contactInfoArrayList.add(contactInfoStore);
	                  
	             } // a contact detail ends here
	        	
	        	Log.d("ContactManager.findContact", "All contacts returning\n");
	       	    resultContactTable.close();   
	            return contactInfoArrayList;
	        }// if ends here	
	        else{
	        	  Log.d("ContactManager.findContact", "No Contact Found\n");
	          	  messageString="No Contact Found In the ContactBook\n";
	          	  return null;
	        }
      	   
	     }
		 catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
		    String  debugString="\nContactManager.findContact: ";
			debugString+=ex.getMessage();
		    Log.d("ContactManager.findContact",debugString);
		   //  Toast.makeText(context,debugString,Toast.LENGTH_LONG).show();
		     messageString=debugString;
		     return null;   
		 
		 }
	        
	}
	
	
	/**
	 * 
	 * Extract the information for a given name 
	 * from the contact list.  
	 * 
	 *   * <p>The following information are extracted by this method</p>
	    * <ui>
	    *   <li>DisplayName</li>
	    *   <li>MobileNumber</li>
	    *   <li>MobileType</li>
	    *   <li>Email Address</li>
	    *   <li>Email Type </li>
	   
	 * @param  context  The application context of the Android activity from 
	 *  where this method is called. 
	 * 
	 * @param displayName   The name of the contact as displayed 
	 *                      in the contact list for which all the 
	 *                      information is required
	 *                      
	 * @return The object of ContactInformationStore where all the information are
	 *         stored.
	 *                 
	 */
	public ArrayList<ContactInformationStore> findContactByDisplayName(Context context,String displayName){
		/*
		 * Form the columns to be extracted
		 * 
		 * Query the contact container for the contacts with conditional check
		 * 
		 * traverse the result to extract information
		 * 
		 * store the information in object
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
        
		try{
			 /**
	         * Error message to be displayed if any error occurs
	         */
			YLogger Log = YLoggerFactory.getLogger();
	    
			/*
			  *  Column names that will be in output table after the
			  *  query to Contacts
			  */
			   
			   
			  String [] columnNames = {
					  ContactsContract.Contacts._ID,
					  ContactsContract.Contacts.DISPLAY_NAME,
						  
			    };
			 
	      	  Log.d("ContactManager.findContactByDisplayName","Columns Made");
			
	      	  // query String for selecting Contact ID from Stored Contacts in Memory
	          queryString = ContactsContract.Contacts.DISPLAY_NAME + "=?";
	          
	          
	      	  Log.d("ContactManager.findContactByDisplayName", "Going for query contacts");
	          
	      	  Cursor nameResult = queryProcessor( context,
	        		                               ContactsContract.Contacts.CONTENT_URI ,
	                                               columnNames,
	                                               queryString, 
	                                               new String[] {String.valueOf(displayName)},
	                                               null);
	          
	         
	      	  Log.d("ContactManager.findContactByDisplayName", "Query for contact over");
	          if(nameResult!=null && nameResult.getCount()>0)
	          {
	        
	      	    Log.d("ContactManager.findContactByDisplayName","At least a contact found\n");
	         	while(nameResult.moveToNext()){
	          	
	        
	           	     Log.d("ContactManager.findContactByDisplayName",
	           	    		 "Processing the contact table obtained\n");
	         		  /*
	         		   *  Entering the loop means there is a contact in contact list
	         		   *  for which Information is to be stored. 
	         		   *  Hence defining an object for ContactInforamtionStore
	         		   *  
	         		   */
	         		  
	           	  ContactInformationStore contactInfoStore = new ContactInformationStore();
	         		  
	         		
	         		  /*
	         		   *  Get the contact Id from the result obtained after query for 
	         		   *  all contacts present in Contact list. 
	         		   */
	          	    columnIndex = nameResult.getColumnIndex(ContactsContract.Contacts._ID);
	         		contactId = nameResult.getString(columnIndex);
	         		
	          	    Log.d("ContactManager.findContactByDisplayName", 
	          	    		"Contact Id for the contact obtained\n");
	         		  /*
	         		   * Extract the information of the contact for the Contact ID
	         		   */
	         		  
	         		contactInfoStore.fullName=displayName;    		  
	         		contactInfoStore.firstName=displayName;
	         		contactInfoStore.lastName="";
	        	     
	         	
	          	    Log.d("ContactManager.findContactByDisplayName",
	          	    		"Going to find email for contact\n");
	         		contactInfoStore.emailStore = getEmailByContactID(context,contactId);
	         		
	         		
	          	    Log.d("ContactManager.findContactByDisplayName", 
	          	    		"Going to find the phoneNumber for contact"); 
	        	    contactInfoStore.phoneNumberStore = getPhoneNumberByContactID(context,contactId);
	        
	          	    Log.d("ContactManager.findContactByDisplayName",
	          	    		"Adding contact in Store ArrayList"); 
	        	    contactInfoArrayList.add(contactInfoStore);
	                  
	             } // while  ends here
	         	 
	       	     Log.d("ContactManager.findContactByDisplayName",
	       	    		 "All contacts found so returning");
	         	 nameResult.close(); 
	             return contactInfoArrayList;
	             
	        }// if ends here i.e. all the contacts for that email address is stored	
	  
      	    Log.d("ContactManager.findContactByDisplayName","No contact for given Name\n");	        
      	    messageString="No contact for given Name\n";
      	    nameResult.close();   
            return null;
	     
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
		    
			String  debugString="ContactManager.findContactByDisplayName: ";
			 debugString+=ex.getMessage();
		     Log.d("ContactManager.findContactByDisplayName",debugString);
		 //    Toast.makeText(context,debugString,Toast.LENGTH_LONG).show();
		     messageString=debugString;
		     return null;   
		 }
	        
	}
		
	
	/**
	 * Extract the information for all the contacts in the contact list
	 * with the given email address.
	    * <p>The following information are extracted by this method</p>
	    * <ui>
	    *   <li>DisplayName</li>
	    *   <li>MobileNumber</li>
	    *   <li>MobileType</li>
	    *   <li>Email Address</li>
	    *   <li>Email Type </li>
	   </ui>
	 * @param  context  The application context of the Android activity from 
	 *  where this method is called. 
	 * 
	 * @param emailAddress  The email address of the contact for which all the 
	 *                      information is required
	 *                      
	 * @return The arraylist storing the objects of ContactInformationStore where
	 *  all the extracted information are stored
	 *  
	 *                 
	 */
	public ArrayList<ContactInformationStore> findContactByEmail(Context context,String emailAddress){
		/*
		 * Form the columns to be extracted
		 * 
		 * Query the contact container for  the contacts  by conditional check email
		 * 
		 * traverse the result to extract information
		 * 
		 * store the information in object
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
      
      try{
			 /*
			  *  Column names that will be in output table after the
			  *  query to CallLog 
			  */

  	    	YLogger Log = YLoggerFactory.getLogger();
  			   
			   
			  String [] columnNames = {
					  ContactsContract.CommonDataKinds.Email.CONTACT_ID,
					  ContactsContract.CommonDataKinds.Email.DATA1,
						  
			    };
			   
			    
	      	  Log.d("ContactManager.findContactByEmail", "Columns for Contact made");
	      	    
			  // query String for selecting Contact ID from Stored Email Table in Memory
	          queryString = ContactsContract.CommonDataKinds.Email.DATA1 + "=?";
	          
	          
	      	  Log.d("ContactManager.findContactByEmail", "Going for querying contact");	
	          
	      	  Cursor emailResult = queryProcessor( context,
	        		                               ContactsContract.CommonDataKinds.Email.CONTENT_URI ,
	                                               columnNames,
	                                               queryString, 
	                                               new String[] {String.valueOf(emailAddress)},
	                                               null);
	          
	       
	      	  Log.d("ContactManager.findContactByEmail", "Query Over for Contact");	
	        
	        if(emailResult!=null && emailResult.getCount()>0)
	        {
	       
		      	Log.d("ContactManager.findContactByEmail", "At least one contact found\n");	
		        
	        	while(emailResult.moveToNext())
	          	{
	         		  
	         		  /*
	         		   *  Entering the loop means there is a contact in contact list
	         		   *  for which Information is to be stored. 
	         		   *  Hence defining an object for ContactInforamtionStore
	         		   *  
	         		   */
	         		  
	        		ContactInformationStore contactInfoStore = new ContactInformationStore();
	         		  
	         		  
	         		  /*
	         		   *  Get the contact Id from the result obtained after query for 
	         		   *  all contacts present in Contact list. 
	         		   */
	          	      columnIndex = emailResult.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID);
	         		  contactId = emailResult.getString(columnIndex);
	         	
	   	      	      Log.d("ContactManager.findContactByEmail", "Getting contact id from the contact table\n");	
	   	        
	         		  /*
	         		   * Extract the information of the contact for the Contact ID
	         		   */
	         		  
	         		  contactInfoStore.fullName=getDisplayNameByContactID(context,contactId);    		  
	         		
	   	      	      Log.d("ContactManager.findContactByEmail", "Display Name from contactId obtained\n");	
	   	        
	         		  contactInfoStore.emailStore = getEmailByContactID(context,contactId);
	         	
	   	      	      Log.d("ContactManager.findContactByEmail", "Email from contactId obtained");	
	   	        
	         		  contactInfoStore.phoneNumberStore = getPhoneNumberByContactID(context,contactId);
	         	
	   	      	      Log.d("ContactManager.findContactByEmail", "Phone Number from contact ID obtained");	
	   	            
	                  contactInfoArrayList.add(contactInfoStore);
	            
		      	      Log.d("ContactManager.findContactByEmail", "Contact Stored in ArrayList");	
		        
	             } // while  ends here
	        	
	      	      Log.d("ContactManager.findContactByEmail", "All Contact Proccesd");	
	        
	         	 emailResult.close();   
	             return contactInfoArrayList;
	             
	        }// if ends here i.e. all the contacts for that email address is stored	
	    
    	    Log.d("ContactManager.findContactByEmail", "No contact Found");	
    	    messageString="No contact Found For this Email";
	        emailResult.close();   
            return null;
	     
		}catch(Exception ex){
			 String  debugString="ContactManager.findContactByEmail: ";
			 debugString+=ex.getMessage();
			 YLogger Log = YLoggerFactory.getLogger();
	  			
			 Log.d("ContactManager.findContactByEmail",debugString);
		//     Toast.makeText(context,debugString,Toast.LENGTH_LONG).show();
		     messageString=debugString;
		     return null;   
		 }
	        
	}
		
	
	/**
	 * Extract the information for all contacts in the contact list
	 * having a particular phone number that is given as paramter of this function.
	   * <p>The following information are extracted by this method</p>
	    * <ui>
	    *   <li>DisplayName</li>
	    *   <li>MobileNumber</li>
	    *   <li>MobileType</li>
	    *   <li>Email Address</li>
	    *   <li>Email Type </li>
	   </ui>
	   
	 * @param  context  The application context of the Android activity from 
	 *  where this method is called. 
	 *  
	 * @param phoneNumber  The phone number of the contact for which all the 
	 *                      information is required
	 *                      
	 * @return The ArrayList containing object of ContactInformationStore 
	 *          where all the information are
	 *         stored.
	 *                 
	 */
	public ArrayList<ContactInformationStore> findContactByNumber(Context context,
																	String phoneNumber){
		
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
         
		try{
			 /*
			  *  Column names that will be in output table after the
			  *  query to CallLog 
			  */
			YLogger Log = YLoggerFactory.getLogger();
		    	   			   
			  String [] columnNames = {
					  ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
					  ContactsContract.CommonDataKinds.Phone.NUMBER,
						  
			    };
		
			
			  Log.d("ContactManager.findContactByNumber","Column made");
			  
		 	    
			  // query String for selecting Contact ID from Stored Email Table in Memory
	          queryString = ContactsContract.CommonDataKinds.Phone.NUMBER + "=?";
	        
	         Cursor phoneResult = queryProcessor(  context,
	        		                               ContactsContract.CommonDataKinds.Phone.CONTENT_URI ,
	                                               columnNames,
	                                               queryString, 
	                                               new String[] {String.valueOf(phoneNumber)},
	                                               null);
	          
	      
	 	     Log.d("ContactManager.findContactByNumber","Query executed for contact id and phone");
	 	    
	        if(phoneResult!=null && phoneResult.getCount()>0)
	        {
	        
	    	     Log.d("ContactManager.findContactByNumber","at least one contact with phonenumber");
	    	    
	         	  while(phoneResult.moveToNext())
	          	{
	         	
	        	     Log.d("ContactManager.findContactByNumber","Processing a Contact");
	        	    
	         		  /*
	         		   *  Entering the loop means there is a contact in contact list
	         		   *  for which Information is to be stored. 
	         		   *  Hence defining an object for ContactInforamtionStore
	         		   *  
	         		   */
	         		  
	        	     ContactInformationStore contactInfoStore = new ContactInformationStore();
	         
	        	     
	         		  /*
	         		   *  Get the contact Id from the result obtained after query for 
	         		   *  all contacts present in Contact list. 
	         		   */
	          	      columnIndex = phoneResult.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
	         		  contactId = phoneResult.getString(columnIndex);
	         		  
	         	
	        	     Log.d("ContactManager.findContactByNumber",
	        	    		 "contact ID for phone number obtained");
	        	     
	         		  /*
	         		   * Extract the information of the contact for the Contact ID
	         		   */
	        	  
	        	     Log.d("ContactManager.findContactByNumber",
	        	    		 "going to call display name by contact id");
	        	    
	         		 contactInfoStore.fullName=getDisplayNameByContactID(context,contactId);    		  

	        	     Log.d("ContactManager.findContactByNumber","going to call email by contact id");
	         		  
	        	     contactInfoStore.emailStore = getEmailByContactID(context,contactId);
	         		  
	        	     Log.d("ContactManager.findContactByNumber","Going to call phoneNumberByContact ID"); 
	         		  
	         		 contactInfoStore.phoneNumberStore = getPhoneNumberByContactID(context,contactId);
	         		 
	        	     Log.d("ContactManager.findContactByNumber","going to add contact in store arraylist");
	        	     
	                 contactInfoArrayList.add(contactInfoStore);
	                  
	             } // while  ends here
	         	  
	         	
        	     Log.d("ContactManager.findContactByNumber","All contact over returning");
	         	 phoneResult.close();   
	             return contactInfoArrayList;
	             
	        }// if ends here i.e. all the contacts for that email address is stored	
	       
   	        Log.d("ContactManager.findContactByNumber","No contact found");
	        phoneResult.close(); 
	        messageString="No contact found for this Number";
	        return null;
	     
		}catch(Exception ex){
			String debugString="ContactManager.findContactByNumber: ";
			debugString+=ex.getMessage();
			YLogger Log = YLoggerFactory.getLogger();		    
			Log.d("ContactManager.findContactByNumber",debugString); 
		//	  Toast.makeText(context,debugString,Toast.LENGTH_LONG).show();
			messageString=debugString;
		    return null;   
		 }
	}
}

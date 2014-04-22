/**
 * 
 * This package contains classes used to
 * display the GUI for a Contact Processor.
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.ContactManager;

import java.util.ArrayList;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.ContactInformationStore;
import fr.inria.arles.yarta.middleware.msemanagement.ContactManager;

import fr.inria.arles.yarta.middleware.msemanagement.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * Activity to display all contact stores in the android phone contact list.
 * It is started when the option "Display All Contact" is clicked
 * on the ContactManager home activity.
 * <p>It starts a thread, calls the ContactManager.findContact
 * of msemanagement package to extract all contact and then displays all contact 
 * stored in the contact list. 
 * </p>
 * @author Nishant Kumar
 *
 */
public class FindAllContact extends Activity {

	/**
	 * The arraylist that stores the result returned by the call of 
	 * ContactManager.findContact function. It is used to 
	 * display the contacts extracted from the android phone.
	 * 
	 */
	ArrayList<ContactInformationStore> contactInfoArrayStore;
	/**
	 * The progress wheel that is displayed while a thread is in execution.
	 */
	ProgressDialog progressWheel;
	/**
	 * The array adapter that is used to display the result in list view.
	 */
	
	ArrayAdapter<String> adapter;
/**
 * The thread that runs to make a call for extraction of all contact
 *  and then forming the array adpater for displaing the result in the listview
 */
	Thread backgroundThread;
	
	/**
	 * The message string that stores any error message
	 */
	String messageString;
	/**
	 * The constant int used by the thread handler to 
	 * transfer the control after the thread finish its execution
	 */
	int DISPLAY_MESSAGE;
	int DISPLAY_CONTACT;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.allcontact);
			
			contactInfoArrayStore = null;
			progressWheel = null;
			adapter = null;
			messageString = "";
			DISPLAY_MESSAGE =0;
			DISPLAY_CONTACT = 1;
	   
			try{
			progressWheel = 
				ProgressDialog.show( this, "" , "Extracting All Contact ... ", true,true);
			
			/**
			 * The work thread that calls the function to start the execution of 
			 * contact extraction from the phone.
			 * The message handler is used to get the response back from it.
			 * 
			 */
			backgroundThread = new Thread(new Runnable()
	        {
	               public void run()
	                {
	             
	                	boolean isContactFound = startExecution();
	           
	                	Message message = threadHandler.obtainMessage();
                            
	                    if(!isContactFound)
	                       	message.what=DISPLAY_MESSAGE;
	                    else
	                       	message.what=DISPLAY_CONTACT;
	                        
	                    threadHandler.sendMessage(message);

	                }
 	        });
          
			
			backgroundThread.start();
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			String debug = "Inside FindAllContact.onCreate: ";
			Log.d("FindAllContact.onCreate",debug+ex.toString());
		    messageString = debug+ex.toString();
		    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
	 		displayContactMessage.setText(messageString);
	 		progressWheel.dismiss();
		    return;
		}
	      
	}
	
	/**
	 * The thread handler that receives messasge from the 
	 * thread after the thread complets the extraction of contacts.
	 * The message received is used to display the result.
	 */
	Handler threadHandler = new Handler()
    {
            @Override public void handleMessage(Message message)
            {
             	if(message.what==DISPLAY_MESSAGE){
             		TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
             		displayContactMessage.setText(messageString);
                  
            	}else if(message.what==DISPLAY_CONTACT)
            		displayResult(); 
                
                progressWheel.dismiss();
            }
    };

	/**
	 * Called by the work thread to extract all contacts from the
	 * Android phone.
	 * <p>It calls the ContactManager.findContact of the msemanagement
	 * package and gets the result from it.</p><p>
	 * Then the obtained result is checked and the array adapter is formed.
	 * 
	 * </p>
	 * 
	 * 
	 * @return returns true if the extraction of contact is sucessful, otherwise
	 * retuns false if no contact is found or any error takes place. 
	 */
    private boolean startExecution(){
		
		try{
			 
				YLogger Log = YLoggerFactory.getLogger();
		        String debug = " Inside FindAllContact ";
				Log.d("FindAllContact.onCreate",debug);
	
				ContactManager contactManager = new ContactManager();
				contactInfoArrayStore = contactManager.findContact(getApplicationContext());
				
				if(contactInfoArrayStore==null ||contactInfoArrayStore.size()<=0){
					Log.d("FindAllContact.onCreate"," No Contact found");
					
					messageString = contactManager.messageString;
					return false;
				
				}
				else if(contactInfoArrayStore!=null && contactInfoArrayStore.size()>0){
					
					String[] allDisplayName = new String[contactInfoArrayStore.size()];
					
					for(int k = 0;k<contactInfoArrayStore.size();k++)
						allDisplayName[k] = contactInfoArrayStore.get(k).fullName;
					
					
					debug = "Forming arrayAdapter for DisplayAll Contact ListView";
					Log.d("FindAllContact.onCreate",debug);
					
					
					try{
					adapter = new ArrayAdapter<String>(getApplicationContext(),
														R.layout.contactmanagerhome_list_text,
														allDisplayName);
					return true;
					
					}catch(Exception ex){
	
						debug = "In FindAllContact.onCreate ArrayAdapter For Display notformed";
						Log.d("FindAllContact.onCreate",debug);
	                    messageString = debug+ex.toString();					
					    return false;
					}
		
				}
				debug = "All Contact DataExtraction Over";
				Log.d("FindAllContact.onCreate",debug);
                messageString = "FindAllContact.onCreate: contactArraInformationExtracted is"+
                                 "neither null not size greter than 0";
                
				return false;
						
			}catch(Exception ex){
				YLogger Log = YLoggerFactory.getLogger();
				String debug = "Inside FindAllContactGUI:  ";
				Log.d("FindAllContact.onCreate",debug+ex.toString());
			//	Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();
                  messageString = debug+ex.toString();
                  return false;
			}

		
	}


	
	
    /**
     *  Used to display the result obtained by the extraction of 
     *  contact. It displays the array adapter formed in the startexecution method 
     *  of this class.
     *  
     */
    private void displayResult(){
    	try{
    		ListView list = (ListView)findViewById(R.id.displayAllContact);

    		String	debug = "Setting FindAllContact ListView";
    		YLogger Log = YLoggerFactory.getLogger();
    		 
    		Log.d("GUI:FindAllContact.displayResult",debug);
			if(adapter!=null){		
				list.setAdapter(adapter);
				
				debug = "All Contact ListView Displayed";
				Log.d("GUI:FindAllContact.displayResult",debug);
				
				list.setOnItemClickListener(new OnItemClickListener() {
					
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				     
					
				    	YLogger Log = YLoggerFactory.getLogger();
				      	String debug = "All Contact display Clicked at "+position;
						Log.d("GUI:FindAllContact.onItemClick",debug);
						
						debug = " Going to call remove diagol ";
						Log.d("GUI:FindAllContact.onItemClick",debug);
						
						removeDialog(position);
						debug = " Going to call show diagol ";
						Log.d("GUI:FindAllContact.onItemClick",debug);
						
						showDialog(position);
						debug = " After shown dialog ";
						Log.d("GUI:FindAllContact.onItemClick",debug);
						
						
					}
			  });					
			}else{
				debug = "In DisplayResult for All ontact,Adaptor Not set ";
				Log.d("GUI:FindAllContact.displayResult",debug);
			    messageString = debug;
			    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
	     		displayContactMessage.setText(messageString);
	            return; 	
			}
			
    	}catch(Exception ex){
    		YLogger Log = YLoggerFactory.getLogger();
			String debug = "Inside FindAllContact.displayResult:  ";
			Log.d("GUI:FindAllContact.displayResult",debug+ex.toString());
		    messageString = debug+ex.toString();
		    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
     		displayContactMessage.setText(messageString);
            return;
    	}

    }
	
	/**
	 * The dialog box shown when a user clicks on any contact displayed. 
	 * It displays the information about that particular user.
	 */
	  protected Dialog onCreateDialog(int position){
		
		  if(contactInfoArrayStore==null)
			  return null;
		  YLogger Log = YLoggerFactory.getLogger();
	        		  
 			String debug = " IN Create diagol ";
			Log.d("FindAllContact.onCreateDialog",debug);

			ContactInformationStore contactInfo = new ContactInformationStore();
			contactInfo = contactInfoArrayStore.get(position);
 			debug = " Object fetched for position "+position;
			Log.d("FindAllContact.onCreateDialog",debug);
 			debug = " getting context ";
			Log.d("FindAllContact.onCreateDialog",debug);

			Dialog dialog = new Dialog(this);
			debug = " getting dialog ";
			Log.d("FindAllContact.onCreateDialog",debug);

			dialog.setContentView(R.layout.contactcustomdialog);
			debug = " Setting title ";
			Log.d("FindAllContact.onCreateDialog",debug);

			dialog.setTitle("Contact Detail");

			debug = " Title set";
			Log.d("FindAllContact.onCreateDialog",debug);

			debug = " finding text View for name";
			Log.d("FindAllContact.onCreateDialog",debug);

			TextView textName = (TextView) dialog.findViewById(R.id.customDialogNameTop);
			textName.setText(contactInfo.fullName);
			
			debug = " finding text view for phone ";
			Log.d("FindAllContact.onCreateDialog",debug);
			
			TextView textPhoneLabel = (TextView) dialog.findViewById(R.id.customDialogPhoneTop);
			TextView textPhoneNumber = (TextView) dialog.findViewById(R.id.customDialogPhoneBottom);
			
			
			if(contactInfo.phoneNumberStore!=null){
				debug = " Setting phone number ";
				Log.d("FindContactByEmail.onCreateDialog",debug);
				String toDisplayNumber="";
				for(int j=0;j<contactInfo.phoneNumberStore.length;j++){
					toDisplayNumber+=contactInfo.phoneNumberStore[j].phoneNumberType+
					                ":"+ contactInfo.phoneNumberStore[j].phoneNumber+
					                "\n";
				}
				textPhoneLabel.setText("PhoneNumber:");
				textPhoneNumber.setText(toDisplayNumber);
			}else{
				textPhoneLabel.setText("Phone Number");
				textPhoneNumber.setText("No Number");
				
			}
			debug = " finding text View for email ";
			Log.d("FindContactByEmail.onCreateDialog",debug);
			                                                      					
			TextView textEmailLabel = (TextView) dialog.findViewById(R.id.customDialogEmailTop);
			TextView textEmail = (TextView) dialog.findViewById(R.id.customDialogEmailBottom);
			                                                       
			if(contactInfo.emailStore!=null){
				debug = " Setting email Address ";
				Log.d("FindContactByEmail.onCreateDialog",debug);
				String toDisplayEmail="";
				for(int j=0;j<contactInfo.emailStore.length;j++){
					toDisplayEmail+=contactInfo.emailStore[j].emailType+
					                ":"+ contactInfo.emailStore[j].emailAddress+
					                "\n";
				}
				
				textEmailLabel.setText("Email:");
				textEmail.setText(toDisplayEmail);
			}else{
				textEmailLabel.setText("Email Address");
				textEmail.setText("No Email");
				
			}
			
			debug = "returning dialog ";
			Log.d("FindAllContact.onCreateDialog",debug);

			return dialog;
	  }

}

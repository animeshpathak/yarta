/**
 *  * This package contains classes needed for displaying the GUI for 
 * extraction of information from the SMS store of the 
 * Android phone.
 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.SMSManager;

import java.util.ArrayList;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.R;
import fr.inria.arles.yarta.middleware.msemanagement.SMSInformationStore;
import fr.inria.arles.yarta.middleware.msemanagement.SMSManager;
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
 An Android activity  to display all inbox SMS.
 
 * <p>The  sms  are extracted by SMSManager.extractAllInboxSMS of 
 * the msemanagement package. </p>
 * 
 * @author Nishant Kumar
 *
 */
public class DisplayInboxSMS extends Activity {

	/**
	 *  The array list used to receive the result returned by the 
	 *  function called to extract sms.
	 *  
	 */
	ArrayList<SMSInformationStore> smsInfoArrayStore;

	/**
	 * The array adapter to be displayed in the listview
	 */
	ArrayAdapter<String> adapter;
	/**
	 * The message string storing any error message.
	 */
	String messageString;
	/**
	 * The progress wheel displayed during the extraction of friend
	 */
	ProgressDialog progressWheel;
	/**
	 * The background thread to start the execution of extraction of friend. 
	 */
	Thread backgroundThread;
	
	/**
	 * The constant used by the message handler to transfer the control after the 
	 * thread ends.
	 * 
	 */
	int  DISPLAY_MESSAGE;
	int DISPLAY_CONTACT;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.allcontact);
			
			smsInfoArrayStore = null;
			adapter = null;
			messageString = "";
			 progressWheel = null;
			 DISPLAY_MESSAGE =0;
			 DISPLAY_CONTACT = 1;
			
			try{
				
				progressWheel = 
					ProgressDialog.show( this, "" , " Please wait ... ", true,true);
			
				/**
				 * The background thread that calls the startExecution method of this 
				 * class.
				 * The startExecution method then starts the steps to extract sms.
				 * A message handler receives the message after the thread completes the 
				 * execution 
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
	    		String debug = "Inside DisplayInboxSMS.startExecution: ";
	    		Log.d("DisplayInboxSMS.OnCreate",debug+ex.toString());
	    	    messageString = debug+ex.toString();
	    	    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
	     		displayContactMessage.setText(messageString);
	          
	    	    progressWheel.dismiss();
	    	    return;
			}
		

	}
	

	/**
	 * The thread handler is used to receive the message after the executing thread
	 * ends. It transfer the control to display the extracted result.
	 * 
	 * 
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
     * 
	 * Called by the thread in execution to start the steps for data extraction.
	 * This function calls the SMSManager.extractAllInboxSMS of msemanagement
	 * package to extract the sms from the inbox box. If the extraction of 
	 * sms is successful, then the array adapter is formed that will be used to 
	 * display the result in listview.
	 *  
	 * 
	 * @return returns true if the sms are successfully extracted and 
	 * the array adapter
	 * for the list view is formed, else false.
	 * 
	 */
	
	private boolean startExecution(){
		
		try{
			YLogger Log = YLoggerFactory.getLogger();
			String debug = " Inside DisplayInboxSMS ";
			Log.d("DisplayInboxSMS.startExecution",debug);

		
			SMSManager smsManager = new SMSManager();
		
			smsInfoArrayStore = smsManager.extractAllInboxSMS(getApplicationContext());
		
			if(smsInfoArrayStore==null ||smsInfoArrayStore.size()<=0){
				Log.d("DisplayInboxSMS.startExecution",
						" No InboxSMS found");
				//TextView displayError = (TextView)findViewById(R.id.displayAllContactMessage);
				//displayError.setText("No InboxSMS Found" );
				
				messageString = smsManager.messageString;
				return false;
			}
		
			if(smsInfoArrayStore!=null && smsInfoArrayStore.size()>0){
			
				String[] allDisplayName = new String[smsInfoArrayStore.size()];
			
				debug = "Forming List from findViewById";
				Log.d("DisplayInboxSMS.startExecution",debug);
			
				for(int k = 0;k<smsInfoArrayStore.size();k++)
					allDisplayName[k] = smsInfoArrayStore.get(k).fullName;
			
			
				try{
					adapter = new ArrayAdapter<String>(this,
												R.layout.contactmanagerhome_list_text,
												allDisplayName);
			

					debug = "Setting FindAllContact ListView";
					Log.d("DisplayInboxSMS.startExecution",debug);
				    return true;
				    
				}catch(Exception ex){
					debug = "Array For Names cannot be formed";
					Log.d("DisplayInboxSMS.startExecution",debug);
					messageString = "Cannot Create ArrayAdapter for Display";
					return false;
				}
			
		
			}
			messageString = "No recognizable data obtained";
			return false;
			
		}catch(Exception ex){
			String debug = "Array For Names cannot be formed";
			YLogger Log = YLoggerFactory.getLogger();
			
			Log.d("DisplayInboxSMS.startExecution",debug);
			messageString = "DisplayInboxSMS.startExecution "+ex.toString();
			return false;
		}
	
	}
    
	/**
	 * This function is called by the thread handler after
	 * the sms are extracted successfully. This function displays the 
	 * result in the listview.  
	 */
	private void displayResult(){
		try{
    		ListView list = (ListView)findViewById(R.id.displayAllContact);

    		String	debug = "Setting DisplayAllSMSFriend ListView";
    		YLogger Log = YLoggerFactory.getLogger();
    		Log.d("DisplayInboxSMS.displayResult",debug);
			
    		
    		if(adapter!=null){		
				list.setAdapter(adapter);
				
				debug = "All Contact ListView Displayed";
				Log.d("DisplayInboxSMS.displayResult",debug);
				
				list.setOnItemClickListener(new OnItemClickListener() {
					
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				     
					
				    	YLogger Log = YLoggerFactory.getLogger();
				      	String debug = "All Calllog display Clicked at "+position;
						Log.d("DisplayInboxSMS.onClickListener",debug);
						
						debug = " Going to call remove diagol ";
						Log.d("DisplayInboxSMS.onClickListener",debug);
						
						removeDialog(position);
						debug = " Going to call show diagol ";
						Log.d("DisplayInboxSMS.onClickListener",debug);
						
						showDialog(position);
						debug = " After shown dialog ";
						Log.d("DisplayInboxSMS.onClickListener",debug);
						
						
					}
			  });					
			}else{
				debug = "In DisplayResult for Inbox SMS,Adaptor Not set ";
				Log.d("DisplayInboxSMS.displayResult",debug);
			    messageString = debug;
			    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
	     		displayContactMessage.setText(messageString);
	             return; 	
			}
			
    	}catch(Exception ex){
    		YLogger Log = YLoggerFactory.getLogger();
			String debug = "Inside DisplayInboxSMS.displayResult:  ";
			Log.d("DisplayInboxSMS.displayResult",debug+ex.toString());
		    messageString = debug+ex.toString();
		    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
     		displayContactMessage.setText(messageString);
            return;
    	}
	}
	
	
	/**
	 * The dialog box formed when the user selects any contact
	 * displayed in the result.
	 * 
	 */
	  protected Dialog onCreateDialog(int position){
		
		  if(smsInfoArrayStore==null)
			  return null;
		  YLogger Log = YLoggerFactory.getLogger();
	        		  
 			String debug = " IN Create diagol ";
			Log.d("DisplayInboxSMS.onCreateDialog",debug);

			SMSInformationStore smsInfo = new SMSInformationStore();
			smsInfo = smsInfoArrayStore.get(position);
 			debug = " SMS Object fetched for position "+position;
			Log.d("DisplayInboxSMS.onCreateDialog",debug);
 		
			Dialog dialog = new Dialog(this);
			debug = " getting dialog ";
			Log.d("DisplayInboxSMS.onCreateDialog",debug);

			dialog.setContentView(R.layout.contactcustomdialog);
			debug = " Setting title ";
			Log.d("DisplayInboxSMS.onCreateDialog",debug);

			dialog.setTitle("Contact Detail");

			debug = " Title set";
			Log.d("DisplayInboxSMS.onCreateDialog",debug);

			debug = " finding text View for name";
			Log.d("DisplayInboxSMS.onCreateDialog",debug);

			TextView textName = (TextView) dialog.findViewById(R.id.customDialogNameTop);
			textName.setText(smsInfo.fullName);
			
			debug = " finding text view for phone ";
			Log.d("DisplayInboxSMS.onCreateDialog",debug);
			
			TextView textPhoneLabel = (TextView) dialog.findViewById(R.id.customDialogPhoneTop);
			TextView textPhoneNumber = (TextView) dialog.findViewById(R.id.customDialogPhoneBottom);
			
	
			if(smsInfo.smsNumber!=null){
				debug = " Setting phone number ";
				Log.d("DisplayInboxSMS.onCreateDialog",debug);
				
				textPhoneLabel.setText("SMS Recieved From");
				textPhoneNumber.setText(smsInfo.smsNumber);
			}else{
				textPhoneLabel.setText("Phone Number");
				textPhoneNumber.setText("No Number");
				
			}
			debug = " finding text View for email ";
			Log.d("DisplayInboxSMS.onCreateDialog",debug);
			                                                      					
			TextView textEmailLabel = (TextView) dialog.findViewById(R.id.customDialogEmailTop);
			TextView textEmail = (TextView) dialog.findViewById(R.id.customDialogEmailBottom);
			                                                       
			if(smsInfo.emailStore!=null){
				debug = " Setting email Address ";
				Log.d("DisplayInboxSMS.onCreateDialog",debug);
				
				debug = " email Address is"+smsInfo.emailStore[0].emailAddress;
				Log.d("DisplayInboxSMS.onCreateDialog",debug);
			
				textEmailLabel.setText(smsInfo.emailStore[0].emailType);
				textEmail.setText(smsInfo.emailStore[0].emailAddress);
			}else{
				textEmailLabel.setText("Email Address");
				textEmail.setText("No Email");
				
			}
			debug = "returning dialog ";
			Log.d("DisplayInboxSMS.onCreateDialog",debug);

			return dialog;
	  }

	
}

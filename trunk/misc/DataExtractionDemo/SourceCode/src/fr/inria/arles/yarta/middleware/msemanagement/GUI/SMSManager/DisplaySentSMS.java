/**
 *  * This package has all classes needed for displaying the GUI for 
 * extraction of information from the SMS store of the 
 * Android phone.
 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.SMSManager;

import java.util.ArrayList;

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
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.R;
import fr.inria.arles.yarta.middleware.msemanagement.SMSInformationStore;
import fr.inria.arles.yarta.middleware.msemanagement.SMSManager;

/**
 An Android activity. 
 * to display all sent SMS.
 * 
 * <p>The  sms  are extracted by SMSManager.extractAllSentSMS of 
 * the msemanagement package. </p>
 * 
 * @author Nishant Kumar
 *
 */
public class DisplaySentSMS extends Activity{
	
	/**
	 *  The array list used to receive the result returned by the 
	 *  function called to extract sms.
	 *   
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
				 * The startExecution method then starts the steps to extract sent sms.
				 * A message handler receives the message after the thread completes the 
				 * execution 
				 * 
				 */
					backgroundThread = new Thread(new Runnable()
			        {
			           public void run(){
				             
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
		    	String debug = "DisplaySentSMS.onCreate: ";
		    	Log.d("DisplaySentSMS.onCreate",debug+ex.toString());
		        messageString = debug+ex.toString();
		        TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
		    	displayContactMessage.setText(messageString);
		        progressWheel.dismiss();
		        return;
			}


	}

	/**
     * 
	 * Called by the thread in execution to start the steps for extraction.
	 * This function calls the SMSManager.extractAllSentSMS of msemanagement
	 * package to extract sent sms. If the extraction of 
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
			String debug = " Inside DisplaySentSMS ";
			Log.d("DisplaySentSMS.startExecution",debug);

		
			SMSManager smsManager = new SMSManager();
			smsInfoArrayStore = smsManager.extractAllSentSMS(getApplicationContext());
		
			if(smsInfoArrayStore==null ||smsInfoArrayStore.size()<=0){
				Log.d("DisplaySentSMS.startExecution"," No SentSMS found");
				messageString = smsManager.messageString;
				return false;
			}
		
			if(smsInfoArrayStore!=null && smsInfoArrayStore.size()>0){
			
				String[] allDisplayName = new String[smsInfoArrayStore.size()];
			
				debug = "Forming List from findViewById";
				Log.d("DisplaySentSMS.startExecution",debug);
			
				for(int k = 0;k<smsInfoArrayStore.size();k++)
					allDisplayName[k] = smsInfoArrayStore.get(k).fullName;
			
			
				debug = "Forming arrayAdapter for DisplaySentSMS Contact ListView";
				Log.d("DisplaySentSMS.startExecution",debug);
			
				try{
					adapter = new ArrayAdapter<String>(this,
														R.layout.contactmanagerhome_list_text,
														allDisplayName);

					debug = "Setting FindAllContact ListView";
					Log.d("DisplaySentSMS.startExecution",debug);
		
			        return true;
				}catch(Exception ex){
					debug = "Array For Names cannot be formed";
					Log.d("DisplaySentSMS.startExecution",debug);
					messageString = "DisplaySentSMS.startExecution: ArrayAdapter not formed";
					return false;
				}
			
			}
			
			debug = "Sent SMS arrayAdapter not formed";
			Log.d("DisplaySentSMS.startExecution",debug);
			messageString = "DisplaySentSMS.startExecution: ArrayAdapter not formed";
			return false;
			
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			String debug = "DisplaySentSMS: ";
			Log.d("DisplaySentSMS.startExecution",debug+ex.toString());
			messageString= debug+ex.toString();
			return false;
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
	 * Called by the thread handler after
	 * the sms are extracted successfully. This function displays the 
	 * result in the listview. 
	 *  
	 */
	private void displayResult(){
		try{
    		ListView list = (ListView)findViewById(R.id.displayAllContact);

    		String	debug = "Setting DisplaySentSMS ListView";
    		YLogger Log = YLoggerFactory.getLogger();
    		Log.d("DisplaySentSMS.displayResult",debug);
			
    		
    		if(adapter!=null){		
				list.setAdapter(adapter);
				
				debug = "All Contact ListView Displayed";
				Log.d("DisplaySentSMS.displayResult",debug);
				
				list.setOnItemClickListener(new OnItemClickListener() {
					
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				     
					
				    	YLogger Log = YLoggerFactory.getLogger();
				      	String debug = "All DisplaySentSMS Clicked at "+position;
						Log.d("DisplaySentSMS.onClickListener",debug);
						
						debug = " Going to call remove diagol ";
						Log.d("DisplaySentSMS.onClickListener",debug);
						
						removeDialog(position);
						debug = " Going to call show diagol ";
						Log.d("DisplaySentSMS.onClickListener",debug);
						
						showDialog(position);
						debug = " After shown dialog ";
						Log.d("DisplaySentSMS.onClickListener",debug);
						
						
					}
			  });					
			}else{
				debug = "In DisplayResult for DisplaySentSMS,Adaptor Not set ";
				Log.d("DisplayAllSMSFriend.displayResult",debug);
			    messageString = debug;
			    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
	     		displayContactMessage.setText(messageString);
	             return; 	
			}
			
    	}catch(Exception ex){
    		YLogger Log = YLoggerFactory.getLogger();
			String debug = "CATCH: Inside DisplaySentSMS.displayResult:  ";
			Log.d("DisplaySentSMS.displayResult",debug+ex.toString());
		    messageString = debug+ex.toString();
		    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
     		displayContactMessage.setText(messageString);
            return;
    	}
	}
	
	/**
	 * The dialog box formed when the user selects any sms
	 * displayed in the result.
	 * 
	 */
	  protected Dialog onCreateDialog(int position){
		
		  if(smsInfoArrayStore==null)
			  return null;
		  YLogger Log = YLoggerFactory.getLogger();
	        		  
 			String debug = " IN Create diagol ";
			Log.d("DisplaySentSMS.onCreateDialog",debug);

			SMSInformationStore smsInfo = new SMSInformationStore();
			smsInfo = smsInfoArrayStore.get(position);
 			debug = " SMS Object fetched for position "+position;
			Log.d("DisplaySentSMS.onCreateDialog",debug);
 		
			Dialog dialog = new Dialog(this);
			debug = " getting dialog ";
			Log.d("DisplaySentSMS.onCreateDialog",debug);

			dialog.setContentView(R.layout.contactcustomdialog);
			debug = " Setting title ";
			Log.d("DisplaySentSMS.onCreateDialog",debug);

			dialog.setTitle("Contact Detail");

			debug = " Title set";
			Log.d("DisplaySentSMS.onCreateDialog",debug);

			debug = " finding text View for name";
			Log.d("DisplaySentSMS.onCreateDialog",debug);

			TextView textName = (TextView) dialog.findViewById(R.id.customDialogNameTop);
			textName.setText(smsInfo.fullName);
			
			debug = " finding text view for phone ";
			Log.d("DisplaySentSMS.onCreateDialog",debug);
			
			TextView textPhoneLabel = (TextView) dialog.findViewById(R.id.customDialogPhoneTop);
			TextView textPhoneNumber = (TextView) dialog.findViewById(R.id.customDialogPhoneBottom);
			
	
			if(smsInfo.smsNumber!=null){
				debug = " Setting phone number ";
				Log.d("DisplaySentSMS.onCreateDialog",debug);
				
				textPhoneLabel.setText("SMS Sent To");
				textPhoneNumber.setText(smsInfo.smsNumber);
			}else{
				textPhoneLabel.setText("Phone Number");
				textPhoneNumber.setText("No Number");
				
			}
			debug = " finding text View for email ";
			Log.d("DisplaySentSMS.onCreateDialog",debug);
			                                                      					
			TextView textEmailLabel = (TextView) dialog.findViewById(R.id.customDialogEmailTop);
			TextView textEmail = (TextView) dialog.findViewById(R.id.customDialogEmailBottom);
			                                                       
			if(smsInfo.emailStore!=null){
				debug = " Setting email Address ";
				Log.d("DisplaySentSMS.onCreateDialog",debug);
				
				debug = " email Address is"+smsInfo.emailStore[0].emailAddress;
				Log.d("DisplaySentSMS.onCreateDialog",debug);
			
				textEmailLabel.setText(smsInfo.emailStore[0].emailType);
				textEmail.setText(smsInfo.emailStore[0].emailAddress);
			}else{
				textEmailLabel.setText("Email Address");
				textEmail.setText("No Email");
				
			}
			debug = "returning dialog ";
			Log.d("DisplaySentSMS.onCreateDialog",debug);

			return dialog;
	  }


}

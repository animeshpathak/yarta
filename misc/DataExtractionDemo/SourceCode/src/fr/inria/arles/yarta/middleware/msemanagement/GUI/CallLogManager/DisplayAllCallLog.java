/**
 *  This package contains classes for the GUI for 
 * Callog extraction
 *
 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.CallLogManager;

import java.util.ArrayList;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.CallLogInformationStore;
import fr.inria.arles.yarta.middleware.msemanagement.CallLogManager;

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
 * Activity to extract all call history of the user. 
 * <ui>
 * <li>It is started after the user selects Display All CallLog 
 *     option on the CallLog Home Screen 
 * </li> 
 * <li>The extraction of call log information is done by CallLogManager.getCallHistory
 *     function of msemanagement package.
 * </li>
 * <li>The CallLogManager.getCallHistory is called in a separate work thread</li>
 * <li>The result are displayed after the CallLOgManager.getCallHistory 
 *     returns the extracted information</li>
 * </ui>
 * 
 * @author Nishant Kumar
 *
 */
public class DisplayAllCallLog extends Activity {


	/**
	 *  To store the result returned 
	 *  after the extraction of the call log .
	 *  
	 *  It has objects of CallLogInformationStore
	 *  class. An object represents the  detail of an entry in  call log. 
	 *  
	 *  
	 *  
	 */
	ArrayList<CallLogInformationStore> callLogInfoArrayStore;
	
	/**
	 * The array adapter that stores the result to be displayed in the 
	 * listview.
	 */
	ArrayAdapter<String> adapter;
	
	/**
	 * The messageString that stores any error message
	 * 
	 */
	String messageString;
	
	/**
	 * The progress wheel displayed when the thread is executing.
	 */
	
	ProgressDialog progressWheel;
	
	/**
	 * The background thread where the extraction of 
	 * call log takes place.
	 */
	Thread backgroundThread;
	
	/**
	 * The constants used to notify the 
	 * thread handler to start a specific job
	 */
	int  DISPLAY_MESSAGE;
	
	/**
	 * The constants used to notify the 
	 * thread handler to start a specific job
	 */
	int DISPLAY_CONTACT;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
			
		super.onCreate(savedInstanceState);
		setContentView(R.layout.allcontact);
		callLogInfoArrayStore = null;
		adapter = null;
		messageString = "";
		progressWheel = null;
		DISPLAY_MESSAGE =0;
		DISPLAY_CONTACT = 1;
		
		
		try{
			
			progressWheel = 
				ProgressDialog.show( this, "" , " Extracting All CallLog Details... ", true,true);
			
			/**
			 *  Calls the startExecution method of this class.
			 *  <p>After the startExecution function ends, the thread sets a message
			 *  depending on the value returned by start execution method.
			 *  </p>
			 *  <p>The thread then passes the message to thread handler.
			 * </p>
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
    		String debug = "CATCH: Inside DisplayAllCallLog.startExecution: ";
    		Log.d("DisplayAllCallLog.onCreate",debug+ex.toString());
    	    messageString = debug+ex.toString();
    	    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
     		displayContactMessage.setText(messageString);
          
    	    progressWheel.dismiss();
    	    return;
		}


	}
/**
 *  This handler handles the response returned by the thread.
 *  The thread informs this handler after the extraction of all call
 *  log is done.
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
 * Called by the work thread. It calls
 * the CallLogManager.getCallHistory function of the msemanagement package.
 * <p>
 * If the result is obtained , then the array adapter to be displayed in the 
 * listview is formed. If all goes successful, this function returns true to the
 * thread from where it was called. 
 * </p>
 * <p>
 * The thread then passes a message to the thread handler to display the result
 * by using the array adapter formed.
 * 
 * </p>
 * @return true if the extraction of all call log is successfull and then 
 *         array adapter to be displaed in listview is formed.
 *         
 *         
 */
    private boolean startExecution(){
    
    	try{
    		YLogger Log = YLoggerFactory.getLogger();
    		String debug = " Inside DisplayAllCallLog.startExecution ";
    		Log.d("DisplayAllCallLog.startExecution",debug);

		
    		CallLogManager callLogManager = new CallLogManager();
		
    		callLogInfoArrayStore = callLogManager.getCallHistory(getApplicationContext());
		
    		if(callLogInfoArrayStore==null ||callLogInfoArrayStore.size()<=0){
    			Log.d("DisplayAllCallLog.startExecution",
    			" No call Log found");
    			
                messageString = callLogManager.messageString+"\n"+
                					"1.Check your CallLog record\n"+
                					"2.Enter the Extract Search value(if any)\n"+
                					"3.View your Log for complete Information";
    			return false;
    		}
		
    		if(callLogInfoArrayStore!=null && callLogInfoArrayStore.size()>0){
			
    			String[] allDisplayName = new String[callLogInfoArrayStore.size()];
			
    			debug = "Forming List from findViewById";
    			Log.d("DisplayAllCallLog.startExecution",debug);
			
    			for(int k = 0;k<callLogInfoArrayStore.size();k++)
    				allDisplayName[k] = callLogInfoArrayStore.get(k).fullName;
			
		
    			debug = "Forming arrayAdapter for DisplayAll Contact ListView";
    			Log.d("DisplayAllCallLog.startExecution",debug);
			
			
    			try{
    				adapter = new ArrayAdapter<String>(this,
    											R.layout.contactmanagerhome_list_text,
    											allDisplayName);
                    return true;
    			}catch(Exception ex){

    				debug = "IN DisplayAllCallLog.startExecution Array For Names cannot be formed";
    				Log.d("DisplayAllCallLog.startExecution",debug+ex.toString());
    				messageString = debug+ex.toString();
    				return false;
    			}
			
    		}else{
    			
    			messageString = "CallLog Extracted is neither null nor greater than 0";
    			return false;
    		}
    	}catch(Exception ex){
    		YLogger Log = YLoggerFactory.getLogger();
    		String debug = "CATCH: Inside DisplayAllCallLog.startExecution  ";
    		Log.d("DisplayAllCallLog.startExecution",debug+ex.toString());
    	    messageString = debug+ex.toString();
    	    return false;
    			
    	}
    }
		
    /**
     * Called by the thread handler to display the result.
     * This method displays the call log in a listview and sets the dialog listener
     * for click on any entry in listview.
     */
    private void displayResult(){
    	
    	try{
    		ListView list = (ListView)findViewById(R.id.displayAllContact);

    		String	debug = "Setting DisplayAllCallLog ListView";
    		YLogger Log = YLoggerFactory.getLogger();
    		Log.d("DisplayAllCallLog.displayResult",debug);
			 
    		if(adapter!=null){		
				list.setAdapter(adapter);
				
				debug = "All Contact ListView Displayed";
				Log.d("DisplayAllCallLog.displayResult",debug);
				
				list.setOnItemClickListener(new OnItemClickListener() {
					
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				     
					
				    	YLogger Log = YLoggerFactory.getLogger();
				      	String debug = "All Calllog display Clicked at "+position;
						Log.d("DisplayAllCallLog.onClickListener",debug);
						
						debug = " Going to call remove diagol ";
						Log.d("DisplayAllCallLog.onClickListener",debug);
						
						removeDialog(position);
						debug = " Going to call show diagol ";
						Log.d("DisplayAllCallLog.onClickListener",debug);
						
						showDialog(position);
						debug = " After shown dialog ";
						Log.d("DisplayAllCallLog.onClickListener",debug);
						
						
					}
			  });					
			}else{
				debug = "In DisplayResult for CallLog,Adaptor Not set ";
				Log.d("DisplayAllCallLog.displayResult",debug);
			    messageString = debug;
			    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
	     		displayContactMessage.setText(messageString);
	             return; 	
			}
			
    	}catch(Exception ex){
    		YLogger Log = YLoggerFactory.getLogger();
			String debug = "CATCH: Inside DisplayAllCallLog.displayResult:  ";
			Log.d("DisplayAllCallLog.displayResult",debug+ex.toString());
		    messageString = debug+ex.toString();
		    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
     		displayContactMessage.setText(messageString);
            return;
    	}
		
    }
    

    /**
     * The dialog box formed, when any entry in the result is cliecked. 
     * This dialog displays the information about the call log.
     * 
     */
	  protected Dialog onCreateDialog(int position){
		
		  if(callLogInfoArrayStore==null)
			  return null;
		  YLogger Log = YLoggerFactory.getLogger();
	        		  
 			String debug = " IN Create diagol ";
			Log.d("DisplayAllCallLog.onCreateDialog",debug);

			CallLogInformationStore callLogInfo = new CallLogInformationStore();
			callLogInfo = callLogInfoArrayStore.get(position);
 			debug = " Object fetched for position "+position;
			Log.d("DisplayAllCallLog.onCreateDialog",debug);
 			debug = " getting context ";
			Log.d("DisplayAllCallLog.onCreateDialog",debug);

			Dialog dialog = new Dialog(this);
			debug = " getting dialog ";
			Log.d("DisplayAllCallLog.onCreateDialog",debug);

			dialog.setContentView(R.layout.contactcustomdialog);
			debug = " Setting title ";
			Log.d("DisplayAllCallLog.onCreateDialog",debug);

			dialog.setTitle("Contact Detail");

			debug = " Title set";
			Log.d("DisplayAllCallLog.onCreateDialog",debug);

			debug = " finding text View for name";
			Log.d("DisplayAllCallLog.onCreateDialog",debug);

			TextView textName = (TextView) dialog.findViewById(R.id.customDialogNameTop);
			textName.setText(callLogInfo.fullName);
			
			debug = " finding text view for phone ";
			Log.d("DisplayAllCallLog.onCreateDialog",debug);
			
			TextView textPhoneLabel = (TextView) dialog.findViewById(R.id.customDialogPhoneTop);
			TextView textPhoneNumber = (TextView) dialog.findViewById(R.id.customDialogPhoneBottom);
			
			
			if(callLogInfo.mobileNumber!=null){
				
				textPhoneLabel.setText("Time: "+callLogInfo.callDate+" "+callLogInfo.callTime);
				textPhoneNumber.setText("Type: "+callLogInfo.callType);
				
			}
			debug = " finding text View for email ";
			Log.d("DisplayAllCallLog.onCreateDialog",debug);
			                                                      					
			TextView textEmailLabel = (TextView) dialog.findViewById(R.id.customDialogEmailTop);
			TextView textEmail = (TextView) dialog.findViewById(R.id.customDialogEmailBottom);
			                                                       
			if(callLogInfo.emailStore!=null){
				debug = " Setting email Address ";
				Log.d("DisplayAllCallLog.onCreateDialog",debug);
				
				debug = " email Address is"+callLogInfo.emailStore[0].emailAddress;
				Log.d("DisplayAllCallLog.onCreateDialog",debug);
			
				textEmailLabel.setText(callLogInfo.emailStore[0].emailType);
				textEmail.setText(callLogInfo.emailStore[0].emailAddress);
			}else{
				textEmailLabel.setText("Email Address");
				textEmail.setText("No Email");
				
			}
			
			
			
			debug = "returning dialog ";
			Log.d("DisplayAllCallLog.onCreateDialog",debug);

			return dialog;
	  }

	
}

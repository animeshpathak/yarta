/**
 * This package contains classes for the GUI of 
 * Calllog extraction
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
 * 
 * Used to display all the call log friends of the user.
 * <ui>
 * 	<li>It calls the CallLogManager.findFriendFromCallLog function of the
 * 		msemanagement package to find the friend.</li>
 * 	<li> The extraction of friend is executed in a thread.</li>
 * </ui>
 *  
 * 
 * @author Nishant Kumar
 *
 */
public class DisplayCallLogFriend extends Activity {

	/**
	 *  The arraystore that stores the result returned 
	 *  after the extraction of the call log friends.
	 *  
	 *  This arraylist has objects of CallLogInformationStore
	 *  class. An object represents the  detail of a user 
	 *  who is a friend.
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
	 * The background thread where the extraction of friends from
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
			 messageString = "";
			 progressWheel = null;		 
			 adapter = null;
			 DISPLAY_MESSAGE =0;
			 DISPLAY_CONTACT = 1;
			 
			 try{
				
				progressWheel = 
					ProgressDialog.show( this, "" , " Finding Friends from Call History.. ", true,true);
				
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
		             
		                	boolean isFriendFound = startExecution();
		           
		                	Message message = threadHandler.obtainMessage();
	                            
		                    if(!isFriendFound)
		                       	message.what=DISPLAY_MESSAGE;
		                    else
		                       	message.what=DISPLAY_CONTACT;
		                        
		                    threadHandler.sendMessage(message);

		                }
	 	        });
	          
		    	backgroundThread.start();
							
			}catch(Exception ex){
				YLogger Log = YLoggerFactory.getLogger();
	    		String debug = "CATCH: Inside DisplayCallLogFriend.onCreate: ";
	    		Log.d("DisplayCallLogFriend.onCreate",debug+ex.toString());
	    	    messageString = debug+ex.toString();
	    	    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
	     		displayContactMessage.setText(messageString);
	          
	    	    progressWheel.dismiss();
	    	    return;
			}

	}
	
	/**
	 *   Handles the response returned by the thread.
	 *  The thread informas this handler after the extractioin of all call
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
    

    /** Called by the thread handler to display the result.
    * This method displays the call log in a listview and sets the dialog listener
    * for click on any entry in listview.
    */
    
  private void displayResult(){
	  
	  try{    
	    ListView list = (ListView)findViewById(R.id.displayAllContact);

		String	debug = "Setting DisplayCallLogFriend ListView";
		YLogger Log = YLoggerFactory.getLogger();
		Log.d("DisplayCallLogFriend.displayResult",debug);
		
		if(adapter!=null){		
			list.setAdapter(adapter);
			
			debug = "All CallLogFriend ListView Displayed";
			Log.d("DisplayCallLogFriend.displayResult",debug);
			
			list.setOnItemClickListener(new OnItemClickListener() {
				
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			     
				
			    	YLogger Log = YLoggerFactory.getLogger();
			      	String debug = "All Calllog display Clicked at "+position;
					Log.d("DisplayCallLogFriend.onClickListener",debug);
					
					debug = " Going to call remove diagol ";
					Log.d("DisplayCallLogFriend.onClickListener",debug);
					
					removeDialog(position);
					debug = " Going to call show diagol ";
					Log.d("DisplayCallLogFriend.onClickListener",debug);
					
					showDialog(position);
					debug = " After shown dialog ";
					Log.d("DisplayCallLogFriend.onClickListener",debug);
					
					
				}
		  });					
		}else{
			debug = "IN DisplayCallLogFriend:displayresult:No CallLog Friends were found ";
			Log.d("DisplayCallLogFriend.displayResult",debug);
		    messageString = debug;
		    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
   		    displayContactMessage.setText(messageString);
           return; 	
		}
		
	}catch(Exception ex){
		YLogger Log = YLoggerFactory.getLogger();
		String debug = "CATCH: Inside DisplayCallLogFriend.displayResult:  ";
		Log.d("DisplayCallLogFriend.displayResult",debug+ex.toString());
	    messageString = debug+ex.toString();
	    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
		displayContactMessage.setText(messageString);
      return;
	}
	  
				
			
  }
  
  /**
   * Called by the work thread to start the steps for extraction. It calls
   * the CallLogManager.getFriendFromCallLog function of the msemanagement package.
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
   * @return true if the extraction of all call log is successful and then 
   *         array adapter to be displayed in listview is formed.
   *         
   *         
   */

  private boolean startExecution(){
	  	  
	  try{
			YLogger Log = YLoggerFactory.getLogger();
	        String debug = "****************** Inside FindCallLogFriendGUI ";
			Log.d("DisplayCallLogFriend.startExecution",debug);

			
			CallLogManager callLogManager = new CallLogManager();
			
			callLogInfoArrayStore = callLogManager.findFriendFromCallLog(getApplicationContext());
			
			if(callLogInfoArrayStore==null ||callLogInfoArrayStore.size()<=0){
				Log.d("DisplayCallLogFriend.startExecution",
							" No CallLog friend found");
				
				messageString = callLogManager.messageString;
				return false;
			}
			
			
			
			if(callLogInfoArrayStore!=null && callLogInfoArrayStore.size()>0){
				
				String[] allDisplayName = new String[callLogInfoArrayStore.size()];
				
				debug = "Forming List from findViewById";
				Log.d("DisplayCallLogFriend.startExecution",debug);
				
				for(int k = 0;k<callLogInfoArrayStore.size();k++)
					allDisplayName[k] = callLogInfoArrayStore.get(k).fullName;
				
			
				
				try{
				adapter = new ArrayAdapter<String>(this,R.layout.contactmanagerhome_list_text,
																		allDisplayName);

				debug = "Setting FindAllContact ListView";
				Log.d("DisplayCallLogFriend.startExecution",debug);
						
				return true;
				
				}catch(Exception ex){

					debug = "ArrayAdapter Not Formed ";
					Log.d("DisplayCallLogFriend.startExecution ",debug+ex.toString());
					messageString = "In DisplayCallLogFriend.startExecution: "+
					                  debug+ex.toString();
					return false;
				}
			}else{
				   debug = "Friend ArrayList is neither null nor Has any object";
					Log.d("DisplayCallLogFriend.startExecution",debug);
					messageString = "In DisplayCallLogFriend.startExecution: "+
					                  debug;
					return false;
			}
				
			 
			
	  }catch(Exception ex){
		    YLogger Log = YLoggerFactory.getLogger();
	        String debug = "CATCH: DisplayCallLogFriend.startExecution "+ex.toString();
			Log.d("DisplayCallLogFriend.startExecution",debug);
			messageString = debug;
			return false;
	  }
				
				
  }
  
  /**
   * Dialog box displayed when a user in the result is clicked.
   */
	  protected Dialog onCreateDialog(int position){
		
			
		  if(callLogInfoArrayStore==null)
			  return null;
		  YLogger Log = YLoggerFactory.getLogger();
	        		  
 			String debug = "  Create diagol ";
			Log.d("DisplayCallLogFriend.onCreateDialog",debug);

			CallLogInformationStore callLogInfo = new CallLogInformationStore();
			callLogInfo = callLogInfoArrayStore.get(position);
 			debug = " Object fetched for position "+position;
			Log.d("DisplayCallLogFriend.onCreateDialog",debug);
 			debug = " getting context ";
			Log.d("DisplayCallLogFriend.onCreateDialog",debug);

			Dialog dialog = new Dialog(this);
			debug = " getting dialog ";
			Log.d("DisplayCallLogFriend.onCreateDialog",debug);

			dialog.setContentView(R.layout.contactcustomdialog);
			debug = " Setting title ";
			Log.d("DisplayCallLogFriend.onCreateDialog",debug);

			dialog.setTitle("Contact Detail");

			debug = " Title set";
			Log.d("DisplayCallLogFriend.onCreateDialog",debug);

			debug = " finding text View for name";
			Log.d("DisplayCallLogFriend.onCreateDialog",debug);

			TextView textName = (TextView) dialog.findViewById(R.id.customDialogNameTop);
			textName.setText(callLogInfo.fullName);
			
			debug = " finding text view for phone ";
			Log.d("DisplayCallLogFriend.onCreateDialog",debug);
			
			TextView textPhoneLabel = (TextView) dialog.findViewById(R.id.customDialogPhoneTop);
			TextView textPhoneNumber = (TextView) dialog.findViewById(R.id.customDialogPhoneBottom);
			
			textPhoneLabel.setText("MobileNo");
			textPhoneNumber.setText(callLogInfo.mobileNumber);
		
			/*
			if(callLogInfo.phoneNumberStore!=null){
				debug = " Setting phone number ";
				Log.d("DisplayCallLogFriend.onCreateDialog",debug);
				
				debug = " phone number is "+callLogInfo.phoneNumberStore[0].phoneNumber;
				Log.d("DisplayCallLogFriend.onCreateDialog",debug);
				textPhoneLabel.setText(callLogInfo.phoneNumberStore[0].phoneNumberType);
				textPhoneNumber.setText(callLogInfo.phoneNumberStore[0].phoneNumber);
			}else{
				textPhoneLabel.setText("Phone Number");
				textPhoneNumber.setText("No Number");
				
			}
			*/
			debug = " finding text View for email ";
			Log.d("DisplayCallLogFriend.onCreateDialog",debug);
			                                                      					
			TextView textEmailLabel = (TextView) dialog.findViewById(R.id.customDialogEmailTop);
			TextView textEmail = (TextView) dialog.findViewById(R.id.customDialogEmailBottom);
			                                                       
			if(callLogInfo.emailStore!=null){
				debug = " Setting email Address ";
				Log.d("DisplayCallLogFriend.onCreateDialog",debug);
				
				debug = " email Address is"+callLogInfo.emailStore[0].emailAddress;
				Log.d("DisplayCallLogFriend.onCreateDialog",debug);
			
				textEmailLabel.setText(callLogInfo.emailStore[0].emailType);
				textEmail.setText(callLogInfo.emailStore[0].emailAddress);
			}else{
				textEmailLabel.setText("Email Address");
				textEmail.setText("No Email");
				
			}
			debug = "returning dialog ";
			Log.d("DisplayCallLogFriend.onCreateDialog",debug);

			return dialog;
	  }


}

/**
 * This package contains classes that display the extraction of 
 * information from a Linkedin Profile
 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.LinkedInManager;


import java.util.ArrayList;


import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;

import fr.inria.arles.yarta.middleware.msemanagement.LinkedInExtractProfile;
import fr.inria.arles.yarta.middleware.msemanagement.LinkedInInformationStore;

import fr.inria.arles.yarta.middleware.msemanagement.R;
import android.app.Activity;
import android.app.ProgressDialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Connects to Linkedin profile of the user 
 * if the access token is available.
 * <p>It reads the access token from the internal memory
 * of the Android and use it to connect to the profile of
 * user for this access token.</p>
 * 
 * @author Nishant Kumar
 *
 */
public class DefaultConnectLinkedIn extends Activity {
	
	
	/**
	 * The progress wheel that is displayed while a thread is in execution.
	 */
	ProgressDialog progressWheel;
	/**
	 * The array adapter that is used to display the result in list view.
	 */
	
	ArrayAdapter<String> adapter;
/**
 * The thread that runs to make a call for extraction of all information
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
				adapter = null;
				messageString = "";
				progressWheel = null;
				DISPLAY_MESSAGE =0;
				DISPLAY_CONTACT = 1;
				try{
					
					progressWheel = 
						ProgressDialog.show( this, "" , " Connecting to LinkedIn ... ", true,true);

					/**
					 * The back ground calls the startExecution method of this 
					 * class that starts the steps for extraction of information
					 * from the linkedin profile of the user.
					 * 
					 */
					backgroundThread = new Thread(new Runnable()
			        {
			               public void run()
			                {
			             
			                	boolean isProfileExtracted = startExecution();
			           
			                	Message message = threadHandler.obtainMessage();
		                            
			                    if(!isProfileExtracted)
			                       	message.what=DISPLAY_MESSAGE;
			                    else
			                       	message.what=DISPLAY_CONTACT;
			                        
			                    threadHandler.sendMessage(message);

			                }
		 	        });
		          
			    	backgroundThread.start();
								
				}catch(Exception ex){
					YLogger Log = YLoggerFactory.getLogger();
		    		String debug = "CATCH: Inside DefaultConnectLinkedIn.onCreate: ";
		    		Log.d("DefaultConnectLinkedIn.onCreate",debug+ex.toString());
		    	    messageString = debug+ex.toString();
		    	    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
		     		displayContactMessage.setText(messageString);
		     		progressWheel.dismiss();
		    	    return;
				}

	
		}
		
		/**
		 * The thread handler that receives the message after the background thread 
		 * ends. This function transfer the control to display the result. 
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
	     * The function used to display the extracted information from the Linkedin 
	     * profile of the user.
	     * 
	     */
		private void displayResult(){
			
			
			ListView list = (ListView)findViewById(R.id.displayAllContact);

    		String	debug = "Setting DefaultConnectLinkedIn  ListView";
    		YLogger Log = YLoggerFactory.getLogger();
    		Log.d("DefaultConnectLinkedIn.displayResult",debug);
			
    		
    		if(adapter!=null){		
				list.setAdapter(adapter);
				debug = "All Contact ListView Displayed";
				Log.d("DefaultConnectLinkedIn.displayResult",debug);
				
    		}else{
    			TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
         		displayContactMessage.setText("In DefaultConnectLinkedIn.displayResult"+
         									" Array Adapter for Profile Information is Empty");
    		}
		
		}
			
		/**
		 * Starts the execution of the extraction of information
		 * from the linkedin profile of the user.
		 * <p>It calls the LinkedinExtractProfile.callSequenceOfFunction
		 *  method in the msemanagement package to extract the information from
		 *  the profile 
		 *  </p>
		 * <p>If the extraction is successful, then the array adapter is formed 
		 * </p>
		 * 
		 * @return This function returns true if the information is extracted and
		 * array adapter is formed successfully, else returns false. 
		 */
	    private boolean startExecution(){
				
				
			try{	
				YLogger Log = YLoggerFactory.getLogger();
		        String debug = " Inside DefaultConnectLinkedIn.startExecution ";
				Log.d("DefaultConnectLinkedIn.startExecution",debug);
				
		
				
				 LinkedInExtractProfile linkedInExtractProfile = new LinkedInExtractProfile();
				 boolean isSuccess = linkedInExtractProfile.callSequenceOfFunction(this.getApplicationContext());
					
				 Log.d("DefaultConnectLinkedIn.startExecution","LinkedIn extraction over");
					
			
				 if(!isSuccess){
					 Log.d("DefaultConnectLinkedIn.startExecution",
							 "LinkedIn extraction over , was not successfull");
					
					 messageString = linkedInExtractProfile.messageString;
					 
					 messageString+="\n" + "Cannot Connect To Profile\n\n"+
					    "1. Check your Internet Connection"+
		       			"\n2. Check Your Access Token"+
		       			"\n3. Access Token may be Expired"+
		       			"\n4. Try First Time Connection";
					 
				     return false;
				 }else{
						
					 LinkedInInformationStore linkedInInfoStore = new LinkedInInformationStore();
			
					 linkedInInfoStore.firstName = linkedInExtractProfile.getFirstName();
					 linkedInInfoStore.lastName =  linkedInExtractProfile.getLastName();
					 linkedInInfoStore.fullName= linkedInExtractProfile.getFullName();
					 linkedInInfoStore.websiteURL= linkedInExtractProfile.getWebsiteUrl();
					 linkedInInfoStore.emailStore =  linkedInExtractProfile.getEmail();
					 linkedInInfoStore.affiliation =  linkedInExtractProfile.getAffiliation();
					 linkedInInfoStore.friends =  linkedInExtractProfile.getFriends();
				
					ArrayList<String> allDisplay=new ArrayList<String>();
					
				    debug = "Storing first name";
					Log.d("DefaultConnectLinkedIn.startExecution",debug);
                     
				     
					if(linkedInInfoStore.fullName!=null)
						allDisplay.add("Full Name: "+linkedInInfoStore.fullName);
					
					debug = "Storing Current Position";
					Log.d("DefaultConnectLinkedIn.startExecution",debug);
					if(linkedInInfoStore.affiliation!=null)
						allDisplay.add("Current Position:\n "+linkedInInfoStore.affiliation);
					debug = "Storing Website Url";
					Log.d("DefaultConnectLinkedIn.startExecution",debug);
					
					if(linkedInInfoStore.websiteURL!=null)
						allDisplay.add("Profile URL:\n "+linkedInInfoStore.websiteURL);
					
					
					if(linkedInInfoStore.emailStore!=null)
						allDisplay.add("Email: "+linkedInInfoStore.emailStore[0].emailAddress);
					
					debug = "Storing Total Connection";
					Log.d("DefaultConnectLinkedIn.startExecution",debug);
					
					if(linkedInInfoStore.totalConnection!=null)
						allDisplay.add("Total Connection: "+linkedInInfoStore.totalConnection);
					
					debug = "Storing friends name";
					Log.d("DefaultConnectLinkedIn.startExecution",debug);
					
					
					if(linkedInInfoStore.friends!=null){	
						allDisplay.add("Friends Detail:");	
						for(int k = 0;k<linkedInInfoStore.friends.size();k++){	
							allDisplay.add(linkedInInfoStore.friends.get(k).fullName);
							allDisplay.add(linkedInInfoStore.friends.get(k).websiteURL);
						}
					}
					
					
					debug = "Forming arrayAdapter for DisplayAll Contact ListView";
					Log.d("DefaultConnectLinkedIn.startExecution",debug);
				
					try{
						adapter = new ArrayAdapter<String>(this,R.layout.contactmanagerhome_list_text,
																			allDisplay);
					    return true;
			 
					}catch(Exception ex){

						debug = "ArrayAdapter For Names cannot be formed";
						Log.d("DefaultLinkedInConnectGUI.startExecution",debug+ex.toString());
						messageString ="DefaultLinkedInConnectGUI.startExecution" + 
						               "Cannot form ArrayAdapter for Display: "+
						                ex.toString();
						 
					     return false;
					}
					
            	}
	
			}catch(Exception ex){
					YLogger Log = YLoggerFactory.getLogger();
					String debug = "CATCH: Inside DefaultLinkedInConnectGUI  ";
					Log.d("DefaultConnectLinkedIn.startExecution",debug+ex.toString());
			
					messageString ="CATCH: DefaultConnectLinkedIn.startExecution: " + 
		               ex.toString();
		                
		            return false;
			}
		}
			
				
				
}
			

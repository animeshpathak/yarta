/**
 * This package stores the class that are used to display the GUI for 
 * data extraction from the facebook profile of the user.
 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.FacebookManager;


import java.util.ArrayList;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.FacebookExtractProfile;
import fr.inria.arles.yarta.middleware.msemanagement.FacebookFindAccessToken;
import fr.inria.arles.yarta.middleware.msemanagement.MSEPersonInformationStore;
import fr.inria.arles.yarta.middleware.msemanagement.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 
 * Activity to extract all information from facebook profile.
 * 
 * <p>This activity first transfer the control to FacebookFindAccessToken.java
 * activity in the msemanagement package to obtain the access token</p>
 * 
 * <p>After the access token is received in the result, this activity starts 
 * a thread and calls the FacebookExtractProfile class in the msemanagement package.
 * The access token is passed as an argument in the call.
 * </p> 
 * <p>If the extraction of information from Facebook profile is successful, 
 * then different functions are used to display 
 * the result.</p>
 * 
 * @author Nishant Kumar
 *
 */

public class FacebookManagerHome extends Activity{

	/**
	 * The access token that will be obtained from FacebookFindAccessToken class
	 * of msemanagement package. This access token will be used to extract information
	 * from the facebook profile.
	 */
	String accessToken;
	
	/**
	 * The array adapter used to display the result in the listview
	 */
	ArrayAdapter<String> adapter;
	/**
	 * The string that store any error message.
	 */
	String messageString;
	
	/**
	 * The progress wheel shown while the thread executes the data extraction
	 */
	ProgressDialog progressWheel;
	
	/**
	 * The thread that runs to extract information from facebook profile.
	 */
	
	Thread backgroundThread;
	
	/**
	 * The constant used by the thread handler to transfer the control  
	 */
	int  DISPLAY_MESSAGE;
	int DISPLAY_CONTACT;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
	
			 accessToken="";
			 adapter = null;
			 messageString = "";
			 progressWheel = null;
			 DISPLAY_MESSAGE =0;
			 DISPLAY_CONTACT = 1;
			 int SECONDARY_ACTIVITY_REQUEST_CODE=0;
			
			 super.onCreate(savedInstanceState);
			 setContentView(R.layout.allcontact);
			
            YLogger Log = YLoggerFactory.getLogger();
	        
			String debug = " Inside FacebookManagerHome ";
			Log.d("FacebookManagerHome.onCreate",debug); 
			Intent intent = new Intent(this, FacebookFindAccessToken.class);
            startActivityForResult(intent,SECONDARY_ACTIVITY_REQUEST_CODE);
		   
			
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			Log.d("FacebookManagerHome.onCreate","CATCH "+ex.toString()); 
			Toast.makeText(getApplicationContext(),"CATCH:FacebookManagerHome.onCreate:"+ex.toString(),Toast.LENGTH_LONG).show();

		}
	}
	

	public void onActivityResult(int requestCode,int resultCode,Intent newintent){
		super.onActivityResult(requestCode, resultCode, newintent);
		
		YLogger Log = YLoggerFactory.getLogger();
		String debug = " Inside On ActivityResult  ";
		Log.d("FacebookManagerHome.onActivityResult",debug);
        
		if(resultCode==RESULT_OK){
		    debug = "Result Ok obtained ";
			Log.d("FacebookManagerHome.onActivityResult",debug);
						
			try{
			    debug = " Extracting bundle  ";
				Log.d("FacebookManagerHome.onActivityResult",debug);
			
				Bundle extras = newintent.getExtras();
				debug = " Bundled extracted Obtained  ";
				Log.d("FacebookManagerHome.onActivityResult",debug);
				
				if(extras!=null){
				
					Log.d("FacebookManagerHome.onActivityResult", 
							" Cheking if Extraction of AccessToken was Success");
					
					boolean isSuccess = extras.getBoolean("isSuccess");
					
			
					if(!isSuccess){
					   Log.d("FacebookManagerHome.onActivityResult",
							   " Acces Token was not successfully extracted");
					   TextView displayError = (TextView)findViewById(R.id.displayAllContactMessage);
					   displayError.setText("Access Token was Not Extracted.\n" +
					   		"1.Check your Internet Connection\n"+
					   		"2.Retry after some time");
					   return;
					}
					
						
					try{
					    accessToken = extras.getString("accessToken");
						Log.d("FacebookManagerHome.onActivityResult"," Access Token obtained "+accessToken);
						
						try{
							
							progressWheel = 
								ProgressDialog.show( this, "" , " Connecting to Facebook ... ", true,true);
							
							/**
							 * The work thread that calls the startExecution method of this 
							 * class. The startExecution function starts the steps to extract profile 
							 * information
							 * 
							 */
							backgroundThread = new Thread(new Runnable()
					        {
					               public void run()
					                {
					             
					                	boolean isFacebookDataFound = startExecution();
					           
					                	Message message = threadHandler.obtainMessage();
				                            
					                    if(!isFacebookDataFound)
					                       	message.what=DISPLAY_MESSAGE;
					                    else
					                       	message.what=DISPLAY_CONTACT;
					                        
					                    threadHandler.sendMessage(message);

					                }
				 	        });
				          
					    	backgroundThread.start();
										
						}catch(Exception ex){
							
				    		debug = "CATCH: Inside FacebookManagerHome.onActivityResult: ";
				    		Log.d("FacebookManagerHome.onActivityResult",debug+ex.toString());
				    	    messageString = debug+ex.toString();
				    	    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
				     		displayContactMessage.setText(messageString);
				          
				    	    progressWheel.dismiss();
				    	    return;
						}
						
						
					}catch(Exception ex){
						
						Log.d("FacebookManagerHome.onActivityResult","CATCH: "+ex.toString());
						Toast.makeText(getApplicationContext(),
								"FacebookManagerHome.onActivityResult:"+ex.toString(),
								Toast.LENGTH_LONG).show();
						
						messageString = "FacebookManagerHome.onActivityResult "+
					                       "Cannot Read Access Token from the Returend Activity";

						TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
				        displayContactMessage.setText(messageString);
				          
						return;
					}
					   				
				}else{
					debug = " Result bundle Null  Obtained  ";
		 	    	Log.d("FacebookManagerHome.onActivityResult",debug);
		 	    	messageString = "FacebookManagerHome.onActivityResult: "+
		 	    	                "No result obtained from Facebook FindAccessToken Activity";
		 	   	    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
		            displayContactMessage.setText(messageString);
		            return;
				}
			
			
			}catch(Exception ex){
				debug = " Error in retrieving extra  "+ex.toString();
				Log.d("FacebookManagerHome.onActivityResult",debug);
				Toast.makeText(getApplicationContext(),debug,Toast.LENGTH_LONG).show();
				TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
		        displayContactMessage.setText(debug);
		        return;
			}
		
		}else{
			Toast.makeText(getApplicationContext(),"Result received NOT OK",
					Toast.LENGTH_SHORT).show();
		     
		     TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
             displayContactMessage.setText("Result received from Facebook AccessToken  NOT OK");
             return;
		}
	}
	
    /**
     * The thread handler that receives the message from the thread.
     * The message recieved is used to transfer the control to display result
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
	 * The function called by the thread to start the execution to extract data from 
	 * facebook.
	 * <p>This function calls the FacebookExtractProfile class in msemanagement package
	 * and gets the result</p>
	 * 
	 * <p>If the data extraction is successfull, then the function forms an arrayadapter
	 *    to display the result on the screen.
	 * </p>
	 * 
	 * @return returns true if the extractin from the faceobook is succcessful
	 * and the array adapter is formed else  returns false 
	 * 
	 */
    private boolean startExecution(){
    
    	try{
    		YLogger Log = YLoggerFactory.getLogger();
    		FacebookExtractProfile fbExtractProfile = 
    			new FacebookExtractProfile();
    		
    		boolean isExtracted =	fbExtractProfile.extractFacebookProfile(accessToken);
		
    		if(isExtracted){
		
    			ArrayList<String> profileInfo = new ArrayList<String>();
			
    			String firstName = fbExtractProfile.getFirstName();
    			String lastName = fbExtractProfile.getLastName();
    			String fullName = fbExtractProfile.getFullName();
    			String webUrl = fbExtractProfile.getWebsiteUrl();
    			String affiliation = fbExtractProfile.getAffiliation();
			
    			ArrayList<MSEPersonInformationStore> friendsList = null;
    			friendsList = fbExtractProfile.getFriends();
			    
    			profileInfo.add("FirstName: "+firstName);
    			profileInfo.add("LastName: "+lastName);
    			profileInfo.add("FullName: "+fullName);
    			profileInfo.add("ProfileUrl:\n "+webUrl);
    			profileInfo.add("Affiliation:\n "+affiliation);
			
				profileInfo.add("My FriendsList:");
				
				if(friendsList!=null && friendsList.size()>0){
					for(int k=0;k<friendsList.size();k++){
					
						MSEPersonInformationStore msePerson = friendsList.get(k);
				
						if(msePerson!=null){
							if(msePerson.fullName!=null)
								profileInfo.add("Name: "+msePerson.fullName);
							if(msePerson.websiteURL!=null)
								profileInfo.add("URL: "+msePerson.websiteURL);
						}
				
					}	
				}else{
					Log.d("FacebookManagerHome.onActivityResult","Facebook Friend is null");
					profileInfo.add("No Friend");
				}
	
				Log.d("FacebookManagerHome.displayProfile","Forming arrayAdapter to Display Fbook data");
			
				try{
					adapter = new ArrayAdapter<String>(this,
												R.layout.contactmanagerhome_list_text,
												profileInfo);
					return true;
				}catch(Exception ex){
			
					Log.d("FacebookManagerHome.startExecution: ",
						"Cannot set adapter from Arraylist obtained: "+ex.toString());
						messageString = "FacebookManagerHome.startExecution: "+
						"Cannot Set Adapter from FBData: "+ex.toString();	
					return false;
				}
			
			
    		}else{
    	   	
    			Log.d("FacebookManagerHome.startExecution: ",
    					"Data From FacebookExtractProfile was not obtained ");
    			
    			messageString = "Data From Facebook was not extracted: "+
    		                 fbExtractProfile.messageString;
    			return false;
    		}
    		
    	}catch(Exception ex){
    		YLogger Log = YLoggerFactory.getLogger();
    		Log.d("FacebookManagerHome.startExecution: ","CATCH: "+ex.toString());
			messageString = "CATCH: FacebookManagerHome.startExecution: "+ex.toString(); 	
    		return false;
    	}
    	
    }

    /**
     * The function called by the activity to display the obtained result.
     * This method forms the listview and displays the array adapter to it.
     * 
     */
	private void displayResult(){
		try{
		    YLogger Log = YLoggerFactory.getLogger();

		    Log.d("FacebookManagerHome.displayResult","Getting Listviex to display Info");
			
			ListView list = (ListView)findViewById(R.id.displayAllContact);
		    
		    Log.d("FacebookManagerHome.displayResult",
					"Setting All Facebook Data in  ListView");
			if(adapter!=null){		
			   list.setAdapter(adapter);
				Log.d("FacebookManagerHome.displayResult",
				"All Contact ListView Displayed");
				return;

			}else{
				Log.d("FacebookManagerHome.displayResult"," ArrayAdapter for Listview is null");
			    messageString = "FacebookManagerHome.displayResult: "+"ArrayAdapter for Listview is Null";
			    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
	     		displayContactMessage.setText(messageString);
                return;
			}
			
		}catch(Exception ex){
			
			YLogger Log = YLoggerFactory.getLogger();
			Log.d("FacebookManagerHome.displayResult",ex.toString());
		    messageString = "CATCH: FacebookManagerHome.displayResult: "+ex.toString();
		    TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
     		displayContactMessage.setText(messageString);
            return;
		}
    }
}
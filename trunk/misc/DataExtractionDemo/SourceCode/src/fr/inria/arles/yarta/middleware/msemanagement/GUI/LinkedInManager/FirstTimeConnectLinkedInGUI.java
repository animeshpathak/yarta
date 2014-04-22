/**
 * 
 * This package contains classes that display the extraction of 
 * information from a Linkedin Profile
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.LinkedInManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.LinkedInFindAccessToken;
import fr.inria.arles.yarta.middleware.msemanagement.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * An Android activity to start the extraction of information from 
 * linkedin profile of a user.
 *  It is started after the user selects the 
 * FirstTime LinkedIn Connection from the LinkedIn Manager Home screen.
 * 
 * <p>It first starts the LinkedInFindAccessToken Activity in the msemanagement
 * package to set the access token for the user in a "LinkedInStore" file
 * in the Android internal memory.
 * </p>
 * <p>Then it calls the DefaultConnectLinkedIn Activity in this package to starts the 
 * extraction of information from the linkedin
 * </p>
 * 
 * @author Nishant Kumar
 *
 */
public class FirstTimeConnectLinkedInGUI extends Activity {
	int SECONDARY_ACTIVITY_REQUEST_CODE=0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			YLogger Log = YLoggerFactory.getLogger();
			
			Log.d("FirstTimeConnectLinkedInGUI.onCreate",
					" Inside GUI of FirstTimeConnectLinkedInGUI");
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);

			Log.d("FirstTimeConnectLinkedInGUI.onCreate",
					"Calling Activity FirstTimeConnect for extraction of Access Token ");

			Intent intent = new Intent(this, LinkedInFindAccessToken.class);
            startActivityForResult(intent,SECONDARY_ACTIVITY_REQUEST_CODE);
			
		}catch(Exception ex){

			YLogger Log = YLoggerFactory.getLogger();
			Log.d("FirstTimeConnectLinkedInGUI.onCreate",
					"CATCH Inside GUI of FirstTimeConnectLinkedInGUI: "+ex.toString());
			
		}
	}
	
	
	
	public void onActivityResult(int requestCode,int resultCode,Intent newintent){
		super.onActivityResult(requestCode, resultCode, newintent);
		
		YLogger Log = YLoggerFactory.getLogger();
		String debug = " Inside On ActivityResult  ";
		Log.d("FirstTimeConnectLinkedInGUI.onActivityResult",debug);
        
		if(resultCode==RESULT_OK){
		    debug = "Result Ok obtained ";
			Log.d("FirstTimeConnectLinkedInGUI.onActivityResult",debug);
						
			try{
			    debug = " Extracting bundle  ";
				Log.d("FirstTimeConnectLinkedInGUI.onActivityResult",debug);
			
				Bundle extras = newintent.getExtras();
				debug = " Bundled extracted Obtained  ";
				Log.d("FirstTimeConnectLinkedInGUI.onActivityResult",debug);
				
				if(extras!=null){
				
					Log.d("FirstTimeConnectLinkedInGUI.onActivityResult", 
							" Cheking if Access Token was Successfully Stored ");
					
					boolean isSuccess = extras.getBoolean("isSuccess");
					
					Log.d("FirstTimeConnectLinkedInGUI.onActivityResult",
							" Result isSuccess extracted");
      
					if(!isSuccess){
					   Log.d("FirstTimeConnectLinkedInGUI.onActivityResult",
							   " Acces Token was not successfully store, received false");
					   TextView displayError = (TextView)findViewById(R.id.mainTV);
					   String messageString = extras.getString("messageString");
					   displayError.setText(messageString+
							"\n\nAccess Token was Not Stored.\n" +
					   		"1.Check your Internet Connection\n"+
					   		"2.Press Confirm Button after entering your Pin\n"+
					   		"Try Again...");
					   
					   return;
					}
					
					Log.d("FirstTimeConnectLinkedInGUI.onActivityResult"," Acces Token was Stored , reading it ");
					
					try{
						File file = getFileStreamPath("LinkedInStore");
						if(!file.exists()){
							Log.d("FirstTimeConnectLinkedInGUI.onActivityResult","Access Token File doesnot exist");
                            							
							TextView displayError = (TextView)findViewById(R.id.mainTV);
							   displayError.setText(" Access Token Store doesnot exists\n" +
							   		"1.Test your Access Token\n"+
							   		"2.Try First Time LinkedIn Connection\n"+
							   		"Try Again...");
							
							return;
						}
						
						
						BufferedReader bufread = new BufferedReader(new FileReader(file));
						String accessKey =    bufread.readLine();
						String accessKeySecret = bufread.readLine();
						Log.d("FirstTimeConnectLinkedInGUI.onActivityResult"," Acces Token was read"+accessKey);
						Log.d("FirstTimeConnectLinkedInGUI.onActivityResult"," Acces Token Secret was read"+accessKeySecret);
						Log.d("FirstTimeConnectLinkedInGUI.onActivityResult","Transferring control to Default Connect LinkedIn");
						Intent transferIntent = new Intent(this, DefaultConnectLinkedIn.class);
						startActivity(transferIntent);
						
					}catch(Exception ex){
						Log.d("FirstTimeConnectLinkedInGUI.onActivityResult","Cannot read the Stored Access Tokens");
						Log.d("FirstTimeConnectLinkedInGUI.onActivityResult",ex.toString());
						TextView displayError = (TextView)findViewById(R.id.mainTV);
						   displayError.setText("Unable to read Access Token\n" +
								   ex.toString()+"\n"+
						   		"1.Test your Access Token\n"+
						   		"2.Try First Time LinkedIn Connection\n"+
						   		"Try Again...");
						
						return;
					}
					   				
				}else{
					debug = " Result bundle Null  Obtained  ";
		 	    	Log.d("FirstTimeConnectLinkedInGUI.onActivityResult",debug);
		 	    	TextView displayError = (TextView)findViewById(R.id.mainTV);
					   displayError.setText("FirstTimeConnectLinkedInGUI.onActivityResult"+
							   ": No Values returned from  LinkedInFindAccessToken"+					   	
					     		"Try Again...");
					
					return;
				}
			
			
			}catch(Exception ex){
				debug = " Error in retrieving extra  "+ex.toString();
				Log.d("FirstTimeConnectLinkedInGUI.onActivityResult",debug);
				Toast.makeText(getApplicationContext(),debug,Toast.LENGTH_LONG).show();
				TextView displayError = (TextView)findViewById(R.id.mainTV);
				   displayError.setText("FirstTimeConnectLinkedInGUI.onActivityResult: "+
						                        ex.toString()       );
				
				return;
			}
		
		}else
			Toast.makeText(getApplicationContext(),"Result received NOT OK",
					Toast.LENGTH_SHORT).show();

	}
	
	}
	
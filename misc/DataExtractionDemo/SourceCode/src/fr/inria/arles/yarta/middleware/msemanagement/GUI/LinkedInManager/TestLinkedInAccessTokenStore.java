/**
 * 
 * This package contains classes that display the extraction of 
 * information from a Linkedin Profile

 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.LinkedInManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.R;
import android.app.Activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * 
 * Android activity to test the availabilty of access token in internal memory.
 * 
 * It is started if the user selects the 
 * Test LinkedIn Access Token on the linkedIn manager home screen. 
 * 
 * It reads the access token from the "LinkedInStore"
 * file in the android internal memory.
 * 
 * @author Nishant Kumar
 *
 */
public class TestLinkedInAccessTokenStore extends Activity {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);
			
			YLogger Log = YLoggerFactory.getLogger();
			Log.d("TestLinkedInAccessTokenStore.onCreate",
					" Reading access Token from memory ");
	    	
			try{
				File file = getFileStreamPath("LinkedInStore");
				if(!file.exists()){
					TextView displayText = (TextView)findViewById(R.id.mainTV);
					displayText.setText("AccessTokenFile Doesnot Exsist"+
					"\nGo back and Launch First Time LinkedIn Connection");
	
			         return;
				}
				
				BufferedReader bufread = new BufferedReader(new FileReader(file));
				String accessKey =    bufread.readLine();
				String accessKeySecret = bufread.readLine();
				Log.d("TestLinkedInAccessTokenStore.onCreate",
						" In readAccessToken Acces Token was read:"+accessKey);
				Log.d("TestLinkedInAccessTokenStore.onCreate",
						" Acces Token Secret was read:"+accessKeySecret);
				
				TextView displayText = (TextView)findViewById(R.id.mainTV);
				if(accessKey==null ||accessKeySecret==null){
					displayText.setText("Access Token is not Stored"+
							"\nGo back and Launch First Time LinkedIn Connection");
			
					return;
				}else{
					displayText.setText("Success:Access Token Found\n"+
							"AccessToken:\n"+accessKey+
							"\nAccessSecret:\n"+accessKeySecret);
					
					return;
				}
			}catch(Exception ex){
				Log.d("TestLinkedInAccessTokenStore.onCreate",
						"CATCH: Cannot read the Stored Access Tokens");
				Log.d("TestLinkedInAccessTokenStore.onCreate",ex.toString());
				  Toast.makeText(getApplicationContext(),"TestLinkedInAccessTokenStore.onCreate "+ex.toString(),Toast.LENGTH_LONG).show();

				return ;
			}
		    
		}catch(Exception ex){
			
		}
	}
}

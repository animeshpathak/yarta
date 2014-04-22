/** <p>This package contains classes that extract information from
 * various social data source.
 * </p>
 * <p>The classes of this package forms the functional unit of Data
 * Extraction Manager</p>
 */
package fr.inria.arles.yarta.middleware.msemanagement;

import java.io.File;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.widget.TextView;




/**
 * 
 *  An Android activity to delete the access token stored in the 
 *  "LinkedInStore" file stored in Android internal memory. 
 *  
 *  <p>The access token was obtained when the user started the extraction of 
 *     LinkedIn information for the first time.  
 *  </p>
 *  <ui>
 *       <li>It is required to connect to a Linkedin profile</li>
 *       <li>The new access token can be obtained by again using the first time
 *           LinkedIn connection  </li>
 *          
 *  </ui> 
 * 
 */
public class DeleteLinkedInAccessTokenStore extends Activity {
	
   private String accessTokenFileName = "LinkedInStore";
   
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);
			
			
			 AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		        alertDialog.setTitle("Confirm");
		        alertDialog.setMessage("Delete LinkedIn AccessToken ?");
		        alertDialog.setButton("Yes", 
		        		new DialogInterface.OnClickListener() {
		        				public void onClick(DialogInterface dialog, int which) {
		        				YLogger Log = YLoggerFactory.getLogger();
		        				Log.d("DeleteLinkedInAccessTokenStore.onCrete",
		        						"Yes  Button Dialog");		
		        				deleteFile();
		        				return;
		        				} 
		        				}); 
		        
		        alertDialog.setButton2("No", 
		        		new DialogInterface.OnClickListener() {
		        				public void onClick(DialogInterface dialog, int which) {
		        				YLogger Log = YLoggerFactory.getLogger();
		        				Log.d("DeleteLinkedInAccessTokenStore.onCreate",
		        						"No  Button Dialog");		
		        				TextView displayText = (TextView)findViewById(R.id.mainTV);
		        				displayText.setText("File deletion Cancelled!!");
		        				return;
		        				} 
		        				}); 
		        
		        alertDialog.show();
				
			}catch(Exception ex){
				TextView displayText = (TextView)findViewById(R.id.mainTV);
				displayText.setText("DeleteLinkedInAccessTokenStore:Cannot delete file: "+ex.toString());
				YLogger Log = YLoggerFactory.getLogger();
				Log.d("Catch: DeleteLinkedInAccessTokenStore.onCrete",
				ex.toString());		
				return;
				
			}
      }
	
	public void deleteFile(){
		
		try{
			YLogger Log = YLoggerFactory.getLogger();
			Log.d("DeleteLinkedInAccessTokenStore.deleteFile",
					" Getting access Token File Path ");
	    	
			File file = getFileStreamPath(accessTokenFileName);
			
			if(!file.exists()){
				TextView displayText = (TextView)findViewById(R.id.mainTV);
				displayText.setText("AccessTokenFile Doesnot Exsist"+
				"\nGo back and Launch First Time LinkedIn Connection");
				Log.d("DeleteLinkedInAccessTokenStore.deleteFile",
				" File doesnot exists ");
		        return;
			}
			
			if(file.delete()){
				TextView displayText = (TextView)findViewById(R.id.mainTV);
				displayText.setText("AccessTokenFile Deleted Successfully");
		        return;
			}else{
				
				TextView displayText = (TextView)findViewById(R.id.mainTV);
				displayText.setText("Cannot delete LinkedIn AccessToken File");
				Log.d("DeleteLinkedInAccessTokenStore.deleteFile",
				" Cannot delete the file ");
		        return;
			}
		}catch(Exception ex){
			TextView displayText = (TextView)findViewById(R.id.mainTV);
			displayText.setText("DeleteLinkedInAccessTokenStore: Cannot Delete file: "+ex.toString());
			YLogger Log = YLoggerFactory.getLogger();
			
			Log.d("DeleteLinkedInAccessTokenStore.deleteFile",
			" CATCH: "+ex.toString());
			return;
		
		}
		
	}
	
}

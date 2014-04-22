/**
 *  * This package contains classes required for displaying the GUI for 
 * extraction of information from the SMS store of the 
 * Android phone.
 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.SMSManager;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 ***
 * Android activity to display the home screen for
 * extraction of sms information from Android phone.
 * 
 * It displays the various option related to extraction of sms and sms friend
 * from the Android phone
 * 
 * @author Nishant Kumar
 *
 
 */
public class SMSManagerHome extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.managerhome);
			
			YLogger Log = YLoggerFactory.getLogger();
	        
			String debug = " Inside SMSManagerHome ";
			Log.d("SMSManagerHome.onCreate",debug);

			debug = "Forming Columns for Menu of SMSManagerHome";
			Log.d("SMSManagerHome.onCreate",debug);
			
			String[] smsManagerFields = getResources().getStringArray(R.array.smsManagerList);

			debug = "Forming List from findViewById";
			Log.d("SMSManagerHome.onCreate",debug);
			
			ListView list = (ListView)findViewById(R.id.managerHomeList);
			
			debug = "Forming arrayAdapter for ListView";
			Log.d("SMSManagerHome.onCreate",debug);
			
			ArrayAdapter<String> adapter = null;
			try{
			adapter = new ArrayAdapter<String>(this,R.layout.contactmanagerhome_list_text,
																	smsManagerFields);
				
		//	TextView listHeader = (TextView) findViewById(R.id.HeaderTop);
		//	View view = new View(this);
			
		//	list.addHeaderView(listHeader);
			
			
			debug = "Setting ListView";
			Log.d("SMSManagerHome.onCreate",debug);
					
			list.setAdapter(adapter);
			
			debug = "ListView Displayed";
			Log.d("SMSManagerHome.onCreate",debug);
			
			
			}catch(Exception ex ){
					
					debug = "CATCH: Forming ArrayAdapter for SMSManager Home List";
					Log.d("SMSManagerHome.onCreate",debug+ex.toString());
					 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();

					return;
			}
			
			list.setOnItemClickListener(new OnItemClickListener() {
				
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			     
			   
			    	YLogger Log = YLoggerFactory.getLogger();
			      	String debug = "SMSManager Home ListView Clicked at "+position;
					Log.d("SMSManagerHome.onItemClick",debug);
					
			    	
			    	Intent intent = null;
			    	
			    	switch(position){
			    	
			    	case 0:		intent=	new Intent(SMSManagerHome.this, DisplayAllSMS.class);
			    				break;
			    	case 1:		intent=	new Intent(SMSManagerHome.this, DisplayInboxSMS.class);
								break;
			    	case 2:		intent=	new Intent(SMSManagerHome.this, DisplaySentSMS.class);
								break;									
			    	case 3:		intent=	new Intent(SMSManagerHome.this, DisplayAllSMSFriend.class);
								break;			    	
			    	case 4:		intent=	new Intent(SMSManagerHome.this, DisplayAllInboxFriend.class);
								break;			    	
			      	case 5:		intent=	new Intent(SMSManagerHome.this, DisplayAllSentSMSFriend.class);
			      				break;
			    	default :   intent = null;
			    				break;
			    	};
			    	if(intent!=null)
			    		startActivity(intent);
			    	else{
			    		debug = "Cannot find position of click on ListView Menu.";
			 			Log.d("SMSManagerHome.onItemClick: ",debug);
			    	}
			    	
				}
		  });						
		
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			String debug = "SMSManagerHome: ";
			 Log.d("SMSManagerHome.onItemClick",debug+ex.toString());
			 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();

			return;
		}
	
	}


}

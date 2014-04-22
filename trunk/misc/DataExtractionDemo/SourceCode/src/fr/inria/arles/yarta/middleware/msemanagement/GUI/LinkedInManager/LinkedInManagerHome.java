/**
 * 
 * This package contains classes that display the extraction of 
 * information from a Linkedin Profile

 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.LinkedInManager;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.DeleteLinkedInAccessTokenStore;
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
 * An Android activity to display various options for data extraction 
 * from linkedin profile of a user.
 * <p>It is the home screen for
 * extraction of information from linkedin profile of user.</p>
 * It displays the various option related to extraction of data from linkedin
 * profile of user.
 * 
 * @author Nishant Kumar
 *
 */
public class LinkedInManagerHome extends Activity {

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.managerhome);
			
			YLogger Log = YLoggerFactory.getLogger();
	        
			String debug = " Inside LinkedInManagerHome ";
			Log.d("LinkedInManagerHome.onCreate",debug);

			debug = "Forming Columns for Menu of LinkedManagerHome";
			Log.d("LinkedInManagerHome.onCreate",debug);
			
			String[] linkedInManagerFields = getResources().getStringArray(R.array.linkedInManagerList);

			debug = "Forming List from findViewById";
			Log.d("LinkedInManagerHome.onCreate",debug);
			
			ListView list = (ListView)findViewById(R.id.managerHomeList);
			
			debug = "Forming arrayAdapter for ListView";
			Log.d("LinkedInManagerHome.onCreate",debug);
			
			ArrayAdapter<String> adapter = null;
			try{
			adapter = new ArrayAdapter<String>(this,R.layout.contactmanagerhome_list_text,
																	linkedInManagerFields);
						
			

			debug = "Setting ListView";
			Log.d("LinkedInManagerHome.onCreate",debug);
					
			list.setAdapter(adapter);
			
			debug = "ListView Displayed";
			Log.d("LinkedInManagerHome.onCreate",debug);

			}catch(Exception ex ){
					
					debug = "CATCH: Forming ArrayAdapter for LinkedInManager Home List";
					Log.d("LinkedInManagerHome.onCreate",debug+ex.toString());
			}
			
		//	TextView listHeader = (TextView) findViewById(R.id.HeaderTop);
		//	View view = new View(this);
			
		//	list.addHeaderView(listHeader);
			
			list.setOnItemClickListener(new OnItemClickListener() {
				
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			     
			   
			    	YLogger Log = YLoggerFactory.getLogger();
			      	String debug = "LinkedInManager Home ListView Clicked at "+position;
					Log.d("LinkedInManagerHome.onItemClick",debug);
					
			    	
			    	Intent intent = null;
			    	
			    	switch(position){
			    	
			    	case 0:		intent=	new Intent(LinkedInManagerHome.this,FirstTimeConnectLinkedInGUI.class);
			    				break;
			    	case 1:		intent=	new Intent(LinkedInManagerHome.this, DefaultConnectLinkedIn.class);
								break;
			    	case 2:		intent=	new Intent(LinkedInManagerHome.this, TestLinkedInAccessTokenStore.class);
								break;
			    
			    	case 3:		intent=	new Intent(LinkedInManagerHome.this, DeleteLinkedInAccessTokenStore.class);
								break;
			    
			    	default :   intent = null;
			    				break;
			    	};
			    	if(intent!=null)
			    		startActivity(intent);
			    	else{
			    		debug = "Cannot find position of click on ListView Menu.";
			 			Log.d("LinkedInManagerHome.onItemClick: ",debug);
			    	}
			    	
				}
		  });						
			
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			String debug = " CATCH LinkedInManagerHome: ";
			 Log.d("LinkedInManagerHome.onCreate",debug+ex.toString());
			 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();

			return;
		}
	
	}

}

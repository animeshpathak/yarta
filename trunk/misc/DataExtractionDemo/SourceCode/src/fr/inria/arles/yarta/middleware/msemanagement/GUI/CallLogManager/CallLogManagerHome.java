/**
 * This package contains classes for the GUI of 
 * Calllog extraction
 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.CallLogManager;

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
 * Home screen for data extraction from
 * call log of a Android phone.
 * <p>It is started when the user selects CallLog Processor
 * option on the Home Screen</p>
 * <p>It displays options for extraction of information from
 * CallLog of the user.</p>
 * 
 * @author Nishant Kumar
 *
 */
public class CallLogManagerHome extends Activity {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.managerhome);
		
			YLogger Log = YLoggerFactory.getLogger();
	        
			String debug = " Inside CallLogManagerHome ";
			Log.d("CallLogManagerHome.onCreate",debug);

			debug = "Forming Columns for Menu of ContactManagerHome";
			Log.d("CallLogManagerHome.onCreate",debug);
			
			String[] contactManagerFields = getResources().getStringArray(R.array.callLogManagerList);

			debug = "Forming List from findViewById";
			Log.d("CallLogManagerHome.onCreate",debug);
			
			ListView list = (ListView)findViewById(R.id.managerHomeList);
			
			debug = "Forming arrayAdapter for ListView";
			Log.d("CallLogManagerHome.onCreate",debug);
			
			ArrayAdapter<String> adapter = null;
			try{
			adapter = new ArrayAdapter<String>(this,R.layout.contactmanagerhome_list_text,
																	contactManagerFields);
						
			

			debug = "Setting ListView";
			Log.d("CallLogManagerHome.onCreate",debug);
					
			list.setAdapter(adapter);
			
			debug = "ListView Displayed";
			Log.d("CallLogManagerHome.onCreate",debug);

			}catch(Exception ex ){
					
					debug = "CATCH: Forming ArrayAdapter for CallLogManager Home List";
					Log.d("CallLogManagerHome.onCreate",debug+ex.toString());
			}
			
			list.setOnItemClickListener(new OnItemClickListener() {
				
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			     
			   
			    	YLogger Log = YLoggerFactory.getLogger();
			      	String debug = "CallLogManager Home ListView Clicked at "+position;
					Log.d("CallLogManagerHome. onItemClick",debug);
					
			    	
			    	Intent intent = null;
			    	
			    	switch(position){
			    	
			    	case 0:		intent=	new Intent(CallLogManagerHome.this, DisplayAllCallLog.class);
			    				break;
			    	case 1:		intent=	new Intent(CallLogManagerHome.this, DisplayCallLogByName.class);
								break;
			    	case 2:		intent=	new Intent(CallLogManagerHome.this, DisplayCallLogByNumber.class);
								break;									
			    	case 3:		intent=	new Intent(CallLogManagerHome.this, DisplayCallLogFriend.class);
								break;			    	
		
			    	default :   intent = null;
			    				break;
			    	};
			    	if(intent!=null)
			    		startActivity(intent);
			    	else{
			    		debug = "Cannot find position of click on ListView Menu.";
			 			Log.d("CallLogManagerHome. onItemClick: ",debug);
			    	}
			    	
				}
		  });						
			
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			String debug = " CATCH ContactManagerHome: ";
			 Log.d("CallLogManagerHome.onCreate",debug+ex.toString());
			 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();

			return;
		}
	}
}

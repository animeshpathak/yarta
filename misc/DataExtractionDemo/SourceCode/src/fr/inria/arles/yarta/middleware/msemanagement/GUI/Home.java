/**
This package contains classes for the home
screeen of thr GUI
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI;


import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.R;
import fr.inria.arles.yarta.middleware.msemanagement.GUI.CallLogManager.CallLogManagerHome;
import fr.inria.arles.yarta.middleware.msemanagement.GUI.ContactManager.ContactManagerHome;
import fr.inria.arles.yarta.middleware.msemanagement.GUI.FacebookManager.FacebookManagerHome;
import fr.inria.arles.yarta.middleware.msemanagement.GUI.LinkedInManager.LinkedInManagerHome;
import fr.inria.arles.yarta.middleware.msemanagement.GUI.SMSManager.SMSManagerHome;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View;

/**
 * The starting point of the GUI. 
 * It displays a list of option from where the information
 * is to be extracted.
 * <p>The user selects a particular option by clicking the image or text
 *     and the control is passes
 *    to the respective home screen
 *  </p>
    <p> List of options</p>
 * <ui>
 *      <li>Contact Processor</li>
 *      <li>CallLog Processor</li>
 *      <li>Message Processor</li>
 *      <li>LinkedIn Processor</li>
 *      <li>Facebook Processor</li>
 * </ui> 
 * 
 * 
 * 
 * @author Nishant Kumar
 *
 */
public class Home extends Activity {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.home);
			
			YLogger Log = YLoggerFactory.getLogger();
	        String debug = " Started DataExtractionManager ";
			Log.d("Home.onCreate",debug);
			
			debug = "Setting textView for Welcome Message";
			Log.d("Home.onCreate",debug);

			GridView gridview = (GridView) findViewById(R.id.gridview);
		    gridview.setAdapter(new ImageAdapter(this));
		    	
		    gridview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		            
		            YLogger Log = YLoggerFactory.getLogger();
			        String debug = "ListView Clicked at "+position;
					Log.d("Home.onCreate",debug);
					
			    	
			    	Intent intent = null;
			    	
			    	switch(position){
			    	
			    	case 0:		intent=	new Intent(Home.this, ContactManagerHome.class);
			    				break;
			    	case 1:		intent=	new Intent(Home.this, CallLogManagerHome.class);
								break;
			    	case 2:		intent=	new Intent(Home.this, SMSManagerHome.class);
								break;									
			    	case 3:		intent=	new Intent(Home.this, LinkedInManagerHome.class);
								break;			    	
			    	case 4:		intent=	new Intent(Home.this, FacebookManagerHome.class);
								break;			    	
			 
			    	default :   intent = null;
			    				break;
			    	};
			    	if(intent!=null)
			    		startActivity(intent);
			    	else{
			    		debug = "Cannot fid position of click on ListView Menu.";
			 			Log.d("Home.onCreate: ",debug);
			    	}

		    }
		       
		});

		
			debug = "Forming Columns for Data in ListView";
			Log.d("Home.onCreate",debug);
			
			String[] componentsDEM = getResources().getStringArray(R.array.DEMComponent);

			debug = "Forming List from findViewById";
			Log.d("Home.onCreate",debug);
			
			ListView list = (ListView)findViewById(R.id.optionList);
			
			debug = "Forming arrayAdapter for ListView";
			Log.d("Home.onCreate",debug);
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
																	R.layout.list_text_display,
																	componentsDEM);
			
			debug = "Setting ListView";
			Log.d("Home.onCreate",debug);
			
			list.setAdapter(adapter);
			
			debug = "ListView Displayed";
			Log.d("Home.onCreate",debug);
			
			list.setOnItemClickListener(new OnItemClickListener() {
				
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			     
			   // 	Toast.makeText(getApplicationContext(),((TextView) view).getText(),
			   // 		  												Toast.LENGTH_SHORT).show();
			    	YLogger Log = YLoggerFactory.getLogger();
			        String debug = "ListView Clicked at "+position;
					Log.d("Home.onItemClick",debug);
					
			    	
			    	Intent intent = null;
			    	
			    	switch(position){
			    	
			    	case 0:		intent=	new Intent(Home.this, ContactManagerHome.class);
			    				break;
			    	case 1:		intent=	new Intent(Home.this, CallLogManagerHome.class);
								break;
			    	case 2:		intent=	new Intent(Home.this, SMSManagerHome.class);
								break;									
			    	case 3:		intent=	new Intent(Home.this, LinkedInManagerHome.class);
								break;			    	
			        case 4:		intent=	new Intent(Home.this, FacebookManagerHome.class);
								break;			    	
   
			    	default :   intent = null;
			    				break;
			    	
			    	
			    	};
			    	if(intent!=null)
			    		startActivity(intent);
			    	else{
			    		debug = "Cannot find position of click on ListView Menu.";
			 			Log.d("Home.onItemClick: ",debug);
			    	}
			    }
			});
		}
		catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			String debug = "CATCH : Inside Home";
			Log.d("Home.onCreate: ",debug+ex.toString());
			  Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();

		}
	}
}

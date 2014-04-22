/**
 * This package contains the classes required to
 * display the GUI for a Contact Processor.
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.ContactManager;

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
 * An Android activity to display the option 
 * that user can choose to extract specific information
 * from the contact list of the Android phone.
 * 
 * 
 * @author Nishant Kumar
 *
 */
public class ContactManagerHome extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.managerhome);
		//	ProgressDialog progressWheel = ProgressDialog.show( this, "Processing" , " Please wait ... ", true,true);
	        
			YLogger Log = YLoggerFactory.getLogger();
	        
			String debug = " Inside ContactManagerHome ";
			Log.d("ContactManagerHome.onCreate",debug);

			debug = "Forming Columns for Menu of ContactManagerHome";
			Log.d("ContactManagerHome.onCreate",debug);
			
			String[] contactManagerFields = getResources().getStringArray(R.array.contactManagerList);

			debug = "Forming List from findViewById";
			Log.d("ContactManagerHome.onCreate",debug);
			
			ListView list = (ListView)findViewById(R.id.managerHomeList);
			
			debug = "Forming arrayAdapter for ListView";
			Log.d("ContactManagerHome.onCreate",debug);
			
			ArrayAdapter<String> adapter = null;
			try{
				adapter = new ArrayAdapter<String>(this,R.layout.contactmanagerhome_list_text,
																	contactManagerFields);
			
				debug = "Setting ListView";
				Log.d("ContactManagerHome.onCreate",debug);
					
				list.setAdapter(adapter);
			
				debug = "ListView Displayed";
				Log.d("ContactManagerHome.onCreate",debug);
			
				//	TextView listHeader = (TextView) findViewById(R.id.HeaderTop);
				//	View view = new View(this);
			
				//	list.addHeaderView(listHeader);
			}catch(Exception ex ){
				
				debug = "CATCH: Forming ArrayAdapter for CotactManager Home List";
				Log.d("ContactManagerHome.onCreate",debug+ex.toString());
				
			}
			
			
			list.setOnItemClickListener(new OnItemClickListener() {
				
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			     
			   
			    	YLogger Log = YLoggerFactory.getLogger();
			      	String debug = "ContactManager Home ListView Clicked at "+position;
					Log.d("ContactManagerHome.onItemClick",debug);
					
			    	
			    	Intent intent = null;
			    	
			    	switch(position){
			    	
			    	case 0:		intent=	new Intent(ContactManagerHome.this, FindAllContact.class);
			    				break;
			    	case 1:		intent=	new Intent(ContactManagerHome.this, FindContactByName.class);
								break;
			    	case 2:		intent=	new Intent(ContactManagerHome.this, FindContactByNumber.class);
								break;									
			    	case 3:		intent=	new Intent(ContactManagerHome.this, FindContactByEmail.class);
								break;			    	
			    	case 4:		intent=	new Intent(ContactManagerHome.this, FindNameByNumber.class);
								break;			    	

			      	case 5:		intent=	new Intent(ContactManagerHome.this, FindNameByEmail.class);
			      				break;
			      	case 6:		intent=	new Intent(ContactManagerHome.this, FindNumberByName.class);
			      				break;
			      	case 7:		intent=	new Intent(ContactManagerHome.this, FindNumberByEmail.class);
			      				break;									
			      	case 8:		intent=	new Intent(ContactManagerHome.this, FindEmailByName.class);
			      				break;			    	
			      	case 9:		intent=	new Intent(ContactManagerHome.this, FindEmailByNumber.class);
					break;			    	
					
			    	
			    	default :   intent = null;
			    				break;
			    	};
			    	if(intent!=null){
			    		debug = "Before ContactManagerHome to AllContact";
			    		Log.d("ContactManagerHome.onItemClick: ",debug);
		 			
			    		startActivity(intent);
			    		debug = "After ContactManagerHome to AllContact.";
			 			Log.d("ContactManagerHome.onItemClick: ",debug);
			    	}else{
			    		debug = "Cannot find position of click on ListView Menu.";
			 			Log.d("ContactManagerHome.onItemClick: ",debug);
			    	}
			    	
				}
		  });						
			
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			String debug = " CATCH ContactManagerHome: ";
			 Log.d("ContactManagerHome.onCreate",debug+ex.toString());
			 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();

			return;
		}
	
	}
}
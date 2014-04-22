/*** 
 * This package contains the classes required to
 * display the GUI for a Contact Processor.
 * 
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.ContactManager;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.ContactManager;
import fr.inria.arles.yarta.middleware.msemanagement.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity to display the name of a contact having a 
 * particular email address.
 *  
 * 
 * @author Nishant Kumar
 *
 */
public class FindNameByEmail extends Activity {
	
	String displayName;
	String debug;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.commonsearch);

			YLogger Log = YLoggerFactory.getLogger();
	        debug = " Inside GUI of FindNameByEmail";
			Log.d("FindNameByEmail.onCreate",debug);
			
			TextView searchTV = (TextView) findViewById(R.id.searchTV);
			searchTV.setText("Enter Email Address");
			
			Button searchB = (Button) findViewById(R.id.SearchButton);
			searchB.setOnClickListener(searchBClicked);
			
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			debug = "GUI of FindName By Email ";
			Log.d("FindNameByEmail.onCreate",debug+ex.toString());
			Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/**
	 * 
	 * The listner for search button. When started, it calls the 
	 * ContactManager.getDisplayNameByEmail of msemanagement package
	 * to search for the name for the given email address.
	 * 
	 */
	private final OnClickListener searchBClicked = new OnClickListener() {
		public void onClick(View v) {
			try {
				YLogger Log = YLoggerFactory.getLogger();
				debug = " Searched Button Clicked";
				Log.d("FindNameByEmail.searchBClicked",debug);
		
				EditText searchET = ((EditText) findViewById(R.id.searchET));
				String searchText = searchET.getText().toString();
				
				debug = " Searched text is "+searchET;
				Log.d("FindNameByEmail.searchBClicked",debug);
			
				if(searchText==null ||searchText.equals("")||searchText.indexOf("@")<0){
					Toast.makeText(getApplicationContext(),"Please Input a Valid Text",
																	Toast.LENGTH_SHORT).show();
					return;
				}
				
				ContactManager contactManager = new ContactManager();
				
				debug = " FindDisplay Name By Email objecd declare in GUI, going for search";
				Log.d("FindNameByEmail.searchBClicked",debug);
		
				displayName =	contactManager.getDisplayNameByEmail
															(getApplicationContext(), searchText);
				
				debug = "After Search, In GUI";
				Log.d("FindNameByEmail.searchBClicked",debug);
				
				ListView list = (ListView)findViewById(R.id.displayResult);
				
		        if(displayName==null){
		        	Toast.makeText(getApplicationContext(),
		        			"No Contact Obtained\n\n1. Exact Email Address required",
						
							Toast.LENGTH_LONG).show();
                    list.setAdapter(null);
		        	return;
		        }
		        
		        String []allDisplayName = new String [1];
		    	
		        debug = "Forming List from findViewById";
				Log.d("FindNameByEmail.searchBClicked",debug);
				
					allDisplayName[0] = displayName;
				
				
				debug = "Forming arrayAdapter for DisplaybyEmail Contact ListView";
				Log.d("FindNameByEmail.searchBClicked",debug);
				
				ArrayAdapter<String> adapter = null;
				try{
				adapter = new ArrayAdapter<String>(getApplicationContext(),
													R.layout.contactmanagerhome_list_text,
													allDisplayName);
				
				}catch(Exception ex){

					debug = "Array For Names cannot be formed";
					Log.d("FindNameByEmail.searchBClicked",debug);
					return;	
				}
				
			//	TextView listHeader = (TextView) findViewById(R.id.HeaderTop);
			//	View view = new View(this);
				
			//	list.addHeaderView(listHeader);
				
				debug = "Setting DisplayName ny Email ListView";
				Log.d("FindNameByEmail.searchBClicked",debug);
						
				list.setAdapter(adapter);
				
				debug = "All Contact ListView Displayed";
				Log.d("FindNameByEmail.searchBClicked",debug);
				
				
			} catch (Exception ex) {
				
				YLogger Log = YLoggerFactory.getLogger();
				debug = " CATCH Button Click of FindNameBBy Email ";
				Log.d("FindNameByEmail.searchBClicked",debug+ex.toString());
				 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();
				 return;
				
			}
		}
	};


}

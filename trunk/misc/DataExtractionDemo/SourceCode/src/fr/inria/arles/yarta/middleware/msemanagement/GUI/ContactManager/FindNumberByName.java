/**
 * * This package contains the classes used to
 * display the GUI for a Contact Processor.
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.ContactManager;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.ContactManager;
import fr.inria.arles.yarta.middleware.msemanagement.PhoneNumberStore;
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
 * Activity to display the phone number of a user with a 
 * given name.
 * 
 * It is started when a user selects 
 * Find Number by Name on the Contact Manager Home screen.
 * 
 * @author Nishant Kumar
 *
 */
public class FindNumberByName extends Activity {


	PhoneNumberStore [] phoneStore;
	String debug;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		try{
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.commonsearch);

			YLogger Log = YLoggerFactory.getLogger();
	        debug = " Inside GUI of FindNumberByNames";
			Log.d("FindNumberByName.onCreate",debug);
			
			TextView searchTV = (TextView) findViewById(R.id.searchTV);
			searchTV.setText("Enter Display Name");
			
			Button searchB = (Button) findViewById(R.id.SearchButton);
			searchB.setOnClickListener(searchBClicked);
			
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			debug = "GUI of FindNumber by Name ";
			Log.d("FindNumberByName.onCreate",debug+ex.toString());
			 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();
			 return;
			
		}
	}
	
	private final OnClickListener searchBClicked = new OnClickListener() {
		public void onClick(View v) {
		
			try {
				YLogger Log = YLoggerFactory.getLogger();
				debug = " Searched Button Clicked";
				Log.d("FindNumberByName.searchBClicked",debug);
		
				EditText searchET = ((EditText) findViewById(R.id.searchET));
				String searchText = searchET.getText().toString();
				
				debug = " Searched text is "+searchET;
				Log.d("FindNumberByName.searchBClicked",debug);
			
				if(searchText==null ||searchText.equals("")){
					Toast.makeText(getApplicationContext(),"Please Input a Valid Text",
																	Toast.LENGTH_SHORT).show();
					return;
				}
				
				ContactManager contactManager = new ContactManager();
				
				debug = " FindNumber By Name objecd declare in GUI, going for search";
				Log.d("FindNumberByName.searchBClicked",debug);
		
				phoneStore =	contactManager.getPhoneNumberByName
															(getApplicationContext(), searchText);
				
				debug = "After Search, In GUI";
				Log.d("FindNumberByName.searchBClicked",debug);
				
				ListView list = (ListView)findViewById(R.id.displayResult);
				
		        if(phoneStore==null){
		         	Toast.makeText(getApplicationContext(),
		        			"No Contact Obtained\n\n1. Exact Display Name Required"+
		        			"\n2. Case, Space Sensitive",
							Toast.LENGTH_LONG).show();
                    list.setAdapter(null);
		        	return;
		        }
		        
		        String []allDisplayName = new String [phoneStore.length+1];
		    	
		        debug = "Forming List from findViewById";
				Log.d("FindNumberByName.searchBClicked",debug);
				
				allDisplayName[0] = searchText;
				
				for(int k = 0;k<phoneStore.length;k++)
				{	allDisplayName[k+1]=phoneStore[k].phoneNumberType+": "+
										phoneStore[k].phoneNumber;
				debug = "Number Stored in Array is ";
				Log.d("FindNumberByName.searchBClicked",debug+allDisplayName[k]);
				
				}
				debug = "Forming arrayAdapter for DisplaybyEmail Contact ListView";
				Log.d("FindNumberByName.searchBClicked",debug);
				
				ArrayAdapter<String> adapter = null;
				try{
				adapter = new ArrayAdapter<String>(getApplicationContext(),
													R.layout.contactmanagerhome_list_text,
													allDisplayName);
				

				debug = "Setting Number by Name ListView";
				Log.d("FindNumberByName.searchBClicked",debug);
						
				list.setAdapter(adapter);
				
				debug = "All Contact ListView Displayed";
				Log.d("FindNumberByName.searchBClicked",debug);
				
				
				}catch(Exception ex){

					debug = "Array For Display cannot be formed";
					Log.d("FindNumberByName.searchBClicked",debug);
					return;	
				}
				
			//	TextView listHeader = (TextView) findViewById(R.id.HeaderTop);
			//	View view = new View(this);
				
			//	list.addHeaderView(listHeader);
				
				
			} catch (Exception ex) {
				
				YLogger Log = YLoggerFactory.getLogger();
				debug = "Button Click of FindNumber By Name";
				Log.d("FindNumberByName.searchBClicked",debug+ex.toString());
				 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();

				return;
			}
		}
	};


}

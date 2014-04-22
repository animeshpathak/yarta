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
 * 
 * Activity to display the number of a contact in contact 
 * list having a given email address.
 * 
 * 
 * @author Nishant Kumar
 *
 */
public class FindNumberByEmail extends Activity {

	PhoneNumberStore [] phoneStore=null;
	String debug = "";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.commonsearch);

			YLogger Log = YLoggerFactory.getLogger();
	        debug = " Inside GUI of FindNumberByEmail";
			Log.d("FindNumberByEmail.onCreate",debug);
			
			TextView searchTV = (TextView) findViewById(R.id.searchTV);
			searchTV.setText("Enter Email Address");
			
			Button searchB = (Button) findViewById(R.id.SearchButton);
			searchB.setOnClickListener(searchBClicked);
			
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			debug = " CATCH GUI of FindNumber by Email ";
			Log.d("FindNumberByEmail.onCreate",debug+ex.toString());
			 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();
			 return;
			
		}
	}
	
	/**
	 * The listener for the search button. This listener calls the 
	 * ContactManager.getPhoneNumberByEmail of the msemanagement package
	 * to find the phone number for the given email address of a contact.
	 *  
	 * 
	 */
	private final OnClickListener searchBClicked = new OnClickListener() {
		public void onClick(View v) {
		
			try {
				YLogger Log = YLoggerFactory.getLogger();
				debug = " Searched Button Clicked";
				Log.d("FindNumberByEmail.searchBClicked",debug);
		
				EditText searchET = ((EditText) findViewById(R.id.searchET));
				String searchText = searchET.getText().toString();
				
				debug = " Searched text is "+searchET;
				Log.d("FindNumberByEmail.searchBClicked",debug);
			
				if(searchText==null ||searchText.equals("")||searchText.indexOf("@")<0){
					Toast.makeText(getApplicationContext(),"Please Input a Valid Text",
																	Toast.LENGTH_SHORT).show();
					return;
				}
				
				ContactManager contactManager = new ContactManager();
				
				debug = " FindNumber By EMail objecd declare in GUI, going for search";
				Log.d("FindNumberByEmail.searchBClicked",debug);
		
				phoneStore =	contactManager.getPhoneNumberByEmail
															(getApplicationContext(), searchText);
				
				debug = "After Search, In GUI";
				Log.d("FindNumberByEmail.searchBClicked",debug);
				
				ListView list = (ListView)findViewById(R.id.displayResult);
				
		        if(phoneStore==null){
		         	Toast.makeText(getApplicationContext(),
		        			"No Contact Obtained\n\n1. Check The email Address",
		        			
							Toast.LENGTH_LONG).show();
                    list.setAdapter(null);
		        	return;
		        }
		        
		        String []allDisplayName = new String [phoneStore.length+1];
		    	
		        debug = "Forming List from findViewById";
				Log.d("FindNumberByEmail.searchBClicked",debug);
				
				allDisplayName[0] = searchText;
				
				for(int k = 0;k<phoneStore.length;k++)
					allDisplayName[k+1]=phoneStore[k].phoneNumberType+": "+
										phoneStore[k].phoneNumber;
				
				debug = "Forming arrayAdapter for DisplayNumberbyEmail Contact ListView";
				Log.d("FindNumberByEmail.searchBClicked",debug);
				
				ArrayAdapter<String> adapter = null;
				try{
				adapter = new ArrayAdapter<String>(getApplicationContext(),
													R.layout.contactmanagerhome_list_text,
													allDisplayName);
				
				}catch(Exception ex){

					debug = "Array For Display cannot be formed";
					Log.d("FindNumberByEmail.searchBClicked",debug);
					return;	
				}
				
			//	TextView listHeader = (TextView) findViewById(R.id.HeaderTop);
			//	View view = new View(this);
				
			//	list.addHeaderView(listHeader);
				
				debug = "Setting Number by Email ListView";
				Log.d("FindNumberByEmail.searchBClicked",debug);
						
				list.setAdapter(adapter);
				
				debug = "All Contact ListView Displayed";
				Log.d("FindNumberByEmail.searchBClicked",debug);
				
				
			} catch (Exception ex) {
				
				YLogger Log = YLoggerFactory.getLogger();
				debug = " CATCH Button Click of FindNumber By Email";
				Log.d("FindNumberByEmail.searchBClicked",debug+ex.toString());
				 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();
				 return;
				
			}
		}
	};


}

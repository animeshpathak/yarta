/**
 * * This package contains the classes used to
 * display the GUI for a Contact Processor.
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
 * Activity to display the name of the contact that has the given mobile number.
 * 
 * It is started when the user selects "Display Name by Email"
 * on the Contactmanager home screen.
 * 
 * 
 * 
 * @author Nishant Kumar
 *
 */
public class FindNameByNumber extends Activity {

	String displayName=null;
	String debug = "";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.commonsearch);

			YLogger Log = YLoggerFactory.getLogger();
	        debug = " Inside GUI of FindNameByNumber";
			Log.d("FindNameByNumber.onCreate",debug);
			
			TextView searchTV = (TextView) findViewById(R.id.searchTV);
			searchTV.setText("Enter phone number");
			
			Button searchB = (Button) findViewById(R.id.SearchButton);
			searchB.setOnClickListener(searchBClicked);
			
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			debug = "GUI of FindName By Number ";
			Log.d("FindNameByNumber.onCreate",debug+ex.toString());
			 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();
			 return;
			
		}
	}
	
	/**
	 * Listener for the search button click.
	 * When executed, it calls the ContactManager.getDisplayNameByNumber
	 * method of msemanagement package.
	 * 
	 */
	private final OnClickListener searchBClicked = new OnClickListener() {
		public void onClick(View v) {
		
			try {
				YLogger Log = YLoggerFactory.getLogger();
				debug = " Searched Button Clicked";
				Log.d("FindNameByNumber.searchBClicked",debug);
		
				EditText searchET = ((EditText) findViewById(R.id.searchET));
				String searchText = searchET.getText().toString();
				
				debug = " Searched text is "+searchET;
				Log.d("FindNameByNumber.searchBClicked",debug);
			
				if(searchText==null ||searchText.equals("")){
					Toast.makeText(getApplicationContext(),"Please Input a Valid Text",
																	Toast.LENGTH_SHORT).show();
					return;
				}
				
				ContactManager contactManager = new ContactManager();
				
				debug = " FindDisplay Name By Number objecd declare in GUI, going for search";
				Log.d("FindNameByNumber.searchBClicked",debug);
		
				displayName =	contactManager.getDisplayNameByNumber
															(getApplicationContext(), searchText);
				
				debug = "After Search, In GUI";
				Log.d("FindNameByNumber.searchBClicked",debug);
				
				ListView list = (ListView)findViewById(R.id.displayResult);
				
		        if(displayName==null){
		        	Toast.makeText(getApplicationContext(),
		        			"No Contact Obtained\n\n1. Exact Number Required"+
		        			"\n2. Take Care of Dash (-), CountryCode (+33) ",
						
							Toast.LENGTH_LONG).show();
                    list.setAdapter(null);
		        	return;
		        }
		        
		        String []allDisplayName = new String [1];
		    	
		        debug = "Forming List from findViewById";
				Log.d("FindNameByNumber.searchBClicked",debug);
				
					allDisplayName[0] = displayName;
				
				
				debug = "Forming arrayAdapter for DisplaybyEmail Contact ListView";
				Log.d("FindNameByNumber.searchBClicked",debug);
				
				ArrayAdapter<String> adapter = null;
				try{
				adapter = new ArrayAdapter<String>(getApplicationContext(),
													R.layout.contactmanagerhome_list_text,
													allDisplayName);
				
				}catch(Exception ex){

					debug = "Array For Names cannot be formed";
					Log.d("FindNameByNumber.searchBClicked",debug);
					 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();

					return;	
				}
				
				
			//	TextView listHeader = (TextView) findViewById(R.id.HeaderTop);
			//	View view = new View(this);
				
			//	list.addHeaderView(listHeader);
				
				debug = "Setting DisplayName ny Number ListView";
				Log.d("FindNameByNumber.searchBClicked",debug);
						
				list.setAdapter(adapter);
				
				debug = "All Contact ListView Displayed";
				Log.d("FindNameByNumber.searchBClicked",debug);
				
				
			} catch (Exception ex) {
				
				YLogger Log = YLoggerFactory.getLogger();
				debug = "Button Click of FindContactByName ";
				Log.d("FindNameByNumber.searchBClicked",debug+ex.toString());
				 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();
                 return;
				
			}
		}
	};

}

/**
 *  This package contains the classes used to
 * display the GUI for a Contact Processor.
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.ContactManager;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.ContactManager;
import fr.inria.arles.yarta.middleware.msemanagement.EmailStore;
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
 * Activity to display the email 
 * address of a contact by a given mobile number
 * 
 * <p>It calls ContactManager.getEmailByPhoneNumber
 *    of the msemanagement package to get the email address for the 
 *    contact having the given number
 * </p>
 * 
 * @author Nishant Kumar
 *
 */
public class FindEmailByNumber extends Activity {

	EmailStore [] emailStore=null;
	String debug = "";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.commonsearch);

			YLogger Log = YLoggerFactory.getLogger();
	        debug = " Inside GUI of FindEmailByNumber";
			Log.d("FindEmailByNumber.onCreate",debug);
			
			TextView searchTV = (TextView) findViewById(R.id.searchTV);
			searchTV.setText("Enter phone number");
			
			Button searchB = (Button) findViewById(R.id.SearchButton);
			searchB.setOnClickListener(searchBClicked);
			
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			debug = "GUI of FindEmail by Number ";
			Log.d("FindEmailByNumber.onCreate",debug+ex.toString());
			return;
		}
	}
	
	
	/**
	 *  Listener for  search button click.
	 *  <p>It calls the ContactManager.getEmailByPhoneNumber to find the email address 
	 *  for the given phone  number 
	 *   </p>
	 *   
	 *  
	 */
	private final OnClickListener searchBClicked = new OnClickListener() {
		public void onClick(View v) {
		
			try {
				YLogger Log = YLoggerFactory.getLogger();
				debug = " Searched Button Clicked";
				Log.d("FindEmailByNumber.onCreate",debug);
		
				EditText searchET = ((EditText) findViewById(R.id.searchET));
				String searchText = searchET.getText().toString();
				
				debug = " Searched text is "+searchET;
				Log.d("FindEmailByNumber.onCreate",debug);
			
				if(searchText==null ||searchText.equals("")){
					Toast.makeText(getApplicationContext(),"Please Input a Valid Text",
																	Toast.LENGTH_SHORT).show();
					return;
				}
				
				ContactManager contactManager = new ContactManager();
				
				debug = " FindEmail By Number objecd declare in GUI, going for search";
				Log.d("FindEmailByNumber.onCreate",debug);
		
				emailStore =	contactManager.getEmailByPhoneNumber
															(getApplicationContext(), searchText);
				
				debug = "After Search, In GUI";
				Log.d("FindEmailByNumber.onCreate",debug);
				
				ListView list = (ListView)findViewById(R.id.displayResult);
				
		        if(emailStore==null){
		        	Toast.makeText(getApplicationContext(),
		        			"No Contact Obtained\n\n1. Exact Number Required"+
		        			"\n2. Take Care of Dash (-), CountryCode (+33) ",
						
							Toast.LENGTH_LONG).show();
                    list.setAdapter(null);
		        	return;
		        }
		        
		        String []allDisplayName = new String [emailStore.length+1];
		    	
		        debug = "Forming List from findViewById";
				Log.d("FindEmailByNumber.onCreate",debug);
				
				allDisplayName[0] ="  Email Address:";
				
				for(int k = 0;k<emailStore.length;k++)
					allDisplayName[k+1]=emailStore[k].emailType+": "+
										emailStore[k].emailAddress;
				
				debug = "Forming arrayAdapter for DisplayEmailbyName Contact ListView";
				Log.d("FindEmailByNumber.onCreate",debug);
				
				ArrayAdapter<String> adapter = null;
				try{
				adapter = new ArrayAdapter<String>(getApplicationContext(),
													R.layout.contactmanagerhome_list_text,
													allDisplayName);
				
				}catch(Exception ex){

					debug = "Array For Display cannot be formed";
					Log.d("FindEmailByNumber.onCreate",debug);
					
					return;	
				}
				
			//	TextView listHeader = (TextView) findViewById(R.id.HeaderTop);
			//	View view = new View(this);
				
			//	list.addHeaderView(listHeader);
				
				debug = "Setting Email by Number ListView";
				Log.d("FindEmailByNumber.onCreate",debug);
						
				list.setAdapter(adapter);
				
				debug = "All Contact ListView Displayed";
				Log.d("FindEmailByNumber.onCreate",debug);
				
				
			} catch (Exception ex) {
				
				YLogger Log = YLoggerFactory.getLogger();
				debug = " Button Click of FindEmail By Number";
				Log.d("FindEmailByNumber.onCreate",debug+ex.toString());
				 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();

				return;
			}
		}
	};




}

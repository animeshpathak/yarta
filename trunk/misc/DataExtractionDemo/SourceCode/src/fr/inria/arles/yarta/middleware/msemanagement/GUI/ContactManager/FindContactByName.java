/**
 * * This package contains the classes used to
 * display the GUI for a Contact Processor.
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.ContactManager;


import java.util.ArrayList;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.ContactInformationStore;
import fr.inria.arles.yarta.middleware.msemanagement.ContactManager;
import fr.inria.arles.yarta.middleware.msemanagement.R;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Activity to display the contact in the android phone having a given name.
 * 
 * 
 * It is started when the user selects the option
 * "Display Contact By Name" on the ContactManager Home page.
 * 
 * @author Nishant Kumar
 *
 */
public class FindContactByName extends Activity {

	
	
	ArrayList<ContactInformationStore> contactInfoArrayStore;
	String debug;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.commonsearch);

			contactInfoArrayStore = null;
			debug = "";			
	
			YLogger Log = YLoggerFactory.getLogger();
	        debug = " Inside GUI of FindContactByName ";
			Log.d("FindContactByName.onCreate",debug);
			
			TextView searchTV = (TextView) findViewById(R.id.searchTV);
			searchTV.setText("Enter Name");
			
			Button searchB = (Button) findViewById(R.id.SearchButton);
			searchB.setOnClickListener(searchBClicked);
			
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			debug = "FindContactByName ";
			Log.d("FindContactByName.onCreate",debug+ex.toString());
			Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/**
	 *  Listener for search button.
	 *  <p>It calls the ContactManger.findContactByDisplayName
	 *  method in msemanagement package and obtain the result. </p>
	 *  <p>The result is then displayed in the listview of the activity</p>
	 */
	private final OnClickListener searchBClicked = new OnClickListener() {
		public void onClick(View v) {
		
			try {
				YLogger Log = YLoggerFactory.getLogger();
				debug = " Searched Button Clicked";
				Log.d("FindContactByName.searchBClicked",debug);
		
				EditText searchET = ((EditText) findViewById(R.id.searchET));
				String searchText = searchET.getText().toString();
				
				debug = " Searched text is "+searchET;
				Log.d("FindContactByName.searchBClicked",debug);
			
				if(searchText==null ||searchText.equals("") ){
					Toast.makeText(getApplicationContext(),"Please Input a Valid Text",
																	Toast.LENGTH_SHORT).show();
					return;
				}
				
				ContactManager contactManager = new ContactManager();
				
				debug = " FindContact By Name object declare in GUI, going for search";
				Log.d("FindContactByName.searchBClicked",debug);
		
				contactInfoArrayStore =	contactManager.findContactByDisplayName
															(getApplicationContext(), searchText);
				
				debug = "After Search, In GUI";
				Log.d("FindContactByName.searchBClicked",debug);
				

				ListView list = (ListView)findViewById(R.id.displayResult);
				
		        if(contactInfoArrayStore==null ||contactInfoArrayStore.size()<=0){
		        	Toast.makeText(getApplicationContext(),
		        			"No Contact Obtained\n\n1. Exact Display Name Required"+
		        			"\n2. Case, Space Sensitive",
							Toast.LENGTH_LONG).show();
		        	
		        	list.setAdapter(null);
		        	return;
		        }
		        
		        String []allDisplayName = new String [contactInfoArrayStore.size()];
		    	
		        debug = "Forming List from findViewById";
				Log.d("FindContactByName.searchBClicked",debug);
				
				for(int k = 0;k<contactInfoArrayStore.size();k++)
					allDisplayName[k] = contactInfoArrayStore.get(k).fullName;
				
				debug = "Forming arrayAdapter for DisplaybyName Contact ListView";
				Log.d("FindContactByName.searchBClicked",debug);
				
				ArrayAdapter<String> adapter = null;
				try{
					
				adapter = new ArrayAdapter<String>(getApplicationContext(),
													R.layout.contactmanagerhome_list_text,
													allDisplayName);
				
				}catch(Exception ex){

					debug = "Array For Names cannot be formed";
					Log.d("FindContactByName.searchBClicked",debug);
					return;	
				}
				
				
			//	TextView listHeader = (TextView) findViewById(R.id.HeaderTop);
			//	View view = new View(this);
				
			//	list.addHeaderView(listHeader);
				
				debug = "Setting FindAllContact ListView";
				Log.d("FindContactByName.searchBClicked",debug);
						
				list.setAdapter(adapter);
				
				debug = "All Contact ListView Displayed";
				Log.d("FindContactByName.searchBClicked",debug);
				
				
				list.setOnItemClickListener(new OnItemClickListener() {
					
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				     
				 //   	Toast.makeText(getApplicationContext(),((TextView) view).getText(),
				 //   		  												Toast.LENGTH_SHORT).show();
				
					
				    	YLogger Log = YLoggerFactory.getLogger();
				      	String debug = "All Contact display Clicked at "+position;
						Log.d("FindContactByName.onItemClick",debug);
						
						removeDialog(position);
						
						showDialog(position);
						debug = " After shown dialog ";
						Log.d("FindContactByName.onItemClick",debug);
						
						
					}
			  });						

				
			} catch (Exception ex) {
				
				YLogger Log = YLoggerFactory.getLogger();
				debug = "Button Click of FindContactByName ";
				Log.d("FindContactByName.searchBClicked",debug+ex.toString());
				 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();
                return;
			}
		}
	};

/**
 * The dialog box shown when the user clicks on a contact shown in the result
 */
	  protected Dialog onCreateDialog(int position){
		
		  if(contactInfoArrayStore==null)
			  return null;
		  YLogger Log = YLoggerFactory.getLogger();
	        		  
			String debug = " IN Create diagol ";
			Log.d("FindContactByName.onCreateDialog",debug);

			ContactInformationStore contactInfo = new ContactInformationStore();
			contactInfo = contactInfoArrayStore.get(position);
			debug = " Object fetched for position "+position;
			Log.d("FindContactByName.onCreateDialog",debug);

			Dialog dialog = new Dialog(this);
			debug = " getting dialog ";
			Log.d("FindContactByName.onCreateDialog",debug);

			dialog.setContentView(R.layout.contactcustomdialog);
			debug = " Setting title ";
			Log.d("FindContactByName.onCreateDialog",debug);

			dialog.setTitle("Contact Detail");

			debug = " Title set";
			Log.d("FindContactByName.onCreateDialog",debug);

			debug = " finding text View for name";
			Log.d("FindContactByName.onCreateDialog",debug);

			TextView textName = (TextView) dialog.findViewById(R.id.customDialogNameTop);
			textName.setText(contactInfo.fullName);
			
			debug = " finding text view for phone ";
			Log.d("FindContactByName.onCreateDialog",debug);
			
			TextView textPhoneLabel = (TextView) dialog.findViewById(R.id.customDialogPhoneTop);
			TextView textPhoneNumber = (TextView) dialog.findViewById(R.id.customDialogPhoneBottom);
			
			if(contactInfo.phoneNumberStore!=null){
				debug = " Setting phone number ";
				Log.d("FindContactByEmail.onCreateDialog",debug);
				String toDisplayNumber="";
				for(int j=0;j<contactInfo.phoneNumberStore.length;j++){
					toDisplayNumber+=contactInfo.phoneNumberStore[j].phoneNumberType+
					                ":"+ contactInfo.phoneNumberStore[j].phoneNumber+
					                "\n";
				}
				textPhoneLabel.setText("PhoneNumber:");
				textPhoneNumber.setText(toDisplayNumber);
			}else{
				textPhoneLabel.setText("Phone Number");
				textPhoneNumber.setText("No Number");
				
			}
			debug = " finding text View for email ";
			Log.d("FindContactByEmail.onCreateDialog",debug);
			                                                      					
			TextView textEmailLabel = (TextView) dialog.findViewById(R.id.customDialogEmailTop);
			TextView textEmail = (TextView) dialog.findViewById(R.id.customDialogEmailBottom);
			                                                       
			if(contactInfo.emailStore!=null){
				debug = " Setting email Address ";
				Log.d("FindContactByEmail.onCreateDialog",debug);
				String toDisplayEmail="";
				for(int j=0;j<contactInfo.emailStore.length;j++){
					toDisplayEmail+=contactInfo.emailStore[j].emailType+
					                ":"+ contactInfo.emailStore[j].emailAddress+
					                "\n";
				}
				
				textEmailLabel.setText("Email:");
				textEmail.setText(toDisplayEmail);
			}else{
				textEmailLabel.setText("Email Address");
				textEmail.setText("No Email");
				
			}
						debug = "returning dialog ";
			Log.d("FindContactByName.onCreateDialog",debug);

			return dialog;
	  }
	
}

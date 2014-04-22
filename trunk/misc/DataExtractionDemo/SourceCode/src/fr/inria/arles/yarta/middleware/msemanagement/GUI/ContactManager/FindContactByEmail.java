/**
 ** This package contains the classes used to
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
 * Activity to Display contact by email address of a contact
 * 
 * 
 * This activity calls the ContactManager.findContactByEmail method of
 * msemanagement package to display the result.
 * 
 * @author Nishant Kumar
 *
 */
public class FindContactByEmail extends Activity {

	

	ArrayList<ContactInformationStore> contactInfoArrayStore;
	String debug;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.commonsearch);

			YLogger Log = YLoggerFactory.getLogger();
	        debug = " Inside GUI of FindContactByEmail";
			Log.d("FindContactByEmail.onCreate",debug);
			
			TextView searchTV = (TextView) findViewById(R.id.searchTV);
			searchTV.setText("Enter Email Address");
			
			Button searchB = (Button) findViewById(R.id.SearchButton);
			searchB.setOnClickListener(searchBClicked);
			
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			debug = " GUI of FindContactByEmail ";
			Log.d("FindContactByEmail.onCreate",debug+ex.toString());
			 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();

		}
	}
	
	
	/**
	 * The button listener called when the search button is clicked.
	 * <p>
	 * It makes a call to ContactManager.findContactByEmail method
	 * of msemanagement package to extract the contact having the given
	 * email address.
	 * </p><p>
	 * If any result is found, it is displayed in the listview of this activity.
	 * </p>
	 */
	private final OnClickListener searchBClicked = new OnClickListener() {
		public void onClick(View v) {
		
			try {
				YLogger Log = YLoggerFactory.getLogger();
				debug = " Searched Button Clicked";
				Log.d("FindContactByEmail.OnClickListener",debug);
		
				EditText searchET = ((EditText) findViewById(R.id.searchET));
				String searchText = searchET.getText().toString();
				
				debug = " Searched text is "+searchET;
				Log.d("FindContactByEmail.OnClickListener",debug);
			
				if(searchText==null ||searchText.equals("")||searchText.indexOf("@")<0 ){
					Toast.makeText(getApplicationContext(),"Please Input a Valid Text",
																	Toast.LENGTH_SHORT).show();
					return;
				}
				
				ContactManager contactManager = new ContactManager();
				
				debug = " FindContact By Email objecd declare in GUI, going for search";
				Log.d("FindContactByEmail.OnClickListener",debug);
		
				contactInfoArrayStore =	contactManager.findContactByEmail
															(getApplicationContext(), searchText);
				
				debug = "After Search, In GUI";
				Log.d("FindContactByEmail.OnClickListener",debug);
				
				ListView list = (ListView)findViewById(R.id.displayResult);
				
		        if(contactInfoArrayStore==null ||contactInfoArrayStore.size()<=0){
		        	Toast.makeText(getApplicationContext(),
		        			"No Contact Obtained\n\n1. Exact Email Required",
						
							Toast.LENGTH_LONG).show();
                    list.setAdapter(null);
		        	return;
		        }
		        
		        String []allDisplayName = new String [contactInfoArrayStore.size()];
		    	
		        debug = "Forming List from findViewById";
				Log.d("FindContactByEmail.OnClickListener",debug);
				
				for(int k = 0;k<contactInfoArrayStore.size();k++)
					allDisplayName[k] = contactInfoArrayStore.get(k).fullName;
				
				
				debug = "Forming arrayAdapter for DisplaybyEmail Contact ListView";
				Log.d("FindContactByEmail.OnClickListener",debug);
				
				ArrayAdapter<String> adapter = null;
				try{
				adapter = new ArrayAdapter<String>(getApplicationContext(),
													R.layout.contactmanagerhome_list_text,
													allDisplayName);
				
				}catch(Exception ex){

					debug = "Array For Names cannot be formed";
					Log.d("FindContactByEmail.OnClickListener",debug);
					return;	
				}
				
				
			//	TextView listHeader = (TextView) findViewById(R.id.HeaderTop);
			//	View view = new View(this);
				
			//	list.addHeaderView(listHeader);
				
				debug = "Setting FindContact Email ListView";
				Log.d("FindContactByEmail.OnClickListener",debug);
						
				list.setAdapter(adapter);
				
				debug = "All Contact ListView Displayed";
				Log.d("FindContactByEmail.OnClickListener",debug);
				
				list.setOnItemClickListener(new OnItemClickListener() {
					
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				     
				 //   	Toast.makeText(getApplicationContext(),((TextView) view).getText(),
				 //   		  												Toast.LENGTH_SHORT).show();
				
					
				    	YLogger Log = YLoggerFactory.getLogger();
				      	String debug = "All Contact display Clicked at "+position;
						Log.d("FindContactByEmail.onItemClick",debug);
						
						removeDialog(position);
						
						showDialog(position);
						debug = " After shown dialog ";
						Log.d("FindContactByEmail.onItemClick",debug);
						
						
					}
			  });						

				
			} catch (Exception ex) {
				
				YLogger Log = YLoggerFactory.getLogger();
				debug = "Button Click of FindContactByEmail ";
				Log.d("FindContactByEmail.OnClickListener",debug+ex.toString());
				 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();

				
			}
		}
	};

	
/**
 * The dialog shown when a user clicks on any contact found.
 */
	  protected Dialog onCreateDialog(int position){
		
		  if(contactInfoArrayStore==null)
			  return null;
		  YLogger Log = YLoggerFactory.getLogger();
	        		  
			String debug = " IN Create diagol ";
			Log.d("FindContactByEmail.onCreateDialog",debug);

			ContactInformationStore contactInfo = new ContactInformationStore();
			contactInfo = contactInfoArrayStore.get(position);
			debug = " Object fetched for position "+position;
			Log.d("FindContactByEmail.onCreateDialog",debug);

			Dialog dialog = new Dialog(this);
			debug = " getting dialog ";
			Log.d("FindContactByEmail.onCreateDialog",debug);

			dialog.setContentView(R.layout.contactcustomdialog);
			debug = " Setting title ";
			Log.d("FindContactByEmail.onCreateDialog",debug);

			dialog.setTitle("Contact Detail");

			debug = " Title set";
			Log.d("FindContactByEmail.onCreateDialog",debug);

			debug = " finding text View for name";
			Log.d("FindContactByEmail.onCreateDialog",debug);

			TextView textName = (TextView) dialog.findViewById(R.id.customDialogNameTop);
			textName.setText(contactInfo.fullName);
			
			debug = " finding text view for phone ";
			Log.d("FindContactByEmail.onCreateDialog",debug);
			
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
			Log.d("FindContactByEmail.onCreateDialog",debug);

			return dialog;
	  }

}

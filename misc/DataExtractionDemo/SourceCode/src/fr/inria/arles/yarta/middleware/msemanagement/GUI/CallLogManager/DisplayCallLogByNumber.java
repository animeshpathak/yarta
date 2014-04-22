/**
 *  This package contains classes for the GUI of 
 * Callog extraction
 */
package fr.inria.arles.yarta.middleware.msemanagement.GUI.CallLogManager;

import java.util.ArrayList;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.CallLogInformationStore;
import fr.inria.arles.yarta.middleware.msemanagement.CallLogManager;
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
 * 
 * Activity to display all call log by a number
 * <p>It uses the method CallLogManager.getCallHistoryByNumber of msemanagement
 * package to search the call log for the given number </p> 
 * <p>The result obtained is displayed in the listview</p>
 * 
 * @author Nishant Kumar
 *
 */
public class DisplayCallLogByNumber extends Activity {
	
	ArrayList<CallLogInformationStore> callLogInfoArrayStore;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.commonsearch);
			callLogInfoArrayStore = null;
			
			
			YLogger Log = YLoggerFactory.getLogger();
	        String debug = " *********************Inside DisplayCallLogByNumberGUI ";
			Log.d("DisplayCallLogByNumber.onCreate",debug);

			TextView searchTV = (TextView) findViewById(R.id.searchTV);
			searchTV.setText("Enter Number");
			
			
			Button searchB = (Button) findViewById(R.id.SearchButton);
			searchB.setOnClickListener(searchBClicked);

		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			String debug = " CATCH GUI of FindCallLogByNumber ";
			Log.d("DisplayCallLogByNumber.onCreate",debug+ex.toString());
			  Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();

			
		}
	}
				

	/**
	 * It calls the CallLogManager.getCallHistoyByNumber
	 *    of the msemanagement package and then the result is displayed
	 * <p>
	 * It is called when the button is clicked
	 * to start the search for the call log.
	 *    
	 * </p>
	 */
	private final OnClickListener searchBClicked = new OnClickListener() {
		public void onClick(View v) {
		
			try {
				YLogger Log = YLoggerFactory.getLogger();
				String debug = " Searched Button Clicked";
				Log.d("DisplayCallLogByNumber.OnClickListener",debug);
		
				EditText searchET = ((EditText) findViewById(R.id.searchET));
				String searchText = searchET.getText().toString();
				
				debug = " Searched text is "+searchET;
				Log.d("DisplayCallLogByNumber.OnClickListener",debug);
			
				if(searchText==null ||searchText.equals("") ){
					Toast.makeText(getApplicationContext(),"Please Input a Valid Text",
																	Toast.LENGTH_SHORT).show();
					return;
				}
	
				CallLogManager callLogManager = new CallLogManager();
			
				callLogInfoArrayStore = callLogManager.getCallHistoryByNumber
												(getApplicationContext(),searchText);
			
				
				ListView list = (ListView)findViewById(R.id.displayResult);
				
				if(callLogInfoArrayStore==null || callLogInfoArrayStore.size()<=0){
					Toast.makeText(getApplicationContext(),
		        			"No Contact Obtained\n\n1. Exact Number Required"+
		        			"\n2. Take Care of Dash (-), CountryCode (+33) ",
						
							Toast.LENGTH_LONG).show();	
		        	list.setAdapter(null);
		        	return;
		        
				
				}
				
				
				
				String[] allDisplayName = new String[callLogInfoArrayStore.size()];
				
				debug = "Forming List from findViewById";
				Log.d("DisplayCallLogByNumber.OnClickListener",debug);
				
				for(int k = 0;k<callLogInfoArrayStore.size();k++)
					allDisplayName[k] = callLogInfoArrayStore.get(k).fullName;
				
				debug = "Forming arrayAdapter for DisplayResult ListView";
				Log.d("DisplayCallLogByNumber.OnClickListener",debug);
				
				ArrayAdapter<String> adapter = null;
				try{
					adapter = new ArrayAdapter<String>(getApplicationContext(),
														R.layout.contactmanagerhome_list_text,
														allDisplayName);
	
					debug = "Setting ListView";
					Log.d("DisplayCallLogByNumber.OnClickListener",debug);
							
					list.setAdapter(adapter);
					
					debug = "All Contact ListView Displayed";
					Log.d("DisplayCallLogByNumber.OnClickListener",debug);
					
				
				}catch(Exception ex){

					debug = "Array For Names cannot be formed";
					Log.d("DisplayCallLogByNumber.OnClickListener",debug+ex.toString());
					  Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();

						
				}
				
				
			//	TextView listHeader = (TextView) findViewById(R.id.HeaderTop);
			//	View view = new View(this);
				
			//	list.addHeaderView(listHeader);
				
				
				list.setOnItemClickListener(new OnItemClickListener() {
					
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				     
				 //   	Toast.makeText(getApplicationContext(),((TextView) view).getText(),
				 //   		  												Toast.LENGTH_SHORT).show();
				
					
				    	YLogger Log = YLoggerFactory.getLogger();
				      	String debug = "CallLog display Clicked at "+position;
						Log.d("DisplayCallLogByNumber.onItemClick",debug);
						
						debug = " Going to call remove diagol ";
						Log.d("DisplayCallLogByNumber.onItemClick",debug);
						
						removeDialog(position);
						debug = " Going to call show diagol ";
						Log.d("DisplayCallLogByNumber.onItemClick",debug);
						
						showDialog(position);
						debug = " After shown dialog ";
						Log.d("DisplayCallLogByNumber.onItemClick",debug);
						
						
					}
			  });						
			
									
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			String debug = "CATCH: Inside DisplayAllCallLog  ";
			Log.d("DisplayCallLogByNumber.onItemClick",debug+ex.toString());
			  Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();

			
		}


	}
	};
	 
	protected Dialog onCreateDialog(int position){
		
		  if(callLogInfoArrayStore==null)
			  return null;

		  YLogger Log = YLoggerFactory.getLogger();
	        		  
 			String debug = " IN Create diagol ";
			Log.d("DisplayCallLogByNumber.onCreateDialog",debug);

			CallLogInformationStore callLogInfo = new CallLogInformationStore();
			callLogInfo = callLogInfoArrayStore.get(position);
 			debug = " Object fetched for position "+position;
			Log.d("DEBUG",debug);
 			debug = " getting context ";
			Log.d("DisplayCallLogByNumber.onCreateDialog",debug);

			Dialog dialog = new Dialog(this);
			debug = " getting dialog ";
			Log.d("DisplayCallLogByNumber.onCreateDialog",debug);

			dialog.setContentView(R.layout.contactcustomdialog);
			debug = " Setting title ";
			Log.d("DisplayCallLogByNumber.onCreateDialog",debug);

			dialog.setTitle("Contact Detail");

			debug = " Title set";
			Log.d("DisplayCallLogByNumber.onCreateDialog",debug);

			debug = " finding text View for name";
			Log.d("DisplayCallLogByNumber.onCreateDialog",debug);

			TextView textName = (TextView) dialog.findViewById(R.id.customDialogNameTop);
			textName.setText(callLogInfo.fullName);
			
			debug = " finding text view for phone ";
			Log.d("DisplayCallLogByNumber.onCreateDialog",debug);
			
			TextView textPhoneLabel = (TextView) dialog.findViewById(R.id.customDialogPhoneTop);
			TextView textPhoneNumber = (TextView) dialog.findViewById(R.id.customDialogPhoneBottom);
			
			
			if(callLogInfo.mobileNumber!=null){
				
				textPhoneLabel.setText("Time: "+callLogInfo.callDate+" "+callLogInfo.callTime);
				textPhoneNumber.setText("Type: "+callLogInfo.callType);
				
			}
			debug = " finding text View for email ";
			Log.d("DisplayCallLogByNumber.onCreateDialog",debug);
			                                                      					
			TextView textEmailLabel = (TextView) dialog.findViewById(R.id.customDialogEmailTop);
			TextView textEmail = (TextView) dialog.findViewById(R.id.customDialogEmailBottom);
			                                                       
			if(callLogInfo.emailStore!=null){
				debug = " Setting email Address ";
				Log.d("DisplayCallLogByNumber.onCreateDialog",debug);
				
				debug = " email Address is"+callLogInfo.emailStore[0].emailAddress;
				Log.d("DisplayCallLogByNumber.onCreateDialog",debug);
			
				textEmailLabel.setText(callLogInfo.emailStore[0].emailType);
				textEmail.setText(callLogInfo.emailStore[0].emailAddress);
			}else{
				textEmailLabel.setText("Email Address");
				textEmail.setText("No Email");
				
			}
			
			
			
			debug = "returning dialog ";
			Log.d("DisplayCallLogByNumber.onCreateDialog",debug);

			return dialog;
	  }

}

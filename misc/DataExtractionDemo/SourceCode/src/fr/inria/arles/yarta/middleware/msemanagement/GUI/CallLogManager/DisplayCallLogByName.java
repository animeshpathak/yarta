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
import android.app.ProgressDialog;
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
 *  Activity to display a call log for a particular Name.
 * 
 * <p>It is started after the user selects Call Log by Name option
 *    from the CallLog Home Screen.
 * </p>
 * 
 * @author Nishant Kumar
 *
 */
public class DisplayCallLogByName extends Activity {
	

	ArrayList<CallLogInformationStore> callLogInfoArrayStore;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.commonsearch);
			callLogInfoArrayStore = null;
			
			YLogger Log = YLoggerFactory.getLogger();
	        String debug = " *********************Inside DisplayCallLogByNameGUI ";
			Log.d("DisplayCallLogByName.onCreate",debug);

			TextView searchTV = (TextView) findViewById(R.id.searchTV);
			searchTV.setText("Enter name");
			
			
			Button searchB = (Button) findViewById(R.id.SearchButton);
			searchB.setOnClickListener(searchBClicked);

		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			String debug = " CATCH:DisplayCallLogByName.onCreate";
			Log.d("DisplayCallLogByName.onCreate",debug+ex.toString());
			 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();

		}
	}
				
    /**
     * The start of the search for a call log by the given name.
     * <p>It calls the CallLogManager.getCallHistoryByName
     * of the msemanagement package to obtain the result.
     * </p><p>The result obtained is then displayed in a listview.
     * </p><p>The result obtained may be clicked to display more information
     * about the call log.
     * </p>
     */
	private final OnClickListener searchBClicked = new OnClickListener() {
		public void onClick(View v) {
		
			try {
				YLogger Log = YLoggerFactory.getLogger();
				String debug = " Searched Button Clicked";
				Log.d("DisplayCallLogByName.OnClickListener",debug);
		
				EditText searchET = ((EditText) findViewById(R.id.searchET));
				String searchText = searchET.getText().toString();
				
				debug = " Searched text is "+searchET;
				Log.d("DisplayCallLogByName.OnClickListener",debug);
			
				if(searchText==null ||searchText.equals("") ){
					Toast.makeText(getApplicationContext(),"Please Input a Valid Text",
																	Toast.LENGTH_SHORT).show();
					return;
				}
				ProgressDialog progressWheel = ProgressDialog.show( DisplayCallLogByName.this, "Processing" , " Please wait ... ", true,true);
		        
				CallLogManager callLogManager = new CallLogManager();
			
				callLogInfoArrayStore = callLogManager.getCallHistoryByName
												(getApplicationContext(),searchText);
			
				
				ListView list = (ListView)findViewById(R.id.displayResult);
				
				if(callLogInfoArrayStore==null || callLogInfoArrayStore.size()<=0){
				  	Toast.makeText(getApplicationContext(),
		        			"No Contact Obtained\n\n1. Exact Display Name Required"+
		        			"\n2. Case, Space Sensitive",
							Toast.LENGTH_LONG).show();
		        	
		        	list.setAdapter(null);
		        	progressWheel.dismiss();
		        	return;
		        
				
				}
				
				String[] allDisplayName = new String[callLogInfoArrayStore.size()];
				
				debug = "Forming List from findViewById";
				Log.d("DisplayCallLogByName.OnClickListener",debug);
				
				for(int k = 0;k<callLogInfoArrayStore.size();k++)
					allDisplayName[k] = callLogInfoArrayStore.get(k).fullName;
				
				
				debug = "Forming arrayAdapter for DisplayResult ListView";
				Log.d("DisplayCallLogByName.OnClickListener",debug);
				
				ArrayAdapter<String> adapter = null;
				try{
					adapter = new ArrayAdapter<String>(getApplicationContext(),
														R.layout.contactmanagerhome_list_text,
														allDisplayName);
	
					debug = "Setting FindAllContact ListView";
					Log.d("DisplayCallLogByName.OnClickListener",debug);
							
					list.setAdapter(adapter);
					
					debug = "All Contact ListView Displayed";
					Log.d("DisplayCallLogByName.OnClickListener",debug);
					progressWheel.dismiss();
				
				}catch(Exception ex){

					debug = "Array For Names cannot be formed";
					Log.d("DisplayCallLogByName.OnClickListener",debug);
					progressWheel.dismiss();	
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
						Log.d("DisplayCallLogByName.onItemClick",debug);
						
						debug = " Going to call remove diagol ";
						Log.d("DisplayCallLogByName.onItemClick",debug);
						
						removeDialog(position);
						debug = " Going to call show diagol ";
						Log.d("DisplayCallLogByName.onItemClick",debug);
						
						showDialog(position);
						debug = " After shown dialog ";
						Log.d("DisplayCallLogByName.onItemClick",debug);
						
						
					}
			  });						
			
									
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			String debug = "CATCH: Inside DisplayAllCallLog  ";
			Log.d("DisplayCallLogByName.OnClickListener",debug+ex.toString());
		 Toast.makeText(getApplicationContext(),debug+ex.toString(),Toast.LENGTH_LONG).show();

		}


	}
	};
	 
	
	/**
	 * The dialog box that shows the information about the call log displayed 
	 * in the result
	 */
	protected Dialog onCreateDialog(int position){
		
		  if(callLogInfoArrayStore==null)
			  return null;

		  YLogger Log = YLoggerFactory.getLogger();
	        		  
 			String debug = " IN Create diagol ";
			Log.d("DisplayCallLogByName.onCreateDialog",debug);

			CallLogInformationStore callLogInfo = new CallLogInformationStore();
			callLogInfo = callLogInfoArrayStore.get(position);
 			debug = " Object fetched for position "+position;
			Log.d("DisplayCallLogByName.onCreateDialog",debug);
 			debug = " getting context ";
			Log.d("DisplayCallLogByName.onCreateDialog",debug);

			Dialog dialog = new Dialog(this);
			debug = " getting dialog ";
			Log.d("DisplayCallLogByName.onCreateDialog",debug);

			dialog.setContentView(R.layout.contactcustomdialog);
			debug = " Setting title ";
			Log.d("DisplayCallLogByName.onCreateDialog",debug);

			dialog.setTitle("Contact Detail");

			debug = " Title set";
			Log.d("DisplayCallLogByName.onCreateDialog",debug);

			debug = " finding text View for name";
			Log.d("DisplayCallLogByName.onCreateDialog",debug);

			TextView textName = (TextView) dialog.findViewById(R.id.customDialogNameTop);
			textName.setText(callLogInfo.fullName);
			
			debug = " finding text view for phone ";
			Log.d("DisplayCallLogByName.onCreateDialog",debug);
			
			TextView textPhoneLabel = (TextView) dialog.findViewById(R.id.customDialogPhoneTop);
			TextView textPhoneNumber = (TextView) dialog.findViewById(R.id.customDialogPhoneBottom);
			
			
			if(callLogInfo.mobileNumber!=null){
				
				textPhoneLabel.setText("Time: "+callLogInfo.callDate+" "+callLogInfo.callTime);
				textPhoneNumber.setText("Type: "+callLogInfo.callType);
				
			}
			debug = " finding text View for email ";
			Log.d("DisplayCallLogByName.onCreateDialog",debug);
			                                                      					
			TextView textEmailLabel = (TextView) dialog.findViewById(R.id.customDialogEmailTop);
			TextView textEmail = (TextView) dialog.findViewById(R.id.customDialogEmailBottom);
			                                                       
			if(callLogInfo.emailStore!=null){
				debug = " Setting email Address ";
				Log.d("DisplayCallLogByName.onCreateDialog",debug);
				
				debug = " email Address is"+callLogInfo.emailStore[0].emailAddress;
				Log.d("DisplayCallLogByName.onCreateDialog",debug);
			
				textEmailLabel.setText(callLogInfo.emailStore[0].emailType);
				textEmail.setText(callLogInfo.emailStore[0].emailAddress);
			}else{
				textEmailLabel.setText("Email Address");
				textEmail.setText("No Email");
				
			}
			
			
			
			debug = "returning dialog ";
			Log.d("DisplayCallLogByName.onCreateDialog",debug);

			return dialog;
	  }

	
}	
	
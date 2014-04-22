/**
* <p>This package contains  classes that extract information from
 * various social data source.
 * </p>
 * <p>The classes of this package forms the functional unit of Data
 * Extraction Manager</p>
 
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;

import android.content.Intent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import android.view.Window;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * An Android activity.
 * It is the starting point of data extraction from Facebook profile.
 * 
 * <p>The class returns the access token to the calling activity by 
 *    setResult(RESULT_OK , intent). Hence this activity
 *    should always be started by startActivityForResult</p>
 * <p>After the calling activity gets the access token from this class,
 *    then FacebookExtractProfile class of this package should be used to 
 *    extraact data from the facebook profile 
 *       
 * </p>
 *
 *  <p>The application id for the app is obtained after registering an application 
 *  for Facebook.</p>
 *  <p>The application registered at Facebook is at:</p>
 *  <p>http://www.facebook.com/apps/application.php?id=122447924463934</p>
 * 
 * 
 * @author Nishant Kumar
 *
 */
public class FacebookFindAccessToken extends Activity {

	
/**
 * The webview that will load the webpage for the facebook authorization
 * 
 */
	WebView mWebView;
	/**
	 * The dialog box that loads the webview
	 */
	Dialog dialog;
	/**
	 * The status of the execution that represents is access token is extracted 
	 * successfully. 
	 */
	boolean isSuccess;
	
	/**
	 * The progress dialog that shows a progress wheel in a dialog webview
	 */
	ProgressDialog  mSpinner;
	
	/**
	 * The progress dialog that shows a progress wheel in the activity
	 * 
	 */
	ProgressDialog progressWheel;
	
	/**
	 * The working thread that tests the internet connection
	 * 
	 */
	Thread backgroundThread;
	
	/**
	 * The access token that authorizes to connect to facebook rofile and extract data
	 * 
	 */
	String accessToken;

	/**  <p>The application id for the app is obtained after registering an application 
	 *  for Facebook.</p>
	 *  <p>The application registered at Facebook is at:</p>
	 *  <p>http://www.facebook.com/apps/application.php?id=122447924463934</p>
	 */ 
	String mseFacebookAppId;
	
	/**  <p>The application secret for the app is obtained after registering an application 
	 *  for Facebook.</p>
	 *  <p>The application registered at Facebook is at:</p>
	 *  <p>http://www.facebook.com/apps/application.php?id=122447924463934</p>
	 */ 
	String mseFacebookAppSecret;
	
	/**
	 * The message string that stores any error message if any.
	 * 
	 */
	String messageString;
	
	
	/**
	 * The constant value used by the thread handler
	 * 
	 */
	final int  DISPLAY_MESSAGE =0;
	/**
	 *   The constant value used by the thread handler
	 */
	final int EXTRACT_PROFILE = 1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allcontact);
       
        accessToken = null;
        mseFacebookAppId = "122447924463934";
        mseFacebookAppSecret="0c644776ca0d992ac4a886e1d20371fc";
        messageString = "";
        isSuccess = false;
        dialog=null;
        
   	 try{
			
			progressWheel = 
				ProgressDialog.show( FacebookFindAccessToken.this, 
						"" ,
						" Connecting to Facebook ... ",
						true,true);

			/**
			 * THe thread that starts the steps to obtain access token 
			 */
			backgroundThread = new Thread(new Runnable()
		    {
		        public void run()
		        {
		             
		        	boolean isInternetConnected = testInternetConnection();
		 		               
		           	Message message = threadHandler.obtainMessage();
	                            
		            if(!isInternetConnected)
		               	message.what=DISPLAY_MESSAGE;
		            else
		               	message.what=EXTRACT_PROFILE;
		                        
		                threadHandler.sendMessage(message);

		            }
	 	       });
	          
		  	backgroundThread.start();
							
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
	    	String debug = "Inside FacebookFindAccessToken.startFacebookExtraction: ";
	    	Log.d("FacebookFindAccessToken.startFacebookExtraction",debug+ex.toString());
	        messageString = debug+ex.toString();
	        TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
	    	displayContactMessage.setText(messageString);
	    	progressWheel.dismiss();
	        return;
		}

        
	}
/**
 * The handler that receivs the message when the thread ends.	
 */
	Handler threadHandler = new Handler()
    {
            @Override 
            public void handleMessage(Message message)
            {
             	if(message.what==DISPLAY_MESSAGE){
             		YLogger Log = YLoggerFactory.getLogger();
			        Log.d("FacebookFindAccessToken.threadHandler",
			        		"Internet connection failed");
			        TextView displayMessage = (TextView)findViewById(R.id.displayAllContactMessage);
		            displayMessage.setText("Check Your Internet Connection\n"+
		            		"Cannot Connect to Facebook\n");
		            progressWheel.dismiss();
			        return;
			        
            	}else if(message.what==EXTRACT_PROFILE)
                startExecution(); 
                
                progressWheel.dismiss();
            }
    };
    
	/**
	 * Connects to the http://www.facebook.com
       It is used to check if this application can connect to internet
       successfully.
       	 <p> A connection timeout limit has been set to 30 seconds</p>
       	 
	 * @return return true if the connection is successful before 30 seconds,
	 * otherwise false.
	 * 
	 */
	private boolean testInternetConnection(){
		
		YLogger Log = YLoggerFactory.getLogger();
		Log.d("FacebookFindAccessToken.testInternetConnection","Going to Connect to google");
		HttpURLConnection conn =null;
		try {
			Log.d("FacebookFindAccessToken.testInternetConnection",
					"Going to URL OPEN Connection");
			
		     conn = (HttpURLConnection) new URL("http://www.facebook.com").openConnection();
		     Log.d("FacebookFindAccessToken.testInternetConnection",
				"Going to URL Connect");
		     conn.setConnectTimeout(30000);
		     conn.connect();
		    
		
	 		 Log.d("FacebookFindAccessToken.testInternetConnection",
		    		 "Internet Connection Successful");
	 		 return true;
	 			 
	
		}catch(Exception ex){
	     //	Toast.makeText(getApplicationContext(),"InternetConnection "+
		 //   "Cannot Connect to Facebook:\n"+ex.toString(),Toast.LENGTH_LONG).show();
	     	return false;
		    
		}
	
	}
	
	/**
	 *   Starts a dialog box and then 
	 *   redirect the user to facebook authentication webpage 
	 */
    private void startExecution(){
    	
        YLogger Log = YLoggerFactory.getLogger();
        Log.d("FacebookFindAccessToken.onCreate","Inside onCreate");
        
        int dialogID = 1;
        Log.d("FacebookFindAccessToken.onCreate","Going to remove any dialog if any");
        
        removeDialog(dialogID);
        Log.d("FacebookFindAccessToken.onCreate","Going to show dialog");
        
        showDialog(dialogID);
		
		Log.d("FacebookFindAccessToken.onCreate"," After shown dialog ");
    
    }
    
    /**
     * Creates the dialog box, attach a webview to it
     * and then redirects the user to the facebook authorization page.
     * If the access token is valid, then the access token is automatically
     * obtained by the application, otherwise user has to authorize the application
     */
	protected Dialog onCreateDialog(int dialogID){
		
	   	
		try{
			YLogger Log = YLoggerFactory.getLogger();
			Log.d("FacebookFindAccessToken.onCreateDialog","In OnCreateDialog  ");
			
			Log.d("FacebookFindAccessToken.onCreateDialog","Declaring Progress Dialog");

			mSpinner = new ProgressDialog(this);
			
			Log.d("FacebookFindAccessToken.onCreateDialog",
					 "Setting spinner requestWindowFeature");

			mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
			
		    mSpinner.setMessage("Loading...");

			Log.d("FacebookFindAccessToken.onCreateDialog"," Declaring Dialog");

			dialog = new Dialog(this);
			dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            	
				
			Log.d("FacebookFindAccessToken.onCreateDialog"," Setting dialog Content View");

			dialog.setContentView(R.layout.facebookdialogbrowser);
			
			Log.d("FacebookFindAccessToken.onCreateDialog"," Setting dialog title ");

			dialog.setTitle("Facebook Authorization");
				
			Log.d("FacebookFindAccessToken.onCreateDialog","Title set");

		//	dialog.setOnDismissListener(listener);
					   
			Log.d("FacebookFindAccessToken.onCreateDialog","Finding webView by content ");

		    WebView mWebView = (WebView) dialog.findViewById(R.id.facebookwebview);
			
		    if(mWebView==null)
			  {
			   		Log.d("FacebookFindAccessToken.onCreateDialog","  WebView is null");
						
			   		return null;
			  }
		
			Log.d("FacebookFindAccessToken.onCreateDialog",
						"Setting webView javascript");

			mWebView.getSettings().setJavaScriptEnabled(true);
			
			String urlForCode = setUrlForCode();
			
			    
		    Log.d("FacebookFindAccessToken.onCreateDialog"," Before loading is "+urlForCode);
			
		    mWebView.loadUrl(urlForCode);
			        
			Log.d("FacebookFindAccessToken.onCreateDialog"," Setting webViewClient ");

			mWebView.setWebViewClient(new HelloWebViewClient());
			        
			Log.d("FacebookFindAccessToken.onCreateDialog","returning dialog ");

			return dialog;

		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			Log.d("FacebookFindAccessToken.onCreateDialog","CATCH In onCreateDialog error is "+ex.toString());
			
			Toast.makeText(getApplicationContext(),
					"CATCH  FacebookFindAccessToken.onCreateDialog error is "+ex.toString(),
					Toast.LENGTH_LONG).show();

			messageString = "FacebookFindAccessToken.onCreateDialog: "+ex.toString();
			isSuccess = false;
			returnToCallingActivity();
				//dialog.dismiss();
			return null;
		}
	   
	}

/*	
	 private final OnDismissListener listener = new OnDismissListener(){
	    	
	    	@Override   
	    	public void onDismiss(DialogInterface dialog1){
	    		Log.d("LinkedInFindAccessToken.OnDismissListener",
	    				"Inside Dialog Dismiss Listener");
	    		
	    		returnToCallingActivity();
	    		return;
	    	 }
	    };
	*/
	/**
	 * Handles various functions of the webview of the android.
	 * It is used to extract the required value from the 
	 * url that is loading in the webview
	 */
	private class HelloWebViewClient extends WebViewClient {
	       @Override
	       public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    	   YLogger Log = YLoggerFactory.getLogger();
	   		Log.d("FacebookFindAccessToken.HelloWebViewClient","Inside shouldOverrideURLLoading");
	   	   // view.loadUrl(url);
	  		return false;
	   	 }
	       
	       @Override
	       public void onReceivedError(WebView view, int errorCode,
	               String description, String failingUrl) {
	    	   YLogger Log = YLoggerFactory.getLogger();
	           Log.d("FacebookFindAccessToken.HelloWebViewClient",
	           		"In  OnRecieved Error "+description);
	           Toast.makeText(getApplicationContext(),"Browser Error:"+description,Toast.LENGTH_LONG).show();
	
	           messageString = "OnReceive: FacebookFindAccessToken.HelloWebViewClient: "+description;
	           isSuccess = false;
	           returnToCallingActivity();
 				//dialog.dismiss();
 			 
	       }

	       @Override
	       public void onPageStarted(WebView view, String url, Bitmap favicon) {
	    	   YLogger Log = YLoggerFactory.getLogger();
	           Log.d("FacebookFindAccessToken.HelloWebViewClient", 
	           		"onPageStarted Started WebView loading URL: ");
	           Log.d("DEBUG","showing mSpinner");
	           mSpinner.show();
	       }

	       @Override
	       public void onPageFinished(WebView view, String url) {
	    	   YLogger Log = YLoggerFactory.getLogger();
	    	   Log.d("FacebookFindAccessToken.onPageFinished","onPageFinished Url ");
	           Log.d("FacebookFindAccessToken.onPageFinished","dismissing spinner ");
	           mSpinner.dismiss();
	           
	           int index = url.indexOf("http://www.facebook.com/connect/login_success.html?code=");
	         
	           if(index==0){
	      			Log.d("FacebookFindAccessToken.onPageFinished","Code found");
	      			
	      			mSpinner.setMessage("Extracting access Token.. ");
	      			
	      			String code = url.substring(url.indexOf("code="));
	      			Log.d("FacebookFindAccessToken.onPageFinished","Code is "+code);
	      			
	      			String newUrl = "https://graph.facebook.com/oauth/access_token?"+
	      			"client_id=122447924463934&"+
	      			"redirect_uri=http://www.facebook.com/connect/login_success.html&"+
	      			"client_secret=0c644776ca0d992ac4a886e1d20371fc&"+
	      			code;
	      			
/*    URL FOR GETTING ACCESS TOKEN FROM CODE OBTAINED AFTER AUTHORIZATION
 
https://graph.facebook.com/oauth/access_token?&client_id=122447924463934&redirect_uri=http://www.facebook.com/connect/login_success.html&
client_secret=0c644776ca0d992ac4a886e1d20371fc&code

*/
		      			try{
	          		
	      				
	      				String response = connect(newUrl);
	          			if(response == null){
	          				Log.d("FacebookFindAccessToken.onPageFinished",
		      						"Cannot find AccesToken for the Code");
	          				isSuccess = false;
	          				messageString = "FacebookFindAccessToken.onPageFinished: "+
	          				                 "Cannot get Authentication Token After getting Code";

	          				returnToCallingActivity();
	          				//dialog.dismiss();
	          				return;
	          			}
	          			
	      				Log.d("FacebookFindAccessToken.onPageFinished",
	      						"Response after accessToken connection is "+response);
	      				
	      				int indexEnd =-1;
	      				
	      				if(response!=null)
	      					indexEnd = response.indexOf("&expires");
	          			
	          			if(indexEnd>0){
	          				accessToken = response.substring(0,indexEnd);
	          			
	          				Log.d("DEBUG","Response after accessToken cut is "+accessToken);
	                        
	          				
	          				messageString = "success";
	          				
	          				isSuccess = true;
	          				returnToCallingActivity();
	          				//dialog.dismiss();
	          				
	          				return;
	          			}else{
	          				Log.d("FacebookFindAccessToken.onPageFinished",
      						"Cannot find AccesToken for the Code");
	          				isSuccess = false;
	          				messageString = "FacebookFindAccessToken.onPageFinished: "+
	          								"Cannot get Authentication Token"+
	          								"Response is: "+response;

	          				returnToCallingActivity();
	          				//dialog.dismiss();
	          				return;
	          			}
	          			
	      			}catch(Exception ex){
	          			Log.d("DEBUG","Error in getting access token after code"+ex.toString());
	          			isSuccess = false;
	          			returnToCallingActivity();
          				//dialog.dismiss();
          				return;
	      			
	      			}
	      		}
	      	}   
	   }

	/**
	 * Used to form the url that is first connected 
	 * and the user grant permission for accessing the facebook account
	 * 
	 * @return The url to connect
	 */
	private String setUrlForCode(){
		YLogger Log = YLoggerFactory.getLogger();
		String baseUrl     = "https://graph.facebook.com/oauth/authorize";
		String client_id   = "client_id=122447924463934";
		String redirectUri = "redirect_uri=http://www.facebook.com/connect/login_success.html";
        String permission  = "scope=user_website,read_friendlists,user_work_history,"+
               					"friends_work_history,friends_education_history,"+
               					"user_education_history,email";

/*
https://graph.facebook.com/oauth/authorize&client_id=122447924463934&
redirect_uri=http://www.facebook.com/connect/login_success.html";
&scope=user_website,read_friendlists,user_work_history,friends_work_history,
friends_education_history,user_education_history,email";

         * 
         * 
         */
        
        String totalUrl = baseUrl+"?"+client_id+"&"+redirectUri+"&"+permission;
    	Log.d("FacebookFindAccessToken.setUrlForCode","returning UrlForCode is "+totalUrl);
        return totalUrl;
	}
	
	/**
	 * Used connect to a url over the internet
	 * and get the result obtained.
	 * It makes an HTTP GET request.
	 * 
	 * 
	 * 
	 * @param url The url to connect
	 * @return The response read from the url
	 * 
	 * @throws MalformedURLException
	 * @throws IOException null is returned and the error message is stored in 
	 * messagestring variable of this class
	 * 
	 */
	private  String connect(String url) 
	   throws MalformedURLException, IOException {
		YLogger Log = YLoggerFactory.getLogger();
		Log.d("FacebookFindAccessToken.connect","Going to Connect"+url);
	
		String response = null;;
		HttpURLConnection conn =null;
		try {
		     conn = (HttpURLConnection) new URL(url).openConnection();
	 		 response = read(conn.getInputStream());
	 		 Log.d("FacebookFindAccessToken.connect","Response obtained, returning");
	 		 return response;
	
		} catch (Exception ex) {
	     	response = read(conn.getErrorStream());
	        Toast.makeText(getApplicationContext(),"FacebookFindAccessToken.connect Catch:"+response+ex.toString(),Toast.LENGTH_LONG).show();
		    messageString = "FacebookFindAccessToken.connect: "+
		                     ex.toString()+
		                     "\nResponse: "+response;
		    
		    return null;
		}
		
	}

	/**
	 * Called by the connect function of this class 
	 * It reads the InputStream after a particular Url is connected
	 * by the connect function. The read bytestream is converted to 
	 * string and returned.
	 * 
	 * @param in The InputStream to be read after the url is connected
	 * @return  The obtained result from the connected site.
	 * 
	 * @throws IOException
	 */
	private  String read(InputStream in) throws IOException {
		
		StringBuilder sb = new StringBuilder();
		try{
			BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
			for (String line = r.readLine(); line != null; line = r.readLine()) {
				sb.append(line);
			}
			in.close();
			return sb.toString();
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			Log.d("FacebookFindAccessToken.read","CATCH : "+ex.toString());
			 messageString = "FacebookFindAccessToken.read: "+
			                   "\nUnable to read response after connection: "+
			                   ex.toString();

			return null;	
		}
	}

	/**
	 * Sets the result that is to be returned to the calling activity. It 
	 * forms an intent and stores the bundle in it.
	 * The bundle contains the values
	 * like isSuccess, accessToken, messageString.
	 */
	 private void returnToCallingActivity(){
	     Bundle bundle = new Bundle();
         Intent returnIntent = new Intent();
         YLogger Log = YLoggerFactory.getLogger();
         Log.d("FacebookFindAccessToken.returnToCallingActivity",
         		"ReturnToCallingActivity with isSuccess"+isSuccess);
	    	bundle.putBoolean("isSuccess", isSuccess);
	    	bundle.putString("accessToken", accessToken);
	    	bundle.putString("messageString", messageString);
	    	returnIntent.putExtras(bundle);
	    	setResult(RESULT_OK,returnIntent);
          
         Log.d("returnToCallingActivity","Calling finish from FacebookFindAccessToken");
         //super.finish();
         finish();
         
	    }
	/* 
	 @Override
	 public void onPause(){
		 Log.d("DEBUG","\nON PAUSE CALLED IN FACEBOOK FIND ACCESS TOKEN\n");
	        
		 
		 super.onPause();
		 //mWebView.invalidate();
		// dialog.dismiss();
		 returnToCallingActivity();
		// onDestroy();
	 }
	*/
	 
	 

}

/**
* <p>This package contains classes that extract information from
 * various social data source.
 * </p>
 * <p>The classes of this package forms the functional unit of Data
 * Extraction Manager</p>
 
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;



import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.signature.SignatureMethod;

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
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * An Android activity. It launches a dialog 
 * for user to grant permission to use linkedin profile and then 
 * store the access token in the internal memory of android phone
 * <p>It should be called from other activity by startActivityForResult()</p>
 * 
 * @author Nishant Kumar
 *
 */
public class LinkedInFindAccessToken extends Activity {
	
	/**
	 * Key generated when a user grants permission to this application for 
	 * accessing LinkedIn profile for the first time. It is stored in memory. 
	 *  
	 */

	private String accessKey;

	/**
	 * Secret Key generated when a user grants permission to this application
	 * for accessing LinkedIn profile for the first time. It is stored in memory. 
	 *  
	 *  
	 */
	private String accessKeySecret;	
	
	
	/**
	 *  The OAuthConsumer object that represents the details of application
	 *  owner by the API key and API Secret Key obtained after registering 
	 *  this application.
	 */
	OAuthConsumer MSEOwner;
	
	private String linkedInKeyStoreFileName="LinkedInStore";
	
	
	/**
	 * The OAuthProvider Object that represents the LinkedIn API Provider
	 * and stores the URL for accessing request token and access Token
	 * at the first time connection
	 * 
	 */
	OAuthProvider linkedInAPIProvider;
	
	/**
	 * The linkedin authorization url that is returend by the 
	 * linkedin API. This url is used to get the PIN number 
	 * for the access token.
	 * 
	 */
	String authorizationUrl;
	
	/**
	 * The dialog that loads a webview and then 
	 * displays the webpage for authorization.
	 * 
	 */
	Dialog dialog;
	
	/**
	 * The isSuccess represents if the access token has been successfully
	 *  obtained or not
	 */
	boolean isSuccess;
	
	/**
	 * The messageString stores the error message if any 
	 */
	public String messageString="";
	
	
	/**
	 * The progress dialog that show the progress wheel while a 
	 * page is loading 
	 * 
	 */
	private ProgressDialog  mSpinner;
	
	/**
	 * The progresswheel displayed while a connection to internet is 
	 * taking place.
	 */
	ProgressDialog progressWheel;
	Thread backgroundThread;
	
	/**
	 * The integer used by the thread handler
	 */
	 int DISPLAY_MESSAGE;
	 
	 /**
	  * The integer used by thread handler
	  */
	 int EXTRACT_PROFILE;
	
	
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
			setContentView(R.layout.allcontact);
			accessKeySecret = null;
			accessKey = null;
			MSEOwner=null;
			linkedInAPIProvider=null;
			authorizationUrl="";
			mSpinner =null;
			DISPLAY_MESSAGE =0;
			EXTRACT_PROFILE = 1;
			progressWheel = null;
			isSuccess= false;
			dialog=null;
			
			 try{
					
					progressWheel = 
						ProgressDialog.show( LinkedInFindAccessToken.this, 
								"" , 
								"Connecting to LinkedIn ... ", 
								true,true);
                    /**
                     * The thread that starts the testing of internet connection
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
			    	String debug = "Inside LinkedInFindAccessToken.startLinkedInExtraction: ";
			    	Log.d("DefaultConnectLinkedIn.onCreate",debug+ex.toString());
			        messageString = debug+ex.toString();
			        TextView displayContactMessage = (TextView)findViewById(R.id.displayAllContactMessage);
			    	displayContactMessage.setText(messageString);
			    	progressWheel.dismiss();
			        return;
				}

	
			
			
		}
		
/**
 * The thread handler tha receives the message when a thread ends.
 */
	Handler threadHandler = new Handler()
    {
            @Override public void handleMessage(Message message)
            {
             	if(message.what==DISPLAY_MESSAGE){
             		YLogger Log = YLoggerFactory.getLogger();
			        Log.d("LinkedInFindAccessToken.threadHandler","Internet connection failed");
			        TextView displayMessage = (TextView)findViewById(R.id.displayAllContactMessage);
		            displayMessage.setText("Check Your Internet Connection\n"+
		            		"Cannot Connect to LinkedIn\n");
		            progressWheel.dismiss();
			        return;
			        
            	}else if(message.what==EXTRACT_PROFILE)
                startExecution(); 
                
                progressWheel.dismiss();
            }
    };
    
	/**
	 * Check if this application can connect to linkedin.
	 *  (http://www.linkedin.com).
	 *  
	 * The connection timeout is set to 30 seconds.
	 * @return true if connection is success full
	 */
	private boolean testInternetConnection(){
		
		YLogger Log = YLoggerFactory.getLogger();
		Log.d("LinkedInFindAccessToken.testInternetConnection",
				"Going to Connect to Linkedin");
	
		HttpURLConnection conn =null;
		try {
			Log.d("LinkedInFindAccessToken.testInternetConnection",
					"Going to URL OPEN Connection");
			
		     conn = (HttpURLConnection) new URL("http://www.linkedin.com").openConnection();
		     Log.d("LinkedInFindAccessToken.testInternetConnection",
				"Going to URL Connect");
		     conn.setConnectTimeout(30000);
		     conn.connect();
		    
		
	 		 Log.d("LinkedInFindAccessToken.testInternetConnection",
		    		 "Internet Connection Successful");
	 		 return true;
	 			 
	
		}catch(Exception ex){
	     /*	Toast.makeText(getApplicationContext(),"InternetConnection "+
		    "Cannot Connect to Linkedin:\n"+ex.toString(),Toast.LENGTH_LONG).show();
	     	TextView displayMessage = (TextView)findViewById(R.id.displayAllFacebookMessage);
            displayMessage.setText("Check Your Internet Connection\n"+
            		"Cannot Connect to LinkedIn\n"+ex.toString());
	     	*/
	     	return false;
		    
		}
	
	}
	

	/**
	 * Start the steps required to get Access token from LinkedIn.
	 */
	   private void startExecution(){
		   
		
		 try{	
			    YLogger Log = YLoggerFactory.getLogger();
	
			    String ownerKey="QbPkruUlNGQTqqTTbcVgvBTpzNDS27h7LYSg2L0jHNp6A1BLo2x3ekJGnW43dqKF";
			    String ownerSecretKey="WWZK4dyCxPsi1OKzKfn7OcPdzsGRNRip6UDGN0OSOnT7kfsi6RZrravRXg2q_GdY";
			    Log.d("LinkedInFindAccessToken.startExecution","Owner key defined ");
			    
			   /*
			     * Set the Consumer, i.e. property of owner of this application
			     * The API key and API Secret keys are obtained
			     * from the LinkedIn developer website after registering
			     * your application
			     * 
			     * If any error takes place, then null value will be returned. Hence exit.
			     * 
			     */
			    			
			    Log.d("LinkedInFindAccessToken.startExecution","Going to set Application owner\n ");
				    
				MSEOwner = setMSEOwner(ownerKey , ownerSecretKey);
			    if(MSEOwner==null){
			    	Log.d("LinkedInFindAccessToken.startExecution","set setMSEOwner returns null");
			    	isSuccess = false;
			    	returnToCallingActivity();
			    	return;
			    }
			 
				Log.d("LinkedInFindAccessToken.startExecution","MSE owner set ");
			    
			
				    /*
				     * Set the Website Links for getting the request token
				     * authorization and access token for connection to a
				     * profile.
				     * 
				     */
				Log.d("LinkedInFindAccessToken.startExecution","Going to set API LINKS");
				
				linkedInAPIProvider = setAPILinks();
				    if(linkedInAPIProvider==null){
				    	Log.d("LinkedInFindAccessToken.startExecution"," set setAPILinks returns null");
				    	isSuccess = false;
				    	returnToCallingActivity();
				    	 return;
				    }
				    
				Log.d("LinkedInFindAccessToken.startExecution","LinkedInAPiProvider, API links set up over");
				
				    /*
				     * Get the request token for the application and hence
				     * generate the authorization URL to authorize this
				     * application for accessing the Linkedin Profile
				     * 
				     */
				
				Log.d("LinkedInFindAccessToken.startExecution","Going to form Authorization url by request token");
						
				authorizationUrl = setAuthorizarionURL();
				if(authorizationUrl==null){
					Log.d("LinkedInFindAccessToken.startExecution","set setAuthorizationUrl returns null");
			    	isSuccess = false;
			    	returnToCallingActivity();
			    	 return;
				    }
					
				  
				Log.d("DEBUG","Authorization URL formed is"+authorizationUrl);
				    
				    /*
				     * This method will direct the user to authorization webpage
				     * The user need to enter the LinkedIn account detail of
				     * profile and grant the permisssion to this application
				     * 
				     * After granting permission, a PIN will be generated
				     * This pin is required for getting Access Token.
				     * 
				     */
			
				getAccessTokenPIN(authorizationUrl);
				
			    }catch(Exception ex){
			    	YLogger Log = YLoggerFactory.getLogger();
					Log.d("LinkedInFindAccessToken.startExecution","Unable to Execute steps for Token Retrieval"+ex.toString());
			    	isSuccess= false;
			    //	Toast.makeText(getApplicationContext(),"Unable to Execute steps for retrieval"+ex.toString(),Toast.LENGTH_LONG).show();
                    messageString ="Catch: LinkedInFindAccessToken.startExecution: "+ex.toString();
			    	returnToCallingActivity();
			    	return;
			    } 
			}
		
		
		/**
		 * 
		 * Set the API key obtained from the LinkedIn Developer website 
		 * for the authorization of this Application 
		 * 
		 * 	 
		 * @param ownerKey         The API Key (Consumer key) as obtained after Registering 
		 *                         this application on Linkedin Developer Website 
		 *                         
		 * @param ownerSecretKey   The Secret Key obtained after registering the application
		 *                         on LinkedIn Website
		 *                         
		 * @return                 It returns the consumer object of the OAuthConsumer class
		 */
		private OAuthConsumer setMSEOwner(String ownerKey , String ownerSecretKey){
			/*
			 * 
			 * Set the API key and Api secret key for the consumer object
			 * 
			 * 
			 */ 
			    YLogger Log = YLoggerFactory.getLogger();
			    Log.d("LinkedInFindAccessToken.setMSEOwner","Inside  setMSEOwner");
			    
			    try{
			        OAuthConsumer consumer = new DefaultOAuthConsumer(
		                                                               ownerKey,
		                                                               ownerSecretKey,
		                                                               SignatureMethod.HMAC_SHA1);
		        
			       Log.d("LinkedInFindAccessToken.setMSEOwner",
							"MSE owner property setup over");			        
			        return consumer;
			    }
			    catch(Exception ex){
			    	
			    String	debug ="LinkedInFindAccessToken.setMSEOwner: ";
			    	debug +=	ex.getMessage();
			    	Log.d("LinkedInFindAccessToken.setMSEOwner",debug);	
			   // 	Toast.makeText(getApplicationContext(),debug,Toast.LENGTH_LONG).show();
                    messageString = debug;
			        return null;
			    }
			   
		}
		
		/**
		 * Set the LinkedIn API URL that will be used to fetch Request Token and
		 * authorize this application to access a profile.
		 * 
		 * @return  The OAuthProvider object required for fetching Request Token and other 
		 *          necessary Authorization for this application.
		 *                    
		 */
	    private OAuthProvider setAPILinks(){
	     /*
	      *  Sets the API links (i.e; ) the provider for the profile
	      *  data extraction.  associate the application owner parameter
	      *  set in the previous function with the api provider 
	      * 
	      */
	    	YLogger Log = YLoggerFactory.getLogger();
		    Log.d("LinkedInFindAccessToken.setMSEOwner","Inside setMSEOwner");
		
	    	try{
	    	
	            OAuthProvider provider = new DefaultOAuthProvider
	                 (MSEOwner,
	                 "https://api.linkedin.com/uas/oauth/requestToken",
		             "https://api.linkedin.com/uas/oauth/accessToken",
		             "https://api.linkedin.com/uas/oauth/authorize"
		             );            

			    
				Log.d("LinkedInFindAccessToken.setMSEOwner","API links set and provider formed");
			
	    	return provider; 
	        
	    	}
	    	catch(Exception ex){
	        	String debug ="LinkedInFindAccessToken.setAPILinks: ";
		    	debug +=	ex.getMessage();
		        isSuccess= false;
		        Log.d("LinkedInFindAccessToken.setMSEOwner",debug);	
		       // Toast.makeText(getApplicationContext(),debug,Toast.LENGTH_LONG).show();
		        messageString = debug;
		    	return null;
	    	}
		}
	    	
	    
	    /**
	     * Get the Request Token from the LinkedIn API and generate an 
	     *  authorization URL. This authorization URL will be used to grant permission
	     *  to this application to access the profile.
	     *
	     * @return      The URL to visit to grant permission to this application to access 
	     *               profile
	     */
	    private String setAuthorizarionURL(){
		/*
		 * Get the request token 
		 * form the Url that need to be redirected for the first time authentication
		 * 
		 */
	    	YLogger Log = YLoggerFactory.getLogger();
	    	Log.d("LinkedInFindAccessToken.setAuthorizarionURL",
					"Inside setAuthorization URL");
		
	    	try{
	            String authorizationURL = linkedInAPIProvider.retrieveRequestToken(OAuth.OUT_OF_BAND);
	        
				Log.d("LinkedInFindAccessToken.setAuthorizarionURL",
						"Request token recieved and url formed is "+authorizationURL);
			    
	            return(authorizationURL);
	    	}
	    	catch(Exception ex){
	    		
	        	String debug ="LinkedInFindAccessToken.setAuthorizarionURL: ";
		    	debug +=	ex.getMessage();
		    	isSuccess= false;
		    	   Log.d("LinkedInFindAccessToken.setAuthorizarionURL",debug);	
		    	 //  Toast.makeText(getApplicationContext(),debug,Toast.LENGTH_LONG).show();
		    	   messageString = debug;
		    	return null;
	    	}
	    	
		}
	    
	    /**
	     *  Used to display the webpage to grant permission to the application 
	     *  to access the profile of user.
	     * 
	     * @param authorizationURL  The url generated corresponding to the request token
	     *                          This url will display the webpage for permission to the 
	     *                          application.
	     *                          
	     * @return   Returns true if the process executes successfully, otherwise return false
	     *           if any error occurs.
	     *           
	     */
	    private void getAccessTokenPIN(String authorizationURL){

	    	YLogger Log = YLoggerFactory.getLogger();
	    /*
	     * In this function, I will launch the browser and direct it to the url
	     * Then after generating the pin, User submit the pin
	     *  I will close the browser and display
	     *  This pin number then in next function
	     * generates the url 
	     * 
	     */
	   
	    	int dialogID=1;
			Log.d("LinkedInFindAccessToken.getAccessTokenPIN",
					" If any dialog already present , Removing dialog ");
			
			removeDialog(dialogID);
			
			Log.d("LinkedInFindAccessToken.getAccessTokenPIN"," Going to call show diagol ");
			
			showDialog(dialogID);
			
			Log.d("LinkedInFindAccessToken.getAccessTokenPIN"," After shown dialog ");
	    	 
	    }
	    
	    
	    protected Dialog onCreateDialog(int dialogID){
			
	   	try{
			 Log.d("LinkedInFindAccessToken.onCreateDialog","In OnCreateDialog  ");

			
			 Log.d("LinkedInFindAccessToken.onCreateDialog","declaring Progress Dialog");

			 mSpinner = new ProgressDialog(this);
		       
			 mSpinner.setCancelable(false);
			
			 Log.d("LinkedInFindAccessToken.onCreateDialog",
					 "Setting spinner requestWindowFeature");

			 mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
			
		     mSpinner.setMessage("Loading...");
		
	
			 Log.d("LinkedInFindAccessToken.onCreateDialog"," Declaring Dialog");

				dialog = new Dialog(this);
				dialog.setCancelable(false);
	            dialog.setCanceledOnTouchOutside(false);
	            	
				
				Log.d("LinkedInFindAccessToken.onCreateDialog",
						" Setting dialog Content View to xml");

				dialog.setContentView(R.layout.dialogbrowser);
			
				Log.d("LinkedInFindAccessToken.onCreateDialog"," getting dialog ");

			
				Log.d("LinkedInFindAccessToken.onCreateDialog"," Setting dialog title ");

				dialog.setTitle("Authorize MSE");
			
				Log.d("LinkedInFindAccessToken.onCreateDialog",
						"Title set");

				Log.d("LinkedInFindAccessToken.onCreateDialog",
				"  finding webview for  dialog");
	  
				
				Log.d("LinkedInFindAccessToken.onCreateDialog",
						"Finding webView by content ");

		        WebView mWebView = (WebView) dialog.findViewById(R.id.webview);
			     if(mWebView==null)
			     {
			    		Log.d("LinkedInFindAccessToken.onCreateDialog",
			    				"  WebView is null");
						isSuccess = false;
						returnToCallingActivity();
			    	 return null;
			     }
		
				Log.d("LinkedInFindAccessToken.onCreateDialog",
						"Setting webView javascript");

				mWebView.getSettings().setJavaScriptEnabled(true);
		
				Log.d("LinkedInFindAccessToken.onCreateDialog",
						" Setting BuiltInZoom");

				mWebView.getSettings().setBuiltInZoomControls(true);
	        
		        Log.d("LinkedInFindAccessToken.onCreateDialog",
		        		" AUTH URL Just before loading is "+authorizationUrl);
			
		        mWebView.loadUrl(authorizationUrl);
			        
			   
				Log.d("LinkedInFindAccessToken.onCreateDialog",
						" Setting webViewClient ");

			    mWebView.setWebViewClient(new HelloWebViewClient());
			        
			   
				Log.d("LinkedInFindAccessToken.onCreateDialog",
						" Setting Button Handler ");
				
				Button searchB = (Button) dialog.findViewById(R.id.SearchButton);
				searchB.setOnClickListener(savePINBClicked);

				Log.d("LinkedInFindAccessToken.onCreateDialog","returning dialog ");

				return dialog;
			}catch(Exception ex){
			  	Log.d("LinkedInFindAccessToken.onCreateDialog",
			  			"CATCH"+ex.toString());
				isSuccess = false;
				//Toast.makeText(getApplicationContext(),"CATCH In onCreateDialog error is "+ex.toString(),Toast.LENGTH_LONG).show();
                messageString = "LinkedInFindAccessToken.onCreateDialog: "+ex.toString();
				returnToCallingActivity();
				return null;
			}
	    }
	    
	    private class HelloWebViewClient extends WebViewClient {
	        @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        
	    		Log.d("LinkedInFindAccessToken.HelloWebViewClient",
	    				"Inside shouldOverrideURLLoading  ");
	          //  view.loadUrl(url);
	            return false;
	        }
	        
	        @Override
	        public void onReceivedError(WebView view, int errorCode,
	                String description, String failingUrl) {

	            Log.d("LinkedInFindAccessToken.HelloWebViewClient",
	            		"In  OnRecieved Error "+description);
	       //     Toast.makeText(getApplicationContext(),"Browser Error:"+description,Toast.LENGTH_LONG).show();

	            isSuccess= false;
	            messageString = "In  OnRecievedError: "+description+failingUrl;
	            returnToCallingActivity();
	            return;
	        }

	        @Override
	        public void onPageStarted(WebView view, String url, Bitmap favicon) {
	            Log.d("LinkedInFindAccessToken.HelloWebViewClient", 
	            		"onPageStarted Started WebView loading URL: ");
	            Log.d("LinkedInFindAccessToken.HelloWebViewClient","showing mSpinner ");
	            
	            mSpinner.show();
	        }

	        @Override
	        public void onPageFinished(WebView view, String url) {
	        	Log.d("LinkedInFindAccessToken.HelloWebViewClient","onPageFinished ");
	            Log.d("LinkedInFindAccessToken.HelloWebViewClient","dismissing spinner ");
	            
	            mSpinner.dismiss();
	        }   
	    }
	    
	 	    private final OnClickListener savePINBClicked = new OnClickListener() {
			public void onClick(View v) {
				try{
		    		Log.d("LinkedInFindAccessToken.savePINBClicked",
		    				" Save Button Clicked");
		    		
		    		EditText pinBox = (EditText)dialog.findViewById(R.id.searchET);
		    		
					if( pinBox!=null &&  pinBox.getText()!=null && pinBox.getText().length()>0  ){
						
					String  pin = pinBox.getText().toString();
					Log.d("LinkedInFindAccessToken.savePINBClicked",
					"Pin Obtained is:"+pin);
					setAccessToken(pin);
					}
					else{
						Log.d("LinkedInFindAccessToken.savePINBClicked",
								"From Dialog TextBox received Null ");
					//    Toast.makeText(getApplicationContext(),"No text from Textbox Received",Toast.LENGTH_LONG).show();

						isSuccess = false;
						messageString="No PIN Received from TextBox";
						returnToCallingActivity();
						return;
				    		
					}
		    	}catch(Exception ex){
		    	  	Log.d("LinkedInFindAccessToken.savePINBClicked",
				  			"CATCH"+ex.toString());
					isSuccess = false;
					//Toast.makeText(getApplicationContext(),"CATCH In onCreateDialog error is "+ex.toString(),Toast.LENGTH_LONG).show();
	                messageString = "LinkedInFindAccessToken.savePINBClicked: "+ex.toString();
	    			returnToCallingActivity();
					 return;
		
		    	}


			
			}
	    };
	    
	    
	    /**
	     *   Set the Access Token with the PIN that was obtained
	     *   after granting the permission to this application from the browser  
	     *   for a particular profile. The Access Token will be stored in memory
	     *   and can be directly used for future connections.
	     *        
	     * @param pinNumber  The Pin Number  obtained from the Authorization webpage
	     *                   after granting the permission to the application for the 
	     *                   access of the profile.
	     * 
	     * @return Returns false if any error occur  
	     */
	    private void setAccessToken(String pinNumber){
	    	/*
			 * USed when the user is first time connecting
			 * set the access token from the pin number 
			 * obtained from the browser
			 */
	    	
	    	YLogger Log = YLoggerFactory.getLogger();
	    	String debug ="In setAccessToken";
	        Log.d("LinkedInFindAccessToken.setAccessToken",debug);
	        try{
	    	    
	        	linkedInAPIProvider.retrieveAccessToken(pinNumber);
	           
	        	Log.d("LinkedInFindAccessToken.setAccessToken",
	        			"Access Token from PIN received");
	        	accessKey = MSEOwner.getToken();
	        	accessKeySecret = MSEOwner.getTokenSecret();
	        	
	            Log.d("LinkedInFindAccessToken.setAccessToken",
	            		"AccessToken obtained is "+accessKey+" "+accessKeySecret );
	        
		        try{
			    	File file = getFileStreamPath(linkedInKeyStoreFileName);
			    	BufferedWriter bufw = new BufferedWriter(new FileWriter(file));
					bufw.write(accessKey);
					bufw.write("\n");
					bufw.write(accessKeySecret);
					bufw.close();
					
					Log.d("LinkedInFindAccessToken.setAccessToken",
							"Stored  accessTokens");
					
					Log.d("LinkedInFindAccessToken.setAccessToken",
							"Recheking the access Tokens");
					
					BufferedReader bufread = new BufferedReader(new FileReader(file));
					
					String result =    bufread.readLine();
					if(!result.equals(accessKey)){
					
						Log.d("LinkedInFindAccessToken.setAccessToken",
								"Acces Key not Matches with stored is "+accessKey);
						Log.d("LinkedInFindAccessToken.setAccessToken",
								"Stored AccessKey is  "+result);
						
				    	isSuccess=false;
				    	messageString = "LinkedInFindAccessToken.setAccessToken: Cannot Write the Access Token to memory";
				    	returnToCallingActivity();
					}
				
					result =    bufread.readLine();
					if(!result.equals(accessKeySecret)){
					
						Log.d("LinkedInFindAccessToken.setAccessToken",
								"Acces Key not Matches with stored Secret is "+accessKeySecret);
						Log.d("LinkedInFindAccessToken.setAccessToken",
								"Stored Secret is  "+result);
						isSuccess=false;
					   	messageString = "LinkedInFindAccessToken.setAccessToken: Cannot Write the Access Token to memory";
						 
				    	returnToCallingActivity();
					}
					bufread.close();
				
					Log.d("LinkedInFindAccessToken.setAccessToken","Over read");
					Log.d("LinkedInFindAccessToken.setAccessToken","Read from file is "+result);
				
				}catch(Exception ex){
				   	Log.d("LinkedInFindAccessToken.setAccessToken","In set AcceesToken Error with Storing Data");
					isSuccess = false;
				   	messageString = "LinkedInFindAccessToken.setAccessToken: "+ex.toString();
					returnToCallingActivity();
					return;
				} 
				 
	            isSuccess = true;
	            messageString="success";
	            returnToCallingActivity();
	        }
	        catch(Exception ex){
	        	debug ="CATCH: ";
		    	debug +=	ex.getMessage();
		    	Log.d("LinkedInFindAccessToken.setAccessToken",debug);
		    //    Toast.makeText(getApplicationContext(),debug,Toast.LENGTH_LONG).show();
		    	messageString = "LinkedInFindAccessToken.setAccessToken: "+ex.toString();
				isSuccess=false;
		    	returnToCallingActivity();
		        return;   
	        }
	    }
	    
	    /**
	     * Return to the calling activity.
	     * It forms a bundle and stores the messageString and 
	     * isSuccess value of the variable of this class
	     * 
	     */
	    private void returnToCallingActivity(){
	    	    	
	    	Bundle bundle = new Bundle();
            Intent returnIntent = new Intent();
            Log.d("LinkedInFindAccessToken.returnToCallingActivity",
            		"ReturnToCallingActivity with isSuccess"+isSuccess);
	    	bundle.putBoolean("isSuccess", isSuccess);
	    	bundle.putString("messageString", messageString);
	    	
	    	returnIntent.putExtras(bundle);
	    	setResult(RESULT_OK,returnIntent);
             
            Log.d("returnToCallingActivity","Calling finish from FirstTimeLinkedInConnet  ");
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

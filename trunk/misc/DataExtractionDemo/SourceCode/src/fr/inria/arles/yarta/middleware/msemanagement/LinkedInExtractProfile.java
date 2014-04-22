/**
* <p>This package contains the classes that extract information from
 * various social data source.
 * </p>
 * <p>The classes of this package forms the functional unit of Data
 * Extraction Manager</p>
 
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import android.content.Context;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.signature.SignatureMethod;

/**
 * Connects to the linkedin profile of a user, 
 * whose access token is stored in the Android
 * internal memory by the LinkedInFindAccessToken class of this package. 
 * It extracts information from the user profile
 * 
 * <p>It should be used after the LinkedInFindAccessToken class
 *    of this package has been used at least once to store an access token
 *    and that access token has not expired yet. 
 * </p>
 * 
 * 
 * @author Nishant Kumar
 *
 */



public class LinkedInExtractProfile implements IDataExtraction{

	
	/**
	 *  The OAuthConsumer object that represents the details of application
	 *  owner by the API key and API Secret Key obtained after registering 
	 *  this application.
	 */
	OAuthConsumer MSEOwner;
	
	
	/**
	 * The OAuthProvider Object that represents the LinkedIn API Provider
	 * and stores the URL for accessing request token and access Token
	 * at the first time connection
	 * 
	 */
	OAuthProvider linkedInAPIProvider;
	/**
	 * 
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
	 * The OAuthProvider Object that represents the LinkedIn API Provider
	 * and stores the URL for accessing request token and access Token
	 * at the first time connection
	 * 
	 */
	OAuthProvider apiProvider;
	
	LinkedInInformationStore informationStore;
	
	Context context;
	
	public String messageString="";
	
	
	public LinkedInExtractProfile(){
		MSEOwner =null;
		context = null;
		apiProvider=null;
		accessKeySecret = null;
		accessKey =null;

		linkedInAPIProvider=null;
		
		this.informationStore  = new LinkedInInformationStore();
		this.informationStore.firstName = null;
		this.informationStore.lastName = null;
		this.informationStore.fullName=null;
		this.informationStore.websiteURL=null;;
		this.informationStore.emailStore = null;;
		this.informationStore.affiliation = null;;
		this.informationStore.friends = null;
		this.informationStore.totalConnection= null;
	}
	
	public boolean callSequenceOfFunction(Context context){
			boolean isSuccess = false;
			try{
				this.context=context;
				YLogger Log = YLoggerFactory.getLogger();
				Log.d("LinkedInExtractProfile.callSequenceOfFunction",
						"Inside LinkedInExtractProfile call Sequence of Function");
			
				    String ownerKey="QbPkruUlNGQTqqTTbcVgvBTpzNDS27h7LYSg2L0jHNp6A1BLo2x3ekJGnW43dqKF";
				    String ownerSecretKey="WWZK4dyCxPsi1OKzKfn7OcPdzsGRNRip6UDGN0OSOnT7kfsi6RZrravRXg2q_GdY";
				    Log.d("LinkedInExtractProfile.callSequenceOfFunction",
				    		"Owner key defined ");
				    
				   /*
				     * Set the Consumer, i.e. property of owner of this application
				     * The API key and API Secret keys are obtained
				     * from the LinkedIn developer website after registering
				     * your application
				     * 
				     * If any error takes place, then null value will be returned. Hence exit.
				     * 
				     */
				    			
				    Log.d("LinkedInExtractProfile.callSequenceOfFunction",
				    		"Going to set Application owner\n ");
					    
					MSEOwner = setMSEOwner(ownerKey , ownerSecretKey);
				    if(MSEOwner==null){
				    	Log.d("LinkedInExtractProfile.callSequenceOfFunction",
				    			"set setMSEOwner returns null");
				    	
				    	return false;
				    }
				 
					Log.d("LinkedInExtractProfile.callSequenceOfFunction",
							"Application owner,consumer set ");
				    
				

				/*
			     * To read  the access token stored in the memory
			     * and set the value of the accessKey, accessKeySecret variables 
			     * to the value read.
			     * 
			     */
			
				Log.d("LinkedInExtractProfile.callSequenceOfFunction",
						"Going to read access token from memory");
			    
				isSuccess = readAccessToken(context);  
			    if(!isSuccess){
			       	return false;
			    }	
			     
			   /*
			    * After all the tokens are set for connection, 
			    * this set the url to connect for the 
			    * extraction of data from the profile 
			    */
			 	Log.d("LinkedInExtractProfile.callSequenceOfFunction",
			 			"Going to set Api url to be connected for extracting profile");
			
			    String urlToConnect = setAPIConnectionURL();
			    if(urlToConnect==null){
			    	return false;
			    }
			    
			    
			    /*
			     * Connect to the LinkedIn profile of the user and then 
			     * retrieve the profile with desired parameters value.
			     * Store the data as document in XML format.
			     * 
			     */
				Log.d("LinkedInExtractProfile.callSequenceOfFunction",
						"Going to connect to LinkedIn profile\n"+urlToConnect);
			    
				Document document = connect(urlToConnect); 
			    if(document==null){
			    	return false;
			    }
			
				Log.d("LinkedInExtractProfile.callSequenceOfFunction",
						"Document recieved , Profile extracted");
			    
				
			    // Extracting the desired data from the profile recieved.
				Log.d("LinkedInExtractProfile.callSequenceOfFunction",
						"Going to extract First Name from Document");			    
			    String firstName = extractFirstName(document);
		
				Log.d("LinkedInExtractProfile.callSequenceOfFunction",
						"Going to extract Last Name from Document");			    
			    String lastName = extractLastName(document);
		
				Log.d("LinkedInExtractProfile.callSequenceOfFunction",
						"Going to extract Current Position from Document");			    
			    String currentPosition = extractCurrentPosition(document);
		
				Log.d("LinkedInExtractProfile.callSequenceOfFunction",
						"Going to extract Total Connection from Document");			    
			    String totalConnection = extractNumberOfFriends(document);
			
				Log.d("LinkedInExtractProfile.callSequenceOfFunction",
						"Going to extract email from the document");			    
			    EmailStore []emailStore = extractEmail(document);
			
				Log.d("LinkedInExtractProfile.callSequenceOfFunction",
						"Going to extract Phone Number from Document");			    
			    PhoneNumberStore [] phoneNumberStore = extractMobile(document);
			
				Log.d("LinkedInExtractProfile.callSequenceOfFunction",
						"Going to extract webPage URL");				
				String webpageURL = extractWebPageURL(document);
			    
				Log.d("LinkedInExtractProfile.callSequenceOfFunction",
						"All info extracted , going to store");		
			    ArrayList<LinkedInInformationStore> friends = extractFriends(document);
			    
			    // Store the extracted Information in the Class object
			    
			    
			    informationStore.firstName = firstName;
			    informationStore.lastName = lastName;
			    informationStore.fullName=firstName + " "+lastName;
			    informationStore.websiteURL=webpageURL;
			    informationStore.emailStore = emailStore;
			    informationStore.affiliation = currentPosition;
			    informationStore.friends = friends;
			    informationStore.totalConnection= totalConnection;
			   
				Log.d("LinkedInExtractProfile.callSequenceOfFunction",
						"LinkedIn object formed and value stored");
			   
				return true;
			    
			}catch(Exception ex){
				YLogger Log = YLoggerFactory.getLogger();
			     
				
				String debug ="LinkedInExtractProfile.callSequenceOfFunction: ";
				debug +=ex.toString();
				Log.d("LinkedInExtractProfile.callSequenceOfFunction", debug);
				
			//	Toast.makeText(context,debug,Toast.LENGTH_LONG).show();
                messageString = debug;
				return false;
		
			}
	       
	}
	/**
	 * 
	 * Sets the API key obtained from the LinkedIn Developer website 
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
		
			Log.d("LinkedInExtractProfile.setMSEOwner","Inside  setMSEOwner");
		    
		    try{
		        OAuthConsumer consumer = new DefaultOAuthConsumer
		        				(
	                              ownerKey,
	                              ownerSecretKey,
	                              SignatureMethod.HMAC_SHA1);
	        
		       
				Log.d("LinkedInExtractProfile.setMSEOwner",
						"MSE owner property setup over");			        
		        return consumer;
		    }
		    catch(Exception ex){
		    	
		    	String debug ="LinkedInExtractProfile.setMSEOwner: ";
		    	debug +=	ex.getMessage();
		    	Log.d("LinkedInExtractProfile.setMSEOwner",debug);	
		    	//Toast.makeText(context,debug,Toast.LENGTH_LONG).show();
		    	messageString = debug;
		        return null;
		    }
		   
	}
	/**
     * 
     * Sets the Access token value to accessKey and accessKeySecret
     * variable by reading its value from memory.
     * This function is called when the user has already granted permission
     * to the application to connect to profile, hence the Access Tokens are
     * save in the memory.
     * 
     * @return Returns true if the Access Tokens are successfully read
     *         from the memory and its value is assigned to the variables.
     * 
     */
    private boolean readAccessToken(Context context){
       /*
    	* This function is called when the Access Token is already stored in memory.
        * This means the user has already granted permission to the application
        * to connect to the profile.
        */
		try{

	    	YLogger Log = YLoggerFactory.getLogger();
			
	    	Log.d("LinkedInExtractProfile.readAccessToken"," Reading access Token from memory ");
	    	
			File file = context.getFileStreamPath("LinkedInStore");
			
			if(!file.exists()){
				Log.d("LinkedInExtractProfile.readAccessToken","FILE doesnot exists");
			//	Toast.makeText(context,"AccessToken File Doesnot Exists",Toast.LENGTH_LONG).show();
				messageString = "LinkedInExtractProfile.readAccessToken: "+"File doesnot Exists";
				return false;
			}
			
			BufferedReader bufread = new BufferedReader(new FileReader(file));
			accessKey =    bufread.readLine();
			accessKeySecret = bufread.readLine();
			Log.d("LinkedInExtractProfile.readAccessToken",
					" In readAccessToken Acces Token was read:"+accessKey);
			Log.d("LinkedInExtractProfile.readAccessToken",
					" Acces Token Secret was read:"+accessKeySecret);
			return true;
		}catch(Exception ex){
			String debug = "CATCH: LinkedInExtractProfile.readAccessToken: ";
			debug+=ex.toString();

	    	YLogger Log = YLoggerFactory.getLogger();
			
			Log.d("LinkedInExtractProfile.readAccessToken",debug);
	//		Toast.makeText(context,debug,Toast.LENGTH_LONG).show();
			messageString = debug;
			
			return false;
		}
	
    }
    
    
   
    /**
     * Forms the Url to connect to extract information from the profile
     * 
     * 
     * @return  The Url to connect to extract information from the profile
     */
    private String setAPIConnectionURL(){
    	String apiBaseURL = "http://api.linkedin.com/v1/people/~";
    	String parameters = "(id,first-name,last-name,num-connections,phone-numbers,site-standard-profile-request,im-accounts,positions,connections)";	
        String urlToConnect = apiBaseURL+":"+parameters;
    	return urlToConnect;
    	
    }
    
 /**
  * Used to connect to the linkedin profile 
  * and read the response from it.  The response contains the 
  * extracted infromation  from the profile of user 
  * 
  * 
  * @param urlToConnect The url to connect to extract information. 
  * This url is formed by the setAPIConnectionURL of this class.
  * 
  * @return The xml result in the form of document obtained as a  
  * response of the connection
  */
	private Document connect(String urlToConnect) {
		// TODO Auto-generated method stub
		/*
		 * Connect to the url for 
		 * extracting infor for the profile
		 * 
		 */
		/**
		 *  String message is used to store the progress of connection to the API.
		 *  If the Connection to API fails, then the value stored in message is
		 *  displayed as error message so that user can know at which stage of 
		 *  API connection does the error occurs.
		 *  
		 */		
        
		try{
			YLogger Log = YLoggerFactory.getLogger();
		
			 Log.d("LinkedInExtractProfile.connect","inside try of Connect");
     
		
			 Log.d("LinkedInExtractProfile.connect","Setting the Access Toke and secret for the connection");
      
		     MSEOwner.setTokenWithSecret(accessKey,accessKeySecret);

			 
			 URL url = new URL(urlToConnect);
         
   		     Log.d("LinkedInExtractProfile.connect","URL formed");
   		 
             HttpURLConnection request = (HttpURLConnection) url.openConnection();
             request.setConnectTimeout(40000);
   		     Log.d("LinkedInExtractProfile.connect","Connection opened");
            
   		     Log.d("LinkedInExtractProfile.connect","Before signIn");
   		     Log.d("LinkedInExtractProfile.connect","AccessToken is "+MSEOwner.getToken());
   		     Log.d("LinkedInExtractProfile.connect","AccessTokenSecret  is "+MSEOwner.getTokenSecret());
        
   		     MSEOwner.sign(request);
            
          
   		     Log.d("LinkedInExtractProfile.connect","Sign in by application owner ");
	        
	       
   		     Log.d("LinkedInExtractProfile.connect","Check Your Internect Connection because connecting 2 LinkedIn API");
			 
   		     request.connect();
            
            
   		     Log.d("LinkedInExtractProfile.connect","Connected to API");
            
   			
   		     Log.d("LinkedInExtractProfile.connect","Going to get profile");
	    
   		     SAXReader reader = new SAXReader();
   		     Document document = reader.read(request.getInputStream());
	        
   		     Log.d("LinkedInExtractProfile.connect","Profile received");
            
	        
	        return document; 
		}
		catch(Exception ex){
			String debug ="Catch:LinkedInExtractProfile.connect: ";
	    	debug +=	ex.getMessage();
			YLogger Log = YLoggerFactory.getLogger();

	    	Log.d("LinkedInExtractProfile.connect",
	    			"LinkedInExtractProfile.connect: "+ex.toString());
	    	//Toast.makeText(context,debug,Toast.LENGTH_LONG).show();
            messageString =debug;
	    return null;
			
		}
	}
	
	
	/**
	 * Extracts the first name from the 
	 * obtained xml result from the extracted data of the profile
	 * 
	 * @param document The response obtained from the linkedin profile
	 * connection of a user
	 * 
	 * @return The first name of the user
	 */
	private String extractFirstName(Document document) {
		// TODO Auto-generated method stub
		/*
		 * Extract the node for first names
		 * 
		 * process the node if found
		 * 
		 * return it.
		 * 
		 */
		
	    try{

	    	YLogger Log = YLoggerFactory.getLogger();
	 	     
			Log.d("LinkedInExtractProfile.extractFirstName",
					"Going to select Node for firstname");
			
		    Node node =	document.selectSingleNode("//person/first-name");
		    if(node==null){
            	
    			Log.d("LinkedInExtractProfile.extractFirstName",
    					"No first name for the User");
    			return null;
            }
          
		    String firstName = node.getText().trim();

			Log.d("LinkedInExtractProfile.extractFirstName","returning FirstName");
			
		    return(firstName);
	    
	    }
	    catch(Exception ex){
	    	String debug ="In Catch:LinkedIn.extractFirstName";
	    	debug +=	ex.getMessage();
	    	YLogger Log = YLoggerFactory.getLogger();
	 	    
	    	Log.d("LinkedInExtractProfile.extractFirstName",debug);
	    	
	    	return null;
	    }	    
	}
	
	/**
	 * Extracts the last name of the user
	 * 
	 * @param document The response obtained after the connection
	 * to the user linkedin profile
	 * 
	 * @return The last name of the user
	 */
	private String extractLastName(Document document) {
		// TODO Auto-generated method stub
		/*
		 * Extract the node for last name
		 * 
		 * process the node if found
		 * 
		 * return it.
		 * 
		 */
		YLogger Log = YLoggerFactory.getLogger();
	    Log.d("LinkedInExtractProfile.extractLastName","Inside extractLastName");
		
       try{

			Log.d("LinkedInExtractProfile.extractLastName","Going to Select LastName");
			
            Node node =	document.selectSingleNode("//person/last-name");
            if(node==null){
            	Log.d("LinkedInExtractProfile.extractLastName","No Last Name for the User");
    			return null;
            }
            
            String lastName = node.getText().trim();
		   
		    Log.d("LinkedInExtractProfile.extractLastName","returning LastName");
			
		   return(lastName);
	   }
	   catch(Exception ex){
		  String   debug ="In Catch:LinkedIn.extractLastName";
	    	debug +=	ex.getMessage();
	    	Log.d("LinkedInExtractProfile.extractLastName",debug);
	        return null;
		    	
	   }
		    
	}
		
	
	/**
	 * Extracts email from the response obtained after 
	 * the connection to user linkedin profile.
	 * 
	 * @param document
	 * @return
	 */
	private EmailStore[] extractEmail(Document document) {
		// TODO Auto-generated method stub
		
		/*
		 * Extract the node for im account
		 * 
		 * process the node if found
		 * 
		 * return it.
		 * 
		 */
		String email=null;
		String emailServer=null;
		String accName=null;
		
		
		YLogger Log = YLoggerFactory.getLogger();
		
		Log.d("LinkedInExtractProfile.extractEmail","Inside extractEmail");
		
		try{
		
			Log.d("LinkedInExtractProfile.extractEmail",
					"Going to select node for imAccount");
			
            List<Node> nodes = null;
            nodes = (List<Node>)document.selectNodes("//person/im-accounts/im-account");
      
            if(nodes==null){
        
    			Log.d("LinkedInExtractProfile.extractEmail","No email for the User");
    			return null;
            }
            else if(nodes.size()<=0){
            
    			Log.d("LinkedInExtractProfile.extractEmail","No email for the User");
    			return null;
            }
            
           
			Log.d("LinkedInExtractProfile.extractEmail","email Extracted");

			
			EmailStore[] emailStoreArray = new EmailStore[nodes.size()];
			int index=0;
			
			
		    for (Node node : nodes){  
		    
				Log.d("LinkedInExtractProfile.extractEmail","Processing email");			    	
		    	
				emailServer= node.selectSingleNode("im-account-type").getText().trim();
                accName= node.selectSingleNode("im-account-name").getText().trim();
				
                if(accName.indexOf("@")==-1){
					    if(emailServer.equalsIgnoreCase("gtalk"))
					     {
					    	 email= accName+"@gmail.com";
					    	 EmailStore emailExtracted = new EmailStore();
							 emailExtracted.emailAddress=email;
							 emailExtracted.emailType="IM";
							 emailStoreArray[index++]=emailExtracted;
					     }
					    else if(emailServer.equalsIgnoreCase("yahoo"))
					     {
					    	 email= accName+"@yahoo.com";
					    	 EmailStore emailExtracted = new EmailStore();
							 emailExtracted.emailAddress=email;
							 emailExtracted.emailType="IM";
							 emailStoreArray[index++]=emailExtracted;
					     }
					    else if(emailServer.equalsIgnoreCase("aim"))
					     {
					    	 email= accName+"@aim.com";
					    	 EmailStore emailExtracted = new EmailStore();
							 emailExtracted.emailAddress=email;
							 emailExtracted.emailType="IM";
							 emailStoreArray[index++]=emailExtracted;
					     }
					    else
					    {	emailStoreArray = null;
					    
					    	Log.d("LinkedInExtractProfile.extractEmail",
					    			" Email found not of Gtalk/Yahoo/Aim");			   
						
					        return null;
					    }
					   
						Log.d("LinkedInExtractProfile.extractEmail",
								" Returning from email");			   
						return emailStoreArray;
					    
					    /*if(emailServer.equalsIgnoreCase("icq"))
					     {
					    	 email= accName;
					         EmailStore emailExtracted = new EmailStore();
							 emailExtracted.emailAddress=email;
							 emailExtracted.emailType="IM";
							 emailStoreArray[index++]=emailExtracted;
					     }
					    if(emailServer.equalsIgnoreCase("msn"))
					     {
					    	 email= accName+"@msn.com";
					         EmailStore emailExtracted = new EmailStore();
							 emailExtracted.emailAddress=email;
							 emailExtracted.emailType="IM";
							 emailStoreArray[index++]=emailExtracted;
					     }
				      */  		            
                }
                else{ 
                	email = accName;
                	EmailStore emailExtracted = new EmailStore();
			        emailExtracted.emailAddress=email;
			        emailExtracted.emailType="IM";
			        emailStoreArray[index++]=emailExtracted;                
                }		   
            }
		
			Log.d("LinkedInExtractProfile.extractEmail"," No email Found");			   
			return emailStoreArray;
		    
		}
		catch(Exception ex){
			String  debug ="In Catch ";
		     debug +=	ex.getMessage();
		     Log.d("LinkedInExtractProfile.extractEmail",debug);
	        return null;
			
		}	
	}
		

	
	private PhoneNumberStore[] extractMobile(Document document) {
		// TODO Auto-generated method stub
		/*
		 * Extract the node for phone number
		 * 
		 * process the node if found
		 * 
		 * return it.
		 * 
		 */
		YLogger Log = YLoggerFactory.getLogger();
		String contactType=null;
		String contactNumber=null;

		Log.d("LinkedInExtractProfile.extractMobile","Inside LinkedIn.extractMobile");
		try{
		
			Log.d("LinkedInExtractProfile.extractMobile","Going to extract Node for mobiles");
			
            List<Node> nodes = (List<Node>)document.selectNodes("//person/phone-numbers/phone-number");
            if(nodes==null){
            
    			Log.d("LinkedInExtractProfile.extractMobile","No phone Number for the User");
    			return null;
            }
            else if(nodes.size()<=0){
            
    			Log.d("LinkedInExtractProfile.extractMobile","No phone number for the User");
    			return null;
            }
            
            PhoneNumberStore []phoneNumberStoreArray = new PhoneNumberStore[nodes.size()];
            int index=0;
           
			Log.d("LinkedInExtractProfile.extractMobile","atleast one phone number found");
            
			Node contactTypeNode=null;
			Node contactNumberNode=null;
			
			for (Node node : nodes){  
			   
				contactTypeNode = node.selectSingleNode("phone-type");
				contactNumberNode = node.selectSingleNode("phone-number");
				
				contactType=null;
				contactNumber=null;
				
				if(contactTypeNode!=null)
		    	contactType= contactTypeNode.getText().trim();
				
		    	if(contactNumberNode!=null)
				contactNumber= contactNumberNode.getText().trim();
				
		    	PhoneNumberStore phoneNumber = new PhoneNumberStore();
				phoneNumber.phoneNumberType=contactType;
			    phoneNumber.phoneNumber=contactNumber;
			    
			    phoneNumberStoreArray[index] = phoneNumber;
			
				Log.d("LinkedInExtractProfile.extractMobile","Stored the extracted phone number");
			    
				index++;
            }
			
			Log.d("LinkedInExtractProfile.extractMobile","ALl phone Number Stored");
            return phoneNumberStoreArray;
		    
		}
		catch(Exception ex){
			String debug ="In Catch:LinkedIn.extractMobile";
		     debug +=	ex.getMessage();
		     Log.d("LinkedInExtractProfile.extractMobile",debug);
	        return null;
			
		}	
	}
	
	
	private String extractCurrentPosition(Document document) {
		// TODO Auto-generated method stub
		/*
		 * Extract the node for the position from the document
		 * 
		 * check if the position is current positon
		 * 
		 * if cureent position , process and return it.
		 * 
		 */
		YLogger Log = YLoggerFactory.getLogger();
		
		Log.d("LinkedInExtractProfile.extractCurrentPosition",
				"Extracting Current Work Status");
		try{
			Log.d("LinkedInExtractProfile.extractCurrentPosition",
					"Going to select Node for position");
			
			List<Node> nodes = (List<Node>)document.selectNodes( "//person/positions/position");
			if(nodes==null||nodes.size()<=0){
	          
			
	          Log.d("LinkedInExtractProfile.extractCurrentPosition",
	    				"No current Position for the User");
	    		return null;
	        }
			 
			Log.d("LinkedInExtractProfile.extractCurrentPosition",
					"at least one position found");
			
		    for (Node node : nodes)
		      { 
		    	
				Log.d("LinkedInExtractProfile.extractCurrentPosition",
						"More that one position");
			     
				String isCurrent= node.selectSingleNode("is-current").getText().trim();
					
			     if(isCurrent.equalsIgnoreCase("true")){
			         
					 Log.d("LinkedInExtractProfile.extractCurrentPosition",
							 "If the position is current");
					 
					 String title=null;
					 String company=null;
					 String summary=null;
					 
			    	 title= node.selectSingleNode("title").getText().trim();
			 
			 		 Log.d("LinkedInExtractProfile.extractCurrentPosition",
			 				 "title extracted");
			    	 
			 		 company= node.selectSingleNode("company/name").getText().trim();
			    
			 		 Log.d("LinkedInExtractProfile.extractCurrentPosition",
			 				 "company extracted");
			    	 
			 		 summary= node.selectSingleNode("summary").getText().trim();
			
			 		 Log.d("LinkedInExtractProfile.extractCurrentPosition",
			 				 "summary extracted");
			    	 
			 		 String totalResult=null;
			 		 if(title!=null)
			 			 totalResult = title+" at "+company+"\n"+summary;
			    	 
			 		 Log.d("LinkedInExtractProfile.extractCurrentPosition",
			 				 "Tolal Current status found");
			    	 
			 		 return totalResult;
			     }
			  }
		   
		    Log.d("LinkedInExtractProfile.extractCurrentPosition",
		    		"No position specified");
		     return null;
		}
		catch(Exception ex){
			String debug ="In Catch ";
		     debug +=	ex.getMessage();
		     Log.d("LinkedInExtractProfile.extractCurrentPosition",debug);
	        return null;
			
		}	
	}
	

	private String extractNumberOfFriends(Document document) {
		// TODO Auto-generated method stub
		/*
		 * Extract the node that has total no. for friends in attribute
		 * 
		 *  extract the attribute 
		 *  
		 *  return it.
		 * 
		 */
		YLogger Log = YLoggerFactory.getLogger();
	
		Log.d("LinkedInExtractProfile.extractNumberOfFriends",
				"Extracting total connection"); 
		try{
			String totalConnection="0";
		
	 		Log.d("LinkedInExtractProfile.extractNumberOfFriends",
	 				"Going to select total Connection");
			
	 		Node node =	document.selectSingleNode("//person/connections");
	 		
	 		if(node!=null)
			totalConnection = node.valueOf( "@total" );
	 			
			Log.d("LinkedInExtractProfile.extractNumberOfFriends",
					"returning Total Connection");
			
			return totalConnection;
		 }
		 catch(Exception ex){
			String  debug ="In Catch ";
		     debug +=	ex.getMessage();
		     Log.d("LinkedInExtractProfile.extractNumberOfFriends",debug);
	         return null;
				
		 }

	}
	


	private ArrayList<LinkedInInformationStore> extractFriends(Document document) {
		// TODO Auto-generated method stub
		/*
		 * Extract all the nodes for connection
		 * present in the document
		 * 
		 * Extract the required data from the nodes and 
		 * the name to the arrayList
		 * 
		 */
		ArrayList<LinkedInInformationStore> friendsName= new ArrayList<LinkedInInformationStore>();
		String firstName="";
		String lastName="";
		String fullName="";
		String webpageURL="";
		
		YLogger Log = YLoggerFactory.getLogger();
		Log.d("LinkedInExtractProfile.extractFriends","Inside Extract Friends");
		
 		try{	
			Log.d("LinkedInExtractProfile.extractFriends",
					"Going to select nodes for connection");			
			
	 		List<Node> nodes = (List<Node>)document.selectNodes("//person/connections/person");
	 		 
	 		if(nodes==null){
	           Log.d("LinkedInExtractProfile.extractFriends",
	        		   "No phone Number for the User");
	    		return null;
	        }
	        else if(nodes.size()<=0){
	          Log.d("LinkedInExtractProfile.extractFriends",
	        		  "No phone number for the User");
	    		return null;
	       }
	            
	 		Log.d("LinkedInExtractProfile.extractFriends",
	 				"Nodes found for connection");  
			Node firstNameNode;
			Node lastNameNode;
			Node webUrlNode;
			
			for (Node node : nodes){
				
				firstNameNode = null;
				lastNameNode = null;
				webUrlNode= null;
				firstName=null;
				lastName=null;
				fullName=null;
				webpageURL=null;
				
				firstNameNode=node.selectSingleNode("first-name");
				if(firstNameNode!=null)
				firstName=firstNameNode.getText().trim();
		    	
				lastNameNode= node.selectSingleNode("last-name");
				if(lastNameNode!=null)
				lastName=lastNameNode.getText().trim();  
				
			    fullName= firstName+" "+lastName;
			    
			    Log.d("LinkedInExtractProfile.extractFriends",
			    		"name of friend extracted");
			    
		 		Log.d("LinkedInExtractProfile.extractFriends",
		 				"Going to extract friend webpage url");
			    
		 		webUrlNode = node.selectSingleNode("site-standard-profile-request/url");
		 		if(webUrlNode!=null)
		 		webpageURL=webUrlNode.getText().trim();
		 		
		 		Log.d("LinkedInExtractProfile.extractFriends",
		 				"Friends URL extracted");
		 		
			    if(webpageURL!=null)
		 		webpageURL =processURL(webpageURL);	
		 		
		 		Log.d("LinkedInExtractProfile.extractFriends",
		 				"Friends url procesed");
			    
		 		LinkedInInformationStore information = new LinkedInInformationStore();
		 		information.firstName=firstName;
		 		information.lastName=lastName;
		 		information.fullName = fullName;
		 		information.websiteURL=webpageURL;
		 		
		 		friendsName.add(information);
		 		
			}
			Log.d("LinkedInExtractProfile.extractFriends",
					"Returning ArrayList that contains connection");
			return friendsName;
	    }
	    catch(Exception ex){	    	
	    	 String debug ="In Catch:";
		     debug +=	ex.getMessage();
		     Log.d("LinkedInExtractProfile.extractFriends",debug);
		   //  Toast.makeText(context,debug,Toast.LENGTH_LONG).show();

	         return null;
				
	    }
				
	}


	/**
	 * Extracts the webpage url recieved from the profile
	 * the url is processed to change it to a valid url (for e.g. removing
	 * &amp; to & )
	 * 
	 * @param document  The total xml storing the profile of the user
	 * @return  The valid Webpage Url of the profile user
	 */
	private String extractWebPageURL(Document document){
		/*
		 * Extract the node that has total no. for friends in attribute
		 * 
		 *  extract the attribute 
		 *  
		 *  return it.
		 * 
		 */
		YLogger Log = YLoggerFactory.getLogger();
		Log.d("LinkedInExtractProfile.extractWebPageURL","Extracting Profile URL"); 
		try{
			Log.d("LinkedInExtractProfile.extractWebPageURL","Going to select WebPage URL");
			
	 		Node node =	document.selectSingleNode("//person/site-standard-profile-request/url");
			String webpageURL = node.getText().trim();
			if(webpageURL!=null)
				webpageURL=processURL(webpageURL);
			Log.d("LinkedInExtractProfile.extractWebPageURL","Webpage url found for the user");
			
			return webpageURL;
		 }
		 catch(Exception ex){
			String debug ="In Catch ";
		     debug +=	ex.getMessage();
		     Log.d("LinkedInExtractProfile.extractWebPageURL",debug);
	         return null;
				
		 }
    }
	/**
	 * Used to remove the unwanted part of the 
	 * url that is obtained by the extractWebPageUrl method of this class. The method
	 * gives the url that is the Linkedin profile url of the user.
	 * 
	 * @param webpageURL The linkedin profile url
	 * 
	 * @return The url after removing the unwanted part of the webpageUrl
	 */
	private String processURL(String webpageURL){
		
		YLogger Log = YLoggerFactory.getLogger();
		
		Log.d("LinkedInExtractProfile.processURL","Going to process url "+webpageURL);
	    
		String tempString=webpageURL;
		String finalURL=webpageURL;
		String toReplace = "&amp;";
		
		int index=0;
		
		if(webpageURL.indexOf(toReplace)>0)
		tempString = webpageURL.replaceAll(toReplace, "&");
		
 		Log.d("LinkedInExtractProfile.processURL","all amp replaced");
	    
		index = tempString.lastIndexOf("&trk");
		
		if(index>0)
		finalURL = tempString.substring(0, index);
		
	return finalURL;	
	}
	
	
	
	@Override
	public String getFirstName(){
		
		if(informationStore!=null && informationStore.firstName!=null)
			return informationStore.firstName;
		else 
			return null;
	}
	
	@Override
	public String getLastName(){
		
		if(informationStore!=null && informationStore.lastName!=null)
			return informationStore.lastName;
		else 
			return null;
	}
	
	@Override
	public String getFullName(){
		
		if(informationStore!=null && informationStore.fullName!=null)
			return informationStore.fullName;
		else 
			return null;
	}
	
	@Override
	public String getAffiliation(){
		
		if(informationStore!=null && informationStore.affiliation!=null)
			return informationStore.affiliation;
		else 
			return null;
	}
	
	@Override
	public String getWebsiteUrl(){
		
		if(informationStore!=null && informationStore.websiteURL!=null)
			return informationStore.websiteURL;
		else 
			return null;
	}
	
	@Override
	public EmailStore[] getEmail(){
		
		if(informationStore!=null && informationStore.emailStore!=null)
			return informationStore.emailStore;
		else 
			return null;
	}
	
	@Override
	public ArrayList getFriends(){
		
		if(informationStore!=null && informationStore.friends!=null)
			return informationStore.friends;
		else 
			return null;
	}
	
    
	public LinkedInInformationStore getCompleteExtractedProfile(){
		
		if(informationStore!=null)
			return informationStore;
		else 
			return null;
	}
}
	
	



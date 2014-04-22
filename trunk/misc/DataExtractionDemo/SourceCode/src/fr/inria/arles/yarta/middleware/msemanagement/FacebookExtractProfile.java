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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;


/**
 *  Used to extract information from the Facebook profile of a user.
 *  <p>The application registerd at Facebook is at:</p>
 *  <p>http://www.facebook.com/apps/application.php?id=122447924463934</p>
 *
 * 
 * @author Nishant Kumar
 *
 */
public class FacebookExtractProfile implements IDataExtraction {
	/**
	 *  The application id obtained after registering an application for Facebook.
	 *  <p>The application registerd at Facebook is at:</p>
	 *  <p>http://www.facebook.com/apps/application.php?id=122447924463934</p>
	 */
	
	String mseFacebookAppId;
	/**
	 * The secret key obtained after the application is registered at
	 * Facebook developer website.
	 *  
	 */
	String mseFacebookAppSecret;
	
	
	/**
	 *   The JSON object that stores basic information obtained after the 
	 *   query to Facbook api to extract result
	 */
	JSONObject extractedProfileJSONObject; 
	
	/**
	 * The JSON object obtained after the result obtained for
	 * extraction of freinds of the user.
	 * 
	 * 
	 */
	JSONObject extractedFriendsJSONObject; 
	
	/**
	 * The message string that store any information. This can be used by the 
	 * calling function to know the description of any error that may happen
	 * during the extraction of profile.
	 *  
	 */
	public String messageString;
	
	
	public FacebookExtractProfile(){
		mseFacebookAppId = "122447924463934";
		mseFacebookAppSecret="0c644776ca0d992ac4a886e1d20371fc";
		extractedProfileJSONObject=null;
		extractedFriendsJSONObject=null;
		messageString="";
		
	}
	
	/**
	 * Extracts information from the facebook 
	 * profile of the user.
	 * <p>
	 * It is called after the access token is obtained 
	 * to access the facebook profile of the user.
	 * </p>
	 * 
	 * The access token is obtained by FacebookFindAccessToken class.
	 * This method should be called after the execution of
	 * FacebookFindAcccessToken.java of this package. 
	 * 
	 * @param accessToken The access token obtained in the 
	 *     FacebookFindAccessToken class.
	 * 
	 * @return  returns true if extraction of data from
	 * a facebook profile is successful, else false.
	 */
	public boolean extractFacebookProfile(String accessToken){
		
		YLogger Log = YLoggerFactory.getLogger();
		Log.d("FacebookExtractProfile.extractFacebookProfile",
				"Inside extractFacebookProfile");
		
		try{
		    
			String urlForProfile = getProfileExtractionURL(accessToken);
			
			Log.d("FacebookExtractProfile.extractFacebookProfile",
					"URL to Extract profile is"+urlForProfile);
			
			if(urlForProfile==null){
				Log.d("FacebookExtractProfile.extractFacebookProfile",
						"Some Error:URL is null ");
				
				return false;	
			}
			
		    String extractedProfileString = connect(urlForProfile);
	       
		    if(extractedProfileString==null){
            	Log.d("FacebookExtractProfile.extractFacebookProfile",
				"Some Error:Received Null after Profile Exraction");
            	return false;
            }
		    
		    extractedProfileJSONObject = new JSONObject(extractedProfileString);
		    
		    Log.d("DEBUG","RESPONSE from face book profile extraction"+
		    		extractedProfileString);
            
		    
            String urlForFriends = getFriendExtractionURL(accessToken);
			
			Log.d("FacebookExtractProfile.extractFacebookProfile",
					"URL to Extract Friend is "+urlForFriends);
			
			if(urlForFriends==null){
				Log.d("FacebookExtractProfile.extractFacebookProfile",
						"Some Error:URL for friend is null ");
				return false;	
			}
			
		    extractedProfileString = connect(urlForFriends);
	       
		    if(extractedProfileString==null){
            	Log.d("FacebookExtractProfile.extractFacebookProfile",
				"Some Error:Received Null after Friends Exraction");
            	return false;
            }
		    
		    extractedFriendsJSONObject = new JSONObject(extractedProfileString);
		    
		    Log.d("FacebookExtractProfile.extractFacebookProfile","Response from face book Friend extraction"+extractedProfileString);
          
		    return true;
                
		}catch(Exception ex){
			Log.d("FacebookExtractProfile.extractFacebookProfile","In Catch Extract Facebook Profile"+ex.toString());
		    
			messageString ="FacebookExtractProfile.extractFacebookProfile: "+
			                "Cannot Extract Profile: "+ex.toString();
			
			return false;
		}
	}
	
	
	/**
	 * 
	 * <p> Appends the access token to the deafult url for profile
	 *    and return it
	 * </p>
	 * Returns the url of the facebook graph api
	 * to be connected to extract profile of the user.
	 * 
	 * <p>It is called from the extractFacebookProfile function of this class.
	 * </p>
	 *  
	 * 
	 * @param accessToken  The accesstoken obtained as a parameter value in the 
	 *               extractFacebookProfile function of this class
	 * 
	 * @return The string that is to be connected to extract the profile of the user
	 * 
	 */
	private String getProfileExtractionURL(String accessToken){
	   
		String baseUrl = "https://graph.facebook.com/me";
		
	//	String fields = "fields=id,first_name,last_name,name,email,link,work,website,education,friends";
			
	//	String totalUrl = baseUrl+"?"+ fields+"&"+accessToken; 
      
		String totalUrl = baseUrl+"?"+accessToken; 
	      
		return totalUrl; 
	}
	
	/**
	 * Used to form the url for extracting friends
	 * from the user facebook profile. 
     * <p>It appends the access token to the deafult  url for friends
 	 *    and returns it.
	 * </p>
	 * 
	 * <p>It is called from the extractFacebookProfile function of this 
	 * class.
	 * </p>
	 * @param accessToken The access token obtained 
	 * @return
	 */
	private String getFriendExtractionURL(String accessToken){
		   
		String baseUrl = "https://graph.facebook.com/me/friends";
		
	//	String fields = "fields=id,first_name,last_name,name,email,link,work,website,education,friends";
			
	//	String totalUrl = baseUrl+"?"+ fields+"&"+accessToken; 
      
		String totalUrl = baseUrl+"?"+accessToken; 
	      
		return totalUrl; 
	}

	/**
	 * Used to connect to a url present on internet
	 * and obtain the result .
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
	private String connect(String url) 
	   throws MalformedURLException, IOException {
		YLogger Log = YLoggerFactory.getLogger();
		String response = null;;
		HttpURLConnection conn =null;
		try {
		     conn = (HttpURLConnection) new URL(url).openConnection();
	 		 response = read(conn.getInputStream());
	 		 return response;
		} catch (Exception ex) {
	     	response = read(conn.getErrorStream());
	     	Log.d("FacebookFindAccessToken.connect","CATCH : "+response+ex.toString());
		    messageString ="FacebookFindAccessToken.connect: "+response+ex.toString();
	     	
		    return null;
		}
		
	}

	/**
	 * Called by the connect function of this class 
	 * It reads the InputStream of a Url that is connected
	 * by the connect function. The read bytestream is converted to 
	 * string and returned.
	 * 
	 * @param in The InputStream to be read after the url is connected
	 * @return  The obtained result from the connected site.
	 * 
	 * @throws IOException
	 */
	private String read(InputStream in) throws IOException {
		
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
			 messageString ="FacebookFindAccessToken.read: Cannot read Facebook Response"+ex.toString();
		     return null;	
		}
	}


	@Override
	public String getFirstName(){
		
		try{
			 YLogger Log = YLoggerFactory.getLogger();
			 Log.d("FacebookExtractProfile.getFirstName","Inside GetFirstName");
		    if(extractedProfileJSONObject!=null)
			 return extractedProfileJSONObject.optString("first_name");
		    else
		    	return null;
		    
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			 Log.d("FacebookExtractProfile.getFirstName",
					 "CATCH "+ex.toString());
			 messageString ="FacebookFindAccessToken.getFirstName: "+
			                "Cannot read Facebook FirstName"+ex.toString();
			   
	         return null;
		}

	}

	@Override
	public String getLastName(){
		
		try{
			YLogger Log = YLoggerFactory.getLogger();
			 Log.d("FacebookExtractProfile.getLastName","Inside GetLastName");
			 if(extractedProfileJSONObject!=null)
				 return extractedProfileJSONObject.optString("last_name");
			 else
			    return null; 
		}
		catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			 Log.d("FacebookExtractProfile.getLastName",
					 "CATCH "+ex.toString());
			 messageString ="FacebookFindAccessToken.getLastName"+
             "Cannot read Facebook LastName"+ex.toString();

	         return null;
		}
	}
	

	@Override
	public String getFullName(){
		
		try{
			YLogger Log = YLoggerFactory.getLogger();
			 Log.d("FacebookExtractProfile.getFullName","Inside GetFullName");
			 if(extractedProfileJSONObject!=null)
		     return extractedProfileJSONObject.optString("name");
			    else
			    	return null;
		}
		catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			 Log.d("FacebookExtractProfile.getFullName",
					 "CATCH "+ex.toString());
			 messageString ="FacebookFindAccessToken.getFullName"+
             "Cannot read Facebook FullName"+ex.toString();

	         return null;
		}
  
	}
	


	@Override
	public String getWebsiteUrl(){
		
		try{
			YLogger Log = YLoggerFactory.getLogger();
			 Log.d("FacebookExtractProfile.getWebUrl","Inside GetWebUrl");
			 if(extractedProfileJSONObject!=null)
		     return extractedProfileJSONObject.optString("link");
			    else
			    	return null;
		}
		catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			 Log.d("FacebookExtractProfile.getWebUrl",
					 "CATCH "+ex.toString());
			 messageString ="FacebookFindAccessToken.getWebsiteUrl"+
             "Cannot read Facebook WebsiteUrl"+ex.toString();

	         return null;
		}
	}


	@Override
	public EmailStore[] getEmail(){
		
		try{
			YLogger Log = YLoggerFactory.getLogger();
			 Log.d("FacebookExtractProfile.getFirstName","Inside GetEmail");
			 if(extractedProfileJSONObject!=null){
			     String emailAddress= extractedProfileJSONObject.optString("email");
			 
			     if(emailAddress!=null){
				     EmailStore[] emailStore  = new EmailStore[1];
				     
				     EmailStore emailDetail = new EmailStore();
				     emailDetail.emailAddress=emailAddress;
				     emailDetail.emailType="Office";
				     emailStore[0]=emailDetail;
			         return emailStore;
			         
			     }else{
					 Log.d("FacebookExtractProfile.getEmail",
							 "No Email Address");
					 messageString ="No EmailAddress";
		   
			        	return null; 
				 }
			}else
		    	return null;
		}
		catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			 Log.d("FacebookExtractProfile.getEmail",
					 "CATCH "+ex.toString());
			 messageString ="FacebookFindAccessToken.getEmailAddress"+
             "Cannot read Facebook EmailAddress"+ex.toString();

			 return null;
		}

	}
	
	@Override
	public String getAffiliation(){
		
		try{
			YLogger Log = YLoggerFactory.getLogger();
	        JSONArray affiliationArray= null;
	 	    Log.d("FacebookExtractProfile.getAffiliation","Affiliation object was"+extractedProfileJSONObject.toString());
	 	  
	 	    if(extractedProfileJSONObject!=null)
	            affiliationArray = extractedProfileJSONObject.optJSONArray("work");
		    else
		    	return null;
	 	    
	      if(affiliationArray==null){
	    	  Log.d("FacebookExtractProfile.getAffiliation","Work is null");
	    	  messageString ="FacebookFindAccessToken.getAffiliation"+
              					"No Affiliation for the USer";
 	          return null;
	      }
	      else{
	    	  String endDate = "";
	    	  String affiliationDetail="";
	    	  
	    	  for(int i =0;i<affiliationArray.length();i++){
	    		  
	    		  endDate = affiliationArray.optJSONObject(i).optString("end_date");
		    	  if(!endDate.equalsIgnoreCase("0000-00"))
	    		   continue;
		    	  
		    	  JSONObject affiliationInfo = affiliationArray.optJSONObject(i);
		    	  if(affiliationInfo.optJSONObject("position")!=null)
		    		  affiliationDetail += 
		    			  affiliationInfo.optJSONObject("position").optString("name");
		    	  
		    	  if(affiliationInfo.optJSONObject("employer")!=null)
		    		  affiliationDetail +="\n"+ 
		    			  affiliationInfo.optJSONObject("employer").optString("name");
		    	 
		    	  if(affiliationInfo.optString("description")!=null)
		    		  affiliationDetail +="\n"+ 
		    			  affiliationInfo.optString("description");

	              break; 
	    	  }
	    	  return affiliationDetail;
	      }
		}catch(Exception ex){
			YLogger Log = YLoggerFactory.getLogger();
			Log.d("FacebookExtractProfile.getAffiliation","CATCH: "+ex.toString());
			messageString ="FacebookExtractProfile.getAffiliation"+
            "Cannot read Facebook Affiliation: "+ex.toString();

			return null;
		}
	}
	
	
	
	@Override
	public ArrayList getFriends(){
		  YLogger Log = YLoggerFactory.getLogger();
	      Log.d("FacebookExtractProfile.getFriends","going to extract Friends ");
	      
	     try{ 
	    	
	    	 if(extractedFriendsJSONObject==null){
	    		 Log.d("FacebookExtractProfile.getFriends","Friends Object was not Stored"); 
	    		 messageString ="FacebookExtractProfile.getFriends"+
	             "Friends Object was not extracted ";

	    		 return null;
	    	 }
	    	 
	    	 JSONArray friendsArray = null;
	    	 if(extractedFriendsJSONObject.optJSONArray("data")!=null)
	    	      friendsArray = extractedFriendsJSONObject.optJSONArray("data");
     	   
	    	 if(friendsArray==null){
	    		 Log.d("FacebookExtractProfile.getFriends","No friends in JSON Data Obtained");
	    		 messageString ="FacebookExtractProfile.getFriends"+
	             "Friends Object was not extracted ";

	    		 return null;
	    	 }else{
		      
	    		 ArrayList<MSEPersonInformationStore> friendsArrayList = 
	    			 					new ArrayList<MSEPersonInformationStore>();
	    		
	    		 try{
	    			 Log.d("FacebookExtractProfile.getFriends",
	    					 "Extracting friend from JSON Data Obtained");
	 	    		
	    			 for(int i=0;i<friendsArray.length();i++){
	    				 MSEPersonInformationStore msePerson = 
	    					 new MSEPersonInformationStore();
	    				 msePerson.fullName=friendsArray.optJSONObject(i).optString("name");
	    				 msePerson.websiteURL="http://www.facebook.com/profile.php?id=" +
			    	  						friendsArray.optJSONObject(i).optString("id");
	    				 msePerson.emailStore=null;
	    				 msePerson.firstName=msePerson.fullName;
	    				 msePerson.lastName="";
	    				 friendsArrayList.add(msePerson);		          
			          
	    			 
	    			 }
	    		 
	    			 Log.d("FacebookExtractProfile.getFriends",
	    					 "returning Friends ArrayList");
	 	    		
	    			 return friendsArrayList;
	    			 
			      
	    		 }catch(Exception ex){
	    			 Log.d("FacebookExtractProfile.getFriends",
	    					 "CATCH: Cannot extract friends from JSONData "+ex.toString());
	    			 messageString ="FacebookExtractProfile.getFriends"+
		             "Cannot extract friends from JSON DATA ";

	    			 return null;  	    		
	    		 }
	    	 }
	     }catch(Exception ex){
			 Log.d("FacebookExtractProfile.getFriends",
					 "CATCH: Cannot extract friendsArray from JSONData "+ex.toString());
			 messageString ="FacebookExtractProfile.getFriends"+
             "Cannot extract friends from JSON DATA ";
			 
             return null;  	    		
	 
	     }
	}
	

}

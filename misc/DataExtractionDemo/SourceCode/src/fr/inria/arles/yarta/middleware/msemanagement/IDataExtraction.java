/**
* <p>This package contains classes that extract information from
 * various social data source.
 * </p>
 * <p>The classes of this package forms the functional unit of Data
 * Extraction Manager</p>
 
 *  
 *
 */
package fr.inria.arles.yarta.middleware.msemanagement;


import java.util.ArrayList;



/**
 *    
 *  List of common methods required
 *   for information retrieval from a profile of user.
 *   It is implemented by the class that extracts data from different 
 *   social networking platforms.
 *   
 *  
 *  @author Kumar Nishant 
 */
public interface IDataExtraction {
		
    /**
     * Extract the first name of the profile owner
     * from the results obtained after the connection of API.
     * 
     * 
     * 
     * @return The First name of the profile owner
     * 
     */
	public String getFirstName();
	
	
	/**
     * Extract the last name of the profile owner
     * from the results obtained after the connection to API.
     * 
     * 
     *
     * @return The last name of the profile owner
     * 
     */
	public String getLastName();
	
	
	/**
     * Extract the full name of the profile owner
     * from the results obtained after the connection to API.
     * 
     * 
     * 
     * @return The full name of the profile owner
     * 
     */
	public String getFullName();
	
	
	/**
     * Extract the email address of the profile 
     * owner from the results obtained after the connection to API.
     *  
     *
     * @return The Email Address of the profile owner
     * 
     */
	public EmailStore[] getEmail();
	
	
	/**
     * Extract the Current Status of the profile 
     * owner from the results obtained after the connection to API.
     *  
     * 
     * @return The string containing the current status
     *         of the profile owner
     */
	public String getAffiliation();
	

	/**
     * Extract the full-name of the individuals 
     * connected with the profile owner from the results obtained after 
     * the connection of API.
     *        
     * 
     * @return The arraylist that stores the  information about the friends 
     *         of the profile owner
     * 
     */
	public String getWebsiteUrl();

	
	
	/**
     * Extract the full-name of the individuals 
     * connected with the profile owner from the results obtained after 
     * the connection of API.
     *        
     * 
     * @return The arrayList that stores the  information about the friends 
     *         of the profile owner
     * 
     */
	public ArrayList getFriends();

	
	
	
}

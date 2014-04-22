/**
* <p>This package contains classes that extract information from
 * various social data source.
 * </p>
 * <p>The classes of this package forms the functional unit of Data
 * Extraction Manager</p>
 
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement;

import java.util.ArrayList;

/**
 * Store the information extracted from 
 * the LinkedIn profile
 * 
 * @author Kumar Nishant
 *
 */
public class LinkedInInformationStore extends MSEPersonInformationStore{
	
	

	public String totalConnection;
	public ArrayList<LinkedInInformationStore> friends;
	
	public LinkedInInformationStore(){
		
		
		totalConnection=null;
	    friends = new ArrayList<LinkedInInformationStore>();
	}

}

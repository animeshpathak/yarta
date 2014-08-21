package fr.inria.arles.giveaway.resources;

import java.util.Set;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.giveaway.msemanagement.MSEManagerEx;

/**
 * 
 * Announcement interface definition.
 *
 */
public interface Announcement extends Resource, Content {
	public static final String typeURI = MSEManagerEx.baseMSEURI + "#Announcement";

	/** the URI for property title */
	public static final String PROPERTY_TITLE_URI = baseMSEURI + "#title";

	/** the URI for property time */
	public static final String PROPERTY_TIME_URI = MSEManagerEx.baseMSEURI + "#time";

	/** the URI for property price */
	public static final String PROPERTY_PRICE_URI = MSEManagerEx.baseMSEURI + "#price";

	/** the URI for property source */
	public static final String PROPERTY_SOURCE_URI = baseMSEURI + "#source";

	/** the URI for property description */
	public static final String PROPERTY_DESCRIPTION_URI = MSEManagerEx.baseMSEURI + "#description";

	/** the URI for property format */
	public static final String PROPERTY_FORMAT_URI = baseMSEURI + "#format";

	/** the URI for property identifier */
	public static final String PROPERTY_IDENTIFIER_URI = baseMSEURI + "#identifier";

	/** the URI for property isTagged */
	public static final String PROPERTY_ISTAGGED_URI = baseMSEURI + "#isTagged";

	/** the URI for property picture */
	public static final String PROPERTY_PICTURE_URI = MSEManagerEx.baseMSEURI + "#picture";

	/** the URI for property category */
	public static final String PROPERTY_CATEGORY_URI = MSEManagerEx.baseMSEURI + "#category";

	/** the URI for property hiddenBy */
	public static final String PROPERTY_HIDDENBY_URI = MSEManagerEx.baseMSEURI + "#hiddenBy";

	/**
	 * @return the time
	 */
	public Long getTime();

	/**
	 * @param	time
	 * 			the time to set
	 */
	public void setTime(Long time);

	/**
	 * @return the price
	 */
	public Float getPrice();

	/**
	 * @param	price
	 * 			the price to set
	 */
	public void setPrice(Float price);

	/**
	 * @return the description
	 */
	public String getDescription();

	/**
	 * @param	description
	 * 			the description to set
	 */
	public void setDescription(String description);

	/**
	 * Creates a "picture" edge between this announcement and picture
	 * 
	 * @param	picture
	 *			the Picture
	 *
	 * @return true if all went well, false otherwise
	 */
	public boolean addPicture(Picture picture);
	
	/**
	 * deletes the "picture" link between this announcement and picture
	 * 
	 * @param	picture
	 * 			the Picture
	 * @return true if success. false is something went wrong
	 */
	public boolean deletePicture(Picture picture);
	
	/**
	 * 
	 * @return	The list of resources linked by a "picture" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	public Set<Picture> getPicture();

	/**
	 * Creates a "category" edge between this announcement and category
	 * 
	 * @param	category
	 *			the Category
	 *
	 * @return true if all went well, false otherwise
	 */
	public boolean addCategory(Category category);
	
	/**
	 * deletes the "category" link between this announcement and category
	 * 
	 * @param	category
	 * 			the Category
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteCategory(Category category);
	
	/**
	 * 
	 * @return	The list of resources linked by a "category" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	public Set<Category> getCategory();

	/**
	 * Creates a "hiddenby" edge between this announcement and person
	 * 
	 * @param	person
	 *			the Person
	 *
	 * @return true if all went well, false otherwise
	 */
	public boolean addHiddenBy(Person person);
	
	/**
	 * deletes the "hiddenby" link between this announcement and person
	 * 
	 * @param	person
	 * 			the Person
	 * @return true if success. false is something went wrong
	 */
	public boolean deleteHiddenBy(Person person);
	
	/**
	 * 
	 * @return	The list of resources linked by a "hiddenby" edge with the current resource.
	 *			Empty list if I know no one. null if there was an error
	 *
	 */
	public Set<Person> getHiddenBy();
}
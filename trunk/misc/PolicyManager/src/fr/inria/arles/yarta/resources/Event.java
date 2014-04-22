/**
 * 
 */
package fr.inria.arles.yarta.resources;

/**
 * <p>The notion of event is twofold. From a theoretical perspective, events represent 
 * 4D occurrences, i.e., occurrences that happen both in space and time.
 * Differently from 3D occurrences (e.g., Person) events exist in time and do not change, 
 * at least as a whole, over time. From a social application perspective, events represent 
 * entities happening in time having some kind of social meaning/impact. Social events, 
 * such as meetings, parties or talks, are common examples of events, although they do not 
 * cover all the expressivity of this class. For example, a location-based social application 
 * might model the entering of a person in a room as an Event. Specialized subclasses 
 * are therefore needed for each application.
 * @author pathak
 *
 */
public class Event extends Resource{
	private static String baseMSEURI = 
		"http://yarta.gforge.inria.fr/ontologies/mse.rdf";

	/** The unique "type" URI*/
	public static final String typeURI = 
		"http://yarta.gforge.inria.fr/ontologies/mse.rdf#Event"; 
	
	/** the URI for the property description */
	public static final String PROPERTY_DESCRIPTION_URI =
		baseMSEURI + "#description";

	/** Textual description of an event. Not sure we need it. */
	private String description;

	/** the URI for the property title */
	public static final String PROPERTY_TITLE_URI =
		baseMSEURI + "#title";

	/**
	 * Can be mapped on Dublin Core Metadata Element Set 1.1 title.
	 * See Also: http://dublincore.org/documents/dces/
	 */
	private String title;

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}

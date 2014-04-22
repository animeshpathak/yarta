/**
 * 
 */
package fr.inria.arles.yarta.resources;

/**
 * A generic class representing all possible types of entities carrying some information.
 * @author pathak
 *
 */
public class Content extends Resource {
	
	private static String baseMSEURI = 
		"http://yarta.gforge.inria.fr/ontologies/mse.rdf";

	
	/** The unique "type" URI*/
	public static final String typeURI = 
		baseMSEURI + "#Content"; 

	/** the URI for the property format */
	public static final String PROPERTY_FORMAT_URI =
		baseMSEURI + "#format";
	/** Can be mapped on Dublin Core Metadata Set 1.1 format */
	private String format;
	
	/** the URI for the property identifier */
	public static final String PROPERTY_IDENTIFIER_URI =
		baseMSEURI + "#identifier";
	/** Can be mapped on DC identifier */
	private String identifier;
	
	/** the URI for the property source */
	public static final String PROPERTY_SOURCE_URI =
		baseMSEURI + "#source";

	/**
	 * Can be mapped onto Dublin Core (element set 1.1) source.
	 * See also: http://dublincore.org/documents/dces/
	 */
	private String source;
	
	/** the URI for the property title */
	public static final String PROPERTY_TITLE_URI =
		baseMSEURI + "#title";

	/**
	 * Can be mapped on Dublin Core Metadata Element Set 1.1 title.
	 * See Also: http://dublincore.org/documents/dces/
	 */
	private String title;

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
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

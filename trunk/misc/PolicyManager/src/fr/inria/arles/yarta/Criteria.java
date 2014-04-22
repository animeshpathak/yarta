/**
 * 
 */
package fr.inria.arles.yarta;

/**
 * This defines criteria used to select users etc.
 * @author pathak
 */
public class Criteria {

	/** The language in which it is being expressed */
	private String lang;
	
	/** The representation of the condition, in the language */
	private String conditions;

	/**
	 * Constructs a new criterion, parsing the string 
	 * based on the language it is in.
	 * @param language - Default values are "SPARQL" for 
	 * SPARQL queries, "WilburQL" for Wilbur query language
	 * @param conditions
	 */
	public Criteria(String language, String conditions) {
		this.lang = language;
		this.conditions = conditions;
		//TODO Parse and store in an internal format.
	}
	
	@Override 
	/**
	 * returns a string representation of the Criteria
	 */
	public String toString(){
		return (this.lang + "//" + this.conditions);
	}

	public String getConditions() {
		return conditions;
	}

	public String getLang() {
		return lang;
	}
}

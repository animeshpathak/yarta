package fr.inria.arles.yarta.parser;

/**
 * Property class definition.
 */
public class YartaProperty {

	/**
	 * The one & only constructor;
	 * 
	 * @param name
	 *            the property name
	 * @param comment
	 *            the property comments
	 * @param range
	 *            the property range class
	 * @param isNew
	 *            specifies if this property is new or inherited from base
	 *            ontology
	 * @param isSpecific
	 *            specifies if this property is specific to the current class
	 */
	public YartaProperty(String name, String comment, String range,
			String domain, boolean isNew, boolean isSpecific) {
		this.name = name;
		this.comment = comment;
		this.range = range;
		this.domain = domain;
		this.isNew = isNew;
		this.isSpecific = isSpecific;
	}

	/**
	 * Used in order not to have duplicates properties attached to a class.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof YartaProperty) {
			YartaProperty other = (YartaProperty) obj;
			return other.name.equals(name);
		}
		return false;
	}

	/**
	 * Used for equals.
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	/**
	 * Sets the new flag.
	 * 
	 * @param isNew
	 */
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	/**
	 * Gets the new flag.
	 * 
	 * @return boolean
	 */
	public boolean isNew() {
		return isNew;
	}

	/**
	 * Sets the specific flag.
	 * 
	 * @param isSpecific
	 */
	public void setSpecific(boolean isSpecific) {
		this.isSpecific = isSpecific;
	}

	/**
	 * Gets the specific flag.
	 * 
	 * @return boolean
	 */
	public boolean isSpecific() {
		return isSpecific;
	}

	/**
	 * Sets the comment.
	 * 
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Gets the comment.
	 * 
	 * @return String
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Sets the range for this property.
	 * 
	 * @param range
	 */
	public void setRange(String range) {
		this.range = range;
	}

	/**
	 * Gets the range for this propery.
	 * 
	 * @return String
	 */
	public String getRange() {
		return range;
	}

	/**
	 * Gets the range's short name.
	 * 
	 * @return String
	 */
	public String getRangeShortName() {
		return getRange().replace(Parser.YartaResourcePackage, "");
	}

	/**
	 * Gets the range in upper case (first letter upper case)
	 * 
	 * @return String
	 */
	public String getRangeU() {
		String range = getRange();

		if (range.contains(Parser.YartaResourcePackage)) {
			return range;
		}

		if (range.equals("int")) {
			return "Integer";
		}

		return Character.toUpperCase(range.charAt(0)) + range.substring(1);
	}

	/**
	 * Gets the range in lower case.
	 * 
	 * @return String
	 */
	public String getRangeL() {
		String range = getRange();
		if (range.contains(Parser.YartaResourcePackage)) {
			range = range.replace(Parser.YartaResourcePackage, "");
		}
		return range.toLowerCase();
	}

	/**
	 * Sets the domain for this property.
	 * 
	 * @param domain
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * Gets the domain for this property.
	 * 
	 * @return String
	 */
	public String getDomain() {
		if (domain == null) {
			return "Resource";
		}
		return domain;
	}

	/**
	 * Gets the domain's short name without the package name.
	 * 
	 * @return String
	 */
	public String getDomainShortName() {
		return getDomain().replace(Parser.YartaResourcePackage, "");
	}

	/**
	 * Gets the domain's with the first letter uppercase.
	 * 
	 * @return String
	 */
	public String getDomainU() {
		String domain = getDomain();
		if (domain.contains(Parser.YartaResourcePackage)) {
			domain = domain.replace(Parser.YartaResourcePackage, "");
		}
		return Character.toUpperCase(domain.charAt(0)) + domain.substring(1);
	}

	/**
	 * Gets the domain in lower case.
	 * 
	 * @return String
	 */
	public String getDomainL() {
		String domain = getDomain();
		if (domain.contains(Parser.YartaResourcePackage)) {
			domain = domain.replace(Parser.YartaResourcePackage, "");
		}
		return domain.toLowerCase();
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the name (e.g. string, Place, etc.).
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the name of the property with first letter upper case (e.g. String,
	 * Place, etc.)
	 */
	public String getNameU() {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	/**
	 * Gets the name of the property in lower case.
	 * 
	 * @return String
	 */
	public String getNameL() {
		return name.toLowerCase();
	}

	private String name;
	private String comment;
	private String range;
	private String domain;
	private boolean isNew;
	private boolean isSpecific;
}

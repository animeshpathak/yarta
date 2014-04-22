package fr.inria.arles.yarta.parser;

import java.util.HashSet;
import java.util.Set;

/**
 * Class object passed between {@link Parser} and Velocity templates.
 */
public class YartaClass {

	/**
	 * Name & Comment based constructor.
	 * 
	 * @param name
	 *            String class name
	 */
	public YartaClass(String name) {
		this.name = name;
	}

	/**
	 * Used for removing any duplicate classes.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof YartaClass) {
			YartaClass other = (YartaClass) obj;
			return other.name.equals(name);
		}
		return false;
	}

	/**
	 * Used in equals to remove duplicate classes.
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	/**
	 * Adds a super class to the super classes list.
	 * 
	 * @param superClass
	 *            the super class as a string
	 */
	public void addSuperClass(String superClass) {
		superClasses.add(superClass);
	}

	/**
	 * Checks if this class is derived from a certain class.
	 * 
	 * @param baseClass
	 *            the base class
	 * 
	 * @return is or not derived from baseClass
	 */
	public boolean isDerivedFrom(String baseClass) {
		return superClasses.contains(baseClass) || baseClass.equals(name);
	}

	/**
	 * Returns a list of super classes.
	 * 
	 * @return Set<String>
	 */
	public Set<String> getSuperClasses() {
		return superClasses;
	}

	/**
	 * Checks if this class has new properties attached (newer than pre-defined
	 * ones in the base onthology).
	 * 
	 * @return boolean
	 */
	public boolean hasNewProperties() {
		for (YartaProperty prop : dataProperties) {
			if (prop.isNew()) {
				return true;
			}
		}
		return hasNewObjectProperties();
	}

	/**
	 * Checks if this class has new object properties attached (newer than
	 * pre-defined ones in the base onthology).
	 * 
	 * @return boolean
	 */
	public boolean hasNewObjectProperties() {
		for (YartaProperty prop : objectProperties) {
			if (prop.isNew()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the name of the class.
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Checks if this class is a new defined class or is an extension of an
	 * existing class (e.g. Event extends Event just because it has a new
	 * property).
	 * 
	 * @return boolean
	 */
	public boolean isNew() {
		return isNew;
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
	 * Sets the package name for this class.
	 * 
	 * @param packageName
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * Gets the package name for this class.
	 * 
	 * @return String
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Gets the import list for this class's interface.
	 * 
	 * @return Set<String>
	 */
	public Set<String> getInterfaceImportList() {
		return interfaceImports;
	}

	/**
	 * Gets the import list for this class's implementation.
	 * 
	 * @return Set<String>
	 */
	public Set<String> getImplementationImportList() {
		return implementationImports;
	}

	/**
	 * Adds an entry to the interface import list.
	 * 
	 * @param importPath
	 */
	public void addInterfaceImport(String importPath) {
		interfaceImports.add(importPath);
	}

	/**
	 * Adds an entry to the implementation import list.
	 * 
	 * @param importPath
	 */
	public void addImplementationImport(String importPath) {
		implementationImports.add(importPath);
	}

	/**
	 * Returns a list of this class's object properties.
	 * 
	 * @return Set<YartaProperty>
	 */
	public Set<YartaProperty> getObjectProperties() {
		return objectProperties;
	}

	/**
	 * Returns a list of this class's data properties.
	 * 
	 * @return Set<YartaProperty>
	 */
	public Set<YartaProperty> getDataProperties() {
		return dataProperties;
	}

	/**
	 * Returns a list of this class's inverse properties.
	 * 
	 * @return Set<YartaProperty>
	 */
	public Set<YartaProperty> getInverseProperties() {
		return inverseProperties;
	}

	private String name;
	private String packageName;
	private boolean isNew = true;

	private Set<YartaProperty> dataProperties = new HashSet<YartaProperty>();
	private Set<YartaProperty> objectProperties = new HashSet<YartaProperty>();
	private Set<YartaProperty> inverseProperties = new HashSet<YartaProperty>();

	private Set<String> superClasses = new HashSet<String>();

	private Set<String> implementationImports = new HashSet<String>();
	private Set<String> interfaceImports = new HashSet<String>();
}

package fr.inria.arles.yarta.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import fr.inria.arles.yarta.Main;

/**
 * The Parser class. Parses the RDF & generates the java files necessarily to
 * build new Yarta applications.
 */
public class Parser implements OntDocumentManager.ReadFailureHandler {

	/**
	 * Yarta resource package.
	 */
	public final static String YartaResourcePackage = "fr.inria.arles.yarta.resources.";

	/**
	 * Returns the instance of the parser.
	 * 
	 * @return Parser
	 */
	public static Parser getInstance() {
		return instance;
	}

	/**
	 * Returns the last error encountered.
	 * 
	 * @return String
	 */
	public String getError() {
		return error;
	}

	/**
	 * Sets the namespace for the generated java classes.
	 * 
	 * @param namespace
	 */
	public void setNamespace(String namespace) {
		baseNamespace = namespace;
	}

	/**
	 * Parsers the RDF file.
	 * 
	 * @param rdfFile
	 * @return
	 */
	public boolean parseRDF(String rdfFile) {
		try {
			internalParseRDF(rdfFile);
		} catch (Exception ex) {
			error = ex.getMessage();
			return false;
		}
		return true;
	}

	/**
	 * Generates the Java code.
	 * 
	 * @param outFolder
	 * @param packageName
	 * @return boolean
	 */
	public boolean generateJavaCode(String outFolder, String packageName) {
		Main.printInfo("Generating Java code in " + outFolder);
		try {
			Velocity.init();
			generateResources(outFolder, packageName);
			generateMSELibrary(outFolder, packageName);
		} catch (Exception ex) {
			error = ex.getMessage();
			return false;
		}

		return true;
	}

	/**
	 * Generates resource files.
	 * 
	 * @param outFolder
	 * @param packageName
	 * @throws Exception
	 */
	private void generateResources(String outFolder, String packageName)
			throws Exception {
		String folderPath = String.format("%s/%s/%s/", outFolder,
				packageName.replace(".", "/"), resourcePackage);
		new File(folderPath).mkdirs();

		resolveImports(packageName);

		Main.printInfo("Generating resource files");

		for (YartaClass yartaClass : classes) {
			VelocityContext context = new VelocityContext();

			yartaClass.setPackageName(packageName + ".resources");

			context.put("YartaResourcePackage", YartaResourcePackage);
			context.put("Class", yartaClass);
			context.put("MSEManagementClass", mseManagementClass);

			generateInterface(yartaClass, context, folderPath);
			generateImplementation(yartaClass, context, folderPath);
		}
	}

	/**
	 * Generates the interface file for a given YartaClass.
	 * 
	 * @param yartaClass
	 * @param context
	 * @param folderPath
	 * @throws Exception
	 */
	private void generateInterface(YartaClass yartaClass,
			VelocityContext context, String folderPath) throws Exception {
		Template template = Velocity.getTemplate("interface.vm");
		String outFile = String.format("%s%s.java", folderPath,
				yartaClass.getName());

		Main.printInfo("Generating interface file: " + outFile);

		Writer writer = new BufferedWriter(new FileWriter(outFile));
		template.merge(context, writer);
		writer.close();
	}

	/**
	 * Generates the implementation file for a given YartaClass.
	 * 
	 * @param yartaClass
	 * @param context
	 * @param folderPath
	 * @throws Exception
	 */
	private void generateImplementation(YartaClass yartaClass,
			VelocityContext context, String folderPath) throws Exception {
		Template template = Velocity.getTemplate("implementation.vm");
		String outFile = String.format("%s%sImpl.java", folderPath,
				yartaClass.getName());

		Main.printInfo("Generating implementation file: " + outFile);

		Writer writer = new BufferedWriter(new FileWriter(outFile));
		template.merge(context, writer);
		writer.close();
	}

	/**
	 * Generates the MSE library files.
	 * 
	 * @param outFolder
	 * @param packageName
	 * @throws Exception
	 */
	private void generateMSELibrary(String outFolder, String packageName)
			throws Exception {
		String folderPath = String.format("%s/%s/%s/", outFolder,
				packageName.replace(".", "/"), managementPackage);
		new File(folderPath).mkdirs();

		Main.printInfo("Generating custom MSE library");

		generateMSEManager(packageName, folderPath);
		generateFactory(packageName, folderPath);
	}

	/**
	 * Generates the storage access manager file.
	 * 
	 * @param packageName
	 * @param folderPath
	 * @throws Exception
	 */
	private void generateFactory(String packageName, String folderPath)
			throws Exception {
		Template template = Velocity.getTemplate("factory.vm");
		VelocityContext context = new VelocityContext();

		String outFile = folderPath + "/" + storageAcMgrClass + ".java";

		Main.printInfo("Generating custom MSE factory: " + outFile);

		context.put("Classes", classes);
		context.put("PackageName", packageName + "." + managementPackage);
		context.put("StorageAcMgrClass", storageAcMgrClass);
		context.put("YartaResourcePackage", YartaResourcePackage);
		context.put("ResourcesPackage", packageName + "." + resourcePackage);

		List<String> lstImports = new ArrayList<String>();
		lstImports.add("java.util.Set");
		lstImports
				.add("fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager");

		for (YartaClass yartaClass : classes) {
			lstImports.add(packageName + "." + resourcePackage + "."
					+ yartaClass.getName() + "Impl");
			if (!yartaClass.isNew()) {
				lstImports.add(YartaResourcePackage + yartaClass.getName());
			} else {
				lstImports.add(packageName + "." + resourcePackage + "."
						+ yartaClass.getName());
			}
		}

		context.put("ImportList", lstImports);

		Writer writer = new BufferedWriter(new FileWriter(outFile));
		template.merge(context, writer);
		writer.close();
	}

	/**
	 * Generates the mse manager file.
	 * 
	 * @param packageName
	 * @param folderPath
	 * @throws Exception
	 */
	private void generateMSEManager(String packageName, String folderPath)
			throws Exception {
		Template template = Velocity.getTemplate("manager.vm");

		String outFile = folderPath + "/" + mseManagementClass + ".java";

		Main.printInfo("Generating custom MSE manager: " + outFile);

		VelocityContext context = new VelocityContext();
		context.put("MSEManagementClass", mseManagementClass);
		context.put("StorageAcMgrClass", storageAcMgrClass);
		context.put("PackageName", packageName + "." + managementPackage);
		context.put("CustomURI", baseNamespace);

		Writer writer = new BufferedWriter(new FileWriter(outFile));
		template.merge(context, writer);
		writer.close();
	}

	/**
	 * Resolve imports for all YartaClasses.
	 * 
	 * @param packageName
	 */
	private void resolveImports(String packageName) {
		Main.printInfo("Resolving imports for resources in package: "
				+ packageName);

		for (YartaClass yartaClass : classes) {
			boolean bHasNewObjectSpecificProps = false;

			// its base class is not included in this package (its from Yarta)
			for (String superClass : yartaClass.getSuperClasses()) {
				if (!containsClass(superClass)) {
					yartaClass.addInterfaceImport(YartaResourcePackage
							+ superClass);
				}
			}

			for (YartaProperty prop : yartaClass.getObjectProperties()) {
				if (prop.isNew() && prop.isSpecific()) {
					bHasNewObjectSpecificProps = true;

					if (!containsClass(prop.getRangeShortName())) {
						yartaClass.addInterfaceImport(YartaResourcePackage
								+ prop.getRangeShortName());
					}
				}
				if (!containsClass(prop.getRangeShortName())) {
					yartaClass.addImplementationImport(YartaResourcePackage
							+ prop.getRangeShortName());
				}
			}

			// solve imports for domain inverse functions
			for (YartaProperty prop : yartaClass.getInverseProperties()) {
				Main.printInfo("%s[%s] : %s %s", yartaClass.getName(),
						prop.getName(), prop.getDomain(), prop.getRange());

				if (prop.isNew() && prop.isSpecific()) {
					bHasNewObjectSpecificProps = true;

					if (!containsClass(prop.getDomainShortName())) {
						yartaClass.addInterfaceImport(YartaResourcePackage
								+ prop.getDomainShortName());
					}
				}

				if (!containsClass(prop.getDomainShortName())) {
					yartaClass.addImplementationImport(YartaResourcePackage
							+ prop.getDomainShortName());
				}
			}

			yartaClass.addInterfaceImport(String.format("%s.%s.%s",
					packageName, managementPackage, mseManagementClass));

			if (bHasNewObjectSpecificProps) {
				yartaClass.addInterfaceImport("java.util.Set");
			}

			if (yartaClass.getObjectProperties().size() > 0) {
				yartaClass.addImplementationImport("java.util.Set");
			}

			yartaClass
					.addImplementationImport("fr.inria.arles.yarta.knowledgebase.interfaces.Node");
			yartaClass
					.addImplementationImport("fr.inria.arles.yarta.middleware.msemanagement.ThinStorageAccessManager");
			yartaClass
					.addImplementationImport("fr.inria.arles.yarta.resources.YartaResource");
		}
	}

	/**
	 * error handling for model read.
	 */
	@Override
	public void handleFailedRead(String url, Model model, Exception e) {
		error = e.getMessage() + "[" + url + "]";
	}

	/**
	 * Parses the rdf file collecting all the information necessarily for java
	 * classes generation.
	 * 
	 * @param rdfFile
	 * @throws Exception
	 */
	private void internalParseRDF(String rdfFile) throws Exception {
		Main.printInfo("Parsing rdf file: " + rdfFile);

		OntModel model = ModelFactory.createOntologyModel();

		model.getDocumentManager().setReadFailureHandler(this);

		// read base rdf
		FileManager.get().readModel(model, "res/mse-1.2.rdf");
		// read custom rdf
		FileManager.get().readModel(model, rdfFile);

		if (error != null) {
			throw new Exception(error);
		}

		// list new classes to be added
		ExtendedIterator<OntClass> itClass = model.listClasses();

		while (itClass.hasNext()) {
			OntClass ontClass = itClass.next();
			if (ontClass.getNameSpace() == null)
				continue;
			if (!ontClass.getNameSpace().equals(baseMSENameSpace)
					&& !ontClass.getNameSpace().contains(baseW3CNameSpace)) {

				checkReservedClassName(ontClass.getLocalName());

				YartaClass yartaClass = new YartaClass(ontClass.getLocalName());
				fillUpSuperClasses(yartaClass, ontClass);
				classes.add(yartaClass);
			}
		}

		// list new props to be added (and add those classes)
		ExtendedIterator<OntProperty> itProps = model.listAllOntProperties();

		while (itProps.hasNext()) {
			OntProperty ontProp = itProps.next();

			if (!ontProp.getNameSpace().equals(baseMSENameSpace)
					&& !ontProp.getNameSpace().contains(baseW3CNameSpace)) {

				OntResource domain = ontProp.getDomain();
				if (domain.isClass()) {
					OntClass ontClass = domain.asClass();

					if (ontClass.isUnionClass()) {
						UnionClass domainUnionClass = ontClass.asUnionClass();
						ExtendedIterator<? extends OntClass> unionDomains = domainUnionClass
								.listOperands();

						while (unionDomains.hasNext()) {
							OntClass ontUnionClass = unionDomains.next();
							YartaClass yartaClass = new YartaClass(
									ontUnionClass.getLocalName());
							yartaClass.addImplementationImport(ontUnionClass
									.getLocalName());
							yartaClass.setNew(false);
							fillUpSuperClasses(yartaClass, ontUnionClass);
							classes.add(yartaClass);
						}
					} else {
						YartaClass yartaClass = new YartaClass(
								ontClass.getLocalName());
						yartaClass.addSuperClass(ontClass.getLocalName());
						yartaClass.setNew(false);
						fillUpSuperClasses(yartaClass, ontClass);
						classes.add(yartaClass);
					}
				}
			}
		}

		// add props to classes
		itProps = model.listAllOntProperties();

		while (itProps.hasNext()) {
			OntProperty ontProp = itProps.next();

			boolean isNewProp = !(ontProp.getNameSpace().equals(
					baseMSENameSpace) || ontProp.getNameSpace().equals(
					baseW3CNameSpace));

			String propertyName = ontProp.getLocalName();
			String propertyComment = ontProp.getComment("en");

			if (ontProp.getRange() == null) {
				Main.printWarning("Range is null for " + ontProp.getLocalName());
				continue;
			}
			String propertyRange = ontProp.getRange().getLocalName();

			OntResource ontDomain = ontProp.getDomain();

			try {
				OntClass domainClass = ontDomain.asClass();
				boolean bUnion = domainClass.isUnionClass();
				if (bUnion) {
					UnionClass domainUnionClass = domainClass.asUnionClass();
					ExtendedIterator<? extends OntClass> unionDomains = domainUnionClass
							.listOperands();
					while (unionDomains.hasNext()) {
						OntClass domain = unionDomains.next();

						List<YartaClass> lstClasses = getClasses(domain
								.getLocalName());

						for (YartaClass yartaClass : lstClasses) {
							boolean isSpecific = domain.getLocalName().equals(
									yartaClass.getName());

							if (ontProp.isDatatypeProperty()) {
								yartaClass.getDataProperties().add(
										new YartaProperty(propertyName,
												propertyComment, propertyRange,
												domain.getLocalName(),
												isNewProp, isSpecific));
							} else {
								safeProcessProperty(
										yartaClass.getObjectProperties(),
										propertyName, propertyComment,
										propertyRange, domain.getLocalName(),
										isNewProp, isSpecific);
							}
						}
					}
				} else {
					List<YartaClass> lstClasses = getClasses(domainClass
							.getLocalName());

					for (YartaClass yartaClass : lstClasses) {

						boolean isSpecific = domainClass.getLocalName().equals(
								yartaClass.getName());

						if (ontProp.isDatatypeProperty()) {
							yartaClass.getDataProperties().add(
									new YartaProperty(propertyName,
											propertyComment, propertyRange,
											domainClass.getLocalName(),
											isNewProp, isSpecific));
						} else {
							safeProcessProperty(
									yartaClass.getObjectProperties(),
									propertyName, propertyComment,
									propertyRange, domainClass.getLocalName(),
									isNewProp, isSpecific);
						}
					}
				}

				// for base class -> specific
				// for rest -> !specific
				// add inverse properties to actual classes
				YartaClass baseRangeClass = getClass(propertyRange);

				if (baseRangeClass != null) {
					safeProcessProperty(baseRangeClass.getInverseProperties(),
							propertyName, propertyComment, propertyRange,
							domainClass.getLocalName(), isNewProp, true);
				}

				List<YartaClass> rangeClasses = getClasses(propertyRange);
				for (YartaClass rangeClass : rangeClasses) {
					if (!rangeClass.equals(baseRangeClass)) {
						safeProcessProperty(rangeClass.getInverseProperties(),
								propertyName, propertyComment, propertyRange,
								domainClass.getLocalName(), isNewProp, false);
					}
				}
			} catch (Exception ex) {
				Main.printWarning("Null domain for " + ontProp.getLocalName());
			}
		}
	}

	/**
	 * Safely adds a property to a list of properties checking if it needs to
	 * have package imports added to range, romain, etc.
	 * 
	 * @param lstProps
	 * @param propertyName
	 * @param propertyComment
	 * @param propertyRange
	 * @param propertyDomain
	 * @param isNewProp
	 * @param isSpecific
	 */
	private void safeProcessProperty(Set<YartaProperty> lstProps,
			String propertyName, String propertyComment, String propertyRange,
			String propertyDomain, boolean isNewProp, boolean isSpecific) {
		String domain = propertyDomain;
		if (!isNewProp && containsClass(domain)) {
			domain = YartaResourcePackage + domain;
		}
		String range = propertyRange;
		if (!isNewProp && containsClass(range)) {
			range = YartaResourcePackage + range;
		}

		YartaProperty property = new YartaProperty(propertyName,
				propertyComment, range, domain, isNewProp, isSpecific);

		Main.printInfo("safeProcessPropery[%s]", propertyName);
		Main.printInfo("[%s, %s]", range, domain);

		lstProps.add(property);
	}

	/**
	 * Given a YartaClass & its OntClass adds all its super classes from
	 * OntClass to YartaClass.
	 * 
	 * @param yartaClass
	 * @param ontClass
	 * 
	 * @throws Exception
	 */
	private void fillUpSuperClasses(YartaClass yartaClass, OntClass ontClass)
			throws Exception {
		ExtendedIterator<OntClass> it = ontClass.listSuperClasses();

		if (!it.hasNext()) {
			throw new Exception("Class " + yartaClass.getName()
					+ " does not have an implicit super class!");
		}

		while (it.hasNext()) {
			OntClass ontSuperClass = it.next();
			yartaClass.addSuperClass(ontSuperClass.getLocalName());
		}
	}

	/**
	 * Gets the YartaClass for the specified class name
	 * 
	 * @param baseClass
	 * @return YartaClass
	 */
	private YartaClass getClass(String baseClass) {
		for (YartaClass cls : classes) {
			if (cls.getName().equals(baseClass)) {
				return cls;
			}
		}
		return null;
	}

	/**
	 * Returns a list of classes derived from a given base class
	 * 
	 * @param baseClass
	 *            String
	 * @return List<YartaClass>
	 */
	private List<YartaClass> getClasses(String baseClass) {
		List<YartaClass> lstClasses = new ArrayList<YartaClass>();

		for (YartaClass cls : classes) {
			if (cls.isDerivedFrom(baseClass)) {
				lstClasses.add(cls);
			}
		}

		return lstClasses;
	}

	/**
	 * Checks if a specific class is in the class list.
	 * 
	 * @param className
	 *            String
	 * @return boolean
	 */
	private boolean containsClass(String className) {
		for (YartaClass yartaClass : classes) {
			if (yartaClass.getName().equals(className)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a certain class is reserved & throws an exception if that's the
	 * case.
	 * 
	 * @param className
	 * 
	 * @throws Exception
	 */
	private void checkReservedClassName(String className) throws Exception {
		if (baseClasses.contains(className)) {
			throw new Exception("Class " + className
					+ " is a reserved class name!");
		}
	}

	/**
	 * Private basic constructor for singleton use.
	 */
	private Parser() {
	}

	private final static String resourcePackage = "resources";
	private final static String managementPackage = "msemanagement";
	private final static String mseManagementClass = "MSEManagerEx";
	private final static String storageAcMgrClass = "StorageAccessManagerEx";

	private final static String baseMSENameSpace = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#";
	private final static String baseW3CNameSpace = "http://www.w3.org/";
	private static Parser instance = new Parser();

	private static Set<String> baseClasses = new HashSet<String>();
	static {
		baseClasses.add("Agent");
		baseClasses.add("Group");
		baseClasses.add("Person");
		baseClasses.add("Event");
		baseClasses.add("Content");
		baseClasses.add("Place");
		baseClasses.add("Topic");

		// there are useless anyway IMHO
		baseClasses.add("CompositeEvent");
		baseClasses.add("ParEvent");
		baseClasses.add("SeqEvent");
		baseClasses.add("SingleEvent");
	}

	private Set<YartaClass> classes = new HashSet<YartaClass>();
	private String error;
	private String baseNamespace;
}

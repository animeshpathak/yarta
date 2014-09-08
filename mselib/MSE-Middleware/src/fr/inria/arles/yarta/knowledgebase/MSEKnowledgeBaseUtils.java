package fr.inria.arles.yarta.knowledgebase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;

/**
 * Knowledge base specific utilities functions. Contains methods for
 * sending/receiving knowledge base to a stream.
 */
public class MSEKnowledgeBaseUtils {

	private static YLogger logger = YLoggerFactory.getLogger();

	/**
	 * Imports data from an RDF file to populate the knowledge base
	 * 
	 * @param fileRDF
	 *            - the file containing data
	 * @param kb
	 *            - the knowledge base to be populated
	 * @return - true if everything went fine, false otherwise
	 */
	public static boolean importDataFromRDF(String fileRDF, KnowledgeBase kb) {

		Model model = ((MSEKnowledgeBase) kb).model;

		File f = new File(fileRDF);
		InputStream i = null;

		try {
			i = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			logger.e("MSEKnowledgeBaseUtils.importDataFromRDF",
					"RDF file not found", e);
			return false;
		}

		synchronized (model) {
			if (model.read(i, null, "RDF/XML") == null) {
				logger.w("MSEKnowledgeBase.importDataFromRDF",
						"Could not import data");
				return false;
			}
		}

		return true;
	}

	/**
	 * Import data from an RDF file
	 * 
	 * @param f
	 *            - the input file
	 * @param kb
	 *            - the knowledge base to be populated
	 * @return - true if everything went fine, false otherwise
	 */
	public static boolean importDataFromFile(File f, KnowledgeBase kb) {

		Model model = ((MSEKnowledgeBase) kb).model;

		InputStream i = null;

		try {
			i = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			logger.d("MSEKnowledgeBaseUtils.importDataFromFile",
					"RDF File not found", e);
			return false;
		}

		if (model.read(i, null, "RDF/XML") == null) {
			logger.d("MSEKnowledgeBase.importDataFromFile",
					"Could not import data");
			return false;
		}

		return true;
	}

	/**
	 * Reads an RDF from Internet.
	 * 
	 * @param url
	 * @param kb
	 * @return
	 */
	public static boolean importDataFromURL(String url, KnowledgeBase kb) {
		Model model = ((MSEKnowledgeBase) kb).model;

		try {
			model.read(url);
		} catch (Exception e) {
			logger.d("MSEKnowledgeBaseUtils.importDataFromURL",
					"RDF url not found", e);
			return false;
		}

		return true;
	}

	/**
	 * Import data in the KB from an input stream
	 * 
	 * @param i
	 *            - the input stream
	 * @param kb
	 *            - the knowledge base to be populated
	 * @return - true if everything went fine, false otherwise
	 */
	public static boolean readDataFromStream(InputStream i, KnowledgeBase kb) {

		Model model = ((MSEKnowledgeBase) kb).model;

		if (model.read(i, null, "RDF/XML") == null) {
			logger.d("MSEKnowledgeBase.readDataFromStream",
					"Could not read data from stream");
			return false;
		}

		return true;
	}

	/**
	 * Jena-based implementation
	 * 
	 * @param kb
	 *            the Yarta Knowledge Base to print
	 */
	public static void printMSEKnowledgeBase(MSEKnowledgeBase kb) {

		Model model = kb.model;

		StmtIterator iter = model.listStatements();

		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement(); // get next statement
			Resource subject = stmt.getSubject(); // get the subject
			Property predicate = stmt.getPredicate(); // get the predicate
			RDFNode object = stmt.getObject(); // get the object

			System.out.print(subject.toString());
			System.out.print(" " + predicate.toString() + " ");
			if (object instanceof Resource) {
				System.out.print(object.toString());
			} else {
				// object is a literal
				System.out.print(" \"" + object.toString() + "\"");
			}

			System.out.println(".");
		}
	}

	/**
	 * 
	 * @param kb
	 *            The knowledge base to print
	 * @param output_file
	 *            The output file
	 * @param syntax
	 *            Predefined values are "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE",
	 *            "TURTLE", (and "TTL") and "N3". The default value, represented
	 *            by null, is "RDF/XML
	 */
	public static void printMSEKnowledgeBase(MSEKnowledgeBase kb,
			String output_file, String syntax) {
		try {
			Model model = kb.model;

			synchronized (model) {
				OutputStreamWriter out = null;
				out = new OutputStreamWriter(new FileOutputStream(output_file));
				model.write(out, syntax);
				out.close();
			}
		} catch (Exception e) {
			logger.e("MSEKnowledgeBaseUtils.printMSEKnowledgeBase",
					"Output file not found", e);
		}
	}

	/**
	 * Write data contained in the KB onto an output stream
	 * 
	 * @param o
	 *            - the OutputStream
	 * @param kb
	 *            - the source Knowledge Base
	 * @return - true if everything went fine, false otherwise
	 */
	public static boolean writeDataOnStream(OutputStream o, MSEKnowledgeBase kb) {

		Model model = kb.model;

		if (model.write(o, null) == null) {
			logger.d("MSEKnowledgeBase.writeDataOnStream",
					"Could not write data");
			return false;
		}

		return true;
	}
}

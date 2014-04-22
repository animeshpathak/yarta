/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
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
 * @author alessandra
 *
 */
public class MSEKnowledgeBaseUtils {
	
	private static YLogger logger = YLoggerFactory.getLogger();
	
	/**
	 * Imports data from an RDF file to populate the knowledge base
	 * @param fileRDF the file containing data
	 * @param kb the knowledge base to be populated
	 */
	public static boolean importDataFromRDF (String fileRDF, KnowledgeBase kb)	{
			
			Model model = ((MSEKnowledgeBase)kb).model;
				
			File f = new File(fileRDF);
			InputStream i = null;
			
			try {
				i = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				logger.e("MSEKnowledgeBaseUtils.importDataFromRDF", "RDF file not found", e);
				return false;
			}
			
			if (model.read(i,null,"RDF/XML") == null)	{
				logger.w("MSEKnowledgeBase.importDataFromRDF", "Could not import data");
				return false;
			}
			
			return true;
		}
	/**
	 * Jena-based implementation
	 * @param kb the Yarta Knowledge Base to print
	 */
	public static void printMSEKnowledgeBase(MSEKnowledgeBase kb)	{
		
			Model model = kb.model;

			StmtIterator iter = model.listStatements();
			
			while (iter.hasNext()) {
			    Statement stmt      = iter.nextStatement();  // get next statement
			    Resource  subject   = stmt.getSubject();     // get the subject
			    Property  predicate = stmt.getPredicate();   // get the predicate
			    RDFNode   object    = stmt.getObject();      // get the object

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
	 * @param kb The knowledge base to print
	 * @param output_file The output file
	 * @param syntax Predefined values are "RDF/XML", "RDF/XML-ABBREV", 
	 * "N-TRIPLE", "TURTLE", (and "TTL") and "N3". The default value, represented by null, is "RDF/XML
	 */
	public static void printMSEKnowledgeBase(MSEKnowledgeBase kb, String output_file, String syntax)	{
		
		Model model = kb.model;
		
		OutputStreamWriter out = null;
		
		try {
			out = new OutputStreamWriter(new FileOutputStream(output_file));
		} catch (Exception e) {
			logger.e("MSEKnowledgeBaseUtils.printMSEKnowledgeBase", "Output file not found", e);
		}
		
		model.write(out,syntax);		

	}

}

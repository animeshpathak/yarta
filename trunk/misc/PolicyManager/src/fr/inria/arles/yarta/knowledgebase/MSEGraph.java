/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import fr.inria.arles.yarta.knowledgebase.interfaces.Graph;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;
import fr.inria.arles.yarta.logging.YLogger;

/**
 * @author alessandra
 *
 */
public class MSEGraph implements Graph, Serializable  {

	/** 
	 * This class is a Jena-based implementation of an MSE Graph
	 * @author alessandra
	 */
	
	private static final long serialVersionUID = 7343282100747516432L;
	private Model jenaModel;
	private YLogger logger;
	
	public MSEGraph(Model m)	{
		this.jenaModel = m;
	}
	
	/**
	 * Create an MSEGraph from a string representing a serialized model
	 * @param s - a String representing a serialized model
	 */
	public MSEGraph(InputStream bais) throws UnsupportedEncodingException	{
			
			this.jenaModel = ModelFactory.createDefaultModel();
			jenaModel.read(bais, "");
		
	}
	
	@Override
	public boolean isEmpty() {
		return jenaModel.isEmpty();
	}

	@Override
	/**
	 * @param notation - Predefined values for lang are "RDF/XML", "N-TRIPLE", "TURTLE" (or "TTL") and "N3". 
	 * null represents the default language, "RDF/XML". "RDF/XML-ABBREV" is a synonym for "RDF/XML" (Jena implementation)
	 */
	public void writeGraph(OutputStream output, String notation) {
		
		jenaModel.write(output, notation);
	}

	@Override
	/**
	 * @param notation - Predefined values for lang are "RDF/XML", "N-TRIPLE", "TURTLE" (or "TTL") and "N3". 
	 * null represents the default language, "RDF/XML". "RDF/XML-ABBREV" is a synonym for "RDF/XML" (Jena implementation)
	 */
	public void readGraph(String input, String notation) {
		
		jenaModel.read(input,notation);
	}

	protected Model getJenaModel()	{
		return this.jenaModel;
	}

	@Override
	public ArrayList<Triple> getTriples() {
		
		StmtIterator iter = this.jenaModel.listStatements();
		Node o = null;
		ArrayList<Triple> list = new ArrayList<Triple>();
		
		while (iter.hasNext()) {
		    Statement stmt      = iter.nextStatement();  // get next statement
		    //System.out.println("MSEGraph.getTriples: " + stmt.toString());
		    
		    Resource  subject   = stmt.getSubject();     // get the subject
		    Property  predicate = stmt.getPredicate();   // get the predicate
		    RDFNode   object    = stmt.getObject();      // get the object
			
		    Node s = new MSEResource(subject.getURI(), subject.getClass().toString());
		    Node p = new MSEResource(predicate.getURI(), predicate.getClass().toString());
		    
		    if (object.isResource()) 	{		// object is a resource node
				o = new MSEResource(((Resource)object).getURI(), object.getClass().toString());
				}
			else	{			// object is a literal	
				try {
					//System.err.println(object.isLiteral() + stmt.toString());
					o = new MSELiteral(((Literal)object).getString(), ((Literal) object).getDatatypeURI());
				} catch (KBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		    
		    MSETriple triple = new MSETriple(s,p,o);
		    
		    if (triple == null)	{
		    	logger.w("MSEGraph", "A triple was not added to the graph");
		    }
		    else	{
		    	list.add(triple);
		    }

		}
		//System.out.println("MSEGraph.getTriples: ArrayList<Triple> size is: " + list.size());
	    
		return list;
	}

	
	
	
}

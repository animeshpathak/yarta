package fr.inria.arles.yarta.knowledgebase;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import fr.inria.arles.yarta.knowledgebase.interfaces.Graph;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;

/**
 * This class is a Jena-based implementation of an MSE Graph.
 */
public class MSEGraph implements Graph, Serializable {

	public final static String baseResourceURI = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#Resource";
	private static final long serialVersionUID = 7343282100747516432L;
	private Model jenaModel;

	public MSEGraph(Model m) {
		this.jenaModel = m;
	}

	/**
	 * Create an MSEGraph from a string representing a serialized model
	 * 
	 * @param s
	 *            - a String representing a serialized model
	 */
	public MSEGraph(InputStream bais) throws UnsupportedEncodingException {
		this.jenaModel = ModelFactory.createDefaultModel();
		jenaModel.read(bais, "");

	}

	@Override
	public boolean isEmpty() {
		return jenaModel.isEmpty();
	}

	/**
	 * @param notation
	 *            - Predefined values for lang are "RDF/XML", "N-TRIPLE",
	 *            "TURTLE" (or "TTL") and "N3". null represents the default
	 *            language, "RDF/XML". "RDF/XML-ABBREV" is a synonym for
	 *            "RDF/XML" (Jena implementation)
	 */
	@Override
	public void writeGraph(OutputStream output, String notation) {
		jenaModel.write(output, notation);
	}

	/**
	 * @param notation
	 *            - Predefined values for lang are "RDF/XML", "N-TRIPLE",
	 *            "TURTLE" (or "TTL") and "N3". null represents the default
	 *            language, "RDF/XML". "RDF/XML-ABBREV" is a synonym for
	 *            "RDF/XML" (Jena implementation)
	 */
	@Override
	public void readGraph(String input, String notation) {
		jenaModel.read(input, notation);
	}

	/**
	 * Overrides writeObject function in order to have custom serialization.
	 * 
	 * @param out
	 *            the ObjectOutputStream where to write the object
	 * 
	 * @throws java.io.IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		jenaModel.write(out);
	}

	/**
	 * Overrides readObject function in order to have custom serialization.
	 * 
	 * @param int the ObjectInputStream from where to read the object
	 * 
	 * @throws java.io.IOException
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		jenaModel = ModelFactory.createDefaultModel();
		jenaModel.read(in, "");
	}

	/**
	 * Returns the actual model.
	 * 
	 * @return Model the model
	 */
	protected Model getJenaModel() {
		return this.jenaModel;
	}

	@Override
	public ArrayList<Triple> getTriples() {

		StmtIterator iter = this.jenaModel.listStatements();
		Node o = null;
		ArrayList<Triple> list = new ArrayList<Triple>();
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement(); // get next statement
			// System.out.println("MSEGraph.getTriples: " + stmt.toString());

			Resource subject = stmt.getSubject(); // get the subject
			Property predicate = stmt.getPredicate(); // get the predicate
			RDFNode object = stmt.getObject(); // get the object

			if (subject.getURI() == null || predicate.getURI() == null) {
				continue;
			}

			Node s = new MSEResource(subject.getURI(), baseResourceURI);
			Node p = new MSEResource(predicate.getURI(), predicate.getClass()
					.toString());

			// Node s = getResourceByURINoPolicies(subject.getURI());
			// Node p = getResourceByURINoPolicies(predicate.getURI());

			if (object.isResource()) { // object is a resource node

				if (((Resource) object).getURI() == null) {
					continue;
				}

				o = new MSEResource(((Resource) object).getURI(),
						baseResourceURI);
			} else { // object is a literal
				try {
					// System.err.println(object.isLiteral() + stmt.toString());
					o = new MSELiteral(((Literal) object).getString(),
							((Literal) object).getDatatypeURI());
				} catch (KBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			MSETriple triple = new MSETriple(s, p, o);

			list.add(triple);
		}
		return list;
	}
}

package fr.inria.arles.yarta.knowledgebase;

import fr.inria.arles.yarta.knowledgebase.interfaces.Node;

/**
 * Implementation of a resource node in the knowledge base.
 */
public class MSEResource implements Node {

	/**
	 * Serial id for serialization
	 */
	private static final long serialVersionUID = 1L;
	private String URI;
	private String MSEtype;
	private String namespace;
	private String relName;

	// private Resource jenaRes;

	/**
	 * 
	 * @param uri
	 *            - the complete URI of the resource
	 * @param type
	 *            - the (rdf)type of the resource node, must be chosen among the
	 *            first-class MSE concepts
	 */
	public MSEResource(String uri, String type) {
		this.URI = uri;
		this.MSEtype = type;
		String[] names = uri.split("#");
		this.namespace = names[0] + '#';
		if (!(names.length == 1))
			this.relName = names[1];
		else
			this.relName = "";

	}

	/**
	 * @return the uRI
	 */
	public String getName() {
		return this.URI;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return this.namespace;
	}

	@Override
	public String getRelativeName() {
		return this.relName;
	}

	public String getType() {
		return this.MSEtype;
	}

	/**
	 * @return String representation of the KB Node.
	 */
	public String toString() {
		return this.getName();
	}

	@Override
	public int whichNode() {
		return RDF_RESOURCE;
	}

	/**
	 * @return the Jena class Resource used in the current implementation
	 */
	/*
	 * public Resource getJenaResource() { return jenaRes; }
	 */
}

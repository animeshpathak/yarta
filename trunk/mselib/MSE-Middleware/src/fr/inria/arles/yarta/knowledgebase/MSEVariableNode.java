package fr.inria.arles.yarta.knowledgebase;

import fr.inria.arles.yarta.knowledgebase.interfaces.Node;

public class MSEVariableNode implements Node {

	/**
	 * Serial id for serialization.
	 */
	private static final long serialVersionUID = 1L;
	private String variable_name;
	private String type;

	/**
	 * 
	 * @param var_name
	 *            - the name of the variable. The constructor adds a question
	 *            mark (?) before the name, following the query language
	 *            convention
	 * @param type
	 *            - the MSE type of the variable node, null if the type is not
	 *            known
	 */
	public MSEVariableNode(String var_name, String type) {
		this.variable_name = "?" + var_name;
		this.type = type;

	}

	/**
	 * @see fr.inria.arles.yarta.knowledgebase.interfaces.Node#getType()
	 */
	@Override
	public String getType() {
		return this.type;
	}

	/**
	 * @see fr.inria.arles.yarta.knowledgebase.interfaces.Node#getURI()
	 */
	@Override
	public String getName() {
		return this.variable_name;
	}

	/**
	 * @see fr.inria.arles.yarta.knowledgebase.interfaces.Node#whichNode()
	 */
	@Override
	public int whichNode() {
		return VARIABLE_NODE;
	}

	@Override
	public String getRelativeName() throws KBException {
		KBException e = new KBException(
				"MSE Variable Node does not have relative name!");
		throw (e);
	}
}

package fr.inria.arles.yarta.knowledgebase;

import fr.inria.arles.yarta.knowledgebase.interfaces.Node;

/**
 * Implementation of a literal node in the knowledge base.
 */
public class MSELiteral implements Node {

	/**
	 * Version used in serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see fr.inria.arles.mse.knowledgebase.interfaces.Node
	 */
	private String value;

	/**
	 * The data type is currently expressed as an URI using the XML-schema data
	 * types, e.g., http://www.w3.org/2001/XMLSchema#int For the sake of
	 * simplicity we store all values of literals as string. To get the proper
	 * datatype a conversion should be performed
	 */
	private String dType;

	public MSELiteral(String value, String dataType) throws DataTypeException {

		if (isSupportedDataType(dataType)) {
			this.value = value;
			this.dType = dataType;
		}

		else if (dataType == null) { // this is an XML plain literal, we try to
										// convert it to a string
			this.value = value;
			this.dType = XSD_STRING;
		}

		else {
			DataTypeException e = new DataTypeException(
					"Datatype " + dataType + " not supported");
			throw e;
		}
	}

	/**
	 * @return the value of the literal node
	 */
	@Override
	public String getName() {
		return value;
	}

	/**
	 * @return the data type
	 */
	public String getType() {
		return dType;
	}

	/**
	 * Currently only returns the value of the literal, no type.
	 */
	public String toString() {
		return this.getName();
	}

	@Override
	public int whichNode() {
		return RDF_LITERAL;
	}

	@Override
	public String getRelativeName() throws KBException {
		KBException e = new KBException(
				"MSE Literal does not have relative name!");
		throw (e);
	}
	
	public static boolean isSupportedDataType(String dataTypeURI) {
		if (dataTypeURI == null) {
			return false;
		}

		if (dataTypeURI.equals(MSEKnowledgeBase.XSD_STRING)
				|| dataTypeURI.equals(MSEKnowledgeBase.XSD_INT)
				|| dataTypeURI.equals(MSEKnowledgeBase.XSD_BOOLEAN)
				|| dataTypeURI.equals(MSEKnowledgeBase.XSD_DOUBLE)
				|| dataTypeURI.equals(MSEKnowledgeBase.XSD_LONG))
			return true;
		else
			return false;

	}
	
	public static String XSD_STRING = "http://www.w3.org/2001/XMLSchema#string";
}

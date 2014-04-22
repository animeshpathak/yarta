/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase;

import fr.inria.arles.yarta.knowledgebase.interfaces.Node;

/**
 * Implementation of a literal node in the knowledge base.
 * @author alessandra
 *
 */
public class MSELiteral implements Node {

	/* (non-Javadoc)
	 * @see fr.inria.arles.mse.knowledgebase.interfaces.Node
	 */
	private String value;
	/**
	 * The data type is currently expressed as an URI using the XML-schema data types, e.g., http://www.w3.org/2001/XMLSchema#int
	 */
	private String dType;
	
	public MSELiteral(String value, String dataType) throws DataTypeException {
		
		if (MSEKnowledgeBase.isSupportedDataType(dataType))	{
		this.value = value;
		this.dType = dataType;
		}
		
		else	{
			DataTypeException e = new DataTypeException("Datatype not supported");
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
	public String toString()	{
		return this.getName();
	}

	@Override
	public int whichNode() {
		return 1;
	}

	@Override
	public String getRelativeName() throws KBException {
		KBException e = new KBException("MSE Literal does not have relative name!");
		throw(e);
	}



}

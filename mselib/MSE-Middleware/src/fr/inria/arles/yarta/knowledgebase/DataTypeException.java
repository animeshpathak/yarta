package fr.inria.arles.yarta.knowledgebase;

public class DataTypeException extends KBException {

	private static final long serialVersionUID = -2889002112358090123L;

	public DataTypeException() {
	}

	/**
	 * @param message
	 */
	public DataTypeException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DataTypeException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DataTypeException(String message, Throwable cause) {
		super(message, cause);
	}
}

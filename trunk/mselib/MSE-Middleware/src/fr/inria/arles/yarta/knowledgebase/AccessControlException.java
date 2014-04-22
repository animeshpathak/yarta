package fr.inria.arles.yarta.knowledgebase;

public class AccessControlException extends KBException {

	private static final long serialVersionUID = -6342122679341340392L;

	public AccessControlException() {
	}

	/**
	 * @param message
	 */
	public AccessControlException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public AccessControlException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AccessControlException(String message, Throwable cause) {
		super(message, cause);
	}
}

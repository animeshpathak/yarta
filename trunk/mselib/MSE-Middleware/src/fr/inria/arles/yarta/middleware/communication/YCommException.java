package fr.inria.arles.yarta.middleware.communication;

public class YCommException extends Exception {

	/**
	 * Serialize version for serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public YCommException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public YCommException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public YCommException(String message, Throwable cause) {
		super(message, cause);
	}
}

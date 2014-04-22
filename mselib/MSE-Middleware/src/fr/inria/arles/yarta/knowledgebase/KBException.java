package fr.inria.arles.yarta.knowledgebase;

import java.lang.Exception;

public class KBException extends Exception {

	private static final long serialVersionUID = 6974434499876650646L;

	public KBException() {
	}

	/**
	 * @param message
	 */
	public KBException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public KBException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public KBException(String message, Throwable cause) {
		super(message, cause);
	}
}

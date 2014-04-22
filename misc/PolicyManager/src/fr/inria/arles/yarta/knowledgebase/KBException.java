/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase;

import java.lang.Exception;

/**
 * @author alessandra
 *
 */
public class KBException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6974434499876650646L;

	/**
	 * 
	 */
	public KBException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public KBException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public KBException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public KBException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}

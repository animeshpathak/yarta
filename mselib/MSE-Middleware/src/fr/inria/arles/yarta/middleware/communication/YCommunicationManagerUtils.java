package fr.inria.arles.yarta.middleware.communication;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;

/**
 * Contains utility methods used in communication.
 */
public class YCommunicationManagerUtils {

	private static final String COMM_MGR_UTILS_LOGTAG = "Yarta-CommManager";

	private static YLogger ylogger = YLoggerFactory.getLogger();

	/**
	 * Converts an object to an array of bytes . Uses the Logging utilities for
	 * reporting exceptions.
	 * 
	 * @param object
	 *            the object to convert.
	 * @return the associated byte array.
	 */
	public static final byte[] toBytes(Object object) {
		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		try {
			java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(
					baos);
			oos.writeObject(object);
		} catch (java.io.IOException ioe) {
			ylogger.e(COMM_MGR_UTILS_LOGTAG, "IOException in toBytes", ioe);
		}
		return baos.toByteArray();
	}

	/**
	 * Converts an array of bytes back to its constituent object. The input
	 * array is assumed to have been created from the original object. Uses the
	 * Logging utilities for reporting exceptions.
	 * 
	 * @param bytes
	 *            the byte array to convert.
	 * @return the associated object.
	 */
	public static final Object toObject(byte[] bytes) {
		Object object = null;
		try {
			object = new java.io.ObjectInputStream(
					new java.io.ByteArrayInputStream(bytes)).readObject();
		} catch (java.io.IOException ioe) {
			ylogger.e(COMM_MGR_UTILS_LOGTAG, "IOException in toObject", ioe);
		} catch (java.lang.ClassNotFoundException cnfe) {
			ylogger.e(COMM_MGR_UTILS_LOGTAG,
					"ClassNotFoundException in toObject", cnfe);
		}
		return object;
	}
}

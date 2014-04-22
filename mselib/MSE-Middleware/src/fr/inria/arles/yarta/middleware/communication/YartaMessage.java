package fr.inria.arles.yarta.middleware.communication;

import java.io.Serializable;

public interface YartaMessage extends Serializable {

	/**
	 * Method to de-serialize messages. Should be symmetric wrt.to method
	 * {@link fr.inria.arles.yarta.middleware.communication.RemoteQueryMessage#toBytes()}
	 * 
	 * @param b
	 *            - input byte array used to populate the message (i.e., by
	 *            deserializing it)
	 */
	public void populateObjectFromByte(byte[] b);

	/**
	 * Method to serialize messages. Should be symmetric wrt.to method
	 * {@link fr.inria.arles.yarta.middleware.communication.RemoteQueryMessage#populateObjectFromByte(byte[] b)}
	 * 
	 * @return - a byte array obtained from the serialization of the Yarta
	 *         message
	 */
	public byte[] toBytes();
}

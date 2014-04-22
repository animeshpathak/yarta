package fr.inria.arles.yarta.middleware.communication;

import java.io.Serializable;

/**
 * The Message to say hello. Consists of the requestor's first name, last name,
 * and uniqueId
 */
public class HelloMessage implements Serializable, YartaMessage {

	/**
	 * The serialization UID
	 */
	private static final long serialVersionUID = 1228126955833704559L;

	private BasicPersonInfo basicPersonInfo;

	/**
	 * construct a new HelloMessage
	 * 
	 * @param basicPersonInfo
	 *            The basicPersonInfo to be passed in it.
	 */
	public HelloMessage(BasicPersonInfo basicPersonInfo) {
		this.basicPersonInfo = basicPersonInfo;
	}

	@Override
	public byte[] toBytes() {
		return YCommunicationManagerUtils.toBytes(this);
	}

	/**
	 * @return the basicPersonInfo
	 */
	public BasicPersonInfo getBasicPersonInfo() {
		return basicPersonInfo;
	}

	@Override
	public void populateObjectFromByte(byte[] b) {
		this.basicPersonInfo = (BasicPersonInfo) YCommunicationManagerUtils
				.toObject(b);
	}
}

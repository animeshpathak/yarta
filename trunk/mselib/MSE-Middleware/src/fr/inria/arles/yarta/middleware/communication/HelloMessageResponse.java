package fr.inria.arles.yarta.middleware.communication;

import java.io.Serializable;

public class HelloMessageResponse implements Serializable {

	/**
	 * unique ID for serialization
	 */
	private static final long serialVersionUID = -2680791059325419237L;

	/** status code for OK response to hello message */
	public static final int HELLO_YES = 0;
	/** status code for NOT-OK response to hello message */
	public static final int HELLO_NO = 1;

	private int status;

	private BasicPersonInfo basicPersonInfo;

	public HelloMessageResponse(int status, BasicPersonInfo basicPersonInfo) {
		this.status = status;
		this.basicPersonInfo = basicPersonInfo;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @return the basicPersonInfo
	 */
	public BasicPersonInfo getBasicPersonInfo() {
		return basicPersonInfo;
	}
}

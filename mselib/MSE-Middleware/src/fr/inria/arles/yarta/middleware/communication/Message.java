package fr.inria.arles.yarta.middleware.communication;

import java.io.Serializable;

public class Message implements Serializable {

	public static final int TYPE_HELLO = 1;
	public static final int TYPE_HELLO_REPLY = 2;

	// TODO: make hello/reply update/reply classes
	public static final int TYPE_UPDATE = 3;
	public static final int TYPE_UPDATE_REPLY = 4;

	public static final int TYPE_PUSH = 5;

	public static final int TYPE_NOTIFY = 6;

	public static final int TYPE_UPDATE_REPLY_MULTIPART = 7;

	public static final int TYPE_USER_MESSAGE = 21;

	public Message() {
	}

	public Message(byte[] data) {
		this.type = TYPE_USER_MESSAGE;
		this.data = data.clone();
	}

	public Message(int type, byte[] data) {
		this.type = type;
		this.data = data == null ? new byte[0] : data.clone();
	}

	public Message(int type, byte[] data, String appId) {
		this.type = type;
		this.data = data.clone();
		this.appId = appId;
	}

	public int getType() {
		return type;
	}

	public byte[] getData() {
		return data;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setData(byte[] data) {
		this.data = data.clone();
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppId() {
		return appId;
	}

	private int type;
	private byte[] data;
	private String appId;
	private static final long serialVersionUID = 1L;
}

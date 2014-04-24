package fr.inria.arles.yarta.android.library.web;

import java.io.Serializable;

public class MessageItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private String guid;
	private String subject;
	private String description;
	private long timestamp;
	private boolean read;
	private UserItem from;

	public MessageItem(String guid, String subject, String description,
			long timestamp, boolean read, UserItem from) {
		this.guid = guid;
		this.subject = subject;
		this.description = description;
		this.timestamp = timestamp;
		this.read = read;
		this.from = from;
	}

	public String getDescription() {
		return description;
	}

	public UserItem getFrom() {
		return from;
	}

	public String getGuid() {
		return guid;
	}

	public String getSubject() {
		return subject;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public boolean isRead() {
		return read;
	}
}

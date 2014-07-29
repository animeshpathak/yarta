package fr.inria.arles.iris.web;

public class WireItem {

	private String content;
	private UserItem author;
	private String guid;
	private long time;

	public WireItem(String guid, String content, long time, UserItem author) {
		this.guid = guid;
		this.content = content;
		this.author = author;
		this.time = time;
	}

	public String getGuid() {
		return guid;
	}

	public UserItem getAuthor() {
		return author;
	}

	public String getContent() {
		return content;
	}

	public long getTime() {
		return time;
	}
}

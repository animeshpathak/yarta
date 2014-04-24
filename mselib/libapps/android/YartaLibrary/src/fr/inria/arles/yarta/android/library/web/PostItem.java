package fr.inria.arles.yarta.android.library.web;

import java.io.Serializable;

public class PostItem implements Serializable {
	private static final long serialVersionUID = 1L;

	private String guid;
	private String title;
	private UserItem owner;
	private long time;
	private String description;

	public PostItem(String guid, String title, long time, UserItem owner) {
		this.guid = guid;
		this.title = title;
		this.time = time;
		this.owner = owner;
	}

	public PostItem(String guid, String title, String description, long time,
			UserItem owner) {
		this.guid = guid;
		this.title = title;
		this.description = description;
		this.time = time;
		this.owner = owner;
	}

	public String getGuid() {
		return guid;
	}

	public UserItem getOwner() {
		return owner;
	}

	public long getTime() {
		return time;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
}

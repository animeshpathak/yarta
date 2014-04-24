package fr.inria.arles.yarta.android.library.web;

import java.io.Serializable;

public class UserItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String username;
	private String avatarURL;
	private String website;

	private String location;
	private String room;
	private String phone;

	/**
	 * Used for basic information editting
	 * 
	 * @param name
	 * @param email
	 * @param website
	 */
	public UserItem(String name, String website, String avatar_url,
			String location, String room, String phone) {
		this.name = name;
		this.website = website;
		this.avatarURL = avatar_url;

		this.location = location == null ? "-" : location;
		this.room = room == null ? "-" : room;
		this.phone = phone == null ? "-" : phone;
	}

	public UserItem(String name, String username, String avatarURL) {
		this.name = name;
		this.username = username;
		this.avatarURL = avatarURL;
	}

	public String getLocation() {
		return location;
	}

	public String getRoom() {
		return room;
	}

	public String getPhone() {
		return phone;
	}

	public String getName() {
		return name;
	}

	public String getWebsite() {
		return website;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGuid() {
		return username;
	}

	public String getAvatarURL() {
		return avatarURL;
	}
}

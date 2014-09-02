package fr.inria.arles.iris.web;

public class GroupItem {

	private String guid;
	private String name;
	private String avatarURL;
	private int members;
	private String ownerName;

	public GroupItem(String name, String avatarURL, int members,
			String ownerName) {
		this.name = name;
		this.avatarURL = avatarURL;
		this.members = members;
		this.ownerName = ownerName;
	}

	public GroupItem(String guid, String name, String avatarURL, int members) {
		this.guid = guid;
		this.name = name;
		this.avatarURL = avatarURL;
		this.members = members;
	}

	public String getGuid() {
		return guid;
	}
	
	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getAvatarURL() {
		return avatarURL;
	}

	public int getMembers() {
		return members;
	}

	public String getName() {
		return name;
	}

	public String getOwnerName() {
		return ownerName;
	}
}

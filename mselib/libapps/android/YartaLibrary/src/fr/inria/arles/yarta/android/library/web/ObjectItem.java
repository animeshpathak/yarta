package fr.inria.arles.yarta.android.library.web;

public class ObjectItem {

	private String guid;
	private String name;
	private String description;
	private String type;

	public ObjectItem(String guid, String name, String description, String type) {
		this.guid = guid;
		this.name = name;
		this.description = description;
		this.type = type;
	}

	public String getDescription() {
		if (description.contains("iframe") || description.length() == 0) {
			return null;
		}
		return description;
	}

	public String getGuid() {
		return guid;
	}

	public String getName() {
		if (name.contains("null")) {
			return null;
		}
		return name;
	}

	public String getType() {
		return type;
	}

}

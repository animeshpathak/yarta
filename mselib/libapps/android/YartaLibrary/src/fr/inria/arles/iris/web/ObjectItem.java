package fr.inria.arles.iris.web;

public class ObjectItem {

	public static final String User = "user";
	public static final String Group = "group";
	public static final String Object = "object";
	public static final String Blog = "blog";
	public static final String Page = "page";
	public static final String PageTop = "page_top";
	public static final String Feedback = "feedback";
	public static final String Topic = "groupforumtopic";
	public static final String File = "file";

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

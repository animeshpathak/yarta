package fr.inria.arles.iris.web;

public class FileItem extends PostItem {
	private static final long serialVersionUID = 1L;
	private String name;
	private long size;

	public FileItem(String guid, String title, String description, String name,
			long size, long time, UserItem owner) {
		super(guid, title, description, time, owner);
		this.name = name;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public long getSize() {
		return size;
	}
}

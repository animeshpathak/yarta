package fr.inria.arles.yarta.android.library.web;

public class RiverItem {

	private UserItem subject;
	private String predicate;
	private ObjectItem object;
	private long time;

	public RiverItem(UserItem subject, String predicate, ObjectItem object,
			long time) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
		this.time = time;
	}

	public ObjectItem getObject() {
		return object;
	}

	public String getPredicate() {
		return predicate;
	}

	public UserItem getSubject() {
		return subject;
	}

	public long getTime() {
		return time;
	}

}

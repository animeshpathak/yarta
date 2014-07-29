package fr.inria.arles.iris.web;

import java.io.Serializable;

/**
 * Models a friend request or group join request.
 */
public class RequestItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private UserItem user;
	private GroupItem group;

	public RequestItem(UserItem user) {
		this.user = user;
	}

	public RequestItem(UserItem user, GroupItem group) {
		this.user = user;
		this.group = group;
	}

	public UserItem getUser() {
		return user;
	}

	public GroupItem getGroup() {
		return group;
	}
}

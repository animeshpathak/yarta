package fr.inria.arles.foosball.util;

public class Player {
	String userId;
	String userName;

	public Player(String userId, String userName) {
		this.userId = userId;
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}
}

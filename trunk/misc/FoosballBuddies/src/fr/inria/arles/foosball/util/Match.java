package fr.inria.arles.foosball.util;

public class Match {

	String id;
	Player blueO;
	Player blueD;
	Player redO;
	Player redD;

	public Match(String id, Player blueD, Player blueO, Player redD, Player redO) {
		this.id = id;
		this.blueD = blueD;
		this.blueO = blueO;
		this.redD = redD;
		this.redO = redO;
	}

	public Player getBlueD() {
		return blueD;
	}

	public Player getBlueO() {
		return blueO;
	}

	public Player getRedD() {
		return redD;
	}

	public Player getRedO() {
		return redO;
	}
	
	public String getId() {
		return id;
	}

}

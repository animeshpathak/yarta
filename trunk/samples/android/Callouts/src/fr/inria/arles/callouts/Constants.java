package fr.inria.arles.callouts;

import fr.inria.arles.yarta.resources.Group;

public class Constants {

	// TODO: customize
	private static final int GroupId = 18113;

	public static final String getGroupId() {
		return Group.typeURI + "_" + GroupId;
	}
}

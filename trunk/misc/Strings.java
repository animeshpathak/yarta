package fr.inria.arles.iris.web;

/**
 * Needed for compilation or YartaLibrary project.
 * Please use your own since these are empty strings.
 */
public final class Strings {
	public static String PublicKey = "";
	public static String PrivateKey = "";
	public static String BaseCAS = "";
	public static String BaseService = "";

	static {
		final boolean release = true;
		if (!release) {
			PublicKey = "";
			PrivateKey = "";
			BaseService = "";
			BaseCAS = "";
		}
	}
}

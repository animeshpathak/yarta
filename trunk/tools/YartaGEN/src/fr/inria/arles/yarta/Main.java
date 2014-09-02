package fr.inria.arles.yarta;

import fr.inria.arles.yarta.parser.Parser;

/**
 * Known bugs:
 * TODO: update base mse rdf file
 * TODO: generate readme file
 * TODO: add support for multiple ranges
 * TODO: remove any useless inheritance classes
 * TODO: add error handling (non union errors)
 * TODO: add quite flag
 * TODO: pack everything in a JAR file
 */
public class Main {

	/**
	 * Prints an error.
	 * 
	 * @param error
	 */
	public static void printError(String error) {
		print("ERROR: " + error);
	}

	/**
	 * Prints an warning.
	 * 
	 * @param message
	 */
	public static void printWarning(String message) {
		print("WARNING: " + message);
	}

	/**
	 * Prints an info.
	 * 
	 * @param info
	 */
	public static void printInfo(String format, Object... args) {
		print("INFO: " + String.format(format, args));
	}

	/**
	 * Prints the usage.
	 */
	public static void printUsage() {
		printInfo("Usage: YartaGEN in-rdf-file in-namespace out-directory out-package-name");
	}

	public static void main(String args[]) {
		if (args.length != 4) {
			printUsage();
		} else {
			long lStart = System.currentTimeMillis();
			Parser parser = Parser.getInstance();
			parser.setNamespace(args[1]);
			if (!parser.parseRDF(args[0])) {
				printError(parser.getError());
				return;
			}
			if (!parser.generateJavaCode(args[2], args[3])) {
				printError(parser.getError());
				return;
			}
			long lEnd = System.currentTimeMillis();

			printInfo(String.format("Total time: %.2f seconds.",
					(lEnd - lStart) / 1000.0));
		}
	}

	private static void print(String message) {
		System.out.println(message);
	}
}

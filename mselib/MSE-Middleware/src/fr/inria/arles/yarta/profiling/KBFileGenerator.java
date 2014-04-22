package fr.inria.arles.yarta.profiling;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBaseUtils;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.yarta.resources.Person;

/** utility class for generating stock KB files to be used later */
public class KBFileGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws KBException {

		int initialCount = 1;
		int finalCount = 1002;
		int step = 100;

		generateAddPersonFiles(initialCount, finalCount, step);
		generateAddPersonAddKnowsFiles(initialCount, finalCount, step);

	}

	/**
	 * creates a set of files in the res/ directory with a pre-defined pattern
	 * 
	 * @param initialCount
	 *            The initial number of people in KB
	 * @param finalCount
	 *            The largest number of people in KB
	 * @param step
	 *            the step for the for loop
	 */
	private static void generateAddPersonFiles(int initialCount,
			int finalCount, int step) throws KBException {
		String ownerID = "animesh@yarta.inria.fr";
		String baseOntologyFilePath = "test/mse-1.1.rdf";
		String baseOntologyNameSpace = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#";
		String policyFilePath = "test/policies-demo1";
		String fileNamePrefix = "res/addpersonkb_";
		StorageAccessManager s;
		KnowledgeBase k;
		String filename;
		String fileExtention = ".rdf";

		// set loglevel to quiet
		YLoggerFactory.setLoglevel(YLoggerFactory.LOGLEVEL_QUIET);

		for (int count = initialCount; count < finalCount; count = count + step) {
			// do something
			s = new StorageAccessManager();
			k = new MSEKnowledgeBase();

			k.initialize(baseOntologyFilePath, baseOntologyNameSpace,
					policyFilePath, ownerID);
			s.setKnowledgeBase(k);
			s.setOwnerID(ownerID);
			// add count people
			for (int i = 0; i < count; i++) {
				Person p = s.createPerson("uname-" + i);
				p.setFirstName("FName-" + i);
				p.setLastName("LName-" + i);
				p.setEmail("email-" + i);
				p.setName("Name-" + i);
				p.setHomepage("Homepage-" + i);
			}

			filename = fileNamePrefix + count + fileExtention;

			// now save this KB to a file.
			System.out.println(count + " persons added. Creating file now ...");
			try {
				MSEKnowledgeBaseUtils.writeDataOnStream(new FileOutputStream(
						filename, false), (MSEKnowledgeBase) k);
			} catch (FileNotFoundException e) {
				System.err.println("Why was file " + filename + " not found!");
				e.printStackTrace();
			}

		}
		System.out.println("All done.");

	}

	/**
	 * creates a set of files in the res/ directory with a pre-defined pattern.
	 * adds "knows" from person 0 to 50% of the others
	 * 
	 * @param initialCount
	 *            The initial number of people in KB
	 * @param finalCount
	 *            The largest number of people in KB
	 * @param step
	 *            the step for the for loop
	 */
	private static void generateAddPersonAddKnowsFiles(int initialCount,
			int finalCount, int step) throws KBException {
		String ownerID = "animesh@yarta.inria.fr";
		String baseOntologyFilePath = "test/mse-1.1.rdf";
		String baseOntologyNameSpace = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#";
		String policyFilePath = "test/policies-demo1";
		String fileNamePrefix = "res/addpersonaddknowskb_";
		StorageAccessManager s;
		KnowledgeBase k;
		String filename;

		int minFriends = 50;
		String fileExtention = minFriends + ".rdf";

		// set loglevel to quiet
		YLoggerFactory.setLoglevel(YLoggerFactory.LOGLEVEL_QUIET);

		for (int count = initialCount; count < finalCount; count = count + step) {

			if (count < minFriends)
				continue;

			// do something
			s = new StorageAccessManager();
			k = new MSEKnowledgeBase();

			k.initialize(baseOntologyFilePath, baseOntologyNameSpace,
					policyFilePath, ownerID);
			s.setKnowledgeBase(k);
			s.setOwnerID(ownerID);
			// add count people
			for (int i = 0; i < count; i++) {
				Person p = s.createPerson("uname-" + i);
				p.setFirstName("FName-" + i);
				p.setLastName("LName-" + i);
				p.setEmail("email-" + i);
				p.setName("Name-" + i);
				p.setHomepage("Homepage-" + i);
			}

			// for half of them, add them as knows of person 0
			Person p = s.getPersonByUserId("uname-0");
			Person q;

			for (int i = 0; i < minFriends; i++) {
				// for(int i = 0; i < count; i = i + 2){
				q = s.getPersonByUserId("uname-" + i);
				p.addKnows(q);
			}

			filename = fileNamePrefix + count + fileExtention;

			// now save this KB to a file.
			System.out.println(count + " persons added. Creating file now ...");
			try {
				MSEKnowledgeBaseUtils.writeDataOnStream(new FileOutputStream(
						filename, false), (MSEKnowledgeBase) k);
			} catch (FileNotFoundException e) {
				System.err.println("Why was file " + filename + " not found!");
				e.printStackTrace();
			}

		}
		System.out.println("All done.");
	}
}

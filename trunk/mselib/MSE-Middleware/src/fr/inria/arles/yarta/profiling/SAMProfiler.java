package fr.inria.arles.yarta.profiling;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBaseUtils;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.yarta.resources.Person;

/**
 * Class with methods to measure times for StorageAccessManager operations.
 */
public class SAMProfiler {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws KBException {
		profileAddPerson(1, 500, 10, 20);

		profileGetPerson(1, 2500, 50, 10);

		profileLoadKBAddPerson(1, 1002, 100, 1);
	}

	private static void profileGetPerson(int mincount, int maxcount,
			int countstep, int repetitions) throws KBException {
		String ownerID = "animesh@yarta.inria.fr";
		String baseOntologyFilePath = "test/mse-1.1.rdf";
		String baseOntologyNameSpace = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#";
		String policyFilePath = "test/policies-demo1";
		StorageAccessManager s;
		KnowledgeBase k;
		long startTime;
		long endTime;
		long minTime;
		long maxTime;
		long averageTime;
		long iterTime;
		long cumulTime;

		long minAddTime, maxAddTime, cumulAddTime, averageAddTime, iterAddTime;

		// set loglevel to quiet
		YLoggerFactory.setLoglevel(YLoggerFactory.LOGLEVEL_QUIET);
		System.out
				.println("Number of Persons, Time taken(ms) for addPerson , minAdd, maxAdd, Time taken (ms) for getPerson() after that number of people, minGet, maxGet");

		for (int count = mincount; count < maxcount; count = count + countstep) {
			// going to repeat this for repetition times, and store min, max,
			// and average values
			minTime = Long.MAX_VALUE;
			maxTime = Long.MIN_VALUE;
			cumulTime = 0;
			minAddTime = Long.MAX_VALUE;
			maxAddTime = Long.MIN_VALUE;
			cumulAddTime = 0;

			for (int j = 0; j < repetitions; j++) {

				// do something
				s = new StorageAccessManager();
				k = new MSEKnowledgeBase();

				k.initialize(baseOntologyFilePath, baseOntologyNameSpace,
						policyFilePath, ownerID);
				s.setKnowledgeBase(k);
				s.setOwnerID(ownerID);
				// add count people
				startTime = System.currentTimeMillis();
				for (int i = 0; i < count; i++) {
					Person p = s.createPerson("uname-" + i);
					p.setFirstName("FName-" + i);
					p.setLastName("LName-" + i);
					p.setEmail("email-" + i);
					p.setName("Name-" + i);
					p.setHomepage("Homepage-" + i);
				}
				endTime = System.currentTimeMillis();
				iterAddTime = endTime - startTime;
				cumulAddTime = cumulAddTime + iterAddTime;

				if (iterAddTime < minAddTime)
					minAddTime = iterAddTime;
				if (iterAddTime > maxAddTime)
					maxAddTime = iterAddTime;

				// YLoggerFactory.setLoglevel(YLoggerFactory.LOGLEVEL_DEBUG);
				startTime = System.currentTimeMillis();

				endTime = System.currentTimeMillis();
				iterTime = endTime - startTime;
				cumulTime = cumulTime + iterTime;

				if (iterTime < minTime)
					minTime = iterTime;
				if (iterTime > maxTime)
					maxTime = iterTime;
			}

			averageTime = cumulTime / repetitions;
			averageAddTime = cumulAddTime / repetitions;

			// System.out.println("Time taken (ms) for " + count + " persons = "
			// + (endTime - startTime));
			if (minTime > maxTime)
				System.err
						.println("Something went HORRIBLY wrong! Why is mintime ("
								+ minTime + ") > maxtime (" + maxTime + ") ?");

			System.out.println(count + "," + averageAddTime + "," + minAddTime
					+ "," + maxAddTime + "," + averageTime + "," + minTime
					+ "," + maxTime);

		}

	}

	private static void profileAddPerson(int mincount, int maxcount,
			int countstep, int repetitions) throws KBException {
		String ownerID = "animesh@yarta.inria.fr";
		String baseOntologyFilePath = "test/mse-1.1.rdf";
		String baseOntologyNameSpace = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#";
		String policyFilePath = "test/policies-demo1";
		StorageAccessManager s;
		KnowledgeBase k;
		long startTime;
		long endTime;
		long minTime;
		long maxTime;
		long averageTime;
		long iterTime;
		long cumulTime;

		// set loglevel to quiet
		YLoggerFactory.setLoglevel(YLoggerFactory.LOGLEVEL_QUIET);
		System.out
				.println("Number of Persons, Average Time taken (ms) for addPerson(), minTime, maxTime");

		for (int count = mincount; count < maxcount; count = count + countstep) {
			// going to repeat this for repetition times, and store min, max,
			// and average values
			minTime = Long.MAX_VALUE;
			maxTime = Long.MIN_VALUE;
			cumulTime = 0;

			for (int j = 0; j < repetitions; j++) {
				// do something
				s = new StorageAccessManager();
				k = new MSEKnowledgeBase();

				k.initialize(baseOntologyFilePath, baseOntologyNameSpace,
						policyFilePath, ownerID);
				s.setKnowledgeBase(k);
				s.setOwnerID(ownerID);
				startTime = System.currentTimeMillis();
				// add count people
				for (int i = 0; i < count; i++) {
					Person p = s.createPerson("uname-" + i);
					p.setFirstName("FName-" + i);
					p.setLastName("LName-" + i);
					p.setEmail("email-" + i);
					p.setName("Name-" + i);
					p.setHomepage("Homepage-" + i);
				}

				endTime = System.currentTimeMillis();
				iterTime = endTime - startTime;
				cumulTime = cumulTime + iterTime;

				if (iterTime < minTime)
					minTime = iterTime;
				if (iterTime > maxTime)
					maxTime = iterTime;
			}

			averageTime = cumulTime / repetitions;
			// System.out.println("Time taken (ms) for " + count + " persons = "
			// + (endTime - startTime));
			if (minTime > maxTime)
				System.err
						.println("Something went HORRIBLY wrong! Why is mintime ("
								+ minTime + ") > maxtime (" + maxTime + ") ?");

			System.out.println(count + "," + averageTime + "," + minTime + ","
					+ maxTime);

		}
	}

	/**
	 * profiles the time taken to load a KB containing a certain number of
	 * persons
	 * 
	 * @param initialCount
	 *            initial number of people
	 * @param finalCount
	 *            final count of people
	 * @param step
	 *            stepsize for the loop
	 * @param repetitions
	 *            number of times to repeat the experiment
	 * @throws KBException
	 */
	private static void profileLoadKBAddPerson(int initialCount,
			int finalCount, int step, int repetitions) throws KBException {
		final String ownerID = "animesh@yarta.inria.fr";
		final String baseOntologyFilePath = "test/mse-1.1.rdf";
		final String baseOntologyNameSpace = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#";
		final String policyFilePath = "test/policies-demo1";
		KnowledgeBase k;
		long startTime;
		long endTime;
		long minTime;
		long maxTime;
		long averageTime;
		long iterTime;
		long cumulTime;

		FileInputStream dataFileStream;
		final String fileNamePrefix = "res/addpersonkb_";
		String filename;
		final String fileExtention = ".rdf";

		// set loglevel to quiet
		YLoggerFactory.setLoglevel(YLoggerFactory.LOGLEVEL_QUIET);
		System.out
				.println("Number of Persons, Average Time taken (ms) for loadKB(), minTime, maxTime");

		for (int count = initialCount; count < finalCount; count = count + step) {
			// going to repeat this for repetition times, and store min, max,
			// and average values
			minTime = Long.MAX_VALUE;
			maxTime = Long.MIN_VALUE;
			cumulTime = 0;

			for (int j = 0; j < repetitions; j++) {
				// do something
				k = new MSEKnowledgeBase();

				k.initialize(baseOntologyFilePath, baseOntologyNameSpace,
						policyFilePath, ownerID);

				filename = fileNamePrefix + count + fileExtention;

				try {
					dataFileStream = new FileInputStream(filename);

					startTime = System.currentTimeMillis();
					// load up data from the file
					MSEKnowledgeBaseUtils.readDataFromStream(dataFileStream, k);

					endTime = System.currentTimeMillis();
					iterTime = endTime - startTime;
					cumulTime = cumulTime + iterTime;

					if (iterTime < minTime)
						minTime = iterTime;
					if (iterTime > maxTime)
						maxTime = iterTime;
				} catch (FileNotFoundException e) {
					System.err
							.println("File " + filename + " not found. Why? ");
					e.printStackTrace();
				}
			}

			averageTime = cumulTime / repetitions;
			if (minTime > maxTime)
				System.err
						.println("Something went HORRIBLY wrong! Why is mintime ("
								+ minTime + ") > maxtime (" + maxTime + ") ?");

			System.out.println(count + "," + averageTime + "," + minTime + ","
					+ maxTime);
		}
	}
}

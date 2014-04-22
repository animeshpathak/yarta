package fr.inria.arles.yarta.performance;

import java.util.Set;

import fr.inria.arles.yarta.KnowledgeBaseProfiler;
import fr.inria.arles.yarta.Profiler;
import fr.inria.arles.yarta.StorageAccessManagerProfiler;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBaseUtils;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.yarta.resources.Person;

public class Performance {

	private final static int RUNS = 10;
	private final static String dataSets[] = { "ondin3_10.xml",
			"ondin3_30.xml", "ondin3_50.xml", "ondin3_100.xml",
			"ondin3_300.xml" };

	private final static String mse_namespace = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#";
	private String parentDir;

	public Performance(String parentDir) {
		this.parentDir = parentDir;
	}

	public void runTest(String dataSet, Profiler profiler, String policy)
			throws Exception {
		String ownerId = "me@inria.fr";

		KnowledgeBase kb = new KnowledgeBaseProfiler(profiler);

		kb.initialize(parentDir + "mse.rdf", mse_namespace, parentDir + policy,
				ownerId);

		MSEKnowledgeBaseUtils.importDataFromRDF(parentDir + dataSet, kb);

		StorageAccessManager sam = new StorageAccessManagerProfiler(profiler);
		sam.setKnowledgeBase(kb);
		sam.setOwnerID(ownerId);

		Set<Person> persons = sam.getAllPersons();

		for (Person person : persons) {
			String userId = person.getUserId();
			Person p = sam.getPersonByUserId(userId);
			p.setEmail("test@email.com");
			p.getKnows();

			Person me = sam.getMe();
			me.addKnows(p);
		}

		for (Person person : persons) {
			Person me = sam.getMe();
			me.deleteKnows(person);
		}

		kb.uninitialize();
	}

	public static void main(String args[]) throws Exception {
		Performance p = new Performance("./datasets/");
		Profiler profiler = new Profiler(".");

		for (String dataSet : dataSets) {
			profiler.init(dataSet);

			for (int i = 0; i < RUNS; i++) {
				p.runTest(dataSet, profiler, "policies");
			}
			
			profiler.uninit();
		}
	}
}

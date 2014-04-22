/**
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement;

import org.junit.Before;

import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;

/**
 * @author pathak
 * 
 */
public class StorageAccessManagerTest {

	StorageAccessManager s = new StorageAccessManager();
	String ownerID = "animesh@yarta.inria.fr";
	String baseOntologyFilePath = "test/mse-1.2.rdf";
	// String baseOntologyFilePath = "test/mse-1.1-nc.rdf";
	String baseOntologyNameSpace = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#";
	// String policyFilePath = "test/policies-all";
	String policyFilePath = "test/policies-demo1";
	static final String addingFilePath = "test/demo-conference-ap.rdf";

	KnowledgeBase k;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		k = new MSEKnowledgeBase();

		/*
		 * File f = new File(baseOntologyFilePath); assertTrue(f!=null);
		 * FileInputStream fis = new FileInputStream(f); System.out.print(fis);
		 */

		k.initialize(baseOntologyFilePath, baseOntologyNameSpace,
				policyFilePath, ownerID);
		s.setKnowledgeBase(k);
		s.setOwnerID(ownerID);

	}
}

/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBaseUtils;

/**
 * @author alessandra
 * 
 */
public class ReadWriteStreamTest {

	private MSEKnowledgeBase myKB;

	private static final String baseOntologyFilePath = "test/demo-conference-vi.rdf";
	private static final String policyFilePath = "test/policies-demo1";
	public static String MSE_NAMESPACE = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#";
	private static final String myUserID = "valerie@yarta.inria.fr";
	private static final String addingFilePath = "test/demo-conference-vi.rdf";
	private static final String producedOutputFile = "test/test-output.rdf";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		myKB = new MSEKnowledgeBase();
		myKB.initialize(baseOntologyFilePath, MSE_NAMESPACE, policyFilePath,
				myUserID);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBaseUtils#readDataFromStream(java.io.InputStream, fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase)}
	 * .
	 */
	@Test
	public void testReadDataFromStream() {

		InputStream i = null;

		try {
			i = new FileInputStream(new File(addingFilePath));
		} catch (FileNotFoundException e) {
			fail("MSEKnowledgeBaseUtils.importDataFromRDF - RDF file not found");
		}

		if (MSEKnowledgeBaseUtils.readDataFromStream(i, myKB))
			assertTrue(
					"Reading from input stream succeeded. Content must be checked with the next test",
					true);
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBaseUtils#writeDataOnStream(java.io.OutputStream, fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBase)}
	 * .
	 * 
	 * @throws KBException
	 */
	@Test
	public void testWriteDataOnStream() throws KBException {

		try {
			MSEKnowledgeBaseUtils.writeDataOnStream(new FileOutputStream(
					producedOutputFile), myKB);
		} catch (FileNotFoundException e) {
			fail("Writing on output stream failed");
		}

		MSEKnowledgeBase newKB = new MSEKnowledgeBase();
		newKB.initialize(producedOutputFile, MSE_NAMESPACE, policyFilePath,
				myUserID);

	}

}

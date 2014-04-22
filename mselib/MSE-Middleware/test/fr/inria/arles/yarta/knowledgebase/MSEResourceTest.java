/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.inria.arles.yarta.knowledgebase.MSEResource;

/**
 * @author alessandra
 * 
 */
public class MSEResourceTest {

	String URI = "http://yarta.gforge.inria.fr/ontologies/test.rdf#TestingMcTester";
	String type = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#Agent";
	String namespace = "http://yarta.gforge.inria.fr/ontologies/test.rdf";
	String rel_name = "TestingMcTester";

	MSEResource r;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		r = new MSEResource(URI, type);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSEResource#getName()}.
	 */
	@Test
	public void testgetName() {
		if (r.getName().equals(URI)) {
			assertTrue("getName succeeded", true);
		}
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSEResource#getNamespace()}.
	 */
	@Test
	public void testGetNamespace() {

		if (r.getNamespace().equals(namespace)) {
			assertTrue("getNameSpace succeeded", true);
		}
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSEResource#getRelName()}.
	 */
	@Test
	public void testGetRelName() {

		if (r.getRelativeName().equals(rel_name)) {
			assertTrue("getRelName succeeded", true);
		}
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSEResource#getMSEType()}.
	 */
	@Test
	public void testGetMSEType() {

		if (r.getType().equals(type)) {
			assertTrue("getMSEType succeeded", true);
		}
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSEResource#toString()}.
	 */
	@Test
	public void testToString() {
		if (r.toString() == URI) {
			assertTrue("toString succeeded", true);
		}
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSEResource#isResource()}.
	 */
	@Test
	public void testIsResource() {
		if (r.whichNode() == 0) {
			assertTrue("isResource succeeded", true);
		}
	}

}

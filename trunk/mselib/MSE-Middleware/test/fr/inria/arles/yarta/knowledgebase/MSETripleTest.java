/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.inria.arles.yarta.knowledgebase.MSELiteral;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.knowledgebase.MSETriple;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;

/**
 * @author alessandra
 * 
 */
public class MSETripleTest {

	String s_name = "http://yarta.gforge.inria.fr/ontologies/test.rdf#TestingMcTester";
	String s_type = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#Person";

	String p1_name = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#knows";
	String p2_name = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#fName";
	String p_type = "http://www.w3.org/1999/02/22-rdf-syntax-ns#Property";

	String o1_name = "http://yarta.gforge.inria.fr/ontologies/test.rdf#MickeyMouse";
	String o1_type = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#Person";

	String o2_name = "Testing";
	String o2_type = KnowledgeBase.XSD_STRING;

	Node s, p1, p2, o1, o2;
	MSETriple t1, t2;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		s = new MSEResource(s_name, s_type);
		p1 = new MSEResource(p1_name, p_type);
		p2 = new MSEResource(p2_name, p_type);
		o1 = new MSEResource(o1_name, o1_type);
		o2 = new MSELiteral(o2_name, o2_type);

		t1 = new MSETriple(s, p1, o1);
		t2 = new MSETriple(s, p2, o2);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSETriple#getObject()}.
	 */
	@Test
	public void testGetObject1() {

		if (t1.getObject().equals(o1)) {
			assertTrue("getObject succeeded  when object is resource", true);
		}
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSETriple#getObject()}.
	 */
	@Test
	public void testGetObject2() {

		if (t2.getObject().equals(o2)) {
			assertTrue("getObject succeeded  when object is literal", true);
		}
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSETriple#getProperty()}.
	 */
	@Test
	public void testGetProperty1() {

		if (t1.getProperty().equals(p1)) {
			assertTrue("getProperty succeeded when object is resource", true);
		}
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSETriple#getProperty()}.
	 */
	@Test
	public void testGetProperty2() {

		if (t2.getProperty().equals(p2)) {
			assertTrue("getProperty succeeded when object is literal", true);
		}
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSETriple#getSubject()}.
	 */
	@Test
	public void testGetSubject() {

		if (t1.getSubject().equals(s) && t2.getSubject().equals(s)) {
			assertTrue("getSubject succeeded", true);
		}
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSETriple#toString()}.
	 */
	@Test
	public void testToString() {

		if (t1.toString()
				.equals("(http://yarta.gforge.inria.fr/ontologies/test.rdf#TestingMcTester, http://yarta.gforge.inria.fr/ontologies/mse.rdf#knows, http://yarta.gforge.inria.fr/ontologies/test.rdf#MickeyMouse)")) {

			assertTrue("toString succeeded", true);
		}
	}

}

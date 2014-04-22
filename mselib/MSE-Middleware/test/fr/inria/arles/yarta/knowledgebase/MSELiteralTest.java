/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.inria.arles.yarta.knowledgebase.MSELiteral;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;

/**
 * @author alessandra
 * 
 */
public class MSELiteralTest {

	String value = "Testing";
	String type = KnowledgeBase.XSD_STRING;

	MSELiteral l;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		l = new MSELiteral(value, type);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSELiteral#getName()}.
	 */
	@Test
	public void testgetName() {
		if (l.getName().equals(value)) {
			assertTrue("getName succeeded", true);
		}
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSELiteral#getdType()}.
	 */
	@Test
	public void testGetdType() {
		if (l.getType().equals(type)) {
			assertTrue("getdType succeeded", true);
		}
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSELiteral#toString()}.
	 */
	@Test
	public void testToString() {
		if (l.toString().equals(value)) {
			assertTrue("toString succeeded", true);
		}
		;
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSELiteral#isResource()}.
	 */
	@Test
	public void testIsResource() {
		if (l.whichNode() == 1) {
			assertTrue("isResource succeeded", true);
		}
	}

}

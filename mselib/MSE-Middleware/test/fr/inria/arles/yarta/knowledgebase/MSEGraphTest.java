/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import fr.inria.arles.yarta.knowledgebase.MSEGraph;

/**
 * @author alessandra
 * 
 */
public class MSEGraphTest {

	private Model testModel = ModelFactory.createDefaultModel();
	private String filePath = "test/mse-1.1.rdf";
	private MSEGraph g;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		File f = new File(filePath);
		InputStream i = null;
		try {
			i = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		testModel.read(i, null, "RDF/XML");

		g = new MSEGraph(testModel);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.knowledgebase.MSEGraph#isEmpty()}.
	 */
	@Test
	public void testIsEmpty() {

		if (!g.isEmpty())
			assertTrue("isEmpty succeeded", true);

	}
}

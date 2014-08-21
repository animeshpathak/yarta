package fr.inria.arles.yarta.middleware.communication;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class YCommManagerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetUIDFromURI() {
		String URI = "ibiurl://J2SE:animesh@inria.fr/yarta/CommunicationService/";
		String UID = "animesh@inria.fr";

		assertEquals("GetUIDFromURI Fails!", UID, IbicoopConnection.getUIDFromURI(URI));
	}

}

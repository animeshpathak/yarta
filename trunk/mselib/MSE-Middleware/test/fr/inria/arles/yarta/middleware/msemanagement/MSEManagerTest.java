/**
 * 
 */
package fr.inria.arles.yarta.middleware.msemanagement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;

/**
 * @author F13
 * 
 */
public class MSEManagerTest {

	private MSEManager myMSEManager;
	private static final String baseOntologyFilePath = "test/mse-1.1.rdf";
	private static final String otherId = "animesh@yarta.inria.fr";
	static String partnerURI = "ibiurl://J2SE:" + otherId
			+ "/yarta/CommunicationService/";
	private static final String myUserID = "valerie@yarta.inria.fr";
	private static final String policyFilePath = "test/policies-demo2";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		myMSEManager = new MSEManager();
		UIHandler u = new UIHandler();
		myMSEManager.initialize(baseOntologyFilePath, policyFilePath, u, null);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		myMSEManager.shutDown();
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.middleware.msemanagement.MSEManager#writeKnowledge(java.lang.String)}
	 * .
	 */
	@Test
	public void testWriteKnowledge() {
		String testoutputfilepath = "test/writeknowledgetestoutputfile.rdf";
		myMSEManager.writeKnowledge(testoutputfilepath);
		// fail("Not yet implemented");
	}

}

class UIHandler implements MSEApplication {

	private static final String SAMPLE_APP_UI_TAG = "Yarta-SampleApp-UI";
	YLogger ylogger = YLoggerFactory.getLogger();

	@Override
	public void handleNotification(String notification) {
		// TODO Auto-generated method stub
		ylogger.d(SAMPLE_APP_UI_TAG, notification);

	}

	@Override
	public boolean handleQuery(String query) {
		// TODO Auto-generated method stub
		ylogger.d(SAMPLE_APP_UI_TAG, "Answering \"Yes\" to the query" + query);
		return true;
	}

	@Override
	public void handleKBReady(String userId) {
		ylogger.d(SAMPLE_APP_UI_TAG, "kb ready! userid = " + userId);
	}

	public String getAppId() {
		return "fr.inria.arles.text";
	}
}

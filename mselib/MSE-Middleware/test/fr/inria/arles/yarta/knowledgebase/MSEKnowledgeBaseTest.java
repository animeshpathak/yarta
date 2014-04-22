package fr.inria.arles.yarta.knowledgebase;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.inria.arles.yarta.knowledgebase.interfaces.Graph;
import fr.inria.arles.yarta.knowledgebase.interfaces.KnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.ThinKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;
import fr.inria.arles.yarta.resources.Event;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.Topic;

public class MSEKnowledgeBaseTest {

	KnowledgeBase knowledgeBase;

	private static final String baseOntologyFilePath = "test/mse-1.2.rdf";
	private static final String userID = "valerie@yarta.inria.fr";
	private static final String policyFilePath = "test/policies";
	private static final String mse_namespace = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#";
	private static final String requestorUnknown = "some@dude.com";
	private static final String requestorAllowed = "allowed@inria.fr";

	private String eventId;
	private Node eventKBNode;

	@Before
	public void setUp() throws Exception {
		knowledgeBase = new MSEKnowledgeBase();
		knowledgeBase.initialize(baseOntologyFilePath, mse_namespace,
				policyFilePath, userID);

		eventId = mse_namespace + UUID.randomUUID().toString();
		eventKBNode = knowledgeBase.addResource(eventId, Event.typeURI, userID);
	}

	@After
	public void tearDown() throws Exception {
		knowledgeBase.uninitialize();
	}

	@Test
	public void testRemoveTriple() throws Exception {
		Node eventTitleProperty = knowledgeBase
				.getResourceByURINoPolicies(Event.PROPERTY_TITLE_URI);

		Node eventTitleObject = knowledgeBase.addLiteral((String) "test1",
				ThinKnowledgeBase.XSD_STRING, userID);

		Triple triple = knowledgeBase.getTriple(eventKBNode,
				eventTitleProperty, eventTitleObject, userID);
		assertTrue(triple == null);

		knowledgeBase.addTriple(eventKBNode, eventTitleProperty,
				eventTitleObject, userID);

		triple = knowledgeBase.getTriple(eventKBNode, eventTitleProperty,
				eventTitleObject, userID);
		assertTrue(triple != null);

		knowledgeBase.removeTriple(eventKBNode, eventTitleProperty,
				eventTitleObject, userID);

		triple = knowledgeBase.getTriple(eventKBNode, eventTitleProperty,
				eventTitleObject, userID);
		assertTrue(triple == null);
	}

	@Test
	public void testAddTripleNotAllowed() throws Exception {
		Node eventDescriptionProperty = knowledgeBase
				.getResourceByURINoPolicies(Event.PROPERTY_DESCRIPTION_URI);
		Node eventDescriptionObject = knowledgeBase.addLiteral(
				(String) "description of the event",
				ThinKnowledgeBase.XSD_STRING, userID);

		Triple triple = knowledgeBase.getTriple(eventKBNode,
				eventDescriptionProperty, eventDescriptionObject, userID);
		assertTrue(triple == null);

		boolean bException = false;

		try {
			knowledgeBase.addTriple(eventKBNode, eventDescriptionProperty,
					eventDescriptionObject, "someone@else");
		} catch (Exception ex) {
			bException = true;
		}

		assertTrue(bException);

		triple = knowledgeBase.getTriple(eventKBNode, eventDescriptionProperty,
				eventDescriptionObject, userID);
		assertTrue(triple == null);
	}

	@Test
	public void testQueryAsUnknown() throws Exception {
		Graph g = knowledgeBase.getKB(requestorUnknown);
		g = knowledgeBase.getKB(userID);
		ArrayList<Triple> triples = g.getTriples();

		for (Triple triple : triples) {
			if (triple
					.getSubject()
					.getName()
					.contains(
							"TemporaryUserUriSHOULD_NOT_BE_IN_KB_AFTER_POLICY_CHECK!!")) {
				assertTrue(false);
			}
		}
	}

	@Test
	public void testAddAsUnknown() throws Exception {

		try {
			String placeId = mse_namespace + UUID.randomUUID().toString();
			knowledgeBase.addResource(placeId, Place.typeURI, requestorUnknown);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Graph g = knowledgeBase.getKB(userID);
		ArrayList<Triple> triples = g.getTriples();
		for (Triple triple : triples) {
			if (triple
					.getSubject()
					.getName()
					.contains(
							"TemporaryUserUriSHOULD_NOT_BE_IN_KB_AFTER_POLICY_CHECK!!")) {
				assertTrue(false);
			}
		}
	}

	@Test
	public void testRemoveAsUnknown() throws Exception {
		Graph g = knowledgeBase.getKB(userID);
		ArrayList<Triple> triples = g.getTriples();

		Triple triple = triples.get(0);

		try {
			knowledgeBase.removeTriple(triple.getSubject(),
					triple.getProperty(), triple.getObject(), requestorUnknown);
		} catch (AccessControlException ex) {
			// it's normal
		}

		g = knowledgeBase.getKB(userID);
		triples = g.getTriples();
		for (Triple t : triples) {
			if (t.getSubject()
					.getName()
					.contains(
							"TemporaryUserUriSHOULD_NOT_BE_IN_KB_AFTER_POLICY_CHECK!!")) {
				assertTrue(false);
			}
		}
	}

	@Test
	public void testAddAsUnknownWithApostrophe() throws Exception {
		String brokenTitle = "topic's \" title";
		
		String topicId = mse_namespace + UUID.randomUUID().toString();
		Node topicNode = knowledgeBase.addResource(topicId, Topic.typeURI,
				userID);
		assertTrue(topicNode != null);

		Node topicTitlePropertyNode = knowledgeBase
				.getResourceByURINoPolicies(Topic.PROPERTY_TITLE_URI);
		assertTrue(topicTitlePropertyNode != null);

		Node topicTitleLiteralNode = knowledgeBase.addLiteral(brokenTitle,
				ThinKnowledgeBase.XSD_STRING, userID);
		assertTrue(topicTitleLiteralNode != null);

		Triple addedTriple = knowledgeBase.addTriple(topicNode, topicTitlePropertyNode,
				topicTitleLiteralNode, requestorAllowed);
		
		assertTrue(addedTriple != null);
		
		Triple resultedTriple = knowledgeBase.getTriple(topicNode, topicTitlePropertyNode, topicTitleLiteralNode, requestorAllowed);
		
		assertTrue(resultedTriple != null);
	}
}

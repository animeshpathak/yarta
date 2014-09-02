package fr.inria.arles.yarta.middleware.msemanagement;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.MSEKnowledgeBaseUtils;
import fr.inria.arles.yarta.knowledgebase.interfaces.ThinKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Conversation;
import fr.inria.arles.yarta.resources.Event;
import fr.inria.arles.yarta.resources.Group;
import fr.inria.arles.yarta.resources.Message;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.yarta.resources.Topic;
import fr.inria.arles.yarta.resources.YartaResource;

public class ThinStorageAccessManagerTest {

	ThinStorageAccessManager s = new ThinStorageAccessManager();
	String ownerID = "animesh@yarta.inria.fr";
	String baseOntologyFilePath = "test/mse-1.2.rdf";
	String confOntologyFilePath = "test/mse-conf.rdf";
	// String baseOntologyFilePath = "test/mse-1.1-nc.rdf";
	String baseOntologyNameSpace = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#";
	// String policyFilePath = "test/policies-all";
	String policyFilePath = "test/policies-demo1";
	static final String addingFilePath = "test/demo-conference-ap.rdf";

	ThinKnowledgeBase k;

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

	@Test
	public void testGetMe() {

		// after init, get me and get person with UID same as me, see if they
		// are the same person or not.

		try {
			Person checkme = s.getMe();

			Person checkme_again = s.getPersonByUserId(ownerID);

			assertTrue("Checking person object equality",
					checkme.equals(checkme_again));
			assertEquals("Checking userID property of getme()", ownerID,
					checkme.getUserId());
			assertEquals("Checking userID property of getPersonByUserId",
					ownerID, checkme_again.getUserId());

		} catch (KBException e) {
			fail("got KBException" + e.getMessage());
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager#addPerson(fr.inria.arles.yarta.resources.Person)}
	 * .
	 * 
	 * @throws KBException
	 */
	@Test
	public void testAddPerson() {

		try {

			String p1_userId = "checking@gmail";
			String p1_email = "test2@example.com";
			String p1_fname = "Tommy";
			String p1_lname = "Trojan";
			String p1_homepage = "http://www.yarta.inria.fr";
			String p1_name = "Tommy Trojan";

			Person p = s.createPerson(p1_userId);
			p.setEmail(p1_email);
			p.setFirstName(p1_fname);
			p.setLastName(p1_lname);
			p.setHomepage(p1_homepage);
			p.setName(p1_name);

			assertEquals("User ID Check", p1_userId, p.getUserId());
			assertEquals("Email Check", p1_email, p.getEmail());
			assertEquals("First Name Check", p1_fname, p.getFirstName());
			assertEquals("Last Name Check", p1_lname, p.getLastName());
			assertEquals("Homepage Check", p1_homepage, p.getHomepage());
			assertEquals("Name Check", p1_name, p.getName());

			// Graph g = k.getKB(ownerID);
			// for(Triple t: g.getTriples()){
			// System.out.println(t.getSubject().getType());
			// System.out.println(t.toString());
			// }

			// boolean status = false;
			// try {
			// status = s.addPerson(p);
			// } catch (KBException e) {
			// e.printStackTrace();
			// }
			// assertTrue("At least the addPerson succeeds", status);

			Person q = s.getPersonByUserId(p1_userId);

			assertEquals("Email Not Equal!", q.getEmail(), p.getEmail());
			assertEquals("First Names Not Equal!", q.getFirstName(),
					p.getFirstName());
			assertEquals("Last Name Not Equal!", q.getLastName(),
					p.getLastName());
			assertEquals("Homepage Not Equal!", q.getHomepage(),
					p.getHomepage());
			assertEquals("Name Not Equal!", q.getName(), p.getName());
			assertEquals("Unique ID Not Equal!", q.getUniqueId(),
					p.getUniqueId());
		} catch (KBException e) {
			e.printStackTrace();
		}

		// fail("more tests to be written");
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager#addPerson(fr.inria.arles.yarta.resources.Person)}
	 * . Testing if adding works with null parameters too.
	 * 
	 * @throws KBException
	 */
	@Test
	public void testAddPersonWithNullHomepage() {

		Person p = s.createPerson("test2@example.com");
		p.setEmail("test2@example.com");
		p.setFirstName("Tommy");
		p.setLastName("Trojan");
		// not setting a homepage. Leaving it null
		// p.setHomepage("http://www.yarta.inria.fr");
		p.setName("Tommy Trojan");

		// boolean status = false;
		// try {
		// status = s.addPerson(p);
		// } catch (KBException e) {
		// e.printStackTrace();
		// }
		// assertTrue("At least the addPerson succeeds", status);

		Person q = null;
		try {
			q = s.getPersonByUserId("test2@example.com");
			assertEquals("Email Not Equal!", q.getEmail(), p.getEmail());
			assertEquals("First Names Not Equal!", q.getFirstName(),
					p.getFirstName());
			assertEquals("Last Name Not Equal!", q.getLastName(),
					p.getLastName());
			assertEquals("Homepage Not Equal!", q.getHomepage(),
					p.getHomepage());
			assertEquals("Name Not Equal!", q.getName(), p.getName());
			assertEquals("Unique ID Not Equal!", q.getUniqueId(),
					p.getUniqueId());
		} catch (KBException e) {
			e.printStackTrace();
			fail("getPersonByUserID Failed");
		}

		// fail("more tests to be written");
	}

	/**
	 * Test method for {@link Person.addknows()}.
	 */
	@Test
	public void testAddKnows() {

		Person p1 = s.createPerson("test2@example.com");

		Person p2 = s.createPerson("test3@example.com");

		p1.addKnows(p2);

		Set<Agent> knowsList = p1.getKnows();

		assertEquals("Knows number wrong (" + knowsList.size() + ")", 1,
				knowsList.size());

		Iterator<Agent> iterator = knowsList.iterator();
		Agent agent = iterator.next();
		assertEquals("Wrong Person known", p2.getUniqueId(),
				agent.getUniqueId());

	}

	/**
	 * Tests if the getKnows and deleteKnows works
	 */
	@Test
	public void testGetKnowsAndDeleteKnows() {

		Person p1 = s.createPerson("test2@example.com");

		Person p2 = s.createPerson("test3@example.com");

		Person p3 = s.createPerson("test4@example.com");

		Person me;
		try {
			me = s.getMe();
		} catch (KBException e) {
			e.printStackTrace();
			fail("got exception in getMe()");
			return;
		}

		p1.addKnows(p2);
		p1.addKnows(p3);
		p1.addKnows(me);
		me.addKnows(p1);

		try {
			Person p5 = s.getPersonByUserId("test2@example.com");
			p5.getFirstName();
		} catch (KBException e) {
			e.printStackTrace();
		}

		// for debugging. print the KB
		// MSEKnowledgeBaseUtils.writeDataOnStream((OutputStream)System.out,
		// (MSEKnowledgeBase) k);

		Set<Agent> tempSet = p1.getKnows();
		assertTrue("P1 has " + tempSet.size() + " friends",
				(tempSet.size() == 3));

		tempSet = p2.getKnows();
		assertTrue("P2 has " + tempSet.size() + " friends",
				(tempSet.size() == 0));

		tempSet = me.getKnows();
		assertTrue("I have " + tempSet.size() + " friends",
				(tempSet.size() == 1));

		assertFalse("Why do I suddenly know p2??!", me.deleteKnows(p2));

		assertTrue("Why can p1 not cease to know me??!", p1.deleteKnows(me));
		tempSet = p1.getKnows();
		assertTrue("P1 has " + tempSet.size()
				+ " friends even after deletion?!", (tempSet.size() == 2));

		for (Agent agent : tempSet) {
			assertTrue("p's friend is a Person", agent instanceof Person);
		}
	}

	/**
	 * Test method for
	 * {@link fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager#getPersonByUserId(String)}
	 * .
	 */
	@Test
	public void testGetPersonByUserId() {
		try {
			String testUserId = "test2@example.com";
			Person p = s.createPerson(testUserId);

			Person q = s.getPersonByUserId(testUserId);

			assertEquals("Email Not Equal!", q.getEmail(), p.getEmail());
			assertEquals("First Names Not Equal!", q.getFirstName(),
					p.getFirstName());
			assertEquals("Last Name Not Equal!", q.getLastName(),
					p.getLastName());
			assertEquals("Homepage Not Equal!", q.getHomepage(),
					p.getHomepage());
			assertEquals("Name Not Equal!", q.getName(), p.getName());
			assertEquals("Unique ID Not Equal!", q.getUniqueId(),
					p.getUniqueId());

			assertNull("Why am I getting a non-null person for invalid UID?",
					s.getPersonByUserId("invalid-unique-id"));

		} catch (KBException kbe) {
			kbe.printStackTrace();
			fail("testGetPersonByUniqueId Failed with a KB Exception");
		}
	}

	@Test
	public void testAddDeleteGetIsMemberOf() {
		Person p1 = s.createPerson("test2@example.com");

		Group g1 = s.createGroup();

		p1.addIsMemberOf(g1);

		Set<Group> tempGroupList = p1.getIsMemberOf();

		assertEquals("number of groups p1 isMemberOf (" + tempGroupList.size()
				+ ")", 1, tempGroupList.size());

		Person me;
		try {
			me = s.getMe();
		} catch (KBException e) {
			e.printStackTrace();
			fail("got exception in getMe()");
			return;
		}

		me.addIsMemberOf(g1);
		Group g2 = s.createGroup();
		me.addIsMemberOf(g2);
		tempGroupList = me.getIsMemberOf();

		assertEquals(
				"Number of groups I am isMemberOf (" + tempGroupList.size()
						+ ")", 2, tempGroupList.size());

		assertFalse("Why does p1 suddenly belong to g22??!",
				p1.deleteIsMemberOf(g2));

		assertTrue("Why can I not not cease to be a member of g1??!",
				me.deleteIsMemberOf(g1));
		tempGroupList = me.getIsMemberOf();
		assertTrue("I am member of " + tempGroupList.size()
				+ " groups even after deletion?!", (tempGroupList.size() == 1));

	}

	@Test
	public void testEquals() {
		String uid = "test2@example.com";
		Person p1 = s.createPerson(uid);
		try {
			Person p2 = s.getPersonByUserId(uid);
			assertEquals("Why are uniqueIds not equal! :-(", p1.getUniqueId(),
					p2.getUniqueId());
			assertEquals("Why are uniqueIds not equal! :-(", p1.getUserId(),
					p2.getUserId());
			assertTrue("Why not equal! :-(", p1.equals(p2));

		} catch (KBException e) {
			e.printStackTrace();
			fail("got exception in getPersonByUserId");
			return;
		}

	}

	@Test
	/** Tests if two calls to createPerson() leads to different entities or not*/
	public void testCreatePerson() {
		String uid = "test2@example.com";
		Person p1 = s.createPerson(uid);
		Person p2 = s.createPerson(uid);
		assertNotSame("Why are uniqueIds equal! :-(", p1.getUniqueId(),
				p2.getUniqueId());
		assertEquals("Why are userIds not equal! :-(", p1.getUserId(),
				p2.getUserId());
		assertFalse("Why equal! :-(", p1.equals(p2));

	}

	@Test
	public void testGetAllPersons() {
		try {
			Person me = s.getMe();

			int initialSize = s.getAllPersons().size();

			Person p1 = s.createPerson("test1");
			Person p2 = s.createPerson("test2");

			Set<Person> allPersons = s.getAllPersons();

			assertEquals("Why not equal to 3 Persons in the KB?!",
					initialSize + 2, allPersons.size());
			assertTrue("Why am I not in the list?", allPersons.contains(me));
			assertTrue("Why is p1 not in the list?", allPersons.contains(p1));
			assertTrue("Why is p2 not in the list?", allPersons.contains(p2));
		} catch (KBException e) {
			e.printStackTrace();
			fail("KBException in getMe()");
		}
	}

	@Test
	public void testTopicStuff() {
		Topic t1 = s.createTopic();
		String tag1 = "Tag1";
		t1.setTitle(tag1);
		assertEquals("Testing tag for topic 1", tag1, t1.getTitle());

		Topic t2 = s.createTopic();
		String tag2 = "Tag2";
		t2.setTitle(tag2);
		assertEquals("Testing tag for topic 2", tag2, t2.getTitle());

		Content c = s.createContent();
		String title = "myContentTitle";
		c.setTitle(title);
		assertEquals("Checking content title", title, c.getTitle());

		// c.addIsTagged(t1);
		// List<Topic> topicListForC = c.getIsTagged();
		// assertEquals("Checking number of tags for content c", 1,
		// topicListForC.size());
		// assertTrue("Checking tag name for the single tag for c",
		// t1.equals(topicListForC.get(0)));

	}

	@Test
	public void testGetAllAgents() {
		try {
			Person me = s.getMe();

			int initialSize = s.getAllAgents().size();

			Person p1 = s.createPerson("test1");
			Person p2 = s.createPerson("test2");

			Set<Agent> allPersons = s.getAllAgents();

			assertEquals("Why not equal to 3 Persons in the KB?!",
					initialSize + 2, allPersons.size());
			assertTrue("Why am I not in the list?", allPersons.contains(me));
			assertTrue("Why is p1 not in the list?", allPersons.contains(p1));
			assertTrue("Why is p2 not in the list?", allPersons.contains(p2));
		} catch (KBException e) {
			e.printStackTrace();
			fail("KBException in getMe()");
		}
	}

	@Test
	public void testGetKnows_inverse() {
		Agent a1 = s.createAgent();
		Agent a2 = s.createAgent();
		Agent a3 = s.createAgent();

		a1.addKnows(a2);
		a1.addKnows(a3);

		a2.addKnows(a3);

		Set<Agent> knowsList = a1.getKnows();
		assertEquals("Failed in getKnows", 2, knowsList.size());
		assertTrue("Why is a2 not in the list?", knowsList.contains(a2));
		assertTrue("Why is a3 not in the list?", knowsList.contains(a3));

		Set<Agent> knownByList = a3.getKnows_inverse();
		assertEquals("Failed in getKnows_inverse", 2, knownByList.size());
		assertTrue("Why is a1 not in the list?", knownByList.contains(a1));
		assertTrue("Why is a2 not in the list?", knownByList.contains(a2));
	}

	@Test
	public void testGetIsMemberOf_inverse() {
		Group g1 = s.createGroup();
		Agent a1 = s.createAgent();
		Agent a2 = s.createAgent();

		a1.addIsMemberOf(g1);
		a2.addIsMemberOf(g1);

		Set<Agent> g1MemberList = g1.getIsMemberOf_inverse();
		assertEquals("Failed in getKnows_inverse", 2, g1MemberList.size());
		assertTrue("Why is a1 not in the list?", g1MemberList.contains(a1));
		assertTrue("Why is a2 not in the list?", g1MemberList.contains(a2));
	}

	@Test
	public void testGetIsTagged_inverse() {

		Content c1 = s.createContent();
		Content c2 = s.createContent();

		Topic t1 = s.createTopic();

		c1.addIsTagged(t1);
		c2.addIsTagged(t1);

		Set<Resource> contentList = t1.getIsTagged_inverse();
		assertEquals("Failed in getIsTagged_inverse", 2, contentList.size());

		for (Resource resource : contentList) {
			assertTrue("Why is this element not a Content?",
					resource instanceof Content);
		}

		assertTrue("Why is c1 not in the list?", contentList.contains(c1));
		assertTrue("Why is c2 not in the list?", contentList.contains(c2));
	}

	@Test
	public void testEventContentInference() {
		int eventsBefore = s.getAllEvents().size();

		Content content = s.createContent();
		content.setTitle("Quadrophobia");

		int eventsAfter = s.getAllEvents().size();

		assertTrue(eventsBefore == eventsAfter);
	}

	@Test
	public void testPlaceAgentsInference() {
		int placesBefore = s.getAllPlaces().size();

		Agent agent = s.createAgent();
		agent.setName("Kamadeva");

		int placesAfter = s.getAllPlaces().size();

		assertTrue(placesBefore == placesAfter);
	}

	@Test
	public void testGetResourceByURI() {
		Agent agent = s.createAgent();
		Agent newAgent = (Agent) s.getResourceByURI(agent.getUniqueId());

		assertTrue(agent.equals(newAgent));
	}

	@Test
	public void testIsLocatedInferrence() {
		int agentsBefore = s.getAllAgents().size();

		s.createEvent().addIsLocated(s.createPlace());

		int agentsAfter = s.getAllAgents().size();

		assertEquals(agentsBefore, agentsAfter);
	}

	@Test
	public void testSetEmptyDatatypeProperty() {
		String strName = "George";
		Person p = s.createPerson("test3@example.com");
		p.setFirstName(strName);

		for (int i = 0; i < 10; i++) {
			try {
				p.setFirstName("");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		p.setFirstName(strName);

		MSEKnowledgeBaseUtils.printMSEKnowledgeBase((MSEKnowledgeBase) k,
				"test.xml", "");
		assertEquals(strName, p.getFirstName());
	}

	@Test
	public void testParticipatesTo() {
		Person p = s.createPerson("test4@example.com");
		p.setFirstName("Tony Montana");

		Event event = s.createEvent();
		event.setTitle("One time only.");

		p.addIsAttending(event);

		Set<Event> events = p.getIsAttending();

		assertTrue(events.contains(event));
	}

	@Test
	public void testSimpleMessage() {
		Long timestamp = System.currentTimeMillis();

		Person me = null;
		try {
			me = s.getMe();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Message m = s.createMessage();
		m.setTime(timestamp);
		me.addCreator(m);

		assertEquals(timestamp, m.getTime());

		Set<Message> messages = s.getAllMessages();
		boolean found = false;

		for (Message message : messages) {
			if (message.equals(m)) {
				found = true;
				break;
			}
		}

		MSEKnowledgeBaseUtils.printMSEKnowledgeBase((MSEKnowledgeBase) k,
				"/home/grosca/output2.xml", null);

		assertTrue(found);
	}

	@Test
	public void testSimpleConversation() {
		Conversation c = s.createConversation();

		Person me = null;
		try {
			me = s.getMe();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		me.addParticipatesTo(c);

		assertTrue(c.getParticipatesTo_inverse().contains(me));

		Set<Conversation> conversations = s.getAllConversations();

		boolean found = false;

		for (Conversation conversation : conversations) {
			if (conversation.equals(c)) {
				assertTrue(conversation.getParticipatesTo_inverse()
						.contains(me));
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	@Test
	public void testConversationMessage() {
		Conversation c = s.createConversation();

		Set<Person> ps = s.getAllPersons();

		for (Person p : ps) {
			p.addParticipatesTo(c);
		}

		Person me = null;
		try {
			me = s.getMe();
			me.setName("me");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Message m = s.createMessage();
		me.addCreator(m);
		m.setTime(System.currentTimeMillis());
		m.setTitle("This is my message");
		c.addContains(m);

		m = s.createMessage();
		me.addCreator(m);
		m.setTime(System.currentTimeMillis());
		m.setTitle("This is my second message");
		c.addContains(m);

		assertEquals(2, c.getContains().size());
		c.deleteContains(m);
		assertEquals(1, c.getContains().size());

		Set<Conversation> conversations = s.getAllConversations();

		MSEKnowledgeBaseUtils.printMSEKnowledgeBase((MSEKnowledgeBase) k,
				"/home/grosca/Desktop/output.xml", "RDF/XML");
		for (Conversation conversation : conversations) {
			Set<Agent> agents = conversation.getParticipatesTo_inverse();
			if (agents.contains(me)) {
				for (Message message : conversation.getContains()) {
					Agent creator = null;
					for (Agent a : message.getCreator_inverse()) {
						creator = a;
					}
					System.out.println(creator.getName() + " said "
							+ message.getTitle());
				}
			}
		}
	}
}

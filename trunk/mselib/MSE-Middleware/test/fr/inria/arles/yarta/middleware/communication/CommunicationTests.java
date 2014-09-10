package fr.inria.arles.yarta.middleware.communication;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CommunicationTests implements Receiver {

	private static final String UID = "test";
	private boolean received;
	private Message message;
	private String from;

	private CommPollConnection connection;

	@Before
	public void setUp() throws Exception {
		connection = new CommPollConnection(UID);
		connection.setReceiver(this);
		connection.init(null);
	}

	@After
	public void tearDown() throws Exception {
		connection.uninit();
	}

	@Test
	public void testSendMessage() {
		int type = 123;
		byte[] data = "Hello, world!".getBytes();
		String appId = "fr.inria.arles.yarta.test";

		connection.postMessage(UID, new Message(type, data, appId));
		Message message = CommClient.get(UID);

		assertArrayEquals(data, message.getData());
		assertEquals(type, message.getType());
		assertEquals(appId, message.getAppId());
	}

	@Test
	public void testReceiveMessage() {
		int type = 123;
		byte[] data = "Hello, world!".getBytes();
		String appId = "fr.inria.arles.yarta.test";

		connection.postMessage(UID, new Message(type, data, appId));

		received = false;
		try {
			for (int i = 0; i < 20 && !received; i++) {
				Thread.sleep(1000);
			}
		} catch (Exception ex) {
		}

		assertTrue(received);
		assertEquals(UID, from);
		assertArrayEquals(data, message.getData());

		assertEquals(type, message.getType());
		assertEquals(appId, message.getAppId());
	}

	@Override
	public boolean handleMessage(String id, Message message) {
		this.from = id;
		this.message = message;
		this.received = true;
		return false;
	}

	@Override
	public Message handleRequest(String id, Message message) {
		return null;
	}
}

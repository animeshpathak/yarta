package fr.inria.arles.yarta.middleware.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.ibicoop.communication.common.CommunicationConstants;
import org.ibicoop.communication.common.CommunicationManager;
import org.ibicoop.communication.common.CommunicationMode;
import org.ibicoop.communication.common.CommunicationOptions;
import org.ibicoop.communication.common.IbiReceiver;
import org.ibicoop.communication.common.IbiSender;
import org.ibicoop.communication.common.ReceiverListener;
import org.ibicoop.communication.common.SenderListener;
import org.ibicoop.exceptions.ConnectionFailedException;
import org.ibicoop.exceptions.MalformedIbiurlException;
import org.ibicoop.init.IbicoopInit;
import org.ibicoop.sdp.naming.IBIURL;
import org.ibicoop.utils.DataBuffer;

import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;

public class Connection implements ReceiverListener, SenderListener {

	public Connection(String id) {
		this.id = id;
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public void init(Object context) {
		this.context = context;

		boolean started = true;

		synchronized (ibicoopRefCount) {
			if (ibicoopRefCount == 0) {
				Object[] initObjects = null;

				if (context != null) {
					initObjects = new Object[] { context,
							"example@example.com", "password", "Yarta" };
				} else {
					initObjects = new Object[] { "example@example.com",
							"password" };
				}
				try {
					started = IbicoopInit.getInstance().start(initObjects);
				} catch (Exception ex) {
					started = false;
				}
			}

			if (started) {
				ibicoopRefCount++;
			} else {
				return;
			}
		}

		comMgr = IbicoopInit.getInstance().getCommunicationManager();

		try {
			server = new IBIURL("ibiurl://J2SE:" + id + "/SomeName/YServiceId/");
		} catch (MalformedIbiurlException ex) {
			ex.printStackTrace();
			error("MalformedIbiurlException ex " + ex.getMessage());
		}

		options.setForceNewTunnel(true);
		options.setCommunicationMode(new CommunicationMode(
				CommunicationConstants.MODE_PROXY));

		try {
			ibiReceiver = comMgr.createReceiver(server, options, IbicoopInit
					.getInstance().getProxyURL(), this);
			ibiReceiver.start();
		} catch (ConnectionFailedException ex) {
			ex.printStackTrace();
			error("ConnectionFailedException ex " + ex.getMessage());
		}
	}

	public void uninit() {
		Iterator<Entry<String, IbiSender>> iterator = senders.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			IbiSender sender = iterator.next().getValue();
			sender.stop();
		}

		if (ibiReceiver != null) {
			ibiReceiver.stop();
		}

		synchronized (ibicoopRefCount) {
			ibicoopRefCount--;

			if (ibicoopRefCount == 0) {
				IbicoopInit.getInstance().terminate();
			}
		}
	}

	public int postMessage(String id, Message message) {
		IbiSender sender = getSender(id);

		if (sender != null) {
			int result = sender.send(serialize(message));
			senders.remove(id);
			message = null;
			return result;
		} else {
			error("message could not be sent to %s", id);
		}
		return -1;
	}

	public Message sendMessage(String id, Message message) {
		IbiSender sender = getSender(id);
		Message result = null;
		try {
			result = (Message) deserialize(sender
					.sendRequestResponse(serialize(message)));
		} catch (ConnectionFailedException ex) {
			error("sendMessage ex: %s", ex.getMessage());
		}
		return result;
	}

	protected IbiSender getSender(String otherId) {

		synchronized (ibicoopRefCount) {
			if (ibicoopRefCount == 0) {
				init(context);
			}
		}
		if (senders.containsKey(otherId)) {
			return senders.get(otherId);
		}

		try {
			IBIURL otherServer = new IBIURL("ibiurl://J2SE:" + otherId
					+ "/SomeName/YServiceId/");

			IBIURL monClient = new IBIURL("ibiurl://J2SE:" + id
					+ "/SomeName/YClientId/");

			IbiSender sender = comMgr.createSender(monClient, otherServer,
					options, this);

			if (sender != null) {
				senders.put(otherId, sender);
			}
			return sender;
		} catch (ConnectionFailedException ex) {
			error("getSender error: %s", ex.getMessage());
			ex.printStackTrace();
		} catch (MalformedIbiurlException ex) {
			error("getSender error: %s", ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

	protected void error(String format, Object... args) {
		logger.e("CONNECTION", String.format(format, args));
	}

	private YLogger logger = YLoggerFactory.getLogger();

	protected IBIURL server;
	protected IbiReceiver ibiReceiver;

	protected CommunicationOptions options = new CommunicationOptions();
	protected Receiver receiver;
	protected String id;
	protected CommunicationManager comMgr;
	protected Object context;
	protected Map<String, IbiSender> senders = new HashMap<String, IbiSender>();

	@Override
	public boolean acceptSenderConnection(IbiReceiver arg0, String arg1) {
		return true;
	}

	@Override
	public void connectionStatus(IbiReceiver arg0, int arg1) {
	}

	@Override
	public void receivedMessageData(IbiReceiver arg0, String arg1, int arg2,
			DataBuffer arg3) {
		Message message = (Message) deserialize(arg3.internalData);

		if (receiver != null) {
			receiver.handleMessage(getUIDFromURI(arg1), message);
		}
	}

	@Override
	public byte[] receivedMessageRequest(IbiReceiver arg0, String arg1,
			int arg2, int arg3, byte[] arg4) {
		Message message = (Message) deserialize(arg4);

		if (receiver != null) {
			Message response = receiver.handleRequest(getUIDFromURI(arg1),
					message);
			return serialize(response);
		}
		return null;
	}

	@Override
	public void connectionStatus(IbiSender arg0, int arg1, String arg2) {
	}

	@Override
	public void receivedMessageResponse(IbiSender arg0, String arg1, int arg2,
			byte[] arg3) {
	}

	protected byte[] serialize(Object obj) {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			ObjectOutputStream o = new ObjectOutputStream(b);
			o.writeObject(obj);
			return b.toByteArray();
		} catch (IOException ex) {
			error("serialize exception: %s", ex.getMessage());
		}
		return null;
	}

	protected Object deserialize(byte[] bytes) {
		try {
			ByteArrayInputStream b = new ByteArrayInputStream(bytes);
			ObjectInputStream o = new ObjectInputStream(b);
			return o.readObject();
		} catch (Exception ex) {
			error("deserialize ex: %s", ex.getMessage());
		}
		return null;
	}

	public static String getUIDFromURI(String uri) {
		return uri.split(":")[2].split("/")[0];
	}

	protected static Integer ibicoopRefCount = new Integer(0);
}

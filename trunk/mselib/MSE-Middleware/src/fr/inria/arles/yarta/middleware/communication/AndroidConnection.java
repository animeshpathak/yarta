package fr.inria.arles.yarta.middleware.communication;

import java.util.Iterator;
import java.util.Map.Entry;

import org.ibicoop.communication.common.CommunicationConstants;
import org.ibicoop.communication.common.CommunicationMode;
import org.ibicoop.communication.common.IbiSender;
import org.ibicoop.exceptions.ConnectionFailedException;
import org.ibicoop.exceptions.MalformedIbiurlException;
import org.ibicoop.init.IbicoopInit;
import org.ibicoop.sdp.naming.IBIURL;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import fr.inria.arles.yarta.middleware.communication.Connection;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.Receiver;

public class AndroidConnection extends Connection {

	private Context context;
	private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			onConnectionChanged(intent);
		}
	};

	public AndroidConnection(String id) {
		super(id);
	}

	@Override
	public void init(Object context) {
		this.context = (Context) context;

		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		this.context.registerReceiver(networkStateReceiver, filter);
		ibicoopRefCount = 1;
	}

	@Override
	public void uninit() {
		ibicoopRefCount = 0;

		this.context.unregisterReceiver(networkStateReceiver);
		super.uninit();
	}

	/**
	 * Should be called from a non UI thread.
	 */

	private synchronized void createReceiver() {
		// register receiver
		Object[] initObjects = null;
		initObjects = new Object[] { context, "example@example.com",
				"password", "Yarta" };
		IbicoopInit.getInstance().start(initObjects);

		comMgr = IbicoopInit.getInstance().getCommunicationManager();

		try {
			server = new IBIURL("ibiurl://J2SE:" + id + "/SomeName/YServiceId/");
		} catch (MalformedIbiurlException ex) {
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

	private void onConnectionChanged(Intent intent) {
		if (isNetworkConnected()) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					createReceiver();
				}
			}).start();
		} else {
			Iterator<Entry<String, IbiSender>> iterator = senders.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				IbiSender sender = iterator.next().getValue();
				sender.stop();
			}

			if (ibiReceiver != null) {
				ibiReceiver.stop();
			}

			IbicoopInit.getInstance().terminate();
		}
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	@Override
	public int postMessage(String id, Message message) {
		int result = -1;
		if (isNetworkConnected()) {
			IbiSender sender = getSender(id);
			if (sender != null) {
				result = sender.send(serialize(message));
			}
			senders.remove(id);
		}
		return result;
	}

	@Override
	public Message sendMessage(String id, Message message) {
		Message result = null;

		if (isNetworkConnected()) {
			IbiSender sender = getSender(id);

			if (sender != null) {
				try {
					result = (Message) deserialize(sender
							.sendRequestResponse(serialize(message)));
				} catch (ConnectionFailedException ex) {
					error("sendMessage ex: %s", ex.getMessage());
				}
			}
			senders.remove(id);
		}
		return result;
	}
}

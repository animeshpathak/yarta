package fr.inria.arles.yarta.middleware.communication;

import com.google.android.gcm.GCMRegistrar;

import fr.inria.arles.iris.GCMIntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Android Connection Class which relies on the Google Push Messaging.
 */
public class CommPushConnection implements Connection {

	public CommPushConnection(String userId) {
		CommClient.userId = userId;
	}

	@Override
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	@Override
	public void init(Object context) {
		this.context = (Context) context;
		this.context.registerReceiver(networkStateReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
	}

	@Override
	public void uninit() {
		context.unregisterReceiver(networkStateReceiver);
	}

	@Override
	public int postMessage(String id, Message message) {
		return CommClient.post(id, message);
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

	private void onConnectionChanged() {
		if (isNetworkConnected()) {
			GCMRegistrar.checkDevice(context);
			GCMRegistrar.checkManifest(context);

			if (GCMRegistrar.isRegisteredOnServer(context)) {
				// already registered;
			}
			GCMRegistrar.register(context, GCMIntentService.SENDER_ID);

			CommClient.addCallback(receiver);
		} else {
			CommClient.removeCallback(receiver);
			GCMRegistrar.unregister(context);
		}
	}

	private Context context;
	private Receiver receiver;
	private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			onConnectionChanged();
		}
	};
}

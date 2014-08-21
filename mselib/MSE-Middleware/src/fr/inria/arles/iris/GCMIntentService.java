package fr.inria.arles.iris;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;

import fr.inria.arles.yarta.logging.YLoggerFactory;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.CommClient;

public class GCMIntentService extends GCMBaseIntentService {

	public static final String SENDER_ID = "820579729054";

	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	protected void onError(Context context, String intent) {
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		final String messageid = intent.getExtras().getString("messageid");

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message message = null;
				while ((message = CommClient.get(CommClient.userId)) != null) {
					log("onMessage(%s) = %s", messageid, message);
				}
			}
		}).start();
	}

	@Override
	protected void onRegistered(Context context, final String regId) {
		log("onRegistered(%s)", regId);

		new Thread(new Runnable() {

			@Override
			public void run() {
				CommClient.init(CommClient.userId, regId);
			}
		}).start();
	}

	@Override
	protected void onUnregistered(Context context, final String regId) {
		log("onUnregistered(%s)", regId);

		new Thread(new Runnable() {

			@Override
			public void run() {
				CommClient.uninit(CommClient.userId, regId);
			}
		}).start();
	}

	private void log(String format, Object... args) {
		YLoggerFactory.getLogger().d("GCMIntentService",
				String.format(format, args));
	}
}

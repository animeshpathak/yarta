package fr.inria.arles.yarta.middleware.communication;

import com.google.android.gcm.GCMBroadcastReceiver;

import android.content.Context;

public class GcmBroadcastReceiver extends GCMBroadcastReceiver {
	
	@Override
	protected String getGCMIntentServiceClassName(Context context) {
		return GCMIntentService.class.getCanonicalName();
	}
}

package fr.inria.arles.yarta.android.library.plugins;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class SyncTask extends AsyncTask<Object, Integer, Long> {

	public static final String ACTION_UPDATE = "fr.inria.arles.yarta.android.library.UPDATE_LAST_SYNC";

	@Override
	protected Long doInBackground(Object... params) {

		if (params.length == 0) {
			return 0L;
		}

		if (!(params[0] instanceof Context)) {
			return 0L;
		}

		Context context = (Context) params[0];

		try {
			PluginManager.getInstance().syncAllPlugins(context);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/**
		 * Sends a broadcast for all interested that an update might have been
		 * done.
		 */
		if (params.length > 0 && params[0] instanceof Context) {
			Intent update = new Intent();
			update.setAction(ACTION_UPDATE);
			context.sendBroadcast(update);
		}
		return 0L;
	}
}

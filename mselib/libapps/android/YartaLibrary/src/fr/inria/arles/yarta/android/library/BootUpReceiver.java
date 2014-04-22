package fr.inria.arles.yarta.android.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Used to start the {@link LibraryService} at boot time.
 */
public class BootUpReceiver extends BroadcastReceiver {

	/**
	 * boot time happens.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, LibraryService.class));
	}
}

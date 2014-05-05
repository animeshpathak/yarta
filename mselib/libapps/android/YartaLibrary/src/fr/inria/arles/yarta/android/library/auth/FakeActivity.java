package fr.inria.arles.yarta.android.library.auth;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Fake activity which wraps a context; Used only for passing an activity to
 * {@link AccountManager}.addAccount.
 */
public class FakeActivity extends Activity {

	private Context context;

	public FakeActivity(Context context) {
		this.context = context;
	}

	@Override
	public void startActivity(Intent intent) {
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_NO_ANIMATION);
		context.startActivity(intent);
	}
}

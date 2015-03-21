package fr.inria.arles.yarta.android.library.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.util.BaseDialog;

public class ProfileDialog extends BaseDialog implements View.OnClickListener {
	private Callback callback;

	public ProfileDialog(Context paramContext) {
		super(paramContext);
	}

	private void onCancel() {
		dismiss();
	}

	private void onPost() {
		String str = getCtrlText(2131099743);
		if (str.length() > 0) {
			this.callback.onAdd(str);
			dismiss();
		}
	}

	public static void show(Context paramContext, Callback paramCallback) {
		ProfileDialog localProfileDialog = new ProfileDialog(paramContext);
		localProfileDialog.setCallback(paramCallback);
		localProfileDialog.show();
	}

	public void onClick(View paramView) {
		switch (paramView.getId()) {
		case R.id.set:
			onPost();
			break;
		case R.id.cancel:
			onCancel();
			break;
		}
	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setTitle(R.string.profile_add_friend);
		setContentView(R.layout.dialog_profile);
		findViewById(R.id.set).setOnClickListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);
	}

	public void setCallback(Callback paramCallback) {
		this.callback = paramCallback;
	}

	public static abstract interface Callback {
		public abstract void onAdd(String paramString);
	}
}
package fr.inria.arles.yarta.android.library.util;

import android.content.Context;
import android.os.Bundle;
import fr.inria.arles.iris.R;

public class ProgressDialog extends BaseDialog {

	public ProgressDialog(Context context) {
		super(context, R.style.AppDialogNoTitle);
	}

	public static ProgressDialog show(Context context, String message) {
		ProgressDialog dlg = new ProgressDialog(context);
		dlg.setMessage(message);
		dlg.show();
		return dlg;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private String message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_progress);

		setCtrlText(R.id.message, message);

		setCancelable(false);
		setCanceledOnTouchOutside(false);
	}
}

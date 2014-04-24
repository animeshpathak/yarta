package fr.inria.arles.yarta.android.library.util;

import fr.inria.arles.iris.R;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;

public class PopupDialog extends BaseDialog {

	public static void show(Context context, String message) {
		PopupDialog dialog = new PopupDialog(context);
		dialog.setMessage(message);
		dialog.show();
	}

	public PopupDialog(Context context) {
		super(context, R.style.AppDialogNoTitle);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_popup);
		setCtrlText(R.id.message, Html.fromHtml(message));

		setCancelable(true);
		setCanceledOnTouchOutside(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		dismiss();
		return super.onTouchEvent(event);
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private String message;
}

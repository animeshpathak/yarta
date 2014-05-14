package fr.inria.arles.giveaway;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class AlertDialog extends BaseDialog implements View.OnClickListener {

	public interface Handler {
		public void onOK();
	}

	public static void show(Context context, String message, String title,
			String yes, String cancel, Handler handler) {
		AlertDialog dlg = new AlertDialog(context);
		dlg.setDetails(message, title, yes, cancel);
		dlg.setHandler(handler);
		dlg.show();
	}

	private Handler handler;
	private String message;
	private String title;
	private String yes;
	private String cancel;

	public AlertDialog(Context context) {
		super(context, R.style.AppDialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_alert);
		setTitle(title);

		setCtrlText(R.id.message, message);
		setCtrlText(R.id.ok, yes);
		setCtrlText(R.id.cancel, cancel);

		findViewById(R.id.ok).setOnClickListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void setDetails(String message, String title, String yes,
			String cancel) {
		this.message = message;
		this.title = title;
		this.yes = yes;
		this.cancel = cancel;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok:
			handler.onOK();
			dismiss();
			break;
		case R.id.cancel:
			dismiss();
			break;
		}
	}
}

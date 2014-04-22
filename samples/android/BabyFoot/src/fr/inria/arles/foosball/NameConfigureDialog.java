package fr.inria.arles.foosball;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class NameConfigureDialog extends Dialog implements View.OnClickListener {

	public interface Handler {
		public void onNameSet(String nickName);
	}

	private Handler handler;
	private String nickName;

	public NameConfigureDialog(Context context, String nickName) {
		super(context, R.style.AppDialog);
		this.nickName = nickName;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setCancelable(false);
		setCanceledOnTouchOutside(false);

		setContentView(R.layout.dialog_configurename);
		setTitle(R.string.name_configure_title);

		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.set).setOnClickListener(this);

		setCtrlText(R.id.nickName, nickName);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set:
			onSet();
			break;
		case R.id.cancel:
			onCancel();
			break;
		}
	}

	private void onSet() {
		String nickName = getCtrlText(R.id.nickName);

		if (nickName.length() > 0) {
			handler.onNameSet(nickName);
			dismiss();
		}
	}

	private String getCtrlText(int ctrlId) {
		TextView txt = (TextView) findViewById(ctrlId);
		if (txt != null) {
			return txt.getText().toString();
		}
		return null;
	}

	private void setCtrlText(int ctrlId, String text) {
		TextView txt = (TextView) findViewById(ctrlId);
		if (txt != null) {
			txt.setText(text);
		}
	}

	private void onCancel() {
		dismiss();
	}
}

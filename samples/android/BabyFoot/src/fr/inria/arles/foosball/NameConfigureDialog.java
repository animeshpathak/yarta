package fr.inria.arles.foosball;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class NameConfigureDialog extends BaseDialog implements
		View.OnClickListener {

	private Handler handler;
	private String nickName;

	public interface Handler {
		public void onNameSet(String nickName);
	}

	public NameConfigureDialog(Context context, String nickName) {
		super(context);
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

	private void onCancel() {
		dismiss();
	}
}

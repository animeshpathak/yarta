package fr.inria.arles.yarta.android.library.auth;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.util.BaseDialog;

public class CasDialog extends BaseDialog implements View.OnClickListener {

	public interface Handler {
		public void onProceed(boolean remember);
	}

	public static void show(Context context, Handler handler) {
		CasDialog dlg = new CasDialog(context);
		dlg.setHandler(handler);
		dlg.show();
	}

	private Handler handler;

	public CasDialog(Context context) {
		super(context);
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_cas);
		setTitle(R.string.cas_title);

		findViewById(R.id.set).setOnClickListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set:
			CheckBox check = (CheckBox) findViewById(R.id.neverShow);
			if (handler != null) {
				handler.onProceed(check.isChecked());
			}
			dismiss();
			break;
		case R.id.cancel:
			dismiss();
			break;
		}
	}
}

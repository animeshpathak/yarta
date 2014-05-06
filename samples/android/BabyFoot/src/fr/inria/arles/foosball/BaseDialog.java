package fr.inria.arles.foosball;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

public class BaseDialog extends Dialog {

	public BaseDialog(Context context) {
		super(context, R.style.AppDialog);
	}
	

	protected String getCtrlText(int ctrlId) {
		TextView txt = (TextView) findViewById(ctrlId);
		if (txt != null) {
			return txt.getText().toString();
		}
		return null;
	}

	protected void setCtrlText(int ctrlId, String text) {
		TextView txt = (TextView) findViewById(ctrlId);
		if (txt != null) {
			txt.setText(text);
		}
	}
}

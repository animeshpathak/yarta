package fr.inria.arles.yarta.android.library.util;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.LibraryService;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

public class BaseDialog extends Dialog {

	public BaseDialog(Context context) {
		super(context, R.style.AppDialog);
	}

	public BaseDialog(Context context, int style) {
		super(context, style);
	}

	protected void trackUI(String name) {
		LibraryService.getTracker().trackUIUsage(name);
	}

	protected String getString(int strId) {
		return getContext().getString(strId);
	}

	protected String getCtrlText(int txtId) {
		TextView txt = (TextView) findViewById(txtId);
		if (txt != null) {
			return txt.getText().toString();
		}
		return null;
	}

	protected void setCtrlText(int txtId, String text) {
		TextView txt = (TextView) findViewById(txtId);
		if (txt != null) {
			txt.setText(text);
		}
	}

	protected void setCtrlText(int txtId, Spanned text) {
		TextView txt = (TextView) findViewById(txtId);
		if (txt != null) {
			txt.setText(text);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			int divierId = getContext().getResources().getIdentifier(
					"android:id/titleDivider", null, null);
			View divider = findViewById(divierId);
			divider.setBackgroundColor(getContext().getResources().getColor(
					R.color.AppTextColorStrong));
		} catch (Exception ex) {
			// we don't care the styling failed
		}
	}
}

package fr.inria.arles.yarta.android.library.util;

import fr.inria.arles.iris.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Used in navigation drawer as a workaround for Android 2.3
 */
public class LinearLayoutCheckable extends LinearLayout implements Checkable {

	private boolean checked;

	public LinearLayoutCheckable(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setChecked(boolean checked) {
		this.checked = checked;

		if (checked) {
			setBackgroundResource(R.drawable.item_selected);
		} else {
			setBackgroundResource(R.drawable.list_selector);
		}
	}

	@Override
	public boolean isChecked() {
		return checked;
	}

	@Override
	public void toggle() {
		setChecked(!checked);
	}

}

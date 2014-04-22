package fr.inria.arles.foosball;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class PositionSetDialog extends Dialog implements View.OnClickListener {

	public interface Handler {
		final int BLUE = 0;
		final int RED = 1;

		public void onSetConfiguration(int team);
	}

	private Handler handler;

	public PositionSetDialog(Context context, Handler handler) {
		super(context, R.style.AppDialog);
		this.handler = handler;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel:
			dismiss();
			break;
		case R.id.blue:
			handler.onSetConfiguration(Handler.BLUE);
			dismiss();
			break;
		case R.id.red:
			handler.onSetConfiguration(Handler.RED);
			dismiss();
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_set_position);
		setTitle(R.string.position_title);

		findViewById(R.id.red).setOnClickListener(this);
		findViewById(R.id.blue).setOnClickListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);
	}
}

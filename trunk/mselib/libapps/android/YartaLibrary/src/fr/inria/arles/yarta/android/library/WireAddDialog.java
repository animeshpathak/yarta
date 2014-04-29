package fr.inria.arles.yarta.android.library;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.web.WebClient;
import fr.inria.arles.yarta.android.library.util.BaseDialog;
import fr.inria.arles.yarta.android.library.util.JobRunner;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class WireAddDialog extends BaseDialog implements View.OnClickListener {

	public interface Callback {
		public void onWireAdded();
	}

	private Callback callback;
	private JobRunner runner;

	public WireAddDialog(Context context) {
		super(context);
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public void setRunner(JobRunner runner) {
		this.runner = runner;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(R.string.wire_title);
		setContentView(R.layout.dialog_add_wire);

		Spinner s = (Spinner) findViewById(R.id.access);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext()
				.getApplicationContext(), R.layout.item_spinner, new String[] {
				getString(R.string.wire_logged_users),
				getString(R.string.wire_public) });
		s.setAdapter(adapter);

		setCtrlText(R.id.counter,
				String.format(getString(R.string.wire_text_left), 140));

		EditText txt = (EditText) findViewById(R.id.content);
		txt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				setCtrlText(R.id.counter, String.format(
						getString(R.string.wire_text_left), 140 - s.length()));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.set).setOnClickListener(this);

		trackUI("WireAdd");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel:
			onCancel();
			break;
		case R.id.set:
			onPost();
			break;
		}
	}

	private void onCancel() {
		dismiss();
	}

	private void onPost() {
		final String text = getCtrlText(R.id.content);

		Spinner s = (Spinner) findViewById(R.id.access);
		final int access = s.getSelectedItemPosition();

		if (text.length() > 0 && text.length() <= 140) {
			runner.runBackground(new JobRunner.Job() {

				int result = -1;

				@Override
				public void doWork() {
					result = WebClient.getInstance().addWire(
							text,
							access == 0 ? WebClient.ACCESS_LOGGED_USERS
									: WebClient.ACCESS_PUBLIC);
				}

				@Override
				public void doUIAfter() {
					if (result == -1) {
						Toast.makeText(getContext().getApplicationContext(),
								WebClient.getInstance().getLastError(),
								Toast.LENGTH_LONG).show();
					} else {
						dismiss();
						callback.onWireAdded();
					}
				}
			});
		} else {
			Toast.makeText(getContext().getApplicationContext(),
					R.string.wire_content_length, Toast.LENGTH_LONG).show();
		}
	}
}

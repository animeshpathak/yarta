package fr.inria.arles.yarta;

import fr.inria.arles.yarta.basic.R;
import fr.inria.arles.yarta.core.YartaWrapper;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MessageDialog extends Dialog implements View.OnClickListener {

	public MessageDialog(Context context, String recipient) {
		super(context);
		this.recipient = recipient;
	}

	public MessageDialog(Context context, String recipient,
			String previousMessage) {
		super(context);
		this.recipient = recipient;
		this.previousMessage = previousMessage;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_message);

		setTitle(R.string.message_title);

		findViewById(R.id.sendButton).setOnClickListener(this);
		findViewById(R.id.cancelButton).setOnClickListener(this);

		TextView txtRecipient = (TextView) findViewById(R.id.messageRecipient);
		TextView txtPrevious = (TextView) findViewById(R.id.messagePrevious);

		if (previousMessage != null) {
			String format = getContext().getString(R.string.message_from);
			txtRecipient.setText(String.format(format, recipient));
			txtPrevious.setText(previousMessage);
		} else {
			String format = getContext().getString(R.string.message_to);
			txtRecipient.setText(String.format(format, recipient));
			txtPrevious.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sendButton:
			onClickSend();
			break;
		case R.id.cancelButton:
			onClickCancel();
			break;
		}
	}

	private void onClickSend() {
		EditText text = (EditText) findViewById(R.id.messageToSend);
		String messageBody = text.getText().toString();

		if (messageBody == null) {
			Toast.makeText(getContext(), R.string.message_empty,
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (messageBody.length() == 0) {
			Toast.makeText(getContext(), R.string.message_empty,
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (YartaWrapper.getInstance().sendMessage(recipient, messageBody) > -1)
		{
			Toast.makeText(getContext(), R.string.message_sent, Toast.LENGTH_SHORT).show();
			dismiss();
		}
		else
		{
			Toast.makeText(getContext(), R.string.message_not_sent, Toast.LENGTH_SHORT).show();
		}
	}

	private void onClickCancel() {
		dismiss();
	}

	private String recipient;
	private String previousMessage;
}

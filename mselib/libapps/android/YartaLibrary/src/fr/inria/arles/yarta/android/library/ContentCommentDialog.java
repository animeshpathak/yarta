package fr.inria.arles.yarta.android.library;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.util.BaseDialog;
import fr.inria.arles.yarta.resources.Content;

public class ContentCommentDialog extends BaseDialog implements
		View.OnClickListener {

	public interface Callback {
		public void onCommentAdded();
	}

	private Content content;
	private StorageAccessManagerEx sam;
	private Callback callback;

	public ContentCommentDialog(Context context, Content content) {
		super(context);
		this.content = content;
	}

	public void setSAM(StorageAccessManagerEx sam) {
		this.sam = sam;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(R.string.topic_add_comment);
		setContentView(R.layout.dialog_add_topic);

		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.set).setOnClickListener(this);
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

		if (text.length() > 0 && text.length() <= 140) {
			try {
				Person me = sam.getMe();
				Content comment = sam.createContent();

				me.addCreator(comment);
				comment.setTime(System.currentTimeMillis());
				comment.setTitle(text);
				content.addHasReply(comment);

				callback.onCommentAdded();
				dismiss();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			Toast.makeText(getContext().getApplicationContext(),
					R.string.wire_content_length, Toast.LENGTH_LONG).show();
		}
	}
}
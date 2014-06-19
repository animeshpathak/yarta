package fr.inria.arles.yarta.android.library;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.web.WebClient;
import fr.inria.arles.yarta.android.library.util.BaseDialog;
import fr.inria.arles.yarta.android.library.util.JobRunner;

public class TopicAddDialog extends BaseDialog implements View.OnClickListener {

	public interface Callback {
		public void onReplyAdded();
	}

	private Callback callback;
	private JobRunner runner;

	private String topicGuid;

	public TopicAddDialog(Context context, String topicGuid) {
		super(context);
		this.topicGuid = topicGuid;
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
			runner.runBackground(new JobRunner.Job() {

				int result = -1;

				@Override
				public void doWork() {
					result = WebClient.getInstance().addTopicReply(topicGuid, text);
				}

				@Override
				public void doUIAfter() {
					if (result == -1) {
						Toast.makeText(getContext().getApplicationContext(),
								WebClient.getInstance().getLastError(),
								Toast.LENGTH_LONG).show();
					} else {
						dismiss();
						callback.onReplyAdded();
					}
				}
			});
		} else {
			Toast.makeText(getContext().getApplicationContext(),
					R.string.wire_content_length, Toast.LENGTH_LONG).show();
		}
	}
}
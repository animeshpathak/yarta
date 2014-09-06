package fr.inria.arles.yarta.android.library;

import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ElggClient;
import fr.inria.arles.yarta.android.library.util.BaseDialog;
import fr.inria.arles.yarta.android.library.util.JobRunner;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class TopicCommentDialog extends BaseDialog implements
		View.OnClickListener {

	public interface Callback {
		public void onCommentAdded();
	}

	private Callback callback;
	private JobRunner runner;

	private String postGuid;
	private ElggClient client = ElggClient.getInstance();

	public TopicCommentDialog(Context context, String postGuid) {
		super(context);
		this.postGuid = postGuid;
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

		setTitle(R.string.post_add_comment);
		setContentView(R.layout.dialog_add_comment);

		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.set).setOnClickListener(this);
		trackUI("CommentAdd");
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
					result = client.addGroupPostComment(postGuid, text);
				}

				@Override
				public void doUIAfter() {
					if (result == -1) {
						Toast.makeText(getContext().getApplicationContext(),
								client.getLastError(), Toast.LENGTH_LONG)
								.show();
					} else {
						dismiss();
						callback.onCommentAdded();
					}
				}
			});
		} else {
			Toast.makeText(getContext().getApplicationContext(),
					R.string.wire_content_length, Toast.LENGTH_LONG).show();
		}
	}
}
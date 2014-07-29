package fr.inria.arles.yarta.android.library.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ElggClient;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class FeedbackDialog extends BaseDialog implements View.OnClickListener {

	private JobRunner runner;

	public FeedbackDialog(Context context) {
		super(context, R.style.AppDialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_feedback);
		setTitle(R.string.feedback_title);

		findViewById(R.id.set).setOnClickListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);

		findViewById(R.id.nice).setOnClickListener(this);
		findViewById(R.id.slow).setOnClickListener(this);
		findViewById(R.id.buggy).setOnClickListener(this);
	}

	public void setRunner(JobRunner runner) {
		this.runner = runner;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel:
			dismiss();
			break;
		case R.id.set:
			String content = getCtrlText(R.id.content);
			if (content.length() > 0) {
				sendFeedback(content);
				dismiss();
			} else {
				Toast.makeText(getContext(), R.string.feedback_content_empty,
						Toast.LENGTH_LONG).show();
			}
			break;

		case R.id.nice:
			sendFeedback(getContext().getString(R.string.feedback_app_nice)
					+ " | " + getCtrlText(R.id.content));
			dismiss();
			break;
		case R.id.slow:
			sendFeedback(getContext().getString(R.string.feedback_app_slow)
					+ " | " + getCtrlText(R.id.content));
			dismiss();
			break;
		case R.id.buggy:
			sendFeedback(getContext().getString(R.string.feedback_app_bug)
					+ " | " + getCtrlText(R.id.content));
			dismiss();
			break;
		}
	}

	private void sendFeedback(final String content) {
		runner.runBackground(new Job() {

			private boolean success = false;

			@Override
			public void doWork() {
				success = sendFeedback("fr.inria.arles.iris", ElggClient
						.getInstance().getUsername(), content);
			}

			@Override
			public void doUIAfter() {
				Toast.makeText(
						getContext().getApplicationContext(),
						success ? R.string.feedback_sent_ok
								: R.string.feedback_sent_error,
						Toast.LENGTH_LONG).show();
				if (success) {
					dismiss();
				}
			}
		});
	}

	public boolean sendFeedback(String appId, String from, String content) {
		String feedbackURL = "http://arles.rocq.inria.fr/yarta/feedback/?";

		try {
			feedbackURL += "from=" + URLEncoder.encode(from, "UTF-8");
			feedbackURL += "&appid=" + URLEncoder.encode(appId, "UTF-8");
			feedbackURL += "&content=" + URLEncoder.encode(content, "UTF-8");

			readURL(feedbackURL);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	private static void readURL(String url) throws Exception {
		URL oracle = new URL(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				oracle.openStream()));

		String inputLine;
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		in.close();
	}
}

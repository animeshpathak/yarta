package fr.inria.arles.giveaway;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class FeedbackDialog extends Dialog implements View.OnClickListener {

	private Handler handler;

	public interface Handler {
		public void onSendFeedback(String content);
	}

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

	public void setHandler(Handler handler) {
		this.handler = handler;
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
				handler.onSendFeedback(content);
				dismiss();
			} else {
				Toast.makeText(getContext(), R.string.feedback_content_empty,
						Toast.LENGTH_LONG).show();
			}
			break;

		case R.id.nice:
			handler.onSendFeedback(getContext().getString(
					R.string.feedback_app_nice));
			dismiss();
			break;
		case R.id.slow:
			handler.onSendFeedback(getContext().getString(
					R.string.feedback_app_slow));
			dismiss();
			break;
		case R.id.buggy:
			handler.onSendFeedback(getContext().getString(
					R.string.feedback_app_bug));
			dismiss();
			break;
		}
	}

	private String getCtrlText(int resId) {
		TextView txt = (TextView) findViewById(resId);
		if (txt != null) {
			return txt.getText().toString();
		}
		return null;
	}

	public static boolean sendFeedback(String appId, String from, String content) {
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

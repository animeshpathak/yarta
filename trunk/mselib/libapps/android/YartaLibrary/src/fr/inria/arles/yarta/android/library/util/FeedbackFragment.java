package fr.inria.arles.yarta.android.library.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FeedbackFragment extends BaseFragment implements
		View.OnClickListener {

	private static final int MENU_SEND = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_feedback, container,
				false);

		root.findViewById(R.id.nice).setOnClickListener(this);
		root.findViewById(R.id.slow).setOnClickListener(this);
		root.findViewById(R.id.buggy).setOnClickListener(this);

		setHasOptionsMenu(true);

		return root;
	}

	@Override
	public void refreshUI() {
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem item = menu.add(0, MENU_SEND, 0, R.string.message_send);
		item.setIcon(R.drawable.icon_send);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SEND:
			sendFeedback(getCtrlText(R.id.content));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nice:
			sendFeedback(getString(R.string.feedback_app_nice) + " | "
					+ getCtrlText(R.id.content));
			break;
		case R.id.slow:
			sendFeedback(getString(R.string.feedback_app_slow) + " | "
					+ getCtrlText(R.id.content));
			break;
		case R.id.buggy:
			sendFeedback(getString(R.string.feedback_app_bug) + " | "
					+ getCtrlText(R.id.content));
			break;
		}
	}

	private void sendFeedback(final String content) {
		runner.runBackground(new Job() {

			private boolean success = false;

			@Override
			public void doWork() {
				success = submitFeedback(content);
			}

			@Override
			public void doUIAfter() {
				Toast.makeText(
						getSherlockActivity(),
						success ? R.string.feedback_sent_ok
								: R.string.feedback_sent_error,
						Toast.LENGTH_LONG).show();

				setCtrlText(R.id.content, "");
			}
		});
	}

	public boolean submitFeedback(String content) {
		PackageManager packMgr = getSherlockActivity().getPackageManager();

		String feedbackURL = "http://arles.rocq.inria.fr/yarta/feedback/?";

		try {
			String appId = getSherlockActivity().getPackageName();
			String from = client.getUsername();
			
			content += " " + packMgr.getPackageInfo(appId, 0).versionName;

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

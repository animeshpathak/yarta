package fr.inria.arles.yarta.conference;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class AsyncRunner {

	public interface Job {
		public void doWork();

		public void doUI();
	}

	public AsyncRunner(Activity parent) {
		this.parent = parent;
	}

	public void run(Job job) {
		ProgressDialog dialog = ProgressDialog.show(parent, "Wait",
				"Please wait...", true);
		new AsyncRunnerTask().execute(dialog, job);
	}

	private Activity parent;

	private class AsyncRunnerTask extends AsyncTask<Object, Integer, Long> {

		@Override
		protected Long doInBackground(Object... params) {
			dialog = (ProgressDialog) params[0];
			final Job job = (Job) params[1];
			job.doWork();

			parent.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					job.doUI();
					dialog.dismiss();
				}
			});

			return 0L;
		}

		private ProgressDialog dialog;
	}
}

package fr.inria.arles.foosball;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import fr.inria.arles.foosball.util.FoosballCore;
import fr.inria.arles.foosball.util.JobRunner;
import fr.inria.arles.foosball.util.JobRunner.Job;
import fr.inria.arles.foosball.util.Settings;

public class BaseActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		settings = new Settings(this);

		runner = new JobRunner(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	protected void execute(Job job) {
		runner.runBackground(job);
	}

	protected void showView(int id, boolean show) {
		View txt = findViewById(id);
		if (txt != null) {
			txt.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	protected void setCtrlText(int ctrlId, String text) {
		TextView txt = (TextView) findViewById(ctrlId);
		if (txt != null) {
			txt.setText(text);
		}
	}

	protected Settings settings;
	private JobRunner runner;
	protected FoosballCore core = FoosballCore.getInstance();
}

package fr.inria.arles.giveaway;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import fr.inria.arles.giveaway.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.giveaway.util.JobRunner;
import fr.inria.arles.giveaway.util.JobRunner.Job;

public class BaseFragment extends SherlockFragment {

	public static final int FILTER_NONE = 0;
	public static final int FILTER_MINE = 1;
	public static final int FILTER_DONATIONS = 2;
	public static final int FILTER_SALES = 3;
	public static final int FILTER_REQUESTS = 4;

	private JobRunner runner;
	private DonationApp app;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		runner = new JobRunner((BaseActivity) getActivity());
		app = (DonationApp) getActivity().getApplication();
	}

	protected StorageAccessManagerEx getSAM() {
		return app.getSAM();
	}

	protected void execute(Job job) {
		if (runner != null) {
			runner.runBackground(job);
		}
	}

	public void setFilter(int filterType) {
		this.filterType = filterType;
	}

	public void refreshUI() {
	}

	protected String getCtrlText(int txtId) {
		TextView txt = (TextView) getView().findViewById(txtId);
		if (txt != null) {
			return txt.getText().toString();
		}
		return null;
	}

	protected void setCtrlText(int txtId, String text) {
		TextView txt = (TextView) getView().findViewById(txtId);
		if (txt != null) {
			txt.setText(text);
		}
	}

	protected void enableItem(int item, boolean enable) {
		View view = getView().findViewById(item);
		view.setEnabled(enable);
	}

	protected void notifyAgent() {
		DonationApp app = (DonationApp) getActivity().getApplication();
		app.notifyAgent();
	}

	protected int filterType;
}

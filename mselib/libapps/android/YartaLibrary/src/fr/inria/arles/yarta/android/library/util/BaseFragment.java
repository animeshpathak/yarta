package fr.inria.arles.yarta.android.library.util;

import java.lang.reflect.Field;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.ContentClientPictures;
import fr.inria.arles.yarta.android.library.auth.AuthenticatorActivity;
import fr.inria.arles.yarta.android.library.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.middleware.communication.CommunicationManager;

public abstract class BaseFragment extends SherlockFragment {

	protected CommunicationManager comm;
	protected StorageAccessManagerEx sam;
	protected ContentClientPictures contentClient;

	protected enum Frame {
		Content, Internet, Authentication, Loading
	};

	private JobRunner runner;

	public void setRunner(JobRunner runner) {
		this.runner = runner;
	}

	protected JobRunner getRunner() {
		return runner;
	}

	public void setSAM(StorageAccessManagerEx sam) {
		this.sam = sam;
	}

	public void setCOMM(CommunicationManager comm) {
		this.comm = comm;
	}

	public void setGroupGuid(String groupId) {
		// does nothing;
	}

	public void setContentClient(ContentClientPictures contentClient) {
		this.contentClient = contentClient;
	}

	protected void execute(Job job) {
		if (runner != null) {
			runner.runBackground(job);
		}
	}

	public abstract void refreshUI(String notification);

	protected String getCtrlText(int txtId) {
		TextView txt = (TextView) getView().findViewById(txtId);
		if (txt != null) {
			return txt.getText().toString();
		}
		return null;
	}

	protected void setCtrlText(int txtId, Spanned text) {
		if (getView() != null) {
			TextView txt = (TextView) getView().findViewById(txtId);
			if (txt != null) {
				txt.setText(text);
			}
		}
	}

	protected void setCtrlText(int txtId, String text) {
		if (getView() != null) {
			TextView txt = (TextView) getView().findViewById(txtId);
			if (txt != null) {
				txt.setText(text);
			}
		}
	}

	protected void setFocusable(int viewId, boolean focusable) {
		if (getView() != null) {
			getView().findViewById(viewId).setFocusable(focusable);
		}
	}

	protected void setVisible(int ctrlId, boolean visible) {
		if (getView() != null) {
			View v = getView().findViewById(ctrlId);
			if (v != null) {
				v.setVisibility(visible ? View.VISIBLE : View.GONE);
			}
		}
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);

		if (menuVisible && isAdded()) {
			refreshUI(null);
		}
	}

	protected void showFrame(Frame frame) {
		setVisible(R.id.frame_content, frame == Frame.Content);
		setVisible(R.id.frame_internet, frame == Frame.Internet);
		setVisible(R.id.frame_authentication, frame == Frame.Authentication);
		setVisible(R.id.frame_loading, frame == Frame.Loading);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		View v = getView().findViewById(R.id.login);
		if (v != null) {
			v.setOnClickListener(errorClickListener);
		}

		v = getView().findViewById(R.id.internet);
		if (v != null) {
			v.setOnClickListener(errorClickListener);
		}
	}

	private void onClickLogin() {
		startActivity(new Intent(getSherlockActivity(),
				AuthenticatorActivity.class));
	}

	private void onClickInternet() {
		showFrame(Frame.Content);
		refreshUI(null);
	}

	private final View.OnClickListener errorClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.login:
				onClickLogin();
				break;
			case R.id.internet:
				onClickInternet();
				break;
			}
		}
	};

	/**
	 * Android BUG http://stackoverflow.com/questions/14929907/causing-a-java-
	 * illegalstateexception-error-no-activity-only-when-navigating-to
	 */
	private static final Field sChildFragmentManagerField;

	static {
		Field f = null;
		try {
			f = Fragment.class.getDeclaredField("mChildFragmentManager");
			f.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		sChildFragmentManagerField = f;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		if (sChildFragmentManagerField != null) {
			try {
				sChildFragmentManagerField.set(this, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

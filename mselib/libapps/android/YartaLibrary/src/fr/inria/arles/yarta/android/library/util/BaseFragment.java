package fr.inria.arles.yarta.android.library.util;

import java.lang.reflect.Field;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ElggClient;
import fr.inria.arles.iris.web.ElggClient.WebCallback;
import fr.inria.arles.yarta.android.library.ContentClientPictures;
import fr.inria.arles.yarta.android.library.auth.AuthenticatorActivity;
import fr.inria.arles.yarta.android.library.msemanagement.StorageAccessManagerEx;

public abstract class BaseFragment extends SherlockFragment implements
		WebCallback {

	protected JobRunner runner;
	protected StorageAccessManagerEx sam;
	protected ContentClientPictures contentClient;
	protected ViewGroup container;
	protected View content;
	protected ElggClient client = ElggClient.getInstance();
	private boolean loginState = false;

	public void setRunner(JobRunner runner) {
		this.runner = runner;
	}

	public void setSAM(StorageAccessManagerEx sam) {
		this.sam = sam;
	}

	public void setGroupGuid(String groupId) {
		// does nothing;
	}

	public void setContentClient(ContentClientPictures contentClient) {
		this.contentClient = contentClient;
	}

	public abstract void refreshUI();

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		client.addCallback(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		client.removeCallback(this);
		loginState = false;
	}

	/**
	 * Sets the parent and the current view. It is being used when log-in fails
	 * or there is no Internet.
	 * 
	 * @param container
	 * @param content
	 */
	protected void setViews(ViewGroup container, View content) {
		this.container = container;
		this.content = content;
	}

	@Override
	public void onAuthentication() {
		getSherlockActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				loginState = false;
				showLogin(false);
			}
		});
	}

	@Override
	public void onAuthenticationFailed() {
		getSherlockActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				loginState = true;
				showLogin(true);
			}
		});
	}

	/**
	 * Checks for the existence of viewId in the container. If not, it will
	 * inflate layoutId and search again.
	 * 
	 * @return
	 */
	private View getChild(int viewId, int layoutId) {
		View child = container.findViewById(viewId);

		if (child == null) {
			LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
			inflater = getSherlockActivity().getLayoutInflater();
			child = inflater.inflate(layoutId, container, false);
			container.addView(child);
		}

		return child;
	}

	/**
	 * Shows the log-in frame.
	 * 
	 * @param show
	 */
	private void showLogin(boolean show) {
		if (container != null && content != null) {
			View login = getChild(R.id.frame_login, R.layout.frame_login);
			login.findViewById(R.id.login).setOnClickListener(
					errorClickListener);

			login.setVisibility(show ? View.VISIBLE : View.GONE);
			content.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		showLogin(false);
	}

	@Override
	public void onResume() {
		super.onResume();
		showLogin(loginState);
	}

	@Override
	public void onNetworkFailed() {
	}

	private void onClickLogin() {
		startActivity(new Intent(getSherlockActivity(),
				AuthenticatorActivity.class));
	}

	private void onClickInternet() {
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

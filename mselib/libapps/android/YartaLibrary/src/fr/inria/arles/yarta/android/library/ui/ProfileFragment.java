package fr.inria.arles.yarta.android.library.ui;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ElggClient;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.resources.Picture;
import fr.inria.arles.yarta.android.library.util.AlertDialog;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.Settings;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.knowledgebase.KBException;

public class ProfileFragment extends BaseFragment {

	private static final int MENU_COMPOSE = 2;
	private static final int MENU_ADD = 3;

	private String username;
	private Person user;
	private RiverFragment fragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_profile, container,
				false);
		setHasOptionsMenu(true);
		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (sam != null) {
			refreshUI(null);
		}
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Returns true if its current user displayed.
	 * 
	 * @return
	 */
	private boolean isCurrentUser() {
		return username == null || client.getUsername() == null
				|| client.getUsername().equals(username);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (!isCurrentUser()) {
			if (!isFriend(getUser())) {
				MenuItem item = menu.add(0, MENU_ADD, 0,
						R.string.profile_add_friend);
				item.setIcon(R.drawable.icon_add_user);
				item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			}

			MenuItem item = menu.add(0, MENU_COMPOSE, 0,
					R.string.profile_compose);
			item.setIcon(R.drawable.icon_compose);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {
			if (getView() != null) {
				getView().findViewById(R.id.logout).setVisibility(View.VISIBLE);
				getView().findViewById(R.id.logout).setOnClickListener(
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								AlertDialog
										.show(getSherlockActivity(),
												getString(R.string.main_logout_are_you_sure),
												getString(R.string.main_logout_confirm),
												getString(R.string.main_logout_ok),
												getString(R.string.main_logout_cancel),
												new AlertDialog.Handler() {

													@Override
													public void onOK() {
														BaseActivity activity = (BaseActivity) getSherlockActivity();
														Settings settings = new Settings(
																activity);
														settings.setString(
																Settings.USER_TOKEN,
																null);
														activity.clearMSE();
														activity.finish();
													}
												});
							}
						});
			}
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	private boolean isFriend(Person person) {
		try {
			return sam.getMe().getKnows_inverse().contains(person);
		} catch (Exception ex) {
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_COMPOSE:
			if (user == null) {
				break;
			}
			onCompose();
			break;
		case MENU_ADD:
			if (user == null) {
				break;
			}
			String format = getString(R.string.profile_add_confirm_text);
			AlertDialog.show(getSherlockActivity(),
					String.format(format, user.getName()),
					getString(R.string.profile_add_confirm),
					getString(R.string.profile_add_yes),
					getString(R.string.profile_add_cancel),
					new AlertDialog.Handler() {

						@Override
						public void onOK() {
							onAdd();
						}
					});
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Returns the User;
	 * 
	 * @return
	 */
	private Person getUser() {
		if (user == null) {
			try {
				if (username == null) {
					username = ElggClient.getInstance().getUsername();
				}
				user = sam.getPersonByUserId(username);
			} catch (KBException ex) {
				ex.printStackTrace();
			}
		}
		return user;
	}

	@Override
	public void refreshUI(String notification) {
		if (getUser() == null) {
			return;
		}

		if (user.getName() != null) {
			setCtrlText(R.id.name, Html.fromHtml(user.getName()));
		}
		if (user.getHomepage() != null) {
			setVisible(R.id.website, true);
			setCtrlText(R.id.website, user.getHomepage());
		}

		if (user.getLocation() != null) {
			String location = String.format(getString(R.string.profile_team),
					user.getLocation());
			setCtrlText(R.id.location, Html.fromHtml(location));
		}

		if (user.getRoom() != null) {
			String room = String.format(getString(R.string.profile_room),
					user.getRoom());
			setCtrlText(R.id.room, Html.fromHtml(room));
		}

		if (user.getPhone() != null) {
			String phone = String.format(getString(R.string.profile_phone),
					user.getPhone());
			setCtrlText(R.id.phone, Html.fromHtml(phone));
		}

		ImageView image = (ImageView) getView().findViewById(R.id.icon);

		Bitmap bitmap = null;

		for (Picture picture : user.getPicture()) {
			bitmap = contentClient.getBitmap(picture);
		}
		if (bitmap != null) {
			image.setVisibility(View.VISIBLE);
			image.setImageBitmap(bitmap);
		} else {
			image.setVisibility(View.GONE);
		}

		if (fragment != null) {
			fragment.refreshUI(null);
		} else {
			FragmentTransaction ft = getSherlockActivity()
					.getSupportFragmentManager().beginTransaction();

			fragment = new RiverFragment();
			fragment.setRunner(getRunner());
			fragment.setUsername(username);

			ft.replace(R.id.userActivity, fragment);
			ft.commit();
		}
	}

	@Override
	public void onDestroyView() {
		fragment = null;
		super.onDestroyView();
	}

	private void onCompose() {
		Intent intent = new Intent(getSherlockActivity(), MessageActivity.class);
		intent.putExtra(MessageActivity.UserId, user.getUserId());
		startActivity(intent);
	}

	private void onAdd() {
		execute(new Job() {

			int result = -1;

			@Override
			public void doWork() {
				result = client.addFriend(user.getUserId());
			}

			@Override
			public void doUIAfter() {
				try {
					String message = getString(R.string.profile_add_ok);
					if (result != ElggClient.RESULT_OK) {
						message = client.getLastError();
					}
					Toast.makeText(getSherlockActivity(), message,
							Toast.LENGTH_SHORT).show();
				} catch (IllegalStateException ex) {
					// do nothing
				}
			}
		});
	}
}

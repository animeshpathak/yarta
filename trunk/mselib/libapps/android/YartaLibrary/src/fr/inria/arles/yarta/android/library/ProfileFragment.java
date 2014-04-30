package fr.inria.arles.yarta.android.library;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.web.ImageCache;
import fr.inria.arles.yarta.android.library.web.UserItem;
import fr.inria.arles.yarta.android.library.web.WebClient;
import fr.inria.arles.yarta.android.library.util.AlertDialog;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;

public class ProfileFragment extends BaseFragment {

	private static final int MENU_SAVE = 1;
	private static final int MENU_COMPOSE = 2;
	private static final int MENU_ADD = 3;

	private String username = WebClient.getInstance().getUsername();
	private UserItem user;
	private RiverFragment activityFragment;

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
		refreshUI();
	}

	public void setUsername(String username) {
		this.username = username;
	}

	private boolean isEditable() {
		return client.getUsername().equals(username);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (isEditable()) {
			MenuItem item = menu.add(0, MENU_SAVE, 0, R.string.profile_save);
			item.setIcon(R.drawable.icon_accept);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {
			MenuItem item = menu.add(0, MENU_ADD, 0,
					R.string.profile_add_friend);
			item.setIcon(R.drawable.icon_add_user);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

			item = menu.add(0, MENU_COMPOSE, 0, R.string.profile_compose);
			item.setIcon(R.drawable.icon_compose);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SAVE:
			onSave();
			break;
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

	@Override
	public void refreshUI() {
		runner.runBackground(new Job() {

			@Override
			public void doWork() {
				user = client.getUser(username);

				if (user != null) {
					user.setUsername(username);
					String avatarURL = user.getAvatarURL();
					if (ImageCache.getDrawable(avatarURL) == null) {
						try {
							ImageCache.setDrawable(avatarURL,
									ImageCache.drawableFromUrl(avatarURL));
						} catch (Exception ex) {
						}
					}
				}
			}

			@Override
			public void doUIAfter() {
				if (user == null) {
					return;
				} else if (user.getName() == null) {
					return;
				}

				setCtrlText(R.id.name, Html.fromHtml(user.getName()));

				if (user.getWebsite() != null || isEditable()) {
					setCtrlVisibility(R.id.website, View.VISIBLE);
					setCtrlText(R.id.website, user.getWebsite());
				}

				String location = String.format(
						getString(R.string.profile_team), user.getLocation());
				setCtrlText(R.id.location, Html.fromHtml(location));

				String room = String.format(getString(R.string.profile_room),
						user.getRoom());
				setCtrlText(R.id.room, Html.fromHtml(room));

				String phone = String.format(getString(R.string.profile_phone),
						user.getPhone());
				setCtrlText(R.id.phone, Html.fromHtml(phone));

				ImageView image = (ImageView) getView().findViewById(R.id.icon);
				Drawable drawable = ImageCache.getDrawable(user.getAvatarURL());
				if (drawable != null) {
					image.setImageDrawable(drawable);
				} else {
					image.setVisibility(View.GONE);
				}

				if (!isEditable()) {
					setFocusable(R.id.name, false);
					setFocusable(R.id.website, false);
				}

				// load activity fragment
				if (activityFragment == null) {
					FragmentTransaction ft = getSherlockActivity()
							.getSupportFragmentManager().beginTransaction();
					activityFragment = new RiverFragment();
					activityFragment.setRunner(runner);
					activityFragment.setUsername(user.getUsername());
					ft.replace(R.id.userActivity, activityFragment);
					ft.commit();
				} else {
					activityFragment.refreshUI();
				}
			}
		});
	}

	private void onSave() {
		final String name = getCtrlText(R.id.name);
		final String website = getCtrlText(R.id.website);

		runner.runBackground(new Job() {
			int result;
			String message;

			@Override
			public void doWork() {
				result = client.setUser(username, new UserItem(name, website,
						null, null, null, null));
				message = client.getLastError();
			}

			@Override
			public void doUIAfter() {
				if (result != WebClient.RESULT_OK) {
					Toast.makeText(getSherlockActivity(), message,
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getSherlockActivity(),
							R.string.profile_update_successfull,
							Toast.LENGTH_SHORT).show();

					refreshUI();
				}
			}
		});
	}

	private void onCompose() {
		Intent intent = new Intent(getSherlockActivity(), MessageActivity.class);
		intent.putExtra(MessageActivity.User, user);
		startActivity(intent);
	}

	private void onAdd() {
		runner.runBackground(new Job() {

			int result = -1;

			@Override
			public void doWork() {
				result = client.addFriend(user.getUsername());
			}

			@Override
			public void doUIAfter() {
				try {
					String message = getString(R.string.profile_add_ok);
					if (result != WebClient.RESULT_OK) {
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

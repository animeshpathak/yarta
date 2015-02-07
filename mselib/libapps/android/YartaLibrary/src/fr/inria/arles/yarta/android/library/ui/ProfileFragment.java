package fr.inria.arles.yarta.android.library.ui;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.resources.Picture;
import fr.inria.arles.yarta.android.library.util.AlertDialog;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.Settings;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.knowledgebase.KBException;

public class ProfileFragment extends BaseFragment {

	private static final int MENU_ACCEPT = 1;
	private static final int MENU_COMPOSE = 2;
	private static final int MENU_ADD = 3;
	private static final int MENU_GALLERY = 4;
	private static final int MENU_SYNC = 5;

	private String username;
	private Person user;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_profile, container,
				false);
		return root;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (sam != null) {
			refreshUI(null);
		}
	}

	@Override
	public void setMenuVisibility(final boolean visible) {
		if (visible && sam != null && isAdded()) {
			setHasOptionsMenu(true);
			refreshUI(null);
		} else {
			setHasOptionsMenu(false);
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
		try {
			return username == null || username.equals(sam.getMe().getUserId());
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
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

			item = menu.add(0, MENU_SYNC, 0, R.string.profile_sync);
			item.setIcon(R.drawable.icon_sync);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

			if (getView() != null) {
				// disable edits
				getView().findViewById(R.id.name).setFocusable(false);
				getView().findViewById(R.id.summary).setFocusable(false);
			}
		} else {
			MenuItem item = menu.add(0, MENU_ACCEPT, 0,
					R.string.profile_add_yes);
			item.setIcon(R.drawable.icon_accept);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

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

				getView().findViewById(R.id.icon).setOnClickListener(
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								onClickPhoto();
							}
						});
			}
		}
	}

	private void onClickPhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, MENU_GALLERY);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == MENU_GALLERY && data != null) {
				Picture picture = null;
				if (user.getPicture().size() > 0) {
					picture = user.getPicture().iterator().next();
				} else {
					picture = sam.createPicture();
					user.addPicture(picture);
				}

				String picturePath = getPath(data.getData());
				contentClient.setBitmap(contentClient.getShortName(picture),
						getBitmap(picturePath, 300, 300));
			}
		}
	}

	public static Bitmap getBitmap(String path, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	private String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getSherlockActivity().getContentResolver().query(uri,
				projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private boolean isFriend(Person person) {
		try {
			return sam.getMe().getKnows().contains(person);
		} catch (Exception ex) {
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SYNC:
			execute(new Job() {

				String message = getString(R.string.profile_update_ok);

				@Override
				public void doWork() {
					try {
						comm.sendUpdateRequest(username);
					} catch (Exception ex) {
						message = ex.toString();
					}
				}

				@Override
				public void doUIAfter() {
					Toast.makeText(getSherlockActivity(), message,
							Toast.LENGTH_SHORT).show();
				}
			});
			break;
		case MENU_ACCEPT:
			if (user != null) {
				String agentName = getCtrlText(R.id.name);
				String summary = getCtrlText(R.id.summary);
				if (agentName.length() > 0) {
					user.setName(agentName);
				}

				if (summary.length() > 0) {
					user.setSummary(summary);
				}

				Toast.makeText(getSherlockActivity(), R.string.profile_saved,
						Toast.LENGTH_SHORT).show();
			}
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

	/**
	 * Returns the User;
	 * 
	 * @return
	 */
	private Person getUser() {
		if (sam != null) {
			try {
				if (username == null) {
					user = (Person) sam.getMe();
				} else {
					user = (Person) sam.getPersonByUserId(username);
				}
			} catch (KBException ex) {
				ex.printStackTrace();
			}
		}
		return user;
	}

	@Override
	public void refreshUI(String notification) {
		if (getView() == null) {
			return;
		}
		if (getUser() == null) {
			return;
		}

		String name = user.getName();
		if (name == null) {
			name = user.getUserId();
		}
		setCtrlText(R.id.name, Html.fromHtml(name));

		if (user.getHomepage() != null) {
			setVisible(R.id.website, true);
			setCtrlText(R.id.website, user.getHomepage());
		}

		if (user.getSummary() != null) {
			setCtrlText(R.id.summary, Html.fromHtml(user.getSummary()));
		} else {
			setCtrlText(R.id.summary, "");
		}

		ImageView image = (ImageView) getView().findViewById(R.id.icon);

		Bitmap bitmap = null;

		for (Picture picture : user.getPicture()) {
			bitmap = contentClient.getBitmap(picture);
		}
		if (bitmap != null) {
			image.setVisibility(View.VISIBLE);
			image.setImageBitmap(bitmap);
			image.setBackgroundResource(0);
		} else {
			image.setImageBitmap(null);
			image.setBackgroundResource(R.drawable.user_default);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	private void onCompose() {
		Intent intent = new Intent(getSherlockActivity(), MessageActivity.class);
		intent.putExtra(MessageActivity.UserId, user.getUserId());
		startActivity(intent);
	}

	private void onAdd() {
		execute(new Job() {

			@Override
			public void doWork() {
				try {
					sam.getMe().addKnows(user);
				} catch (Exception ex) {

				}
			}

			@Override
			public void doUIAfter() {
				try {
					String message = getString(R.string.profile_add_ok);
					Toast.makeText(getSherlockActivity(), message,
							Toast.LENGTH_SHORT).show();
				} catch (IllegalStateException ex) {
					// do nothing
				}
			}
		});
	}
}

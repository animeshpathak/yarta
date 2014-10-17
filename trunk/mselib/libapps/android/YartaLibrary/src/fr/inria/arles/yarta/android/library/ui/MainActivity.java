package fr.inria.arles.yarta.android.library.ui;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.sherlock.navigationdrawer.compat.SherlockActionBarDrawerToggle;

import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.RequestItem;
import fr.inria.arles.yarta.android.library.util.AlertDialog;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.FeedbackFragment;
import fr.inria.arles.yarta.android.library.util.Utils;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.android.library.util.Settings;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements
		AdapterView.OnItemClickListener, RequestsFragment.Callback,
		TextView.OnEditorActionListener {

	private static final int MENU_NOTIFICATIONS = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initDrawer();
		refreshUI(null);
		Utils.checkForUpdate(this);
	}

	@Override
	public void refreshUI(String notification) {
		// force first item to be selected if was initialized
		if (drawerToggle != null && drawerLayout != null && drawerList != null
				&& drawerAdapter != null) {
			onDrawerItem(currentPosition);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshRequests();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSherlock().getMenuInflater().inflate(R.menu.main, menu);
		showNotifications(menu);
		showSearchMenu(menu);
		return true;
	}

	private void showSearchMenu(Menu menu) {
		final EditText edit = (EditText) menu.findItem(R.id.main_search)
				.getActionView().findViewById(R.id.search);

		edit.setOnEditorActionListener(this);

		MenuItem menuSearch = menu.findItem(R.id.main_search);
		menuSearch.setOnActionExpandListener(new OnActionExpandListener() {

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {

				edit.post(new Runnable() {

					@Override
					public void run() {
						edit.setText("");
						edit.clearFocus();
						hideSoftKeyboard(edit);

						refreshUI(null);
					}
				});
				return true;
			}

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				drawerLayout.closeDrawer(drawerList);

				edit.post(new Runnable() {
					@Override
					public void run() {
						edit.requestFocus();
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.showSoftInput(edit,
								InputMethodManager.SHOW_IMPLICIT);
					}
				});
				return true;
			}
		});
	}

	private void hideSoftKeyboard(View v) {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			if (view.getText().toString().length() == 0) {
				Toast.makeText(this, R.string.search_empty_content,
						Toast.LENGTH_LONG).show();
			} else {
				displayFragment(searchFragment);
				searchFragment.search(view.getText().toString());
				hideSoftKeyboard(view);
			}
		}
		return true;
	}

	private void showNotifications(Menu menu) {
		if (requests.size() == 0) {
			return;
		}
		MenuItem item = menu.add(0, MENU_NOTIFICATIONS, 0,
				R.string.main_menu_notifications);
		item.setIcon(R.drawable.shape_notification);
		item.setActionView(R.layout.frame_notification);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		View count = item.getActionView();
		TextView notifCount = (TextView) count.findViewById(R.id.notif_count);
		notifCount.setText(String.valueOf(requests.size()));
		notifCount.setVisibility(View.VISIBLE);

		count.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (inRequests) {
					onDrawerItem(currentPosition);
				} else {
					onNotificationsClick();
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (drawerLayout.isDrawerOpen(drawerList)) {
				drawerLayout.closeDrawer(drawerList);
			} else {
				drawerLayout.openDrawer(drawerList);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onDrawerToggle();
			break;
		default:
			super.onOptionsItemSelected(item);
			break;
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.equals(drawerList)) {
			onDrawerItem(position);
		}
	}

	private void onLogoutClicked() {
		AlertDialog.show(this, getString(R.string.main_logout_are_you_sure),
				getString(R.string.main_logout_confirm),
				getString(R.string.main_logout_ok),
				getString(R.string.main_logout_cancel),
				new AlertDialog.Handler() {

					@Override
					public void onOK() {
						settings.setString(Settings.USER_TOKEN, null);
						clearMSE();
						finish();
					}
				});
	}

	private void initDrawer() {
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		drawerList = (ListView) findViewById(R.id.listview_drawer);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		initSideMenu();
		drawerAdapter = new MenuListAdapter(this, sideMenuItems);

		drawerList.setAdapter(drawerAdapter);
		drawerList.setOnItemClickListener(this);

		drawerToggle = new SherlockActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close);
		drawerToggle.syncState();

		drawerLayout.setDrawerListener(drawerToggle);
		drawerToggle.setDrawerIndicatorEnabled(true);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	private void onDrawerToggle() {
		if (drawerLayout.isDrawerOpen(drawerList)) {
			drawerLayout.closeDrawer(drawerList);
		} else {
			drawerLayout.openDrawer(drawerList);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO: if in requests come back to previous?
		if (currentPosition != 0 || inRequests) {
			onDrawerItem(0);
		} else {
			finish();
		}
	}

	private int currentPosition = 0;

	private void onDrawerItem(int position) {
		drawerList.setItemChecked(currentPosition, true);

		if (position == sideMenuItems.size() - 1) {
			drawerLayout.closeDrawer(drawerList);
			onLogoutClicked();
			return;
		} else {
			currentPosition = position;
			drawerList.setItemChecked(currentPosition, true);
		}
		setTitle(sideMenuItems.get(position).getText());
		displayFragment(sideMenuItems.get(position).getFragment());

		drawerLayout.closeDrawer(drawerList);
		inRequests = false;
	}

	/**
	 * Replaces the window content with the specified fragment.
	 * 
	 * @param fragment
	 */
	private void displayFragment(BaseFragment fragment) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		fragment.setRunner(runner);
		fragment.setSAM(getSAM());
		fragment.setContentClient(contentClient);

		if (!fragment.isAdded()) {
			ft.replace(R.id.content_frame, fragment);
		} else {
			fragment.refreshUI(null);
		}
		ft.commit();
	}

	private void initSideMenu() {
		sideMenuItems.add(new SideMenuItem(getString(R.string.main_river),
				R.drawable.drawer_river, new RiverFragment()));
		sideMenuItems.add(new SideMenuItem(getString(R.string.main_wire),
				R.drawable.drawer_wire, new WireFragment()));
		sideMenuItems.add(new SideMenuItem(getString(R.string.main_profile),
				R.drawable.drawer_profile, new ProfileFragment()));
		sideMenuItems.add(new SideMenuItem(getString(R.string.main_friends),
				R.drawable.drawer_contacts, new FriendsFragment()));
		sideMenuItems.add(new SideMenuItem(getString(R.string.main_groups),
				R.drawable.drawer_groups, new GroupsFragment()));
		sideMenuItems.add(new SideMenuItem(getString(R.string.main_message),
				R.drawable.drawer_messages, new ThreadsFragment()));
		sideMenuItems.add(new SideMenuItem(getString(R.string.feedback_title),
				R.drawable.drawer_feedback, new FeedbackFragment()));
		sideMenuItems.add(new SideMenuItem(getString(R.string.main_logout),
				R.drawable.drawer_disconnect, null));
	}

	private void onNotificationsClick() {
		setTitle(getString(R.string.main_menu_notifications));
		displayFragment(requestsFragment);
		inRequests = true;
	}

	private void refreshRequests() {
		execute(new Job() {

			@Override
			public void doWork() {
				requests = requestsFragment.getRequests();
			}

			@Override
			public void doUIAfter() {
				supportInvalidateOptionsMenu();
			}
		});
	}

	@Override
	public void onRefresh(List<RequestItem> requests) {
		this.requests = requests;
		supportInvalidateOptionsMenu();
	}

	private boolean inRequests;
	private RequestsFragment requestsFragment = new RequestsFragment();
	private SearchFragment searchFragment = new SearchFragment();
	private List<RequestItem> requests = new ArrayList<RequestItem>();

	private SherlockActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private MenuListAdapter drawerAdapter;
	private List<SideMenuItem> sideMenuItems = new ArrayList<SideMenuItem>();
}

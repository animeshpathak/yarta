package fr.inria.arles.yarta.android.library;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.RequestItem;
import fr.inria.arles.yarta.android.library.util.AlertDialog;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.FeedbackDialog;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.android.library.util.Settings;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements
		AdapterView.OnItemClickListener, RequestsFragment.Callback {

	private static final int MENU_NOTIFICATIONS = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initDrawer();
		refreshUI();
	}

	@Override
	public void refreshUI() {
		// force first item to be selected
		onDrawerItem(currentPosition);
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

		MenuItem item = menu.add(0, MENU_NOTIFICATIONS, 0,
				R.string.main_menu_notifications);
		item.setIcon(R.drawable.shape_notification);
		item.setActionView(R.layout.frame_notification);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		View count = item.getActionView();
		TextView notifCount = (TextView) count.findViewById(R.id.notif_count);
		notifCount.setText(String.valueOf(requests.size()));
		notifCount.setVisibility(requests.size() == 0 ? View.GONE
				: View.VISIBLE);

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
		return true;
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

	private void onFeedbackClicked() {
		FeedbackDialog dlg = new FeedbackDialog(this);
		dlg.setRunner(runner);
		dlg.show();
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

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			@Override
			public void onDrawerClosed(View drawerView) {
				supportInvalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				supportInvalidateOptionsMenu();
			}
		};

		drawerLayout.setDrawerListener(drawerToggle);
		drawerToggle.setDrawerIndicatorEnabled(true);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
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
		} else if (position == sideMenuItems.size() - 2) {
			drawerLayout.closeDrawer(drawerList);
			onFeedbackClicked();
			return;
		} else {
			currentPosition = position;
			drawerList.setItemChecked(currentPosition, true);
		}

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		BaseFragment fragment = sideMenuItems.get(position).getFragment();
		setTitle(sideMenuItems.get(position).getText());

		if (!fragment.isAdded()) {
			ft.replace(R.id.content_frame, fragment);
		} else {
			fragment.refreshUI();
		}
		ft.commit();

		// setTitle(sideMenuItems.get(position).getText());
		drawerLayout.closeDrawer(drawerList);
		inRequests = false;
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
		sideMenuItems.add(new SideMenuItem(getString(R.string.main_search),
				R.drawable.drawer_search, new SearchFragment()));
		sideMenuItems.add(new SideMenuItem(getString(R.string.feedback_title),
				R.drawable.drawer_feedback, null));
		sideMenuItems.add(new SideMenuItem(getString(R.string.main_logout),
				R.drawable.drawer_disconnect, null));

		for (SideMenuItem item : sideMenuItems) {
			BaseFragment fragment = item.getFragment();

			if (fragment != null) {
				fragment.setRunner(runner);
			}
		}
	}

	private void onNotificationsClick() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		requestsFragment.setRunner(runner);
		requestsFragment.setCallback(this);

		setTitle(getString(R.string.main_menu_notifications));

		if (!requestsFragment.isAdded()) {
			ft.replace(R.id.content_frame, requestsFragment);
		} else {
			requestsFragment.refreshUI();
		}
		ft.commit();
		inRequests = true;
	}

	private void refreshNotificationsMenu() {
		supportInvalidateOptionsMenu();
	}

	private void refreshRequests() {
		execute(new Job() {

			@Override
			public void doWork() {
				requests = requestsFragment.getRequests();
			}

			@Override
			public void doUIAfter() {
				refreshNotificationsMenu();
			}
		});
	}

	@Override
	public void onRefresh(List<RequestItem> requests) {
		this.requests = requests;
		refreshNotificationsMenu();
	}

	private boolean inRequests;
	private RequestsFragment requestsFragment = new RequestsFragment();
	private List<RequestItem> requests = new ArrayList<RequestItem>();

	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private MenuListAdapter drawerAdapter;
	private List<SideMenuItem> sideMenuItems = new ArrayList<SideMenuItem>();
}

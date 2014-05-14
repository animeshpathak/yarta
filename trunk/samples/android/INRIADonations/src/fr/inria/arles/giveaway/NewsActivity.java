package fr.inria.arles.giveaway;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.inria.arles.giveaway.util.JobRunner.Job;
import fr.inria.arles.giveaway.util.MenuListAdapter;
import fr.inria.arles.giveaway.util.SideMenuItem;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.view.GravityCompat;

public class NewsActivity extends BaseActivity implements
		ListView.OnItemClickListener, FeedbackDialog.Handler {

	private static final int MENU_ADD = 1;

	private static final int SIDE_MENU_EDIT_PROFILE = 0;
	private static final int SIDE_MENU_MY_ADS = 1;

	private static final int SIDE_MENU_DONATIONS = 2;
	private static final int SIDE_MENU_REQUESTS = 3;

	private static final int SIDE_MENU_SALES = 4;

	private static final int SIDE_MENU_FEEDBACK = 5;
	private static final int SIDE_MENU_LOGOUT = 6;

	private MenuItem addMenuItem;
	private int currentView = -1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		addMenuItem = menu.add(0, MENU_ADD, 0, R.string.app_name);
		addMenuItem.setIcon(R.drawable.icon_add);
		addMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	private NewsFragment newsFragment = new NewsFragment();
	private List<SideMenuItem> sideMenuItems = new ArrayList<SideMenuItem>();

	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private MenuListAdapter drawerAdapter;

	public void onClickLogout() {
		clearMSE();
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		initDrawer();
	}

	@Override
	protected void onDestroy() {
		uninitMSE();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			refreshUI();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void refreshUI() {
		try {
			if (currentView == -1) {
				onDrawerItem(SIDE_MENU_DONATIONS);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logError("refreshUI onDrawerItem: %s", ex.getMessage());
		}
		newsFragment.refreshUI();
	}

	private void onSendFeedback() {
		FeedbackDialog dlg = new FeedbackDialog(this);
		dlg.setHandler(this);
		dlg.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onDrawerToggle();
			break;
		case MENU_ADD:
			onMenuAdd();
			break;
		}
		return false;
	}

	private void onMenuAdd() {
		Intent intent = new Intent(this, ItemActivity.class);
		switch (currentView) {
		case SIDE_MENU_DONATIONS:
			intent.putExtra(ItemActivity.Type, "0");
			break;
		case SIDE_MENU_REQUESTS:
			intent.putExtra(ItemActivity.Type, "1");
			break;
		case SIDE_MENU_SALES:
			intent.putExtra(ItemActivity.Type, "2");
			break;
		}
		startActivity(intent);
	}

	private void onDrawerToggle() {
		if (drawerLayout.isDrawerOpen(drawerList)) {
			drawerLayout.closeDrawer(drawerList);
		} else {
			drawerLayout.openDrawer(drawerList);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.equals(drawerList)) {
			onDrawerItem(position);
		}
	}

	private void onDrawerItem(int position) {
		drawerLayout.closeDrawer(drawerList);

		if (position == SIDE_MENU_EDIT_PROFILE || position == 0) {
			try {
				String userId = getSAM().getMe().getUserId();
				Intent intent = new Intent("Yarta.Profile");
				intent.putExtra("UserName",
						userId.substring(0, userId.indexOf('@')));
				startActivity(intent);
			} catch (Exception ex) {
				// TODO: some logging;
			}
			return;
		}
		if (position == SIDE_MENU_FEEDBACK) {
			onSendFeedback();
			return;
		}

		if (position == SIDE_MENU_LOGOUT) {
			onClickLogout();
			return;
		}

		int filterType = BaseFragment.FILTER_NONE;

		boolean addvisible = true;

		switch (position) {
		case SIDE_MENU_MY_ADS:
			addvisible = false;
			filterType = BaseFragment.FILTER_MINE;
			break;
		case SIDE_MENU_DONATIONS:
			filterType = BaseFragment.FILTER_DONATIONS;
			break;
		case SIDE_MENU_SALES:
			filterType = BaseFragment.FILTER_SALES;
			break;
		case SIDE_MENU_REQUESTS:
			filterType = BaseFragment.FILTER_REQUESTS;
			break;
		}

		if (addMenuItem != null) {
			addMenuItem.setVisible(addvisible);
		}

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		newsFragment.setFilter(filterType);

		if (!newsFragment.isAdded()) {
			ft.replace(R.id.content_frame, newsFragment);
		} else {
			if (getSAM() == null) {
				initMSE();
			} else {
				newsFragment.refreshUI();
			}
		}
		ft.commit();

		drawerList.setItemChecked(position, true);

		setTitle(sideMenuItems.get(position).getText());
		drawerAdapter.setSelected(position);
		drawerLayout.closeDrawer(drawerList);

		currentView = position;
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
			// TODO: implement?
		};

		drawerLayout.setDrawerListener(drawerToggle);
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

	@Override
	public void setTitle(CharSequence title) {
		getSupportActionBar().setTitle(title);
	}

	private void initSideMenu() {
		sideMenuItems.add(new SideMenuItem(
				getString(R.string.news_edit_profile), SIDE_MENU_EDIT_PROFILE));
		sideMenuItems.add(new SideMenuItem(getString(R.string.news_my_ads),
				SIDE_MENU_MY_ADS));

		sideMenuItems.add(new SideMenuItem(getString(R.string.news_donations),
				SIDE_MENU_DONATIONS));
		sideMenuItems.add(new SideMenuItem(getString(R.string.news_requests),
				SIDE_MENU_REQUESTS));
		sideMenuItems.add(new SideMenuItem(getString(R.string.news_sales),
				SIDE_MENU_SALES));

		sideMenuItems.add(new SideMenuItem(
				getString(R.string.news_send_feedback), SIDE_MENU_FEEDBACK));
		sideMenuItems.add(new SideMenuItem(
				getString(R.string.news_profile_logout), SIDE_MENU_LOGOUT));
	}

	@Override
	public void onSendFeedback(final String content) {
		execute(new Job() {
			boolean success;

			@Override
			public void doWork() {
				String userId = "";

				try {
					userId = getSAM().getMe().getUserId();
				} catch (Exception ex) {
					userId = "err";
				}

				success = FeedbackDialog.sendFeedback(
						"fr.inria.arles.giveaway", userId, content);
			}

			@Override
			public void doUIAfter() {
				Toast.makeText(
						getApplicationContext(),
						success ? R.string.main_feedback_sent_ok
								: R.string.main_feedback_sent_error,
						Toast.LENGTH_LONG).show();
			}
		});
	}
}

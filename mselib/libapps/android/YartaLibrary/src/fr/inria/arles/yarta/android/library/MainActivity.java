package fr.inria.arles.yarta.android.library;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.util.AlertDialog;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.FeedbackDialog;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends BaseActivity implements
		AdapterView.OnItemClickListener {

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
	public boolean onCreateOptionsMenu(Menu menu) {
		getSherlock().getMenuInflater().inflate(R.menu.main, menu);
		return true;
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

	private void onLogout() {
		AlertDialog.show(this, getString(R.string.main_logout_are_you_sure),
				getString(R.string.main_logout_confirm),
				getString(R.string.main_logout_ok),
				getString(R.string.main_logout_cancel),
				new AlertDialog.Handler() {

					@Override
					public void onOK() {
						getMSE().clear();
						uninitMSE();
						finish();
					}
				});
	}

	private void onFeedback() {
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
		if (currentPosition != 0) {
			onDrawerItem(0);
		} else {
			finish();
		}
	}

	private int currentPosition = 0;

	private void onDrawerItem(int position) {

		// TODO: do this better
		if (position == sideMenuItems.size() - 1) {
			trackUI("Logout");
			drawerLayout.closeDrawer(drawerList);
			onLogout();
			return;
		} else if (position == sideMenuItems.size() - 2) {
			trackUI("Feedback");
			drawerLayout.closeDrawer(drawerList);
			onFeedback();
			return;
		}

		currentPosition = position;

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		BaseFragment fragment = sideMenuItems.get(position).getFragment();
		setTitle(sideMenuItems.get(position).getText());

		trackUI(sideMenuItems.get(position).getText());
		if (!fragment.isAdded()) {
			ft.replace(R.id.content_frame, fragment);
		} else {
			fragment.refreshUI();
		}
		ft.commit();

		drawerList.setItemChecked(position, true);

		// setTitle(sideMenuItems.get(position).getText());
		drawerAdapter.setSelected(position);
		drawerLayout.closeDrawer(drawerList);
	}

	private void initSideMenu() {
		// TODO: add the fragments

		sideMenuItems.add(new SideMenuItem(getString(R.string.main_river),
				new RiverFragment()));
		sideMenuItems.add(new SideMenuItem(getString(R.string.main_wire),
				new WireFragment()));
		sideMenuItems.add(new SideMenuItem(getString(R.string.main_profile),
				new ProfileFragment()));
		sideMenuItems.add(new SideMenuItem(getString(R.string.main_friends),
				new FriendsFragment()));
		sideMenuItems.add(new SideMenuItem(getString(R.string.main_groups),
				new GroupsFragment()));
		sideMenuItems.add(new SideMenuItem(getString(R.string.main_message),
				new MessagesFragment()));
		sideMenuItems.add(new SideMenuItem(getString(R.string.main_search),
				new SearchFragment()));
		sideMenuItems.add(new SideMenuItem(getString(R.string.feedback_title),
				null));
		sideMenuItems.add(new SideMenuItem(getString(R.string.main_logout),
				null));

		for (SideMenuItem item : sideMenuItems) {
			BaseFragment fragment = item.getFragment();

			if (fragment != null) {
				fragment.setRunner(runner);
			}
		}
	}

	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private MenuListAdapter drawerAdapter;
	private List<SideMenuItem> sideMenuItems = new ArrayList<SideMenuItem>();
}

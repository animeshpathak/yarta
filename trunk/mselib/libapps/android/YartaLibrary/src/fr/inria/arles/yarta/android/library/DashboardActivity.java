package fr.inria.arles.yarta.android.library;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.inria.arles.yarta.R;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.android.library.util.MenuListAdapter;
import fr.inria.arles.yarta.android.library.util.SideMenuItem;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class DashboardActivity extends BaseActivity implements
		AdapterView.OnItemClickListener, FeedbackDialog.Handler {

	public static final int MENU_ABOUT = 1;
	public static final int MENU_CONSOLE = 2;
	public static final int MENU_FEEDBACK = 5;

	// DRAWER
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private MenuListAdapter drawerAdapter;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent == drawerList) {
			drawerLayout.closeDrawer(drawerList);
			switch (position) {
			case 0:
				break;
			case 1:
				try {
					Intent intent = new Intent(this, PersonActivity.class);
					startActivity(intent);
				} catch (Exception ex) {
					Toast.makeText(this, R.string.dashboard_error,
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 2:
				getMSE().clear();
				uninitMSE();
				finish();
				break;
			}
		}
	}

	private void initDrawer() {
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		drawerList = (ListView) findViewById(R.id.listview_drawer);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		List<SideMenuItem> list = new ArrayList<SideMenuItem>();
		list.add(new SideMenuItem(getString(R.string.dashboard_dashboard), 0));
		list.add(new SideMenuItem(getString(R.string.dashboard_profile), 1));
		list.add(new SideMenuItem(getString(R.string.dashboard_signout), 2));
		drawerAdapter = new MenuListAdapter(this, list);

		drawerList.setAdapter(drawerAdapter);
		drawerList.setOnItemClickListener(this);

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_dashboard);
		initDrawer();
	}

	@Override
	protected void onDestroy() {
		uninitMSE();
		super.onDestroy();
	}

	public void onClickPeople(View view) {
		Intent intent = new Intent(this, PeopleActivity.class);
		startActivity(intent);
	}

	public void onClickGroups(View view) {
		Intent intent = new Intent(this, GroupsActivity.class);
		startActivity(intent);
	}

	public void onClickEvents(View view) {
		Intent intent = new Intent(this, EventsActivity.class);
		startActivity(intent);
	}

	public void onClickMessages(View view) {
		Intent intent = new Intent(this, ConversationsActivity.class);
		startActivity(intent);
	}

	/**
	 * Constructs the contextual menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_CONSOLE, 0, R.string.dashboard_menu_console);
		menu.add(0, MENU_ABOUT, 0, R.string.dashboard_menu_about);

		MenuItem item = menu.add(0, MENU_FEEDBACK, 0,
				R.string.dashboard_menu_feedback);
		item.setIcon(R.drawable.icon_feedback);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	/**
	 * A menu item from contextual menu was pressed.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (drawerLayout.isDrawerOpen(drawerList)) {
				drawerLayout.closeDrawer(drawerList);
			} else {
				drawerLayout.openDrawer(drawerList);
			}
			break;
		case MENU_ABOUT:
			trackUI("DashboardActivity.About");
			startActivity(new Intent(this, AboutActivity.class));
			break;
		case MENU_CONSOLE:
			trackUI("DashboardActivity.Console");
			startActivity(new Intent(this, ConsoleActivity.class));
			break;
		case MENU_FEEDBACK:
			trackUI("DashboardActivity.Feedback");
			FeedbackDialog dlg = new FeedbackDialog(this);
			dlg.setHandler(this);
			dlg.show();
			break;
		}
		return true;
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

				success = FeedbackDialog.sendFeedback("fr.inria.arles.yarta",
						userId, content);
			}

			@Override
			public void doUIAfter() {
				Toast.makeText(
						getApplicationContext(),
						success ? R.string.feedback_sent_ok
								: R.string.feedback_sent_error,
						Toast.LENGTH_LONG).show();
			}
		});
	}
}

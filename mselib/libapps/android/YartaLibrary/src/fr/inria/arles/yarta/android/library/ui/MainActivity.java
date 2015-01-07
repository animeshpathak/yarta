package fr.inria.arles.yarta.android.library.ui;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.astuetz.PagerSlidingTabStrip;

import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.RequestItem;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.FeedbackFragment;
import fr.inria.arles.yarta.android.library.util.IconPageAdapter;
import fr.inria.arles.yarta.android.library.util.Utils;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements
		RequestsFragment.Callback, TextView.OnEditorActionListener {

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
		showView(R.id.content);
		for (int i = 0; i < adapter.getCount(); i++) {
			BaseFragment fragment = (BaseFragment) adapter.getItem(i);
			if (fragment != null) {
				fragment.setRunner(runner);
				fragment.setSAM(getSAM());
				fragment.setContentClient(contentClient);
			}
		}

		int currentPage = pager.getCurrentItem();
		if (currentPage > -1 && currentPage < adapter.getCount()) {
			BaseFragment fragment = (BaseFragment) adapter.getItem(currentPage);
			fragment.refreshUI(notification);
		}
	}

	@Override
	public void onBackPressed() {
		if (findViewById(R.id.content).getVisibility() != View.VISIBLE) {
			showView(R.id.content);
		} else {
			super.onBackPressed();
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

	MenuItem menuSearch;

	private void showSearchMenu(Menu menu) {
		final EditText edit = (EditText) menu.findItem(R.id.main_search)
				.getActionView().findViewById(R.id.search);

		edit.setOnEditorActionListener(this);

		menuSearch = menu.findItem(R.id.main_search);
		menuSearch.setOnActionExpandListener(new OnActionExpandListener() {

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				edit.post(new Runnable() {

					@Override
					public void run() {
						edit.setText("");
						edit.clearFocus();
						hideSoftKeyboard();

						refreshUI(null);
					}
				});
				return true;
			}

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
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

	private void hideSoftKeyboard() {
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
				displayFragment(R.id.searchView, searchFragment);
				showView(R.id.searchView);
				searchFragment.search(view.getText().toString());
				hideSoftKeyboard();
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
				if (findViewById(R.id.requests).getVisibility() == View.VISIBLE) {
					showView(R.id.content);
				} else {
					onNotificationsClick();
				}
			}
		});
	}

	private void initDrawer() {

		initSideMenu();

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);

		pager.setAdapter(adapter);
		tabs.setViewPager(pager);

		tabs.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int selected) {
				setTitle(adapter.getPageTitle(selected));
				if (menuSearch.isActionViewExpanded()) {
					menuSearch.collapseActionView();
				}

				hideSoftKeyboard();

				BaseFragment fragment = (BaseFragment) adapter
						.getItem(selected);
				fragment.setSAM(getSAM());
				fragment.setContentClient(contentClient);
				fragment.refreshUI(null);
			}

			@Override
			public void onPageScrolled(int selected, float f, int i) {
			}

			@Override
			public void onPageScrollStateChanged(int selected) {
			}
		});
	}

	/**
	 * Attaches a fragment to a view id;
	 * 
	 * @param viewId
	 * @param fragment
	 */
	private void displayFragment(int viewId, BaseFragment fragment) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		fragment.setRunner(runner);
		fragment.setSAM(getSAM());
		fragment.setContentClient(contentClient);

		if (!fragment.isAdded()) {
			ft.replace(viewId, fragment);
		} else {
			fragment.refreshUI(null);
		}
		ft.commit();
	}

	private void addFragment(int titleId, int iconId, BaseFragment fragment) {
		if (fragment != null) {
			fragment.setRunner(runner);
			adapter.addFragment(fragment, titleId, iconId);
		}
	}

	private void initSideMenu() {
		adapter = new IconPageAdapter(getSupportFragmentManager(), this);

		addFragment(R.string.main_river, R.drawable.drawer_river,
				new RiverFragment());
		addFragment(R.string.main_wire, R.drawable.drawer_wire,
				new WireFragment());
		addFragment(R.string.main_profile, R.drawable.drawer_profile,
				new ProfileFragment());
		addFragment(R.string.main_friends, R.drawable.drawer_contacts,
				new FriendsFragment());
		addFragment(R.string.main_groups, R.drawable.drawer_groups,
				new GroupsFragment());
		addFragment(R.string.main_message, R.drawable.drawer_messages,
				new ThreadsFragment());
		addFragment(R.string.feedback_title, R.drawable.drawer_feedback,
				new FeedbackFragment());
		addFragment(R.string.main_logout, R.drawable.drawer_disconnect, null);
	}

	/**
	 * Toggles the visibility of one of the content, requests or search views.
	 * 
	 * @param viewId
	 */
	private void showView(int viewId) {
		findViewById(R.id.searchView).setVisibility(
				viewId == R.id.searchView ? View.VISIBLE : View.GONE);
		findViewById(R.id.requests).setVisibility(
				viewId == R.id.requests ? View.VISIBLE : View.GONE);
		findViewById(R.id.content).setVisibility(
				viewId == R.id.content ? View.VISIBLE : View.GONE);

		if (viewId == R.id.content) {
			int currentPage = pager.getCurrentItem();
			if (currentPage > -1 && currentPage < adapter.getCount()) {
				setTitle(adapter.getPageTitle(currentPage));
			}
		}
	}

	private void onNotificationsClick() {
		setTitle(getString(R.string.main_menu_notifications));
		displayFragment(R.id.requests, requestsFragment);
		showView(R.id.requests);
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

	private RequestsFragment requestsFragment = new RequestsFragment();
	private SearchFragment searchFragment = new SearchFragment();

	private List<RequestItem> requests = new ArrayList<RequestItem>();

	private IconPageAdapter adapter;

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
}

package fr.inria.arles.yarta.android.library.ui;

import java.util.Set;

import com.actionbarsherlock.view.Menu;
import com.astuetz.PagerSlidingTabStrip;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.util.AlertDialog;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.FeedbackFragment;
import fr.inria.arles.yarta.android.library.util.IconPageAdapter;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.android.library.util.Utils;
import fr.inria.arles.yarta.resources.Agent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initDrawer();
		Utils.checkForUpdate(this);
	}

	@Override
	protected void onDestroy() {
		uninitMSE();
		super.onDestroy();
	}

	@Override
	public void refreshUI(String notification) {
		showView(R.id.content);
		setupFragments();

		int currentPage = pager.getCurrentItem();
		if (currentPage > -1 && currentPage < adapter.getCount()) {
			BaseFragment fragment = (BaseFragment) adapter.getItem(currentPage);
			fragment.refreshUI(notification);
		}

		refreshNotifications();
	}

	@Override
	public void onBackPressed() {
		if (findViewById(R.id.content).getVisibility() != View.VISIBLE) {
			showView(R.id.content);
		} else {
			super.onBackPressed();
		}
	}

	private void setupFragments() {
		for (int i = 0; i < adapter.getCount(); i++) {
			BaseFragment fragment = (BaseFragment) adapter.getItem(i);
			if (fragment != null) {
				fragment.setRunner(runner);
				fragment.setSAM(getSAM());
				fragment.setCOMM(getCOMM());
				fragment.setContentClient(contentClient);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSherlock().getMenuInflater().inflate(R.menu.main, menu);
		return true;
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

				BaseFragment fragment = (BaseFragment) adapter
						.getItem(selected);
				fragment.setSAM(getSAM());
				fragment.setContentClient(contentClient);
			}

			@Override
			public void onPageScrolled(int selected, float f, int i) {
			}

			@Override
			public void onPageScrollStateChanged(int selected) {
			}
		});
	}

	private void addFragment(int titleId, int iconId, BaseFragment fragment) {
		if (fragment != null) {
			fragment.setRunner(runner);
			adapter.addFragment(fragment, titleId, iconId);
		}
	}

	private void initSideMenu() {
		adapter = new IconPageAdapter(getSupportFragmentManager(), this);
		addFragment(R.string.main_profile, R.drawable.drawer_profile,
				new ProfileFragment());
		addFragment(R.string.main_friends, R.drawable.drawer_contacts,
				new FriendsFragment());
		// addFragment(R.string.main_groups, R.drawable.drawer_groups,
		// new GroupsFragment());
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

	private void refreshNotifications() {
		try {
			final Person me = (Person) getSAM().getMe();

			Set<Agent> known = me.getKnows();
			for (Agent agent : me.getKnows_inverse()) {
				if (!known.contains(agent)) {
					// it is not known
					final Agent a = agent;
					String name = agent.getName();
					if (name == null) {
						name = agent.getUniqueId();
						name = name.substring(name.indexOf('_') + 1);
					}
					String message = String.format(
							getString(R.string.person_knows_you), name);

					AlertDialog.show(this, message,
							getString(R.string.person_knows_you_title),
							getString(R.string.person_knows_you_ok),
							getString(R.string.person_knows_you_cancel),
							new AlertDialog.Handler() {

								@Override
								public void onOK() {

									// add him back
									me.addKnows(a);

									// send him a notification
									execute(new Job() {
										@Override
										public void doWork() {
											String userId = a.getUniqueId();
											userId = userId.substring(userId
													.indexOf('_') + 1);
											getCOMM().sendNotify(userId);
										}
									});
								}
							});
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private IconPageAdapter adapter;

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
}

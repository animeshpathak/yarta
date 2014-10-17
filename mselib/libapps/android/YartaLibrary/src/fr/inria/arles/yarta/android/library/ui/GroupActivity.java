package fr.inria.arles.yarta.android.library.ui;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.astuetz.PagerSlidingTabStrip;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.widget.Toast;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.resources.Group;
import fr.inria.arles.yarta.android.library.resources.GroupImpl;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.GenericPageAdapter;
import fr.inria.arles.yarta.knowledgebase.MSEResource;

public class GroupActivity extends BaseActivity implements
		ContentDialog.Callback {

	private static final int MENU_JOIN = 1;
	private static final int MENU_ADD = 2;

	public static final String GroupGuid = "GroupGuid";

	private String groupGuid;
	private Group group;

	private GenericPageAdapter adapter;

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group);

		groupGuid = getIntent().getStringExtra(GroupGuid);

		adapter = new GenericPageAdapter(getSupportFragmentManager(), this);

		initFragments();

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);

		pager.setAdapter(adapter);
		tabs.setViewPager(pager);

		if (!getIntent().hasExtra("Standalone")) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		} else {
			// discussions
			pager.setCurrentItem(1);
		}
	}

	/**
	 * Fills the adapter with Fragments;
	 */
	private void initFragments() {
		adapter.addFragment(new GroupDescriptionFragment(),
				R.string.group_description);
		adapter.addFragment(new GroupPostsFragment(), R.string.group_posts);
		adapter.addFragment(new RiverFragment(), R.string.group_activity);

		setFragmentsData();
	}

	/**
	 * Sets the needed data to Fragments. Information such as JobRunner,
	 * StorageAccessManager, Group Id, Content Client.
	 */
	private void setFragmentsData() {
		for (int i = 0; i < adapter.getCount(); i++) {
			BaseFragment fragment = (BaseFragment) adapter.getItem(i);
			fragment.setRunner(runner);
			fragment.setSAM(getSAM());
			fragment.setGroupGuid(groupGuid);
			fragment.setContentClient(contentClient);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		refreshUI(null);
	}

	@Override
	public void refreshUI(String notification) {
		if (getSAM() == null) {
			return;
		}
		group = new GroupImpl(getSAM(), new MSEResource(groupGuid,
				Group.typeURI));
		if (group.getName() != null) {
			setTitle(Html.fromHtml(group.getName()));
		}

		setFragmentsData();

		for (int i = 0; i < adapter.getCount(); i++) {
			BaseFragment fragment = (BaseFragment) adapter.getItem(i);
			if (fragment.isAdded()) {
				fragment.refreshUI(notification);
			}
		}

		supportInvalidateOptionsMenu();
	}

	/**
	 * Checks if current user is member of this group.
	 * 
	 * @param group
	 * @return
	 */
	private boolean isMemberOf(Group group) {
		try {
			return getSAM().getMe().getIsMemberOf().contains(group);
		} catch (Exception ex) {
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (group != null && !isMemberOf(group)) {
			MenuItem item = menu.add(0, MENU_JOIN, 0, R.string.group_title);
			item.setIcon(R.drawable.icon_join_group);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		MenuItem item = menu.add(0, MENU_ADD, 0, R.string.group_title);
		item.setIcon(R.drawable.icon_add);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_JOIN:
			try {
				if (group != null) {
					Person person = getSAM().getMe();
					person.addIsMemberOf(group);
					Toast.makeText(this, R.string.group_join_sent,
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			break;
		case MENU_ADD:
			ContentDialog dlg = new ContentDialog(this, group);
			dlg.setCallback(this);
			dlg.setSAM(getSAM());
			dlg.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onContentAdded() {
		refreshUI(null);
	}
}

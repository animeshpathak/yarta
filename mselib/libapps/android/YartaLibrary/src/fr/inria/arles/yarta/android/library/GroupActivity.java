package fr.inria.arles.yarta.android.library;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.GroupItem;
import fr.inria.arles.iris.web.ImageCache;
import fr.inria.arles.yarta.android.library.util.GenericPageAdapter;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;

public class GroupActivity extends BaseActivity {

	public static final String GroupGuid = "GroupGuid";

	private String groupGuid;

	private GenericPageAdapter adapter;
	private TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		groupGuid = getIntent().getStringExtra(GroupGuid);

		adapter = new GenericPageAdapter(getSupportFragmentManager(), this);

		RiverFragment fragmentActivity = new RiverFragment();
		fragmentActivity.setRunner(runner);
		fragmentActivity.setGroupGuid(groupGuid);
		adapter.addFragment(fragmentActivity, R.string.group_activity);

		GroupPostsFragment fragmentPosts = new GroupPostsFragment();
		fragmentPosts.setRunner(runner);
		fragmentPosts.setGroupGuid(groupGuid);
		adapter.addFragment(fragmentPosts, R.string.group_posts);

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		TabSpec tab1 = tabHost.newTabSpec("0");
		TabSpec tab2 = tabHost.newTabSpec("1");

		tab1.setIndicator(createTabView(R.string.group_activity));
		tab1.setContent(R.id.container);

		tab2.setIndicator(createTabView(R.string.group_posts));
		tab2.setContent(R.id.container);

		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				tabHost.clearFocus();
				int position = tabHost.getCurrentTab();
				Fragment fragment = adapter.getItem(position);
				FragmentTransaction ft = getSupportFragmentManager()
						.beginTransaction();
				if (!fragment.isAdded()) {
					ft.replace(R.id.container, fragment);
					ft.commit();
				}
			}
		});

		tabHost.addTab(tab1);
		tabHost.addTab(tab2);

		// ViewPager pager = (ViewPager) findViewById(R.id.pager);
		// pager.setAdapter(adapter);
	}

	private View createTabView(int textId) {
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.item_tab, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(textId);
		return view;
	}

	@Override
	protected void onResume() {
		super.onResume();

		refreshUI();
	}

	public void refreshUI() {
		runner.runBackground(new Job() {

			private GroupItem group;

			@Override
			public void doWork() {
				group = client.getGroup(groupGuid);

				if (group != null) {
					String avatarURL = group.getAvatarURL();
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
				if (group == null) {
					return;
				} else if (group.getName() == null) {
					return;
				}
				String name = String.format(getString(R.string.group_name),
						group.getName());
				setCtrlText(R.id.name, Html.fromHtml(name));

				String members = String.format(
						getString(R.string.group_members), group.getMembers());
				setCtrlText(R.id.members, members);

				String owner = String.format(getString(R.string.group_owner),
						group.getOwnerName());
				setCtrlText(R.id.owner, Html.fromHtml(owner));

				ImageView image = (ImageView) findViewById(R.id.icon);
				Drawable drawable = ImageCache.getDrawable(group.getAvatarURL());
				if (drawable != null) {
					image.setImageDrawable(drawable);
				} else {
					image.setImageResource(R.drawable.group_default);
				}
			}
		});
	}

}

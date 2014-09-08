package fr.inria.arles.yarta.android.library;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.resources.Group;
import fr.inria.arles.yarta.android.library.resources.GroupImpl;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.resources.Picture;
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
	private TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		groupGuid = getIntent().getStringExtra(GroupGuid);
		group = new GroupImpl(getSAM(), new MSEResource(groupGuid,
				Group.typeURI));

		adapter = new GenericPageAdapter(getSupportFragmentManager(), this);

		RiverFragment fragmentActivity = new RiverFragment();
		fragmentActivity.setRunner(runner);
		fragmentActivity.setGroupGuid(groupGuid);
		adapter.addFragment(fragmentActivity, R.string.group_activity);

		GroupPostsFragment fragmentPosts = new GroupPostsFragment();
		fragmentPosts.setRunner(runner);
		fragmentPosts.setSAM(getSAM());
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
		if (group == null) {
			return;
		}

		if (group.getName() != null) {
			setCtrlText(R.id.name, Html.fromHtml(group.getName()));
		}

		try {
			String members = String.format(getString(R.string.group_members),
					group.getMembers());
			setCtrlText(R.id.members, members);
		} catch (NumberFormatException ex) {
			// TODO: we should not throw!
		}

		if (group.getOwnerName() != null) {
			String owner = String.format(getString(R.string.group_owner),
					group.getOwnerName());
			setCtrlText(R.id.owner, Html.fromHtml(owner));
		}

		Bitmap bitmap = null;
		for (Picture picture : group.getPicture()) {
			bitmap = contentClient.getBitmap(picture);
		}
		ImageView image = (ImageView) findViewById(R.id.icon);
		if (bitmap != null) {
			image.setImageBitmap(bitmap);
		} else {
			image.setImageResource(R.drawable.group_default);
		}

		int position = tabHost.getCurrentTab();
		BaseFragment fragment = (BaseFragment) adapter.getItem(position);
		fragment.refreshUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MENU_JOIN, 0, R.string.group_title);
		item.setIcon(R.drawable.icon_join_group);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		item = menu.add(0, MENU_ADD, 0, R.string.group_title);
		item.setIcon(R.drawable.icon_add);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_JOIN:
			try {
				Person person = getSAM().getMe();
				person.addIsMemberOf(group);

				Toast.makeText(this, R.string.group_join_sent,
						Toast.LENGTH_SHORT).show();
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
		refreshUI();
	}
}

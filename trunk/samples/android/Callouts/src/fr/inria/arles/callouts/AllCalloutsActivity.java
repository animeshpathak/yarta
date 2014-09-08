package fr.inria.arles.callouts;

import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Group;
import fr.inria.arles.yarta.resources.GroupImpl;
import fr.inria.arles.yarta.resources.Person;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class AllCalloutsActivity extends BaseActivity implements
		AdapterView.OnItemClickListener {
	private static final int MENU_ADD = 1;

	private CalloutsListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		adapter = new CalloutsListAdapter(this);

		ListView list = (ListView) findViewById(R.id.listCallouts);
		list.setAdapter(adapter);
		list.setEmptyView(findViewById(R.id.none));
		list.setOnItemClickListener(this);

		app.initMSE(this);
	}

	@Override
	protected void onDestroy() {
		app.uninitMSE();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshUI();
	}

	private Group group;
	private Person me;

	@Override
	protected void refreshUI() {
		if (getSAM() != null) {
			try {
				if (group == null || me == null) {
					group = new GroupImpl(getSAM(), new MSEResource(
							Constants.getGroupId(), Group.typeURI));
					me = getSAM().getMe();

					if (me != null) {
						me.addIsMemberOf(group);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (group != null) {
				adapter.setItems(group.getHasContent());
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MENU_ADD, 0, R.string.callout_new);
		item.setIcon(R.drawable.icon_add);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ADD:
			Intent intent = new Intent(this, CreateActivity.class);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Content content = (Content) adapter.getItem(position);

		Intent intent = new Intent(this, CalloutActivity.class);
		intent.putExtra(CalloutActivity.CalloutId, content.getUniqueId());
		startActivity(intent);
	}
}

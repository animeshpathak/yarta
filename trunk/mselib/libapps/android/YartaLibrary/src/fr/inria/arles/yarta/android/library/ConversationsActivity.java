package fr.inria.arles.yarta.android.library;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.inria.arles.yarta.R;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.resources.Conversation;
import fr.inria.arles.yarta.resources.Person;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ConversationsActivity extends BaseActivity implements
		AdapterView.OnItemClickListener {

	private static final int MENU_ADD = 1;

	private ConversationsListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversations);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		adapter = new ConversationsListAdapter(this);

		ListView list = (ListView) findViewById(R.id.listConversations);

		list.setOnItemClickListener(this);
		list.setAdapter(adapter);
		list.setEmptyView(findViewById(R.id.listEmpty));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MENU_ADD, 0, R.string.app_name);
		item.setIcon(R.drawable.icon_add);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ADD:
			onMenuAdd();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void onMenuAdd() {
		Intent intent = new Intent(this, ConversationActivity.class);
		startActivity(intent);
	}

	@Override
	protected void refreshUI() {
		execute(new Job() {

			private Person me;

			@Override
			public void doWork() {
				try {
					me = getSAM().getMe();

					Set<Conversation> cs = me.getParticipatesTo();

					synchronized (conversations) {
						conversations.clear();
						conversations.addAll(cs);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void doUIAfter() {
				synchronized (conversations) {
					adapter.setConversations(conversations, me);
				}
			}
		});
	}

	private List<Conversation> conversations = new ArrayList<Conversation>();

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Conversation conversation = conversations.get(position);
		Intent intent = new Intent(this, ConversationActivity.class);
		intent.putExtra(ConversationActivity.ConversationId,
				conversation.getUniqueId());
		startActivity(intent);
	}
}

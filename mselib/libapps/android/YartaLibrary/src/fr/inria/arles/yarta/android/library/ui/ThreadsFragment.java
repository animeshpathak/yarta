package fr.inria.arles.yarta.android.library.ui;

import java.util.Set;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.PullToRefreshListView;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.resources.Conversation;

public class ThreadsFragment extends BaseFragment implements
		PullToRefreshListView.OnRefreshListener {

	private static final int MENU_COMPOSE = 1;

	private ThreadsListAdapter adapter;
	private PullToRefreshListView list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_threads, container,
				false);

		adapter = new ThreadsListAdapter(getSherlockActivity(), sam);

		list = (PullToRefreshListView) root.findViewById(R.id.listInbox);
		list.setAdapter(adapter);
		list.setOnRefreshListener(this);
		list.setEmptyView(root.findViewById(R.id.listEmpty));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Conversation conversation = (Conversation) adapter
						.getItem(position);
				String conversationId = conversation.getUniqueId();

				Intent intent = new Intent(getSherlockActivity(),
						MessagesActivity.class);
				intent.putExtra(MessagesActivity.ThreadId, conversationId);
				startActivity(intent);
			}
		});

		setHasOptionsMenu(true);
		return root;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem item = menu.add(0, MENU_COMPOSE, 0,
				R.string.main_message_compose);
		item.setIcon(R.drawable.icon_compose);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_COMPOSE:
			onCompose();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();

		refreshUI(null);
	}

	@Override
	public void onRefresh() {
		refreshUI(null);
	}

	private Set<Conversation> items;

	@Override
	public void refreshUI(String notification) {
		execute(new Job() {

			@Override
			public void doWork() {
				items = sam.getAllConversations();
			}

			@Override
			public void doUIAfter() {
				adapter.setItems(items);
				list.onRefreshComplete();
			}
		});
	}

	private void onCompose() {
		Intent intent = new Intent(getSherlockActivity(), MessageActivity.class);
		startActivity(intent);
	}
}

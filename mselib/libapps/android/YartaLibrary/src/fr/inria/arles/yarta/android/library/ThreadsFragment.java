package fr.inria.arles.yarta.android.library;

import java.io.Serializable;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ImageCache;
import fr.inria.arles.iris.web.MessageItem;
import fr.inria.arles.util.PullToRefreshListView;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;

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

		adapter = new ThreadsListAdapter(getSherlockActivity());

		list = (PullToRefreshListView) root.findViewById(R.id.listInbox);
		list.setAdapter(adapter);
		list.setOnRefreshListener(this);
		list.setEmptyView(root.findViewById(R.id.listEmpty));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getSherlockActivity(),
						MessagesActivity.class);
				intent.putExtra(MessagesActivity.Thread,
						(Serializable) adapter.getItem(position));
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

		refreshUI();
	}

	@Override
	public void onRefresh() {
		refreshUI();
	}

	private List<List<MessageItem>> items;

	@Override
	public void refreshUI() {
		runner.runBackground(new Job() {

			@Override
			public void doWork() {
				items = client.getMessageThreads();
			}

			@Override
			public void doUIAfter() {
				adapter.setItems(items);
				list.onRefreshComplete();
				new Thread(lazyImageLoader).start();
			}
		});
	}

	private Runnable lazyImageLoader = new Runnable() {

		@Override
		public void run() {
			for (List<MessageItem> item : items) {
				MessageItem message = item.get(0);

				String url = message.getFrom().getAvatarURL();
				if (ImageCache.getDrawable(url) == null) {
					try {
						ImageCache.setDrawable(url,
								ImageCache.drawableFromUrl(url));
						handler.post(refreshListAdapter);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	};

	private Handler handler = new Handler();
	private Runnable refreshListAdapter = new Runnable() {

		@Override
		public void run() {
			adapter.notifyDataSetChanged();
		}
	};

	private void onCompose() {
		Intent intent = new Intent(getSherlockActivity(), MessageActivity.class);
		startActivity(intent);
	}
}

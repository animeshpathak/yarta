package fr.inria.arles.yarta.android.library;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ImageCache;
import fr.inria.arles.iris.web.UserItem;
import fr.inria.arles.util.PullToRefreshListView;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;

public class FriendsFragment extends BaseFragment implements
		PullToRefreshListView.OnRefreshListener,
		AdapterView.OnItemClickListener {

	private FriendsListAdapter adapter;
	private PullToRefreshListView list;
	private List<UserItem> items;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_friends, container,
				false);

		adapter = new FriendsListAdapter(getSherlockActivity());

		list = (PullToRefreshListView) root.findViewById(R.id.listFriends);
		list.setAdapter(adapter);
		list.setOnRefreshListener(this);
		list.setOnItemClickListener(this);
		list.setEmptyView(root.findViewById(R.id.listEmpty));

		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshUI();
	}

	@Override
	public void refreshUI() {
		runner.runBackground(new Job() {

			@Override
			public void doWork() {
				items = client.getFriends(client.getUsername(), 0);
			}

			@Override
			public void doUIAfter() {
				adapter.setItems(items);
				list.onRefreshComplete();
				new Thread(lazyImageLoader).start();
			}
		});
	}

	private Handler handler = new Handler();
	private Runnable refreshListAdapter = new Runnable() {

		@Override
		public void run() {
			adapter.notifyDataSetChanged();
		}
	};

	private Runnable lazyImageLoader = new Runnable() {

		@Override
		public void run() {
			for (UserItem item : items) {
				String url = item.getAvatarURL();
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

	@Override
	public void onRefresh() {
		refreshUI();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		UserItem item = items.get(position);
		Intent intent = new Intent(getSherlockActivity(), ProfileActivity.class);
		intent.putExtra(ProfileActivity.UserName, item.getUsername());
		startActivity(intent);
	}
}

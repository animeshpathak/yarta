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
import fr.inria.arles.yarta.android.library.web.GroupItem;
import fr.inria.arles.yarta.android.library.web.ImageCache;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.PullToRefreshListView;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;

public class GroupsFragment extends BaseFragment implements
		PullToRefreshListView.OnRefreshListener,
		AdapterView.OnItemClickListener {

	private GroupsListAdapter adapter;
	private PullToRefreshListView list;
	private List<GroupItem> items;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater
				.inflate(R.layout.fragment_groups, container, false);

		adapter = new GroupsListAdapter(getSherlockActivity());

		list = (PullToRefreshListView) root.findViewById(R.id.listGroups);
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
				items = client.getGroups(client.getUsername());
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
			for (GroupItem item : items) {
				String url = item.getAvatarURL();
				if (ImageCache.getDrawable(url) == null) {
					boolean error = false;
					try {
						ImageCache.setDrawable(url,
								ImageCache.drawableFromUrl(url));
						handler.post(refreshListAdapter);
					} catch (Exception ex) {
						error = true;
					}

					if (error) {
						try {
							ImageCache.setDrawable(
									url,
									getActivity().getResources().getDrawable(
											R.drawable.group_default));
							handler.post(refreshListAdapter);
						} catch (Exception ex) {
						}
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
		GroupItem item = items.get(position);
		Intent intent = new Intent(getSherlockActivity(), GroupActivity.class);
		intent.putExtra(GroupActivity.GroupGuid, item.getGuid());
		startActivity(intent);
	}
}

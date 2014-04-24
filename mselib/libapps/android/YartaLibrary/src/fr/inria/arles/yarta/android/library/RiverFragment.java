package fr.inria.arles.yarta.android.library;

import java.util.List;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.android.library.util.PopupDialog;
import fr.inria.arles.yarta.android.library.util.PullToRefreshListView;
import fr.inria.arles.yarta.android.library.web.ImageCache;
import fr.inria.arles.yarta.android.library.web.ObjectItem;
import fr.inria.arles.yarta.android.library.web.RiverItem;
import fr.inria.arles.yarta.android.library.web.UserItem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

public class RiverFragment extends BaseFragment implements
		PullToRefreshListView.OnRefreshListener, RiverListAdapter.Callback,
		AbsListView.OnScrollListener {

	private RiverListAdapter adapter;
	private PullToRefreshListView list;
	private View emptyView;

	private String groupGuid;
	private String username;

	public void setGroupGuid(String groupGuid) {
		this.groupGuid = groupGuid;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_river, container, false);

		adapter = new RiverListAdapter(getSherlockActivity());
		adapter.setCallback(this);

		list = (PullToRefreshListView) root.findViewById(R.id.listRiver);
		list.setAdapter(adapter);
		list.setOnRefreshListener(this);
		list.setOnScrollListener(this);

		emptyView = root.findViewById(R.id.listEmpty);

		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshUI();
	}

	@Override
	public void onRefresh() {
		if (!loadingMore) {
			refreshUI();
		}
	}

	private List<RiverItem> items;

	@Override
	public void refreshUI() {
		if (runner == null) {
			return;
		}
		runner.runBackground(new Job() {

			@Override
			public void doWork() {
				if (groupGuid != null) {
					items = client.getGroupActivity(groupGuid);
				} else if (username != null) {
					items = client.getRiverItems(username);
				} else {
					items = client.getRiverItems(0);
				}
			}

			@Override
			public void doUIAfter() {
				adapter.setItems(items);
				list.onRefreshComplete();
				if (groupGuid != null || username != null) {
					PostActivity.setListViewHeightBasedOnChildren(list);

					boolean noItems = items.size() == 0;
					list.setVisibility(noItems ? View.GONE : View.VISIBLE);
					emptyView.setVisibility(noItems ? View.VISIBLE : View.GONE);
				}
				new Thread(lazyImageLoader).start();
			}
		});
	}

	private Runnable lazyImageLoader = new Runnable() {

		@Override
		public void run() {
			try {
				for (RiverItem item : items) {
					if (item.getSubject() == null) {
						continue;
					}

					String url = item.getSubject().getAvatarURL();
					if (ImageCache.getDrawable(url) == null) {
						try {
							ImageCache.setDrawable(url,
									ImageCache.drawableFromUrl(url));
							handler.post(refreshListAdapter);
						} catch (Exception ex) {
						}
					}
				}
			} catch (Exception ex) {
				// concurrent over river items;
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

	@Override
	public void onProfileClick(UserItem user) {
		Intent intent = new Intent(getSherlockActivity(), ProfileActivity.class);
		intent.putExtra(ProfileActivity.UserName, user.getUsername());
		startActivity(intent);
	}

	@Override
	public void onObjectClick(ObjectItem object) {
		if (object.getType().equals("user")) {
			Intent intent = new Intent(getSherlockActivity(),
					ProfileActivity.class);
			intent.putExtra(ProfileActivity.UserGuid, object.getGuid().trim());
			startActivity(intent);
		} else if (object.getType().equals("group")) {
			Intent intent = new Intent(getSherlockActivity(),
					GroupActivity.class);
			intent.putExtra(GroupActivity.GroupGuid, object.getGuid().trim());
			startActivity(intent);
		} else if (object.getType().equals("blog")) {
			Intent intent = new Intent(getSherlockActivity(),
					PostActivity.class);
			intent.putExtra(PostActivity.PostGuid, object.getGuid().trim());
			startActivity(intent);
		} else {
			String description = object.getDescription();
			if (description == null) {
				description = "-";
			}
			String format = getString(R.string.main_river_cant_display_object);
			String message = String.format(format, object.getName(),
					object.getType(), description);
			PopupDialog.show(getSherlockActivity(), message);
		}
	}

	private boolean loadingMore = false;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, final int totalItemCount) {

		// user & group activity is not infinite scrollable
		if (username != null || groupGuid != null) {
			return;
		}

		if (firstVisibleItem + visibleItemCount == totalItemCount
				&& totalItemCount > 1 && !loadingMore) {
			loadingMore = true;
			runner.runBackground(new Job() {

				List<RiverItem> moreItems;

				@Override
				public void doWork() {
					moreItems = client.getRiverItems(adapter.getCount());
				}

				@Override
				public void doUIAfter() {
					if (moreItems != null) {
						items.addAll(moreItems);
						adapter.appendItems(moreItems);
						list.onRefreshComplete();
						new Thread(lazyImageLoader).start();
					}
					loadingMore = false;
				}
			});
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}

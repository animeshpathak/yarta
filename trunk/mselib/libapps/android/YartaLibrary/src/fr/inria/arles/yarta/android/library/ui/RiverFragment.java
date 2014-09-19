package fr.inria.arles.yarta.android.library.ui;

import java.util.List;

import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ImageCache;
import fr.inria.arles.iris.web.ObjectItem;
import fr.inria.arles.iris.web.RiverItem;
import fr.inria.arles.iris.web.UserItem;
import fr.inria.arles.yarta.android.library.resources.Group;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.PullToRefreshListView;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class RiverFragment extends BaseFragment implements
		PullToRefreshListView.OnRefreshListener, RiverListAdapter.Callback,
		AbsListView.OnScrollListener {

	private RiverListAdapter adapter;
	private PullToRefreshListView list;

	private String groupGuid;
	private String username;

	public void setGroupGuid(String groupGuid) {
		groupGuid = groupGuid.substring(groupGuid.indexOf('_') + 1);
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
		list.setEmptyView(root.findViewById(R.id.listEmpty));

		setViews(container, root);

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
					setListViewHeightBasedOnChildren(list);

					boolean noItems = items.size() == 0;
					list.setVisibility(noItems ? View.GONE : View.VISIBLE);
				}
				new Thread(lazyImageLoader).start();
			}
		});
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
				MeasureSpec.AT_MOST);
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
		listView.postInvalidate();
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
		String type = object.getType();

		if (type.equals(ObjectItem.User)) {
			Intent intent = new Intent(getSherlockActivity(),
					ProfileActivity.class);
			intent.putExtra(ProfileActivity.UserGuid, object.getGuid().trim());
			startActivity(intent);
		} else if (type.equals(ObjectItem.Group)) {
			Intent intent = new Intent(getSherlockActivity(),
					GroupActivity.class);
			String groupId = Group.typeURI + "_" + object.getGuid().trim();
			intent.putExtra(GroupActivity.GroupGuid, groupId);
			startActivity(intent);
		} else if (type.equals(ObjectItem.Topic)) {
			Intent intent = new Intent(getSherlockActivity(),
					ContentActivity.class);
			intent.putExtra(ContentActivity.PostGuid, object.getGuid().trim());
			startActivity(intent);
		} else if (type.equals(ObjectItem.Blog) || type.equals(ObjectItem.Page)
				|| type.equals(ObjectItem.PageTop)
				|| type.equals(ObjectItem.Feedback)) {
			Intent intent = new Intent(getSherlockActivity(),
					BlogActivity.class);
			intent.putExtra(BlogActivity.BlogGuid, object.getGuid().trim());
			startActivity(intent);
		} else if (type.equals(ObjectItem.File)) {
			Intent intent = new Intent(getSherlockActivity(),
					FileActivity.class);
			intent.putExtra(FileActivity.FileGuid, object.getGuid().trim());
			startActivity(intent);
		} else {
			// TODO: handle unknown content type
		}
	}

	private boolean loadingMore = false;
	private boolean bottomReached = false;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, final int totalItemCount) {

		if (bottomReached) {
			return;
		}

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

						if (moreItems.size() == 0) {
							bottomReached = true;
						}
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

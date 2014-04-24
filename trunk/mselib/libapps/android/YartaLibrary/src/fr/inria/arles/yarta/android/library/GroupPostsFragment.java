package fr.inria.arles.yarta.android.library;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.web.ImageCache;
import fr.inria.arles.yarta.android.library.web.PostItem;
import fr.inria.arles.yarta.android.library.web.UserItem;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.PullToRefreshListView;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;

public class GroupPostsFragment extends BaseFragment implements
		PullToRefreshListView.OnRefreshListener, PostsListAdapter.Callback {

	private PostsListAdapter adapter;
	private PullToRefreshListView list;
	private View emptyView;

	private String groupGuid;

	public void setGroupGuid(String groupGuid) {
		this.groupGuid = groupGuid;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_group_posts, container,
				false);

		adapter = new PostsListAdapter(getSherlockActivity());
		adapter.setCallback(this);

		list = (PullToRefreshListView) root.findViewById(R.id.listPosts);
		list.setAdapter(adapter);
		list.setOnRefreshListener(this);

		emptyView = root.findViewById(R.id.listEmpty);

		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshUI();
	}

	@Override
	public void refreshUI() {
		refreshPostsList();
	}

	@Override
	public void onRefresh() {
		refreshPostsList();
	}

	private List<PostItem> items;

	private void refreshPostsList() {
		runner.runBackground(new Job() {

			@Override
			public void doWork() {
				items = client.getGroupPosts(groupGuid);
			}

			@Override
			public void doUIAfter() {
				adapter.setItems(items);
				list.onRefreshComplete();
				PostActivity.setListViewHeightBasedOnChildren(list);

				boolean noItems = items.size() == 0;

				list.setVisibility(noItems ? View.GONE : View.VISIBLE);
				emptyView.setVisibility(noItems ? View.VISIBLE : View.GONE);
				new Thread(lazyImageLoader).start();
			}
		});
	}

	private Runnable lazyImageLoader = new Runnable() {

		@Override
		public void run() {
			for (PostItem item : items) {
				String url = item.getOwner().getAvatarURL();
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

	@Override
	public void onClickProfile(UserItem item) {
		Intent intent = new Intent(getSherlockActivity(), ProfileActivity.class);
		intent.putExtra(ProfileActivity.UserName, item.getUsername());
		startActivity(intent);
	}

	@Override
	public void onClickPost(PostItem item) {
		Intent intent = new Intent(getSherlockActivity(), PostActivity.class);
		intent.putExtra(PostActivity.PostGuid, item.getGuid());
		startActivity(intent);
	}
}

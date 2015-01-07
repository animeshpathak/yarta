package fr.inria.arles.yarta.android.library.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import fr.inria.arles.iris.R;
import fr.inria.arles.iris.bridge.IrisBridge;
import fr.inria.arles.yarta.android.library.resources.Group;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.PullToRefreshListView;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.YartaResource;

public class GroupPostsFragment extends BaseFragment implements
		ContentListAdapter.Callback, PullToRefreshListView.OnRefreshListener {

	private ContentListAdapter adapter;
	private PullToRefreshListView list;

	private Group group;
	private String groupGuid;

	public void setGroupGuid(String groupGuid) {
		this.groupGuid = groupGuid;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_group_posts, container,
				false);

		adapter = new ContentListAdapter(getSherlockActivity());
		adapter.setCallback(this);

		list = (PullToRefreshListView) root.findViewById(R.id.listDiscussions);
		list.setAdapter(adapter);
		list.setOnRefreshListener(this);
		list.setEmptyView(root.findViewById(R.id.listEmpty));

		return root;
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

	@Override
	public void refreshUI(String notification) {
		if (groupGuid == null || sam == null) {
			return;
		}
		YartaResource resource = sam.getResourceByURI(groupGuid);
		if (resource == null) {
			return;
		} else {
			group = (Group) resource;
		}
		if (IrisBridge.PostListEmpty.equals(notification)) {
			// show content, there is no post
			showFrame(Frame.Content);
		} else {
			refreshPostsList();
		}
	}

	private List<Content> items;

	private void refreshPostsList() {
		execute(new Job() {

			@Override
			public void doUIBefore() {
				showFrame(Frame.Loading);
			}

			@Override
			public void doWork() {
				items = new ArrayList<Content>();

				if (sam != null && group != null) {
					items.addAll(group.getHasContent());
					MessagesActivity.sort(items, false);
				}
			}

			@Override
			public void doUIAfter() {
				if (items.isEmpty()) {
				} else {
					showFrame(Frame.Content);
					adapter.setItems(items);
					list.onRefreshComplete();
				}
			}
		});
	}

	@Override
	public void onClickProfile(Person item) {
		Intent intent = new Intent(getSherlockActivity(), ProfileActivity.class);
		intent.putExtra(ProfileActivity.UserName, item.getUserId());
		startActivity(intent);
	}

	@Override
	public void onClickPost(Content item) {
		Intent intent = new Intent(getSherlockActivity(), ContentActivity.class);
		intent.putExtra(ContentActivity.PostGuid, item.getUniqueId());
		startActivity(intent);
	}
}

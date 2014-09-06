package fr.inria.arles.yarta.android.library;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import fr.inria.arles.iris.R;
import fr.inria.arles.util.PullToRefreshListView;
import fr.inria.arles.yarta.android.library.resources.Group;
import fr.inria.arles.yarta.android.library.resources.GroupImpl;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.resources.Content;

public class GroupPostsFragment extends BaseFragment implements
		PullToRefreshListView.OnRefreshListener, ContentListAdapter.Callback {

	private ContentListAdapter adapter;
	private PullToRefreshListView list;
	private View emptyView;

	private Group group;

	public void setGroupGuid(String groupGuid) {
		group = new GroupImpl(sam, new MSEResource(groupGuid, Group.typeURI));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_group_posts, container,
				false);

		adapter = new ContentListAdapter(getSherlockActivity(), sam);
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

	private List<Content> items;

	private void refreshPostsList() {
		runner.runBackground(new Job() {

			@Override
			public void doWork() {
				items = new ArrayList<Content>();
				items.addAll(group.getHasContent());
				MessagesActivity.sort(items, false);
			}

			@Override
			public void doUIAfter() {
				adapter.setItems(items);
				list.onRefreshComplete();
				ContentActivity.setListViewHeightBasedOnChildren(list);

				boolean noItems = items.size() == 0;

				list.setVisibility(noItems ? View.GONE : View.VISIBLE);
				emptyView.setVisibility(noItems ? View.VISIBLE : View.GONE);
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

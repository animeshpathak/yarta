package fr.inria.arles.yarta.android.library;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import fr.inria.arles.iris.R;
import fr.inria.arles.util.PullToRefreshListView;
import fr.inria.arles.yarta.android.library.resources.Group;
import fr.inria.arles.yarta.android.library.resources.GroupImpl;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.MSEResource;

public class GroupsFragment extends BaseFragment implements
		PullToRefreshListView.OnRefreshListener,
		AdapterView.OnItemClickListener {

	private GroupsListAdapter adapter;
	private PullToRefreshListView list;
	private List<Group> items;

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
				items = new ArrayList<Group>();

				try {
					Person person = sam.getMe();

					for (fr.inria.arles.yarta.resources.Group group : person
							.getIsMemberOf()) {
						items.add(new GroupImpl(sam, new MSEResource(group
								.getUniqueId(), Group.typeURI)));
					}
				} catch (KBException ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void doUIAfter() {
				adapter.setItems(items);
				list.onRefreshComplete();
			}
		});
	}

	@Override
	public void onRefresh() {
		refreshUI();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Group item = items.get(position);
		Intent intent = new Intent(getSherlockActivity(), GroupActivity.class);
		intent.putExtra(GroupActivity.GroupGuid, item.getUniqueId());
		startActivity(intent);
	}
}

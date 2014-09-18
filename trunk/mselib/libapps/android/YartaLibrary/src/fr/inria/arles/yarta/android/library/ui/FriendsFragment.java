package fr.inria.arles.yarta.android.library.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.resources.PersonImpl;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.PullToRefreshListView;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.resources.Agent;

public class FriendsFragment extends BaseFragment implements
		PullToRefreshListView.OnRefreshListener,
		AdapterView.OnItemClickListener {

	private FriendsListAdapter adapter;
	private PullToRefreshListView list;
	private List<Person> items;

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

		if (sam != null) {
			refreshUI();
		}
	}

	@Override
	public void refreshUI() {
		runner.runBackground(new Job() {

			@Override
			public void doWork() {
				try {
					items = new ArrayList<Person>();
					Person me = sam.getMe();
					for (Agent agent : me.getKnows_inverse()) {
						items.add(new PersonImpl(sam, new MSEResource(agent
								.getUniqueId(), Person.typeURI)));
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
		Person item = items.get(position);
		Intent intent = new Intent(getSherlockActivity(), ProfileActivity.class);
		intent.putExtra(ProfileActivity.UserName, item.getUserId());
		startActivity(intent);
	}
}

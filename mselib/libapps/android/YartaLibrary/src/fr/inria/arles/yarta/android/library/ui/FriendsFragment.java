package fr.inria.arles.yarta.android.library.ui;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

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
import fr.inria.arles.yarta.resources.Agent;

public class FriendsFragment extends BaseFragment implements
		PullToRefreshListView.OnRefreshListener,
		AdapterView.OnItemClickListener {

	private FriendsListAdapter adapter;
	private PullToRefreshListView list;
	private List<Person> items;

	private static final int MENU_ADD = 1;

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

		setHasOptionsMenu(true);
		return root;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (sam != null) {
			refreshUI(null);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		MenuItem item = menu.add(0, MENU_ADD, 0, R.string.profile_add_friend);
		item.setIcon(R.drawable.icon_add_user);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == MENU_ADD) {
			ProfileDialog.show(getSherlockActivity(),
					new ProfileDialog.Callback() {

						@Override
						public void onAdd(final String userId) {
							// TODO: or just a simple comm.sendHello would
							// suffice I guess

							// TODO: hack! person should have been discovered
							// from different
							// contexts;
							String typeURI = fr.inria.arles.yarta.resources.Person.typeURI;
							String personID = typeURI + "_" + userId;

							// because person might not exist, since I have not
							// discovered it
							Person person = new PersonImpl(sam, sam
									.createNewNode(personID, typeURI));
							person.setUserId(userId);

							try {
								sam.getMe().addKnows(person);
							} catch (Exception ex) {
								ex.printStackTrace();
							}

							// TODO: send notify to peer;
							execute(new Job() {
								@Override
								public void doWork() {
									if (comm != null) {
										comm.sendNotify(userId);
									}
								}
							});

							// to refresh users list
							refreshUI(null);
						}
					});
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void refreshUI(String notification) {
		execute(new Job() {

			@Override
			public void doWork() {
				try {
					items = new ArrayList<Person>();
					Person me = (Person) sam.getMe();

					for (Agent agent : me.getKnows()) {
						items.add((Person) sam.getResourceByURI(agent
								.getUniqueId()));
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
		refreshUI(null);
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

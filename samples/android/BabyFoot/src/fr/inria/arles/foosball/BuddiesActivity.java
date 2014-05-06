package fr.inria.arles.foosball;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.inria.arles.foosball.R;
import fr.inria.arles.foosball.PlayersApp.Observer;
import fr.inria.arles.foosball.resources.Player;
import fr.inria.arles.foosball.resources.PlayerImpl;
import fr.inria.arles.foosball.util.JobRunner.Job;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.resources.Agent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class BuddiesActivity extends BaseActivity implements
		AdapterView.OnItemClickListener, Observer {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_buddies);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		players = new ArrayList<Player>();

		adapter = new PlayerListAdapter(this);

		ListView list = (ListView) findViewById(R.id.listBuddies);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setEmptyView(findViewById(R.id.listBuddiesEmpty));

		addObserver(this);

		// nice color filter
		ImageView buddy = (ImageView) findViewById(R.id.buddy);
		buddy.setColorFilter(getResources().getColor(R.color.AppTextColor));
	}

	@Override
	protected void refreshUI() {
		refreshPlayersList();
	}

	@Override
	protected void onDestroy() {
		removeObserver(this);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();

		refreshPlayersList();
	}

	private void refreshPlayersList() {
		execute(new Job() {

			@Override
			public void doWork() {
				try {
					players.clear();

					Set<Agent> agents = getSAM().getMe().getKnows();

					for (Agent agent : agents) {
						players.add(new PlayerImpl(getSAM(), new MSEResource(
								agent.getUniqueId(), Player.typeURI)));

					}
				} catch (Exception ex) {
				}
			}

			@Override
			public void doUIAfter() {
				adapter.setItems(players);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Player selected = players.get(position);
		Intent intent = new Intent(this, PlayerActivity.class);
		intent.putExtra(PlayerActivity.PlayerGUID, selected.getUniqueId());
		startActivity(intent);
	}

	private List<Player> players;
	private PlayerListAdapter adapter;
}

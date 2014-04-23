package fr.inria.arles.foosball;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.arles.foosball.util.JobRunner.Job;
import fr.inria.arles.foosball.resources.Match;
import fr.inria.arles.foosball.resources.Player;
import fr.inria.arles.foosball.resources.PlayerImpl;
import fr.inria.arles.yarta.knowledgebase.MSEResource;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class GamesActivity extends BaseActivity implements
		AdapterView.OnItemClickListener {

	public static final String PlayerGUID = "PlayerGUID";
	private Player player;
	private MatchListAdapter adapter;

	private List<Match> matches;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_games);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		adapter = new MatchListAdapter(this);

		ListView list = (ListView) findViewById(R.id.listGames);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		player = new PlayerImpl(getSAM(), new MSEResource(getIntent()
				.getStringExtra(PlayerGUID), Player.typeURI));

		execute(new Job() {

			@Override
			public void doWork() {
				matches = new ArrayList<Match>();

				matches.addAll(player.getBlueD());
				matches.addAll(player.getBlueO());
				matches.addAll(player.getRedD());
				matches.addAll(player.getRedO());

				Map<Match, Long> matchTimes = new HashMap<Match, Long>();

				for (Match match : matches) {
					long time = 0;
					try {
						time = match.getTime();
					} catch (Exception ex) {
						time = 0;
					}
					matchTimes.put(match, time);
				}

				for (int i = 0; i < matches.size() - 1; i++) {
					for (int j = i + 1; j < matches.size(); j++) {
						Match mi = matches.get(i);
						Match mj = matches.get(j);
						if (matchTimes.get(mi) < matchTimes.get(mj)) {
							matches.set(j, mi);
							matches.set(i, mj);
						}
					}
				}
			}

			@Override
			public void doUIAfter() {
				adapter.setMatches(matches);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Match selected = matches.get(position);
		Intent intent = new Intent(this, MatchActivity.class);
		intent.putExtra(MatchActivity.MatchId, selected.getUniqueId());
		startActivity(intent);
	}
}

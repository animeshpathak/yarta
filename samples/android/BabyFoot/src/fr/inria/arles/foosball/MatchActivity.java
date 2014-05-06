package fr.inria.arles.foosball;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.inria.arles.foosball.R;
import fr.inria.arles.foosball.util.JobRunner.Job;
import fr.inria.arles.foosball.resources.Match;
import fr.inria.arles.foosball.resources.MatchImpl;
import fr.inria.arles.foosball.resources.Player;
import fr.inria.arles.foosball.resources.PlayerImpl;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.resources.Agent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MatchActivity extends BaseActivity {

	public static final String MatchId = "MatchId";

	private static final int MENU_ACCEPT = 1;

	private Match currentMatch;

	private boolean editable = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_match);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (getIntent().hasExtra(MatchId)) {
			String matchId = getIntent().getStringExtra(MatchId);
			loadMatch(matchId);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (editable) {
			MenuItem item = menu.add(0, MENU_ACCEPT, 0, R.string.match_accept);

			item.setIcon(R.drawable.icon_accept);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ACCEPT:
			onSaveMatch();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void loadMatch(String matchId) {
		currentMatch = new MatchImpl(getSAM(), new MSEResource(matchId,
				MatchImpl.typeURI));

		try {
			if (currentMatch.getBlueScore() != null
					&& currentMatch.getRedScore() != null) {
				editable = false;
			}
		} catch (Exception ex) {
			editable = true;
		}

		execute(new Job() {
			@Override
			public void doWork() {
				blueD = toPlayer(currentMatch.getBlueD_inverse());
				blueO = toPlayer(currentMatch.getBlueO_inverse());
				redD = toPlayer(currentMatch.getRedD_inverse());
				redO = toPlayer(currentMatch.getRedO_inverse());
			}

			@Override
			public void doUIAfter() {
				if (blueD != null) {
					setCtrlText(R.id.blueD, playerToString(blueD));
				}
				if (blueO != null) {
					setCtrlText(R.id.blueO, playerToString(blueO));
				}

				if (redD != null) {
					setCtrlText(R.id.redD, playerToString(redD));
				}

				if (redO != null) {
					setCtrlText(R.id.redO, playerToString(redO));
				}
			}
		});
	}

	/**
	 * Gets one item from a set;
	 * 
	 * @param players
	 * @return
	 */
	protected Player toPlayer(Set<Player> players) {
		if (players.size() > 0) {
			return players.iterator().next();
		}
		return null;
	}

	protected String playerToString(Player player) {
		String nickName = player.getNickName();

		if (nickName == null) {
			if (player.getUserId() != null) {
				nickName = player.getUserId().replace("@inria.fr", "");
			} else {
				nickName = "NULL";
			}
		}
		return nickName;
	}

	public void onBlueDefense(View view) {
		selectPlayer(new SelectPlayerDialog.PlayerSelector() {

			@Override
			public void onPlayerSelected(Player player) {
				blueD = player;
				setCtrlText(R.id.blueD, playerToString(blueD));
			}
		});
	}

	public void onBlueOffense(View view) {
		selectPlayer(new SelectPlayerDialog.PlayerSelector() {

			@Override
			public void onPlayerSelected(Player player) {
				blueO = player;
				setCtrlText(R.id.blueO, playerToString(blueO));
			}
		});
	}

	public void onRedDefense(View view) {
		selectPlayer(new SelectPlayerDialog.PlayerSelector() {

			@Override
			public void onPlayerSelected(Player player) {
				redD = player;
				setCtrlText(R.id.redD, playerToString(redD));
			}
		});
	}

	public void onRedOffense(View view) {
		selectPlayer(new SelectPlayerDialog.PlayerSelector() {

			@Override
			public void onPlayerSelected(Player player) {
				redO = player;
				setCtrlText(R.id.redO, playerToString(redO));
			}
		});
	}

	protected void selectPlayer(final SelectPlayerDialog.PlayerSelector selector) {
		if (!editable) {
			return;
		}
		execute(new Job() {
			List<Player> buddies;

			@Override
			public void doWork() {
				buddies = getRemaniningBuddies();
			}

			@Override
			public void doUIAfter() {
				SelectPlayerDialog.show(MatchActivity.this, buddies, selector);
			}
		});
	}

	private Set<Agent> agents;

	protected Set<Agent> getKnownAgents() {
		if (agents == null) {
			try {
				agents = getSAM().getMe().getKnows();
				agents.add(getSAM().getMe());
			} catch (Exception ex) {
				agents = null;
			}
		}

		return agents;
	}

	protected List<Player> getRemaniningBuddies() {
		List<Player> result = new ArrayList<Player>();
		Set<Agent> agents = getKnownAgents();

		for (Agent agent : agents) {
			result.add(new PlayerImpl(getSAM(), new MSEResource(agent
					.getUniqueId(), Player.typeURI)));

		}

		result.remove(redO);
		result.remove(redD);
		result.remove(blueO);
		result.remove(blueD);

		return result;
	}

	protected void onSaveMatch() {
		if ((redO != null || redD != null) && (blueO != null || blueD != null)) {
			if (currentMatch == null) {
				currentMatch = getSAM().createMatch();
			} else {
				for (Player p : currentMatch.getBlueD_inverse()) {
					p.deleteBlueD(currentMatch);
				}
				for (Player p : currentMatch.getBlueO_inverse()) {
					p.deleteBlueO(currentMatch);
				}
				for (Player p : currentMatch.getRedD_inverse()) {
					p.deleteRedD(currentMatch);
				}
				for (Player p : currentMatch.getRedO_inverse()) {
					p.deleteRedO(currentMatch);
				}
			}
			if (blueD != null) {
				blueD.addBlueD(currentMatch);
			}
			if (blueO != null) {
				blueO.addBlueO(currentMatch);
			}

			if (redD != null) {
				redD.addRedD(currentMatch);
			}

			if (redO != null) {
				redO.addRedO(currentMatch);
			}

			currentMatch.setTime(System.currentTimeMillis());
			finish();
		} else {
			Toast.makeText(this, R.string.match_not_enogh_players,
					Toast.LENGTH_LONG).show();
		}
	}

	private Player redO;
	private Player redD;
	private Player blueO;
	private Player blueD;
}

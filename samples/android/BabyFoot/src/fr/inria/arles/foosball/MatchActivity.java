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
import fr.inria.arles.foosball.resources.Person;
import fr.inria.arles.foosball.resources.PersonImpl;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.resources.Agent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MatchActivity extends BaseActivity implements
		PositionSetDialog.Handler {

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
		} else {
			// PositionSetDialog dlg = new PositionSetDialog(this, this);
			// dlg.show();
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
				blueD = toPerson(currentMatch.getBlueD());
				blueO = toPerson(currentMatch.getBlueO());
				redD = toPerson(currentMatch.getRedD());
				redO = toPerson(currentMatch.getRedO());
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

	protected Person toPerson(Set<Person> person) {
		if (person.size() > 0) {
			return person.iterator().next();
		}
		return null;
	}

	protected String playerToString(Person player) {
		String nickName = player.getNickName();

		if (nickName == null) {
			nickName = player.getUserId().replace("@inria.fr", "");
		}
		return nickName;
	}

	public void onBlueDefense(View view) {
		selectPlayer(new PlayerSelector() {

			@Override
			public void onPlayerSelected(Person person) {
				blueD = person;
				setCtrlText(R.id.blueD, playerToString(blueD));
			}
		});
	}

	public void onBlueOffense(View view) {
		selectPlayer(new PlayerSelector() {

			@Override
			public void onPlayerSelected(Person person) {
				blueO = person;
				setCtrlText(R.id.blueO, playerToString(blueO));
			}
		});
	}

	public void onRedDefense(View view) {
		selectPlayer(new PlayerSelector() {

			@Override
			public void onPlayerSelected(Person person) {
				redD = person;
				setCtrlText(R.id.redD, playerToString(redD));
			}
		});
	}

	public void onRedOffense(View view) {
		selectPlayer(new PlayerSelector() {

			@Override
			public void onPlayerSelected(Person person) {
				redO = person;
				setCtrlText(R.id.redO, playerToString(redO));
			}
		});
	}

	private interface PlayerSelector {
		public void onPlayerSelected(Person person);
	}

	protected void selectPlayer(final PlayerSelector selector) {
		if (!editable) {
			return;
		}
		execute(new Job() {
			String items[];
			List<Person> buddies;

			@Override
			public void doWork() {
				buddies = getRemaniningBuddies();

				items = new String[buddies.size()];

				for (int i = 0; i < items.length; i++) {
					Person p = buddies.get(i);
					String firstName = p.getFirstName();
					if (firstName == null) {
						items[i] = p.getUserId();
					} else {
						items[i] = p.getFirstName() + " " + p.getLastName();
					}
				}
			}

			@Override
			public void doUIAfter() {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MatchActivity.this);
				builder.setTitle(R.string.match_select_player);
				builder.setSingleChoiceItems(items, -1,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

								selector.onPlayerSelected(buddies.get(which));
							}
						});

				AlertDialog dialog = builder.create();
				dialog.show();
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

	protected List<Person> getRemaniningBuddies() {
		List<Person> result = new ArrayList<Person>();
		Set<Agent> agents = getKnownAgents();

		for (Agent agent : agents) {
			Person p = (Person) agent;

			result.add(new PersonImpl(getSAM(), new MSEResource(
					p.getUniqueId(), Person.typeURI)));

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
				for (Person p : currentMatch.getBlueD()) {
					currentMatch.deleteBlueD(p);
				}
				for (Person p : currentMatch.getBlueO()) {
					currentMatch.deleteBlueO(p);
				}
				for (Person p : currentMatch.getRedD()) {
					currentMatch.deleteRedD(p);
				}
				for (Person p : currentMatch.getRedO()) {
					currentMatch.deleteRedO(p);
				}
			}
			currentMatch.addBlueD(blueD);
			currentMatch.addBlueO(blueO);
			currentMatch.addRedD(redD);
			currentMatch.addRedO(redO);

			currentMatch.setTime(System.currentTimeMillis());

			((PlayersApp) getApplication()).submitResource(currentMatch
					.getUniqueId());
			finish();
		} else {
			Toast.makeText(this, R.string.match_not_enogh_players,
					Toast.LENGTH_LONG).show();
		}
	}

	private Person redO;
	private Person redD;
	private Person blueO;
	private Person blueD;

	@Override
	public void onSetConfiguration(int team) {
	}
}

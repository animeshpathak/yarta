package fr.inria.arles.foosball;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.inria.arles.foosball.R;
import fr.inria.arles.foosball.util.JobRunner.Job;
import fr.inria.arles.foosball.util.Player;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MatchActivity extends BaseActivity {

	private static final int MENU_ACCEPT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_match);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MENU_ACCEPT, 0, R.string.match_accept);

		item.setIcon(R.drawable.icon_accept);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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

	protected String getName(String userName) {
		return userName.split(" ")[0];
	}

	public void onBlueDefense(View view) {
		selectPlayer(new PlayerSelector() {

			@Override
			public void onPlayerSelected(Player person) {
				blueD = person;
				setCtrlText(R.id.blueD, getName(person.getUserName()));
			}
		});
	}

	public void onBlueOffense(View view) {
		selectPlayer(new PlayerSelector() {

			@Override
			public void onPlayerSelected(Player person) {
				blueO = person;
				setCtrlText(R.id.blueO, getName(person.getUserName()));
			}
		});
	}

	public void onRedDefense(View view) {
		selectPlayer(new PlayerSelector() {

			@Override
			public void onPlayerSelected(Player person) {
				redD = person;
				setCtrlText(R.id.redD, getName(person.getUserName()));
			}
		});
	}

	public void onRedOffense(View view) {
		selectPlayer(new PlayerSelector() {

			@Override
			public void onPlayerSelected(Player person) {
				redO = person;
				setCtrlText(R.id.redO, getName(person.getUserName()));
			}
		});
	}

	private interface PlayerSelector {
		public void onPlayerSelected(Player person);
	}

	protected void selectPlayer(final PlayerSelector selector) {
		execute(new Job() {
			String items[];

			List<Player> buddies;

			@Override
			public void doWork() {
				buddies = getRemaniningBuddies();

				items = new String[buddies.size()];

				for (int i = 0; i < items.length; i++) {
					Player p = buddies.get(i);
					items[i] = p.getUserName();
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

	private List<Player> agents;

	protected List<Player> getKnownAgents() {
		if (agents == null) {
			try {
				agents = core.getBuddies();
				agents.add(0, new Player(core.getUserId(), core.getUsername()));
			} catch (Exception ex) {
				agents = null;
			}
		}

		return agents;
	}

	protected List<Player> getRemaniningBuddies() {
		List<Player> result = new ArrayList<Player>();

		result.addAll(getKnownAgents());

		result.remove(redO);
		result.remove(redD);
		result.remove(blueO);
		result.remove(blueD);

		return result;
	}

	protected void onSaveMatch() {
		execute(new Job() {

			int error = 0;

			public void doWork() {
				if ((redO != null || redD != null)
						&& (blueO != null || blueD != null)) {

					if (!core.createMatch(blueD, blueO, redD, redO)) {
						error = 1;
					}
				} else {
					error = 2; // yes, magic! quick & dirty
				}
			}

			@Override
			public void doUIAfter() {
				if (error == 0) {
					finish();
				} else if (error == 1) {
					Toast.makeText(getApplicationContext(),
							R.string.match_creation_error, Toast.LENGTH_LONG)
							.show();
				} else if (error == 2) {
					Toast.makeText(getApplicationContext(),
							R.string.match_not_enogh_players, Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}

	private Player redO;
	private Player redD;
	private Player blueO;
	private Player blueD;
}

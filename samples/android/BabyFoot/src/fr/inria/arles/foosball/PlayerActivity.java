package fr.inria.arles.foosball;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.inria.arles.foosball.resources.Player;
import fr.inria.arles.foosball.resources.PlayerImpl;
import fr.inria.arles.foosball.util.JobRunner.Job;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.logging.YLoggerFactory;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class PlayerActivity extends BaseActivity {

	private int totalGames;
	private int winRate;
	private int scorePoints;
	private Player player;

	private static final int MENU_UPDATE = 1;

	public static final String PlayerGUID = "PlayerGUID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_player);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		player = new PlayerImpl(getSAM(), new MSEResource(getIntent()
				.getStringExtra(PlayerGUID), Player.typeURI));

		String name = player.getName();
		if (name == null) {
			name = player.getUserId().replace("@inria.fr", "");
		}
		setCtrlText(R.id.name, name);

		displayStats();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu
				.add(0, MENU_UPDATE, 0, R.string.main_player_update);

		item.setIcon(R.drawable.icon_update);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_UPDATE:
			onUpdate();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void onUpdate() {
		execute(new Job() {

			boolean success = false;

			@Override
			public void doWork() {
				try {
					success = getCOMM().sendUpdateRequest(player.getUserId()) != -1;
				} catch (Exception ex) {
					success = false;
				}
			}

			public void doUIAfter() {
				Toast.makeText(
						getApplicationContext(),
						success ? R.string.main_player_update_success
								: R.string.main_player_update_error,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	private void displayStats() {
		try {
			totalGames = player.getTotalGames();
			scorePoints = player.getScorePoints();
			winRate = player.getWinRate();
		} catch (Exception ex) {
			YLoggerFactory.getLogger().e("Players",
					"displayStats ex: " + ex.toString());
		}

		setCtrlText(R.id.info, String.format(
				getString(R.string.main_player_matches_played), totalGames));

		if (totalGames > 0) {
			CircleProgress circleProgress = (CircleProgress) findViewById(R.id.winrate);
			circleProgress.setProgress(winRate);

			setCtrlText(R.id.winratetext, "" + winRate);

			circleProgress = (CircleProgress) findViewById(R.id.scorepoints);
			circleProgress.setProgress(scorePoints);

			setCtrlText(R.id.scorepointstext, "" + scorePoints);
		}
	}

	public void onGames(View view) {
		Intent intent = new Intent(this, GamesActivity.class);
		intent.putExtra(GamesActivity.PlayerGUID, player.getUniqueId());
		startActivity(intent);
	}
}

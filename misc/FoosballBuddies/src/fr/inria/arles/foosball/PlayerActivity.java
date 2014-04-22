package fr.inria.arles.foosball;

import java.util.Locale;

import fr.inria.arles.foosball.util.JobRunner.Job;
import android.os.Bundle;
import android.view.View;

public class PlayerActivity extends BaseActivity {

	public static final String PlayerId = "PlayerId";

	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_player);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		userId = getIntent().getStringExtra(PlayerId);

		refreshUI();
	}

	public void onGames(View view) {
	}

	private void refreshUI() {
		execute(new Job() {
			String username;

			@Override
			public void doWork() {
				username = core.getUsername(userId);
			}

			@Override
			public void doUIAfter() {
				setCtrlText(R.id.name, username);
			}
		});
		queryStatistics();
	}

	private void queryStatistics() {
		execute(new Job() {

			int totalGames = 0;
			int wonGames = 0;
			int scorePoints = 0;

			@Override
			public void doWork() {
				totalGames = core.getTotalGames(userId);
				wonGames = core.getWonGames(userId);
				scorePoints = core.getScorePoints(userId);
			}

			@Override
			public void doUIAfter() {
				setCtrlText(R.id.info, String.format(
						getString(R.string.main_player_matches_played),
						totalGames));

				if (totalGames > 0) {
					String winRate = String.format(Locale.US, "%d%%", 100
							* wonGames / totalGames);
					setCtrlText(R.id.winrate,
							getString(R.string.player_winrate) + winRate);

					setCtrlText(R.id.scorepoints,
							getString(R.string.player_scorepoints)
									+ scorePoints / totalGames);
				}
			}
		});
	}
}

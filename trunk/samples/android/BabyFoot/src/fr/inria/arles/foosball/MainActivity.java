package fr.inria.arles.foosball;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.inria.arles.foosball.R;
import fr.inria.arles.foosball.util.JobRunner.Job;
import fr.inria.arles.foosball.resources.Match;
import fr.inria.arles.foosball.resources.Player;
import fr.inria.arles.foosball.resources.PlayerImpl;
import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.YartaResource;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements MatchDialog.Handler,
		FeedbackDialog.Handler {

	public static final int MENU_FEEDBACK = 2;

	private Player me;
	private Match latestMatch;
	public static final int RESULT_LOGIN = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onDestroy() {
		uninitMSE();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		updateInfo();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MENU_FEEDBACK, 0,
				R.string.main_menu_feedback);
		item.setIcon(R.drawable.icon_feedback);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_FEEDBACK:
			onFeedbackClicked();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onPlayerProfile(View view) {
		try {
			Intent intent = new Intent(this, PlayerActivity.class);
			intent.putExtra(PlayerActivity.PlayerGUID, me.getUniqueId());
			startActivity(intent);
		} catch (Exception ex) {
			Toast.makeText(this, R.string.main_player_profile_error,
					Toast.LENGTH_SHORT).show();
		}
	}

	public void onBuddies(View view) {
		startActivity(new Intent(this, BuddiesActivity.class));
	}

	public void onQuickMatch(View view) {
		// TODO: should i check for latestMatch ?
		startActivity(new Intent(this, MatchActivity.class));
	}

	public void onCurrentMatch(View view) {
		Intent intent = new Intent(this, MatchActivity.class);
		intent.putExtra(MatchActivity.MatchId, latestMatch.getUniqueId());
		startActivity(intent);
	}

	public void onFinishMatch(View view) {
		if (latestMatch == null) {
			return;
		}
		Set<Player> blue = latestMatch.getBlueD_inverse();
		blue.addAll(latestMatch.getBlueO_inverse());

		Set<Player> red = latestMatch.getRedO_inverse();
		red.addAll(latestMatch.getRedD_inverse());

		String blueTeam = playersToString(blue);
		String redTeam = playersToString(red);

		MatchDialog dialog = new MatchDialog(this, this);
		dialog.setTeams(blueTeam, redTeam);
		dialog.show();
	}

	@Override
	public void onMatchResult(int blueScore, int redScore) {
		latestMatch.setBlueScore(blueScore);
		latestMatch.setRedScore(redScore);

		execute(new Job() {

			private Map<String, Integer> totalGames = new HashMap<String, Integer>();
			private Map<String, Integer> wonGames = new HashMap<String, Integer>();
			private Map<String, Integer> scorePoints = new HashMap<String, Integer>();

			@Override
			public void doWork() {
				Set<Player> allPlayers = new HashSet<Player>();
				for (Match match : getSAM().getAllMatchs()) {
					Set<Player> blue = match.getBlueD_inverse();
					blue.addAll(match.getBlueO_inverse());

					Set<Player> red = match.getRedD_inverse();
					red.addAll(match.getRedO_inverse());

					Set<Player> all = new HashSet<Player>();
					all.addAll(blue);
					all.addAll(red);

					allPlayers.addAll(all);

					int blueScore = 0;
					int redScore = 0;
					try {
						blueScore = match.getBlueScore();
						redScore = match.getRedScore();
					} catch (Exception ex) {
						continue;
					}

					for (Person p : all) {
						String pid = p.getUniqueId();
						Integer total = totalGames.containsKey(pid) ? totalGames
								.get(pid) : 0;
						Integer won = wonGames.containsKey(pid) ? wonGames
								.get(pid) : 0;
						Integer score = scorePoints.containsKey(pid) ? scorePoints
								.get(pid) : 0;
						total += 1;
						if (blue.contains(p)) {
							score += blueScore;
							if (blueScore > redScore) {
								won += 1;
							}
						} else if (red.contains(p)) {
							score += redScore;
							if (redScore > blueScore) {
								won += 1;
							}
						}

						totalGames.put(pid, total);
						wonGames.put(pid, won);
						scorePoints.put(pid, score);
					}
				}

				for (Player p : allPlayers) {
					String pid = p.getUniqueId();
					p.setTotalGames(totalGames.get(pid));
					p.setWinRate(100 * wonGames.get(pid) / totalGames.get(pid));
					p.setScorePoints(scorePoints.get(pid) / totalGames.get(pid));
				}
			}

			@Override
			public void doUIAfter() {
				refreshUI();
				sendNotify();
			}
		});
	}

	protected void setLatestMatch(Match latestMatch) {
		this.latestMatch = latestMatch;
	}

	@Override
	protected void refreshUI() {
		try {
			if (me == null) {
				Person p = getSAM().getMe();
				me = new PlayerImpl(getSAM(), ((YartaResource) p).getNode());
			}
			String name = me.getName();
			if (name != null) {
				setCtrlText(R.id.name, name);
			}
		} catch (KBException ex) {
			ex.printStackTrace();
		}

		int matchesPlayed = 0;

		try {
			matchesPlayed = me.getTotalGames();
		} catch (Exception ex) {
			// do nothing, it's normal
		}
		setCtrlText(R.id.info, String.format(
				getString(R.string.main_player_matches_played), matchesPlayed));

		execute(new Job() {

			Match latestMatch = null;

			@Override
			public void doWork() {
				try {
					// only the owner matches
					Set<Match> ownerMatches = new HashSet<Match>();
					ownerMatches.addAll(me.getBlueD());
					ownerMatches.addAll(me.getBlueO());
					ownerMatches.addAll(me.getRedD());
					ownerMatches.addAll(me.getRedO());

					long latestMatchTime = 0;
					for (Match match : ownerMatches) {
						Set<Player> players = match.getRedD_inverse();
						players.addAll(match.getRedO_inverse());
						players.addAll(match.getBlueD_inverse());
						players.addAll(match.getBlueO_inverse());

						Integer blueScore = null, redScore = null;
						try {
							blueScore = match.getBlueScore();
							redScore = match.getRedScore();
						} catch (NumberFormatException ex) {
						}
						if (blueScore == null || redScore == null) {
							long time = match.getTime();
							if (time > latestMatchTime) {
								latestMatchTime = time;
								latestMatch = match;
							}
						}
					}
				} catch (Exception ex) {
				}
			}

			@Override
			public void doUIAfter() {
				setLatestMatch(latestMatch);

				if (latestMatch != null) {
					showView(R.id.latestMatch, true);
					showView(R.id.noLatestMatch, false);

					Set<Player> blue = latestMatch.getBlueD_inverse();
					blue.addAll(latestMatch.getBlueO_inverse());

					Set<Player> red = latestMatch.getRedO_inverse();
					red.addAll(latestMatch.getRedD_inverse());

					String blueText = getString(R.string.main_blue_team)
							+ playersToString(blue);
					String redText = getString(R.string.main_red_team)
							+ playersToString(red);

					setCtrlText(R.id.blueTeam, blueText);
					setCtrlText(R.id.redTeam, redText);
				} else {
					showView(R.id.latestMatch, false);
					showView(R.id.noLatestMatch, true);
				}
			}
		});
	}

	/**
	 * Gets the string representation of a set.
	 * 
	 * @param players
	 * @return
	 */
	protected String playersToString(Set<Player> players) {
		String result = "";
		for (Player player : players) {
			if (result.length() > 0) {
				result += ", ";
			}

			String name = player.getName();
			if (name == null) {
				name = player.getUserId();
			}
			result += name;
		}
		return result;
	}

	protected void onFeedbackClicked() {
		FeedbackDialog dialog = new FeedbackDialog(this);
		dialog.setHandler(this);
		dialog.show();
	}

	@Override
	public void onSendFeedback(final String content) {
		execute(new Job() {
			boolean success;

			@Override
			public void doWork() {
				success = FeedbackDialog.sendFeedback(
						"fr.inria.arles.foosball", me.getUserId(), content);
			}

			@Override
			public void doUIAfter() {
				Toast.makeText(
						getApplicationContext(),
						success ? R.string.main_feedback_sent_ok
								: R.string.main_feedback_sent_error,
						Toast.LENGTH_LONG).show();
			}
		});
	}
}

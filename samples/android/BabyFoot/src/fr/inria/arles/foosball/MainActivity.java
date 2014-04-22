package fr.inria.arles.foosball;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import fr.inria.arles.foosball.R;
import fr.inria.arles.foosball.util.JobRunner.Job;
import fr.inria.arles.foosball.resources.Match;
import fr.inria.arles.foosball.resources.Person;
import fr.inria.arles.foosball.resources.PersonImpl;
import fr.inria.arles.yarta.resources.YartaResource;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements MatchDialog.Handler,
		NameConfigureDialog.Handler, FeedbackDialog.Handler {

	public static final int MENU_LOGOUT = 1;
	public static final int MENU_FEEDBACK = 2;
	public static final int MENU_NAME = 3;
	public static final int MENU_ABOUT = 4;

	private Person me;
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
		SubMenu subMenu = menu.addSubMenu(R.string.main_menu_more);

		subMenu.add(0, MENU_NAME, 0, R.string.main_menu_configure);
		subMenu.add(0, MENU_FEEDBACK, 0, R.string.main_menu_feedback);
		subMenu.add(0, MENU_LOGOUT, 0, R.string.main_menu_logout);
		// subMenu.add(0, MENU_ABOUT, 0, R.string.main_menu_about);

		MenuItem parent = subMenu.getItem();
		parent.setIcon(R.drawable.abs__ic_menu_moreoverflow_normal_holo_dark);
		parent.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_LOGOUT:
			onLogout();
			break;
		case MENU_FEEDBACK:
			onFeedback();
			break;
		case MENU_ABOUT:
			onAbout();
			break;
		case MENU_NAME:
			onConfigure();
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
		Set<Person> blue = latestMatch.getBlueD();
		blue.addAll(latestMatch.getBlueO());

		Set<Person> red = latestMatch.getRedO();
		red.addAll(latestMatch.getRedD());

		String blueTeam = peopleToString(blue);
		String redTeam = peopleToString(red);

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
				Set<Person> allPersons = new HashSet<Person>();
				for (Match match : getSAM().getAllMatchs()) {
					Set<Person> blue = match.getBlueD();
					blue.addAll(match.getBlueO());

					Set<Person> red = match.getRedD();
					red.addAll(match.getRedO());

					Set<Person> all = new HashSet<Person>();
					all.addAll(blue);
					all.addAll(red);

					allPersons.addAll(all);

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
						score += blueScore;
						if (blueScore > redScore && blue.contains(p)) {
							won += 1;
						} else if (redScore > blueScore && red.contains(p)) {
							won += 1;
						}

						totalGames.put(pid, total);
						wonGames.put(pid, won);
						scorePoints.put(pid, score);
					}
				}

				for (Person p : allPersons) {
					String pid = p.getUniqueId();
					p.setTotalGames(totalGames.get(pid));
					p.setWinRate(100 * wonGames.get(pid) / totalGames.get(pid));
					p.setScorePoints(scorePoints.get(pid) / totalGames.get(pid));
				}
			}

			@Override
			public void doUIAfter() {
				refreshUI();
				getCOMM().sendNotify(PlayersApp.InriaID);
			}
		});
	}

	protected void setLatestMatch(Match latestMatch) {
		this.latestMatch = latestMatch;
	}

	@Override
	public void onNameSet(String nickName) {
		if (me == null) {
			Person p = getSAM().getMe();
			me = new PersonImpl(getSAM(), ((YartaResource) p).getNode());
		}
		me.setNickName(nickName);
		setCtrlText(R.id.name, me.getNickName());
		configureDlg = null;
	}

	@Override
	protected void refreshUI() {
		if (me == null) {
			Person p = getSAM().getMe();
			me = new PersonImpl(getSAM(), ((YartaResource) p).getNode());
		}

		String nickName = me.getNickName();

		if (nickName == null) {
			onConfigure();
		}
		setCtrlText(R.id.name,
				nickName == null ? me.getUserId().replace("@inria.fr", "")
						: nickName);

		int matchesPlayed = 0;

		try {
			matchesPlayed = me.getTotalGames();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		setCtrlText(R.id.info, String.format(
				getString(R.string.main_player_matches_played), matchesPlayed));

		execute(new Job() {

			Match latestMatch = null;

			@Override
			public void doWork() {
				try {
					Set<Match> matches = getSAM().getAllMatchs();
					long latestMatchTime = 0;
					for (Match match : matches) {
						Set<Person> players = match.getRedD();
						players.addAll(match.getRedO());
						players.addAll(match.getBlueD());
						players.addAll(match.getBlueO());

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

					Set<Person> blue = latestMatch.getBlueD();
					blue.addAll(latestMatch.getBlueO());

					Set<Person> red = latestMatch.getRedO();
					red.addAll(latestMatch.getRedD());

					String blueText = getString(R.string.main_blue_team)
							+ peopleToString(blue);
					String redText = getString(R.string.main_red_team)
							+ peopleToString(red);

					setCtrlText(R.id.blueTeam, blueText);
					setCtrlText(R.id.redTeam, redText);
				} else {
					showView(R.id.latestMatch, false);
					showView(R.id.noLatestMatch, true);
				}
			}
		});
	}

	/*
	 * From a set of Persons returns a string containing first names;
	 */
	protected String peopleToString(Set<Person> persons) {
		String result = "";
		for (Person person : persons) {
			if (result.length() > 0) {
				result += ", ";
			}
			String nickName = person.getNickName();
			if (nickName == null) {
				nickName = person.getUserId().replace("@inria.fr", "");
			}
			result += nickName;
		}
		return result;
	}

	protected void onLogout() {
		// TODO: call mse.clear and mse = null;
		// TODO: remove uninit mse which is useless;
		getMSE().clear();
		uninitMSE();
		finish();
	}

	protected void onFeedback() {
		FeedbackDialog dialog = new FeedbackDialog(this);
		dialog.setHandler(this);
		dialog.show();
	}

	protected void onAbout() {
		startActivity(new Intent(this, AboutActivity.class));
	}

	private NameConfigureDialog configureDlg;

	protected void onConfigure() {
		String nickName = me.getNickName();

		if (nickName == null) {
			nickName = me.getUserId().replace("@inria.fr", "");
		}

		if (configureDlg == null) {
			configureDlg = new NameConfigureDialog(this, nickName);
			configureDlg.setHandler(this);
			configureDlg.show();
		} else {
			configureDlg.show();
		}
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

package fr.inria.arles.foosball;

import java.util.HashSet;
import java.util.Set;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.facebook.Session;

import fr.inria.arles.foosball.R;
import fr.inria.arles.foosball.util.JobRunner.Job;
import fr.inria.arles.foosball.util.Match;
import fr.inria.arles.foosball.util.Player;
import fr.inria.arles.foosball.util.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements MatchDialog.Handler {

	public static final int MENU_LOGOUT = 1;
	public static final int MENU_FEEDBACK = 2;
	public static final int MENU_ABOUT = 3;

	private Match latestMatch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu subMenu = menu.addSubMenu(R.string.main_menu_more);

		subMenu.add(0, MENU_LOGOUT, 0, R.string.main_menu_logout);
		subMenu.add(0, MENU_FEEDBACK, 0, R.string.main_menu_feedback);
		subMenu.add(0, MENU_ABOUT, 0, R.string.main_menu_about);

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
		}
		return super.onOptionsItemSelected(item);
	}

	public void onPlayerProfile(View view) {
		execute(new Job() {

			String userId;

			@Override
			public void doWork() {
				userId = core.getUserId();
			}

			@Override
			public void doUIAfter() {
				try {
					Intent intent = new Intent(getApplicationContext(),
							PlayerActivity.class);
					intent.putExtra(PlayerActivity.PlayerId, userId);
					startActivity(intent);
				} catch (Exception ex) {
					Toast.makeText(getApplicationContext(),
							R.string.main_player_profile_error,
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	public void onBuddies(View view) {
		startActivity(new Intent(this, BuddiesActivity.class));
	}

	public void onQuickMatch(View view) {
		// TODO: should i check for latestMatch ?
		startActivity(new Intent(this, MatchActivity.class));
	}

	public void onFinishMatch(View view) {
		Set<Player> blue = new HashSet<Player>();
		blue.add(latestMatch.getBlueD());
		blue.add(latestMatch.getBlueO());

		Set<Player> red = new HashSet<Player>();
		red.add(latestMatch.getRedO());
		red.add(latestMatch.getRedD());

		String blueTeam = peopleToString(blue);
		String redTeam = peopleToString(red);

		MatchDialog dialog = new MatchDialog(this, this);
		dialog.setTeams(blueTeam, redTeam);
		dialog.show();
	}

	@Override
	public void onMatchResult(final int blueScore, final int redScore) {

		execute(new Job() {
			@Override
			public void doWork() {
				core.setMatchScore(latestMatch, blueScore, redScore);
			}

			@Override
			public void doUIAfter() {
				refreshUI();
			}
		});
	}

	protected void setLatestMatch(Match latestMatch) {
		this.latestMatch = latestMatch;
	}

	protected void refreshUI() {
		execute(new Job() {
			String username;
			String userId;
			int gamesPlayed;

			@Override
			public void doWork() {
				username = core.getUsername();
				userId = core.getUserId();
				gamesPlayed = core.getTotalGames(userId);
				setLatestMatch(core.getLatestMatch());
			}

			@Override
			public void doUIAfter() {
				setCtrlText(R.id.name, username);
				setCtrlText(R.id.info, String.format(
						getString(R.string.main_player_matches_played),
						gamesPlayed));
				
				if (latestMatch != null) {
					showView(R.id.latestMatch, true);
					showView(R.id.noLatestMatch, false);

					Set<Player> blue = new HashSet<Player>();

					blue.add(latestMatch.getBlueD());
					blue.add(latestMatch.getBlueO());

					Set<Player> red = new HashSet<Player>();
					red.add(latestMatch.getRedD());
					red.add(latestMatch.getRedO());

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

	protected String peopleToString(Set<Player> persons) {
		String result = "";
		for (Player person : persons) {
			if (result.length() > 0) {
				result += ", ";
			}
			result += person.getUserName();
		}
		return result;
	}

	protected void onLogout() {
		Session session = Session.getActiveSession();
		
		if (session != null) {
			session.closeAndClearTokenInformation();
		}
		settings.setString(Settings.LOGIN_USER, "");
		startActivity(new Intent(this, LoginActivity.class));
		finish();
	}

	protected void onFeedback() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.putExtra(Intent.EXTRA_SUBJECT,
				getString(R.string.main_feedback_subject));
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { "george.rosca@inria.fr" });

		i.setType("text/plain");
		startActivity(Intent.createChooser(i,
				getString(R.string.main_mail_pick)));
	}

	protected void onAbout() {
		startActivity(new Intent(this, AboutActivity.class));
	}
}

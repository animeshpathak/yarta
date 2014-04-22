package fr.inria.arles.foosball.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class FoosballCore {

	public static final String DEFAULT_REDIRECT_URL = "http://yarta.gforge.inria.fr/";
	private static FoosballCore instance = new FoosballCore();

	private FoosballCore() {
	}

	public static FoosballCore getInstance() {
		return instance;
	}

	public String getUsername() {
		if (currentUser != null) {
			return currentUser.getUserName();
		}
		return null;
	}

	public String getUserId() {
		return currentUser.getUserId();
	}

	public String getUsername(String userId) {
		if (userId.endsWith(currentUser.getUserId())) {
			return currentUser.getUserName();
		}

		for (Player player : players) {
			if (player.getUserId().equals(userId)) {
				return player.getUserName();
			}
		}
		return null;
	}

	public void setCurrentUser(String id, String name) {
		currentUser = new Player(id, name);
	}

	public Map<String, Integer> totalGames = new HashMap<String, Integer>();

	public int fastGetTotalGames(String id) {
		if (totalGames.containsKey(id)) {
			return totalGames.get(id);
		}
		return 0;
	}

	public void setFriends(List<Player> players) {
		this.players = players;
	}

	private Player currentUser;
	private List<Player> players;

	public List<Player> getBuddies() {
		return players;
	}

	/**
	 * Client API
	 */
	private static final String queryURL = "http://spatialia.com/foosball/?";

	public boolean createMatch(Player blueD, Player blueO, Player redD,
			Player redO) {
		String url = queryURL;
		url += "method=create";
		url += "&blued=" + blueD.getUserId();
		url += "&blueo=" + blueO.getUserId();
		url += "&redd=" + redD.getUserId();
		url += "&redo=" + redO.getUserId();
		url += "&seed=" + System.currentTimeMillis();

		return readUrl(url).contains("OK");
	}

	public Match getLatestMatch() {
		String url = queryURL;
		url += "method=latest";
		url += "&id=" + getUserId();
		url += "&seed=" + System.currentTimeMillis();

		String result = readUrl(url);

		try {
			StringTokenizer tok = new StringTokenizer(result, ",");
			String id = tok.nextToken();

			String bluedid = tok.nextToken();
			Player blued = new Player(bluedid, getUsername(bluedid));

			String blueoid = tok.nextToken();
			Player blueo = new Player(blueoid, getUsername(blueoid));

			String reddid = tok.nextToken();
			Player redd = new Player(reddid, getUsername(reddid));

			String redoid = tok.nextToken();
			Player redo = new Player(redoid, getUsername(redoid));

			return new Match(id, blued, blueo, redd, redo);
		} catch (Exception ex) {
			return null;
		}
	}

	public boolean setMatchScore(Match match, int blueScore, int redScore) {
		String url = queryURL;
		url += "method=score";
		url += "&id=" + match.getId();
		url += "&bluep=" + blueScore;
		url += "&redp=" + redScore;
		url += "&seed=" + System.currentTimeMillis();
		return readUrl(url).contains("OK");
	}

	public int getTotalGames(String userId) {
		String url = queryURL;
		url += "method=total";
		url += "&id=" + userId;
		url += "&seed=" + System.currentTimeMillis();
		String total = readUrl(url);

		try {
			Integer t = Integer.valueOf(total);
			totalGames.put(userId, t);
			return t;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public int getWonGames(String userId) {
		String url = queryURL;
		url += "method=won";
		url += "&id=" + userId;
		url += "&seed=" + System.currentTimeMillis();
		String total = readUrl(url);

		try {
			return Integer.valueOf(total);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public int getScorePoints(String userId) {
		String url = queryURL;
		url += "method=points";
		url += "&id=" + userId;
		url += "&seed=" + System.currentTimeMillis();
		String total = readUrl(url);

		try {
			return Integer.valueOf(total);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	private String readUrl(String url) {
		System.out.println("reading url:" + url);

		String result = "";
		try {
			URL oracle = new URL(url);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					oracle.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				result += inputLine;
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}
}

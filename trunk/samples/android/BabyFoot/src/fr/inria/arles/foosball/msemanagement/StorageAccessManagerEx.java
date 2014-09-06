package fr.inria.arles.foosball.msemanagement;

import java.util.Set;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.foosball.resources.PlayerImpl;
import fr.inria.arles.foosball.resources.Player;
import fr.inria.arles.foosball.resources.MatchImpl;
import fr.inria.arles.foosball.resources.Match;

/**
 * StorageAccessManager class extension.
 */
public class StorageAccessManagerEx extends StorageAccessManager {

	/**
	 * Basic constructor. Binds interfaces to implementation.
	 */
	public StorageAccessManagerEx() {
		super();

			bindInterfacetoImplementation(fr.inria.arles.yarta.resources.Person.typeURI,
				"fr.inria.arles.foosball.resources.PlayerImpl");
		
		bindInterfacetoImplementation(Player.typeURI,
				"fr.inria.arles.foosball.resources.PlayerImpl");

		
		bindInterfacetoImplementation(Match.typeURI,
				"fr.inria.arles.foosball.resources.MatchImpl");
	}

	/**
	 * Creates a new instance of Player
	 */
	public Player createPlayer(String uniqueId) {
		return (Player) new PlayerImpl(this, uniqueId);
	}

	/**
	 * Returns all instances of type Player
	 */
	public Set<Player> getAllPlayers() {
		return getAllResourcesOfType(getPropertyNode(Player.typeURI));
	}

	/**
	 * Creates a new instance of Match
	 */
	public Match createMatch() {
		return (Match) new MatchImpl(this,
				createNewNode(Match.typeURI));
	}

	/**
	 * Returns all instances of type Match
	 */
	public Set<Match> getAllMatchs() {
		return getAllResourcesOfType(getPropertyNode(Match.typeURI));
	}
}
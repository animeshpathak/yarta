package fr.inria.arles.foosball.msemanagement;

import java.util.Set;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.foosball.resources.MatchImpl;
import fr.inria.arles.foosball.resources.Match;
import fr.inria.arles.foosball.resources.PersonImpl;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.YartaResource;

/**
 * StorageAccessManager class extension.
 */
public class StorageAccessManagerEx extends StorageAccessManager {

	/**
	 * Basic constructor. Binds interfaces to implementation.
	 */
	public StorageAccessManagerEx() {
		super();
		bindInterfacetoImplementation(Match.typeURI,
				"fr.inria.arles.foosball.resources.MatchImpl");
		bindInterfacetoImplementation(Person.typeURI,
				"fr.inria.arles.foosball.resources.PersonImpl");
	}

	public fr.inria.arles.foosball.resources.Person getMe() {
		try {
			Person p = super.getMe();
			return new PersonImpl(this, ((YartaResource) p).getNode());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates a new instance of Match
	 */
	public Match createMatch() {
		return (Match) new MatchImpl(this, createNewNode(Match.typeURI));
	}

	/**
	 * Returns all instances of type Match
	 */
	public Set<Match> getAllMatchs() {
		return getAllResourcesOfType(getPropertyNode(Match.typeURI));
	}

	/**
	 * Creates a new instance of Person
	 */
	public Person createPerson(String uniqueId) {
		return (Person) new PersonImpl(this, uniqueId);
	}

	/**
	 * Returns all instances of type Person
	 */
	public Set<Person> getAllPersons() {
		return getAllResourcesOfType(getPropertyNode(Person.typeURI));
	}
}
package fr.inria.arles.yarta.android.library.msemanagement;

import java.util.Set;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.yarta.android.library.resources.GroupImpl;
import fr.inria.arles.yarta.resources.Group;
import fr.inria.arles.yarta.android.library.resources.AgentImpl;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.android.library.resources.PictureImpl;
import fr.inria.arles.yarta.android.library.resources.Picture;
import fr.inria.arles.yarta.android.library.resources.PersonImpl;
import fr.inria.arles.yarta.resources.Person;

/**
 * StorageAccessManager class extension.
 */
public class StorageAccessManagerEx extends StorageAccessManager {

	/**
	 * Basic constructor. Binds interfaces to implementation.
	 */
	public StorageAccessManagerEx() {
		super();
		bindInterfacetoImplementation(Group.typeURI,
				"fr.inria.arles.yarta.android.library.resources.GroupImpl");
		bindInterfacetoImplementation(Agent.typeURI,
				"fr.inria.arles.yarta.android.library.resources.AgentImpl");
		bindInterfacetoImplementation(Picture.typeURI,
				"fr.inria.arles.yarta.android.library.resources.PictureImpl");
		bindInterfacetoImplementation(fr.inria.arles.yarta.resources.Person.typeURI,
				"fr.inria.arles.yarta.android.library.resources.PersonImpl");
		bindInterfacetoImplementation(Person.typeURI,
				"fr.inria.arles.yarta.android.library.resources.PersonImpl");
	}

	/**
	 * Creates a new instance of Group
	 */
	public Group createGroup() {
		return (Group) new GroupImpl(this,
				createNewNode(Group.typeURI));
	}

	/**
	 * Returns all instances of type Group
	 */
	public Set<Group> getAllGroups() {
		return getAllResourcesOfType(getPropertyNode(Group.typeURI));
	}

	/**
	 * Creates a new instance of Agent
	 */
	public Agent createAgent() {
		return (Agent) new AgentImpl(this,
				createNewNode(Agent.typeURI));
	}

	/**
	 * Returns all instances of type Agent
	 */
	public Set<Agent> getAllAgents() {
		return getAllResourcesOfType(getPropertyNode(Agent.typeURI));
	}

	/**
	 * Creates a new instance of Picture
	 */
	public Picture createPicture() {
		return (Picture) new PictureImpl(this,
				createNewNode(Picture.typeURI));
	}

	/**
	 * Returns all instances of type Picture
	 */
	public Set<Picture> getAllPictures() {
		return getAllResourcesOfType(getPropertyNode(Picture.typeURI));
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

package fr.inria.arles.yarta.android.library.msemanagement;

import java.util.Set;

import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.yarta.android.library.resources.PictureImpl;
import fr.inria.arles.yarta.android.library.resources.Picture;
import fr.inria.arles.yarta.android.library.resources.AgentImpl;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.android.library.resources.PersonImpl;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.android.library.resources.GroupImpl;
import fr.inria.arles.yarta.resources.Group;

/**
 * StorageAccessManager class extension.
 */
public class StorageAccessManagerEx extends StorageAccessManager {

	/**
	 * Basic constructor. Binds interfaces to implementation.
	 */
	public StorageAccessManagerEx() {
		super();
		bindInterfacetoImplementation(Picture.typeURI,
				"fr.inria.arles.yarta.android.library.resources.PictureImpl");
		bindInterfacetoImplementation(Agent.typeURI,
				"fr.inria.arles.yarta.android.library.resources.AgentImpl");
		bindInterfacetoImplementation(Person.typeURI,
				"fr.inria.arles.yarta.android.library.resources.PersonImpl");
		bindInterfacetoImplementation(Group.typeURI,
				"fr.inria.arles.yarta.android.library.resources.GroupImpl");
	}

	public fr.inria.arles.yarta.android.library.resources.Person getMe()
			throws KBException {
		fr.inria.arles.yarta.android.library.resources.Person person = new fr.inria.arles.yarta.android.library.resources.PersonImpl(
				this,
				new MSEResource(
						super.getMe().getUniqueId(),
						fr.inria.arles.yarta.android.library.resources.Person.typeURI));
		return person;
	}

	public fr.inria.arles.yarta.android.library.resources.Person getPersonByUserId(
			String userId) throws KBException {
		Person person = super.getPersonByUserId(userId);

		if (person != null) {
			return new fr.inria.arles.yarta.android.library.resources.PersonImpl(
					this,
					new MSEResource(
							person.getUniqueId(),
							fr.inria.arles.yarta.android.library.resources.Person.typeURI));
		}

		return null;
	}

	/**
	 * Creates a new instance of Picture
	 */
	public Picture createPicture() {
		return (Picture) new PictureImpl(this, createNewNode(Picture.typeURI));
	}

	/**
	 * Returns all instances of type Picture
	 */
	public Set<Picture> getAllPictures() {
		return getAllResourcesOfType(getPropertyNode(Picture.typeURI));
	}

	/**
	 * Creates a new instance of Agent
	 */
	public Agent createAgent() {
		return (Agent) new AgentImpl(this, createNewNode(Agent.typeURI));
	}

	/**
	 * Returns all instances of type Agent
	 */
	public Set<Agent> getAllAgents() {
		return getAllResourcesOfType(getPropertyNode(Agent.typeURI));
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

	/**
	 * Creates a new instance of Group
	 */
	public Group createGroup() {
		return (Group) new GroupImpl(this, createNewNode(Group.typeURI));
	}

	/**
	 * Returns all instances of type Group
	 */
	public Set<Group> getAllGroups() {
		return getAllResourcesOfType(getPropertyNode(Group.typeURI));
	}
}
package fr.inria.arles.yarta.android.library.msemanagement;

import java.util.Set;

import fr.inria.arles.yarta.knowledgebase.KBException;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
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
		bindInterfacetoImplementation(Picture.typeURI,
				"fr.inria.arles.yarta.android.library.resources.PictureImpl");
		bindInterfacetoImplementation(Person.typeURI,
				"fr.inria.arles.yarta.android.library.resources.PersonImpl");
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
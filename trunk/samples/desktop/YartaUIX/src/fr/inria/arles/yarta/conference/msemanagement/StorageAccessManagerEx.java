package fr.inria.arles.yarta.conference.msemanagement;

import java.util.Set;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.yarta.conference.resources.PaperImpl;
import fr.inria.arles.yarta.conference.resources.Paper;
import fr.inria.arles.yarta.conference.resources.MutantImpl;
import fr.inria.arles.yarta.conference.resources.Mutant;
import fr.inria.arles.yarta.conference.resources.MegaSpeakerImpl;
import fr.inria.arles.yarta.conference.resources.MegaSpeaker;
import fr.inria.arles.yarta.conference.resources.EventImpl;
import fr.inria.arles.yarta.resources.Event;
import fr.inria.arles.yarta.conference.resources.PresentationImpl;
import fr.inria.arles.yarta.conference.resources.Presentation;
import fr.inria.arles.yarta.conference.resources.BuildingImpl;
import fr.inria.arles.yarta.conference.resources.Building;
import fr.inria.arles.yarta.conference.resources.SpeakerImpl;
import fr.inria.arles.yarta.conference.resources.Speaker;
import fr.inria.arles.yarta.conference.resources.CompanyImpl;
import fr.inria.arles.yarta.conference.resources.Company;
import fr.inria.arles.yarta.conference.resources.ConferenceImpl;
import fr.inria.arles.yarta.conference.resources.Conference;
import fr.inria.arles.yarta.conference.resources.PersonImpl;
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
		bindInterfacetoImplementation(Paper.typeURI,
				"fr.inria.arles.yarta.conference.resources.PaperImpl");
		bindInterfacetoImplementation(Mutant.typeURI,
				"fr.inria.arles.yarta.conference.resources.MutantImpl");
		bindInterfacetoImplementation(MegaSpeaker.typeURI,
				"fr.inria.arles.yarta.conference.resources.MegaSpeakerImpl");
		bindInterfacetoImplementation(Event.typeURI,
				"fr.inria.arles.yarta.conference.resources.EventImpl");
		bindInterfacetoImplementation(Presentation.typeURI,
				"fr.inria.arles.yarta.conference.resources.PresentationImpl");
		bindInterfacetoImplementation(Building.typeURI,
				"fr.inria.arles.yarta.conference.resources.BuildingImpl");
		bindInterfacetoImplementation(Speaker.typeURI,
				"fr.inria.arles.yarta.conference.resources.SpeakerImpl");
		bindInterfacetoImplementation(Company.typeURI,
				"fr.inria.arles.yarta.conference.resources.CompanyImpl");
		bindInterfacetoImplementation(Conference.typeURI,
				"fr.inria.arles.yarta.conference.resources.ConferenceImpl");
		bindInterfacetoImplementation(Person.typeURI,
				"fr.inria.arles.yarta.conference.resources.PersonImpl");
	}

	/**
	 * Creates a new instance of Paper
	 */
	public Paper createPaper() {
		return (Paper) new PaperImpl(this,
				createNewNode(Paper.typeURI));
	}

	/**
	 * Returns all instances of type Paper
	 */
	public Set<Paper> getAllPapers() {
		return getAllResourcesOfType(getPropertyNode(Paper.typeURI));
	}

	/**
	 * Creates a new instance of Mutant
	 */
	public Mutant createMutant(String uniqueId) {
		return (Mutant) new MutantImpl(this, uniqueId);
	}

	/**
	 * Returns all instances of type Mutant
	 */
	public Set<Mutant> getAllMutants() {
		return getAllResourcesOfType(getPropertyNode(Mutant.typeURI));
	}

	/**
	 * Creates a new instance of MegaSpeaker
	 */
	public MegaSpeaker createMegaSpeaker(String uniqueId) {
		return (MegaSpeaker) new MegaSpeakerImpl(this, uniqueId);
	}

	/**
	 * Returns all instances of type MegaSpeaker
	 */
	public Set<MegaSpeaker> getAllMegaSpeakers() {
		return getAllResourcesOfType(getPropertyNode(MegaSpeaker.typeURI));
	}

	/**
	 * Creates a new instance of Event
	 */
	public Event createEvent() {
		return (Event) new EventImpl(this,
				createNewNode(Event.typeURI));
	}

	/**
	 * Returns all instances of type Event
	 */
	public Set<Event> getAllEvents() {
		return getAllResourcesOfType(getPropertyNode(Event.typeURI));
	}

	/**
	 * Creates a new instance of Presentation
	 */
	public Presentation createPresentation() {
		return (Presentation) new PresentationImpl(this,
				createNewNode(Presentation.typeURI));
	}

	/**
	 * Returns all instances of type Presentation
	 */
	public Set<Presentation> getAllPresentations() {
		return getAllResourcesOfType(getPropertyNode(Presentation.typeURI));
	}

	/**
	 * Creates a new instance of Building
	 */
	public Building createBuilding() {
		return (Building) new BuildingImpl(this,
				createNewNode(Building.typeURI));
	}

	/**
	 * Returns all instances of type Building
	 */
	public Set<Building> getAllBuildings() {
		return getAllResourcesOfType(getPropertyNode(Building.typeURI));
	}

	/**
	 * Creates a new instance of Speaker
	 */
	public Speaker createSpeaker(String uniqueId) {
		return (Speaker) new SpeakerImpl(this, uniqueId);
	}

	/**
	 * Returns all instances of type Speaker
	 */
	public Set<Speaker> getAllSpeakers() {
		return getAllResourcesOfType(getPropertyNode(Speaker.typeURI));
	}

	/**
	 * Creates a new instance of Company
	 */
	public Company createCompany() {
		return (Company) new CompanyImpl(this,
				createNewNode(Company.typeURI));
	}

	/**
	 * Returns all instances of type Company
	 */
	public Set<Company> getAllCompanys() {
		return getAllResourcesOfType(getPropertyNode(Company.typeURI));
	}

	/**
	 * Creates a new instance of Conference
	 */
	public Conference createConference() {
		return (Conference) new ConferenceImpl(this,
				createNewNode(Conference.typeURI));
	}

	/**
	 * Returns all instances of type Conference
	 */
	public Set<Conference> getAllConferences() {
		return getAllResourcesOfType(getPropertyNode(Conference.typeURI));
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
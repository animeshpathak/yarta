package fr.inria.arles.giveaway.msemanagement;

import java.util.Set;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.giveaway.resources.PictureImpl;
import fr.inria.arles.giveaway.resources.Picture;
import fr.inria.arles.giveaway.resources.CategoryImpl;
import fr.inria.arles.giveaway.resources.Category;
import fr.inria.arles.giveaway.resources.DonationImpl;
import fr.inria.arles.giveaway.resources.Donation;
import fr.inria.arles.giveaway.resources.RequestImpl;
import fr.inria.arles.giveaway.resources.Request;
import fr.inria.arles.giveaway.resources.SaleImpl;
import fr.inria.arles.giveaway.resources.Sale;
import fr.inria.arles.giveaway.resources.AnnouncementImpl;
import fr.inria.arles.giveaway.resources.Announcement;
import fr.inria.arles.giveaway.resources.PersonImpl;
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
				"fr.inria.arles.giveaway.resources.PictureImpl");
		bindInterfacetoImplementation(Category.typeURI,
				"fr.inria.arles.giveaway.resources.CategoryImpl");
		bindInterfacetoImplementation(Donation.typeURI,
				"fr.inria.arles.giveaway.resources.DonationImpl");
		bindInterfacetoImplementation(Request.typeURI,
				"fr.inria.arles.giveaway.resources.RequestImpl");
		bindInterfacetoImplementation(Sale.typeURI,
				"fr.inria.arles.giveaway.resources.SaleImpl");
		bindInterfacetoImplementation(Announcement.typeURI,
				"fr.inria.arles.giveaway.resources.AnnouncementImpl");
		bindInterfacetoImplementation(Person.typeURI,
				"fr.inria.arles.giveaway.resources.PersonImpl");
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
	 * Creates a new instance of Category
	 */
	public Category createCategory() {
		return (Category) new CategoryImpl(this,
				createNewNode(Category.typeURI));
	}

	/**
	 * Returns all instances of type Category
	 */
	public Set<Category> getAllCategorys() {
		return getAllResourcesOfType(getPropertyNode(Category.typeURI));
	}

	/**
	 * Creates a new instance of Donation
	 */
	public Donation createDonation() {
		return (Donation) new DonationImpl(this,
				createNewNode(Donation.typeURI));
	}

	/**
	 * Returns all instances of type Donation
	 */
	public Set<Donation> getAllDonations() {
		return getAllResourcesOfType(getPropertyNode(Donation.typeURI));
	}

	/**
	 * Creates a new instance of Request
	 */
	public Request createRequest() {
		return (Request) new RequestImpl(this,
				createNewNode(Request.typeURI));
	}

	/**
	 * Returns all instances of type Request
	 */
	public Set<Request> getAllRequests() {
		return getAllResourcesOfType(getPropertyNode(Request.typeURI));
	}

	/**
	 * Creates a new instance of Sale
	 */
	public Sale createSale() {
		return (Sale) new SaleImpl(this,
				createNewNode(Sale.typeURI));
	}

	/**
	 * Returns all instances of type Sale
	 */
	public Set<Sale> getAllSales() {
		return getAllResourcesOfType(getPropertyNode(Sale.typeURI));
	}

	/**
	 * Creates a new instance of Announcement
	 */
	public Announcement createAnnouncement() {
		return (Announcement) new AnnouncementImpl(this,
				createNewNode(Announcement.typeURI));
	}

	/**
	 * Returns all instances of type Announcement
	 */
	public Set<Announcement> getAllAnnouncements() {
		return getAllResourcesOfType(getPropertyNode(Announcement.typeURI));
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
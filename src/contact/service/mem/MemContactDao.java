package contact.service.mem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import contact.entity.Contact;
import contact.entity.Contacts;
import contact.service.ContactDao;

/**
 * Data access object for saving and retrieving contacts. This DAO uses an
 * in-memory list of person. Use DaoFactory to get an instance of this class,
 * such as: dao = DaoFactory.getInstance().getContactDao()
 * 
 * @author jim, Veerapat Threeravipark 5510547022
 */
public class MemContactDao implements ContactDao {
	private List<Contact> contacts;
	private AtomicLong nextId;

	/**
	 * Construct list of contact.
	 */
	public MemContactDao() {
		contacts = new ArrayList<Contact>();
		importFile();
		nextId = new AtomicLong(1000L);
		// createTestContact(1);
	}

	/**
	 * Import list of contact from xml source file.
	 */
	public void importFile() {
		checkFile();
		JAXBContext ctx;
		try {
			ctx = JAXBContext.newInstance(Contacts.class);
			Unmarshaller unmarshaller;
			unmarshaller = ctx.createUnmarshaller();
			File file = new File(MemDaoFactory.PATH);
			Contacts contactList = (Contacts) unmarshaller.unmarshal(file);
			if (contactList.getContacts() == null) {
				return;
			}
			contacts = contactList.getContacts();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	private void checkFile() {
		File file = new File(MemDaoFactory.PATH);
		if (!file.exists()) {
			try {
				Contacts contacts = new Contacts();
				JAXBContext context = JAXBContext.newInstance(Contacts.class);
				File outputFile = new File(MemDaoFactory.PATH);
				System.out.println("Created new file -> Path: "
						+ outputFile.getPath());
				Marshaller marshaller = null;
				marshaller = context.createMarshaller();
				marshaller.marshal(contacts, outputFile);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Add a single contact with given id for testing.
	 */
	private void createTestContact(long id) {
		Contact test = new Contact("Test contact", "Joe Experimental",
				"none@testing.com", "0812345678");
		test.setId(id);
		contacts.add(test);
	}

	/**
	 * Find a contact by ID in contacts.
	 * 
	 * @param the
	 *            id of contact to find
	 * @return the matching contact or null if the id is not found
	 */
	public Contact find(long id) {
		for (Contact c : contacts) {
			if (c.getId() == id) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Find a contact by title in contacts.
	 * 
	 * @param the
	 *            title of contact to find
	 * @return the matching contacts or null if the title is not found
	 */
	public List<Contact> findByTitle(String title) {
		List<Contact> result = new ArrayList<Contact>();
		for (Contact c : contacts)
			if (c.getTitle() != null) {
				if (c.getTitle().contains(title)) {
					result.add(c);
				}
			}
		return result;
	}

	/**
	 * Find all contacts.
	 * 
	 * @return list of all contacts.
	 */
	public List<Contact> findAll() {
		return java.util.Collections.unmodifiableList(contacts);
	}

	/**
	 * Delete a saved contact.
	 * 
	 * @param id
	 *            the id of contact to delete
	 * @return true if contact is deleted, false otherwise.
	 */
	public boolean delete(long id) {
		for (int k = 0; k < contacts.size(); k++) {
			if (contacts.get(k).getId() == id) {
				contacts.remove(k);
				return true;
			}
		}
		return false;
	}

	/**
	 * Save or replace a contact. If the contact.id is 0 then it is assumed to
	 * be a new (not saved) contact. In this case a unique id is assigned to the
	 * contact. If the contact.id is not zero and the contact already exists in
	 * saved contacts, the old contact is replaced.
	 * 
	 * @param contact
	 *            the contact to save or replace.
	 * @return true if saved successfully
	 */
	public boolean save(Contact contact) {
		if (contact.getId() == 0) {
			contact.setId(getUniqueId());
			return contacts.add(contact);
		}
		// check if this contact is already in persistent storage
		Contact other = find(contact.getId());
		if (other == contact)
			return true;
		if (other != null)
			contacts.remove(other);
		return contacts.add(contact);
	}

	/**
	 * Update a Contact. Only the non-null fields of the update are applied to
	 * the contact.
	 * 
	 * @param update
	 *            update info for the contact.
	 * @return true if the update is applied successfully.
	 */
	public boolean update(Contact update) {
		Contact contact = find(update.getId());
		if (contact == null)
			return false;
		contact.applyUpdate(update);
		save(contact);
		return true;
	}

	/**
	 * Get a unique contact ID.
	 * 
	 * @return unique id not in persistent storage
	 */
	private synchronized long getUniqueId() {
		long id = nextId.getAndAdd(1L);
		while (id < Long.MAX_VALUE) {
			if (find(id) == null)
				return id;
			id = nextId.getAndAdd(1L);
		}
		return id; // this should never happen
	}

	@Override
	public void removeAll() {
		contacts = new ArrayList<Contact>();
	}

}

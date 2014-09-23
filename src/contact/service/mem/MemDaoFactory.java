package contact.service.mem;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import contact.entity.Contact;
import contact.entity.Contacts;
import contact.service.ContactDao;
import contact.service.DaoFactory;

/**
 * Manage instances of Data Access Objects (DAO) used in the app. This enables
 * you to change the implementation of the actual ContactDao without changing
 * the rest of your application.
 * 
 * @author jim, Veerapat Threeravipark 5510547022
 */
public class MemDaoFactory extends DaoFactory {
	// singleton instance of this factory
	private static MemDaoFactory factory;
	private ContactDao daoInstance;

	private MemDaoFactory() {
		daoInstance = new MemContactDao();
	}

	public static MemDaoFactory getInstance() {
		if (factory == null)
			factory = new MemDaoFactory();
		return factory;
	}

	public ContactDao getContactDao() {
		return daoInstance;
	}

	@Override
	public void shutdown() {
		List<Contact> contacts = daoInstance.findAll();
		Contacts allContacts = new Contacts();
		allContacts.setContacts(contacts);
			JAXBContext context = null;
			try {
				context = JAXBContext.newInstance(Contacts.class);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
			File outputFile = new File("/tmp/ContactService.xml");
			System.out.println("Output file status: "+outputFile.isFile() +" --> Path: " +outputFile.getPath());
			Marshaller marshaller = null;
			try {
				marshaller = context.createMarshaller();
			} catch (JAXBException e) {
				e.printStackTrace();
			}
			try {
				marshaller.marshal(allContacts, outputFile);
			} catch (JAXBException e) {
				e.printStackTrace();
			}

	}
}
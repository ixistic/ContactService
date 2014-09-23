package contact.service.mem;
import contact.service.ContactDao;
import contact.service.DaoFactory;


/**
 * Manage instances of Data Access Objects (DAO) used in the app.
 * This enables you to change the implementation of the actual ContactDao
 * without changing the rest of your application.
 * 
 * @author jim
 */
public class MemDaoFactory {
	// singleton instance of this factory
	private static MemDaoFactory factory;
	private MemContactDao daoInstance;
	
	private MemDaoFactory() {
		daoInstance = new MemContactDao();
	}
	
	public static MemDaoFactory getInstance() {
		if (factory == null) factory = new MemDaoFactory();
		return factory;
	}
	
	public MemContactDao getContactDao() {
		return daoInstance;
	}
}
package contact.service.mem;
import contact.service.ContactDao;
import contact.service.DaoFactory;


/**
 * Manage instances of Data Access Objects (DAO) used in the app.
 * This enables you to change the implementation of the actual ContactDao
 * without changing the rest of your application.
 * 
 * @author jim, Veerapat Threeravipark 5510547022
 */
public class MemDaoFactory extends DaoFactory{
	// singleton instance of this factory
	private static MemDaoFactory factory;
	private ContactDao daoInstance;
	
	private MemDaoFactory() {
		daoInstance = new MemContactDao();
	}
	
	public static MemDaoFactory getInstance() {
		if (factory == null) factory = new MemDaoFactory();
		return factory;
	}
	
	public ContactDao getContactDao() {
		return daoInstance;
	}
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
}
package contact.service.jpa;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import contact.service.ContactDao;
import contact.service.DaoFactory;

/**
 * JpaDaoFactory is a factory for DAO that use the Java Persistence API (JPA)
 * to persist objects.
 * The factory depends on the configuration information in META-INF/persistence.xml.
 * 
 * @see contact.service.DaoFactory
 * @version 2014.09.19
 * @author jim, Veerapat Threeravipark 5510547022
 */
public class JpaDaoFactory extends DaoFactory {
	private static final String PERSISTENCE_UNIT = "contacts";
	private static JpaDaoFactory factory;
	private ContactDao contactDao;
	private final EntityManagerFactory emf;
	private EntityManager em;
	private static Logger logger;
	
	static {
		logger = Logger.getLogger(JpaDaoFactory.class.getName());
	}
	
	public JpaDaoFactory() {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		em = emf.createEntityManager();
		contactDao = new JpaContactDao( em );
	}
	
	/**
	 * Get the instance of DaoFactory.
	 * @return instance of DaoFactory.
	 */
	public static JpaDaoFactory getInstance() {
		if ( factory == null ) {
			factory = new JpaDaoFactory();
		}
		return factory;
	}
	
	@Override
	public ContactDao getContactDao() {
		return contactDao;
	}
	
	@Override
	public void shutdown() {
		try {
			if (em != null && em.isOpen()) em.close();
			if (emf != null && emf.isOpen()) emf.close();
		} catch (IllegalStateException ex) {
			// SEVERE - highest
			logger.log( Level.SEVERE, ex.toString() );
		}
	}
}
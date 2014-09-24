package contact.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import contact.entity.Contact;
import contact.server.JettyMain;
import contact.service.jpa.JpaDaoFactory;

/**
 * A few really basic tests of JPA using the JPA DAO. Common problems are: a)
 * the database name or directory in persistence.xml aren't accessible to you.
 * b) "Can't start database" if another instance of Derby is using this
 * database. c) you don't have JDBC driver for Derby correctly configured in
 * persistence.xml or derby.jar isnt' on the build path. (Similarly for HSQLDB
 * or MySQL.) d) you didn't add persistence annotations (@Entity, @Id) to the
 * entity classes. e) persistence unit name in JpaDaoFactory (used to create
 * EntityManagerFactory) isn't same as the name in persistence.xml
 * 
 * @author jim, Veerapat Threeravipark 5510547022
 * 
 */
public class JpaContactDaoTest {
	private static ContactDao contactDao;
	private Contact foo;
	private static String url;

	@BeforeClass
	public static void doFirst() {
		// this method is called before any tests and before the @Before method
		try {
			url = JettyMain.startServer(8080);
			contactDao = DaoFactory.getInstance().getContactDao();
			contactDao.removeAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void doLast() {
		// stop the Jetty server after the last test
		JettyMain.stopServer();
	}

	@Before
	public void setUp() {
		contactDao = (new JpaDaoFactory()).getContactDao();
		foo = new Contact("foo title", "Foo Bar", "foo@bar.com", "0812345678");
	}

	@Test
	public void testSaveAndFind() {
		assertTrue(contactDao.save(foo));
		assertTrue(foo.getId() > 0);
		System.out.println("Saved foo and got foo.id = " + foo.getId());

		// Now find it again
		Contact fooAgain = contactDao.find(foo.getId());
		assertNotNull(fooAgain);
		assertSame("DAO should return the same object reference", foo, fooAgain);
	}

	// this test requires that testSaveAndFind be performed first
	// Answer : Can't order it should add again.
	@Test
	public void testDelete() {
		long id = foo.getId();
		Assume.assumeTrue(id > 0);
		Contact fooAgain = contactDao.find(id);
		Assume.assumeNotNull(fooAgain);

		assertTrue(contactDao.delete(id));
		assertEquals("after deleting the id should be zero", 0L, foo.getId());
	}


}
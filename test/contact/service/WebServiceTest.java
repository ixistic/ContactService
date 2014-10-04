package contact.service;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Response;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import contact.server.JettyMain;

/**
 * JUnit test to test the Contact web service.
 * 
 * @author Veerapat Threeravipark 5510547022
 * 
 */
public class WebServiceTest {
	private static String serviceUrl;
	private static HttpClient client;
	private static ContactDao contactDao;

	@BeforeClass
	public static void doFirst() {
		// Start the Jetty server.
		// Suppose this method returns the URL (with port) of the server
		System.out.println("Start test");
		try {
			String url = JettyMain.startServer(8080);
			serviceUrl = url + "contacts/";
			contactDao = DaoFactory.getInstance().getContactDao();
			contactDao.removeAll();
			client = new HttpClient();
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void doLast() {
		// stop the Jetty server after the last test
		try {
			client.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		JettyMain.stopServer();
	}
	
	@After
	public void doAfterTest() {
		contactDao.removeAll();
	}

	/**
	 * Response 201 CREATED if post success.
	 */
	@Test
	public void testPostSuccess() {
		long testId = 123456;
		ContentResponse contentRes = post(testId);
		assertEquals("Should response with 200 OK.",Response.Status.CREATED.getStatusCode(),contentRes.getStatus());
	}

	/**
	 * Response 409 CONFLICT if post the same contact id.
	 */
	@Test
	public void testPostFail() {
		long testId = 115000;
		post(testId);
		ContentResponse contentRes = post(testId);
		assertEquals("Should response with 409 Conflict if same contact id.",Response.Status.CONFLICT.getStatusCode(),contentRes.getStatus());
	}

	/**
	 * Response 200 OK if get success.
	 */
	@Test
	public void testGetSuccess() {
		long testId = 115333;
		post(testId);
		ContentResponse contentRes = get(testId);
		assertEquals("Should response with 200 OK.",Response.Status.OK.getStatusCode(), contentRes.getStatus());
	}

	/**
	 * Response 404 NOT_FOUND if get invalid id.
	 */
	@Test
	public void testGetFail() {
		long testId = 115445;
		post(testId);
		ContentResponse contentRes = get(111233);
		assertEquals("Should response with 404 NOT_FOUND if get invalid id.",Response.Status.NOT_FOUND.getStatusCode(),contentRes.getStatus());
	}

	/**
	 * Response 200 OK if put success.
	 */
	@Test
	public void testPutSuccess() {
		long testId = 1122245;
		post(testId);
		ContentResponse contentRes = put(testId, testId);
		assertEquals("Should response with 200 OK.",Response.Status.OK.getStatusCode(), contentRes.getStatus());
	}

	/**
	 * Response 400 BAD_REQUEST if path's id != id in xml file.
	 */
	@Test
	public void testPutFail() {
		long testId = 177445;
		post(testId);
		ContentResponse contentRes = put(testId, 1113333);
		assertEquals("Should response with 400 BAD_REQUEST if path's id != id in xml file.",Response.Status.BAD_REQUEST.getStatusCode(),contentRes.getStatus());
	}

	/**
	 * Response 200 OK if delete success.
	 */
	@Test
	public void testDeleteSuccess() {
		long testId = 19995;
		post(testId);
		ContentResponse contentRes = delete(testId);
		assertEquals("Should response with 200 OK.",Response.Status.OK.getStatusCode(), contentRes.getStatus());
	}

	/**
	 * Response 404 NOT_FOUND if delete fail.
	 */
	@Test
	public void testDeleteFail() {
		long testId = 198885;
		post(testId);
		ContentResponse contentRes = delete(33333);
		assertEquals("Should response with 404 NOT_FOUND if delete fail.",Response.Status.NOT_FOUND.getStatusCode(), contentRes.getStatus());
	}

	/**
	 * Get a contact by id.
	 * 
	 * @param id
	 *            identifier of contact
	 * @return contentResponse
	 */
	public ContentResponse get(long id) {
		ContentResponse contentRes = null;
		try {
			contentRes = client.GET(serviceUrl + id);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		return contentRes;
	}

	/**
	 * Create a new contact. If contact id is omitted or 0, the server will
	 * assign a unique ID and return it as the Location header.
	 * 
	 * @param id
	 *            identifier of contact
	 * @return contentResponse
	 */
	public ContentResponse post(long id) {
		StringContentProvider content = initContent(id);
		Request req = client.newRequest(serviceUrl);
		req = req.content(content, "application/xml");
		req = req.method(HttpMethod.POST);
		ContentResponse contentRes = null;
		try {
			contentRes = req.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
		return contentRes;
	}

	/**
	 * Update a contact. Only update the attributes supplied in request body.
	 * 
	 * @param id
	 *            identifier of contact
	 * @param idInXml
	 *            identifier of contact in xml
	 * @return contentResponse
	 */
	public ContentResponse put(long id, long idInXml) {
		String path = serviceUrl + id;
		Request req = client.newRequest(path);
		StringContentProvider content = initContent(idInXml);
		req = req.content(content, "application/xml");
		req = req.method(HttpMethod.PUT);
		ContentResponse contentRes = null;
		try {
			contentRes = req.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
		return contentRes;
	}

	/**
	 * Delete a contact with matching id.
	 * 
	 * @param id
	 *            identifier of contact
	 * @return contentResponse
	 */
	public ContentResponse delete(long id) {
		String path = serviceUrl + id;
		Request req = client.newRequest(path);
		req = req.method(HttpMethod.DELETE);
		ContentResponse contentRes = null;
		try {
			contentRes = req.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
		return contentRes;
	}
	
	/**
	 * Construct demo content of contact
	 * @param id identifier of contact
	 * @return StringContentProvider
	 */
	public StringContentProvider initContent(long id){
		StringContentProvider content = new StringContentProvider(
				"<contact id=\""
						+ id
						+ "\">"
						+ "<title>contact nickname or title edited</title>"
						+ "<name>contact's full name edited</name>"
						+ "<email>contact's email address edited</email>"
						+ "<phoneNumber>contact's telephone number edited</phoneNumber>"
						+ "</contact>");
		return content;
	}
}

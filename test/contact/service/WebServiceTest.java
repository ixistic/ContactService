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

	/**
	 * Response 201 CREATED if post success.
	 */
	@Test
	public void testPostSuccess() {
		System.out.println("POST Success");
		ContentResponse contentRes;
		long testId = 123456;
		contentRes = post(testId);
		delete(testId);
		System.out.println("result = " + contentRes.getStatus());
		assertEquals(Response.Status.CREATED.getStatusCode(),
				contentRes.getStatus());
	}

	/**
	 * Response 409 CONFLICT if post the same contact id.
	 */
	@Test
	public void testPostFail() {
		System.out.println("POST Fail");
		ContentResponse contentRes;
		long testId = 115000;
		post(testId);
		contentRes = post(testId);
		delete(testId);
		System.out.println("result = " + contentRes.getStatus());
		assertEquals(Response.Status.CONFLICT.getStatusCode(),
				contentRes.getStatus());
	}

	/**
	 * Response 200 OK if get success.
	 */
	@Test
	public void testGetSuccess() {
		System.out.println("GET Success");
		ContentResponse contentRes;
		long testId = 115333;
		post(testId);
		contentRes = get(testId);
		delete(testId);
		System.out.println("result = " + contentRes.getStatus());
		assertEquals(Response.Status.OK.getStatusCode(), contentRes.getStatus());
	}

	/**
	 * Response 404 NOT_FOUND if get invalid id.
	 */
	@Test
	public void testGetFail() {
		System.out.println("GET Fail");
		ContentResponse contentRes;
		long testId = 115445;
		post(testId);
		contentRes = get(111233);
		delete(testId);
		System.out.println("result = " + contentRes.getStatus());
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
				contentRes.getStatus());
	}

	/**
	 * Response 200 OK if put success.
	 */
	@Test
	public void testPutSuccess() {
		System.out.println("PUT Success");
		ContentResponse contentRes;
		long testId = 1122245;
		post(testId);
		contentRes = put(testId, testId);
		delete(testId);
		System.out.println("result = " + contentRes.getStatus());
		assertEquals(Response.Status.OK.getStatusCode(), contentRes.getStatus());
	}

	/**
	 * Response 400 BAD_REQUEST if path's id != id in xml file.
	 */
	@Test
	public void testPutFail() {
		System.out.println("PUT Fail");
		ContentResponse contentRes;
		long testId = 177445;
		post(testId);
		contentRes = put(testId, 1113333);
		delete(testId);
		System.out.println("result = " + contentRes.getStatus());
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				contentRes.getStatus());
	}

	/**
	 * Response 200 OK if delete success.
	 */
	@Test
	public void testDeleteSuccess() {
		System.out.println("DELETE Success");
		ContentResponse contentRes;
		long testId = 19995;
		post(testId);
		contentRes = delete(testId);
		System.out.println("result = " + contentRes.getStatus());
		assertEquals(Response.Status.OK.getStatusCode(), contentRes.getStatus());
	}

	/**
	 * Response 200 OK if delete fail.
	 */
	@Test
	public void testDeleteFail() {
		System.out.println("DELETE Fail");
		ContentResponse contentRes;
		long testId = 198885;
		post(testId);
		contentRes = delete(33333);
		delete(198885);
		System.out.println("result = " + contentRes.getStatus());
		assertEquals(Response.Status.OK.getStatusCode(), contentRes.getStatus());
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
		StringContentProvider content = new StringContentProvider(
				"<contact id=\""
						+ id
						+ "\">"
						+ "<title>contact nickname or title</title>"
						+ "<name>contact's full name</name>"
						+ "<email>contact's email address</email>"
						+ "<phoneNumber>contact's telephone number</phoneNumber>"
						+ "</contact>");
		ContentResponse contentRes = null;
		try {
			contentRes = client.newRequest(serviceUrl)
					.content(content, "application/xml")
					.method(HttpMethod.POST).send();
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
		req = req.method(HttpMethod.PUT);
		StringContentProvider content = new StringContentProvider(
				"<contact id=\""
						+ idInXml
						+ "\">"
						+ "<title>contact nickname or title edited</title>"
						+ "<name>contact's full name edited</name>"
						+ "<email>contact's email address edited</email>"
						+ "<phoneNumber>contact's telephone number edited</phoneNumber>"
						+ "</contact>");
		req = req.content(content, "application/xml");
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
}

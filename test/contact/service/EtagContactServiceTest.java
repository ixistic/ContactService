package contact.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Response;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import contact.server.JettyMain;

public class EtagContactServiceTest {
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
	 * 
	 */
	@Test
	public void testEtagFromPost() {
		ContentResponse contentRes;
		long testId = 123456;
		contentRes = post(testId);
		assertFalse("Etag shouldn't be empty.",
				contentRes.getHeaders().get(HttpHeader.ETAG).isEmpty());
		assertEquals("Should response with 201 created.",
				Response.Status.CREATED.getStatusCode(), contentRes.getStatus());
	}

	/**
	 * 
	 */
	@Test
	public void testEtagfromGet() {
		ContentResponse contentRes;
		long testId = 115322;
		post(testId);
		contentRes = get(testId);
		assertFalse("Etag shouldn't be empty.",
				contentRes.getHeaders().get(HttpHeader.ETAG).isEmpty());
		assertEquals("Should response with 200 OK.",
				Response.Status.OK.getStatusCode(), contentRes.getStatus());
	}

	/**
	 * 
	 */
	@Test
	public void testEtagfromGetNoneMatchTrue() {
		ContentResponse contentRes;
		long testId = 1332333;
		post(testId);
		try {
			contentRes = client.newRequest(serviceUrl + testId)
					.header(HttpHeader.IF_NONE_MATCH, "\"testEtag\"")
					.method(HttpMethod.GET).send();
			assertEquals("Should response with 200 OK.",
					Response.Status.OK.getStatusCode(), contentRes.getStatus());
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	@Test
	public void testEtagfromGetNoneMatchFalse() {
		ContentResponse contentRes;
		ContentResponse contentResGet;
		long testId = 11533344;
		post(testId);
		contentResGet = get(testId);
		try {

			contentRes = client
					.newRequest(serviceUrl + testId)
					.header(HttpHeader.IF_NONE_MATCH,
							contentResGet.getHeaders().get(HttpHeader.ETAG))
					.method(HttpMethod.GET).send();
			assertEquals("Should response with 304 Not Modified.",
					Response.Status.NOT_MODIFIED.getStatusCode(),
					contentRes.getStatus());
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Response 200 OK if put success.
	 */
	@Test
	public void testEtagFromPut() {
		ContentResponse contentRes;
		long testId = 1122245;
		post(testId);
		contentRes = put(testId, testId);
		assertFalse("Etag shouldn't be empty.",
				contentRes.getHeaders().get(HttpHeader.ETAG).isEmpty());
		assertEquals("Should response with 201 created.",
				Response.Status.OK.getStatusCode(), contentRes.getStatus());
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

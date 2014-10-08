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
	private static final String DEMO_ETAG = "\"testEtag\"";

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
	 * Test client get ETag in post method.
	 */
	@Test
	public void testEtagFromPost() {
		long testId = 123456;
		ContentResponse contentRes = post(testId);
		String etag = contentRes.getHeaders().get(HttpHeader.ETAG);
		assertFalse("Etag shouldn't be empty.",etag.isEmpty());
		assertEquals("Should response with 201 created.",Response.Status.CREATED.getStatusCode(), contentRes.getStatus());
	}

	/**
	 * Test client get ETag in get method.
	 */
	@Test
	public void testEtagfromGet() {
		long testId = 115322;
		post(testId);
		ContentResponse contentRes = get(testId);
		String etag = contentRes.getHeaders().get(HttpHeader.ETAG);
		assertFalse("Etag shouldn't be empty.",etag.isEmpty());
		assertEquals("Should response with 200 OK.",Response.Status.OK.getStatusCode(), contentRes.getStatus());
	}

	/**
	 * Test response in get method if IF-None-Match = True should return 200 OK.
	 */
	@Test
	public void testEtagfromGetNoneMatchTrue() {
		long testId = 1332333;
		post(testId);
		String path = serviceUrl + testId;
		Request req = client.newRequest(path);
		req = req.header(HttpHeader.IF_NONE_MATCH, DEMO_ETAG);
		req = req.method(HttpMethod.GET);
		try {
			ContentResponse contentRes = req.send();
			assertEquals("Should response with 200 OK.",Response.Status.OK.getStatusCode(), contentRes.getStatus());
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test response in get method if IF-None-Match = False should return 304 Not Modified.
	 */
	@Test
	public void testEtagfromGetNoneMatchFalse() {
		long testId = 11533344;
		post(testId);
		ContentResponse contentResGet = get(testId);
		String etag = contentResGet.getHeaders().get(HttpHeader.ETAG);
		assertFalse("Etag shouldn't be empty.",etag.isEmpty());
		String path = serviceUrl + testId;
		Request req = client.newRequest(path);
		req = req.header(HttpHeader.IF_NONE_MATCH,etag);
		req = req.method(HttpMethod.GET);
		try {
			ContentResponse contentRes = req.send();
			assertEquals("Should response with 304 Not Modified.",Response.Status.NOT_MODIFIED.getStatusCode(),contentRes.getStatus());
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test client get ETag in put method.
	 */
	@Test
	public void testEtagFromPut() {
		long testId = 1122245;
		post(testId);
		ContentResponse contentRes = put(testId, testId);
		String etag = contentRes.getHeaders().get(HttpHeader.ETAG);
		assertFalse("Etag shouldn't be empty.",etag.isEmpty());
		assertEquals("Should response with 200 OK.",Response.Status.OK.getStatusCode(), contentRes.getStatus());
	}

	/**
	 * Test response in put method if IF-Match = True should return 200 OK.
	 */
	@Test
	public void testEtagFromPutMatchTrue() {
		long testId = 112223;
		post(testId);
		String path = serviceUrl + testId;
		ContentResponse contentResGet = get(testId);
		String etag = contentResGet.getHeaders().get(HttpHeader.ETAG);
		assertFalse("Etag shouldn't be empty.",etag.isEmpty());
		Request req = client.newRequest(path);
		req = req.method(HttpMethod.PUT);
		StringContentProvider content = initContent(testId);
		req = req.content(content, "application/xml");
		req = req.header(HttpHeader.IF_MATCH,etag);
		try {
			ContentResponse contentRes = req.send();
			assertEquals("Should response with 200 OK.",Response.Status.OK.getStatusCode(), contentRes.getStatus());
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test response in put method if IF-Match = False should return 412 Precondition Failed.
	 */
	@Test
	public void testEtagFromPutMatchFalse() {
		ContentResponse contentRes;
		long testId = 112223;
		post(testId);
		String path = serviceUrl + testId;
		Request req = client.newRequest(path);
		StringContentProvider content = initContent(testId);
		req = req.content(content, "application/xml");
		req = req.header(HttpHeader.IF_MATCH, DEMO_ETAG);
		req = req.method(HttpMethod.PUT);
		try {
			contentRes = req.send();
			assertEquals("Should response with 412 Precondition Failed.",Response.Status.PRECONDITION_FAILED.getStatusCode(),contentRes.getStatus());
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test response in delete method if IF-Match = True should return 200 OK.
	 */
	@Test
	public void testEtagFromDeleteMatchTrue() {
		long testId = 19995;
		post(testId);
		String path = serviceUrl + testId;
		ContentResponse contentResGet = get(testId);
		String etag = contentResGet.getHeaders().get(HttpHeader.ETAG);
		assertFalse("Etag shouldn't be empty.",etag.isEmpty());
		Request req = client.newRequest(path);
		req = req.header(HttpHeader.IF_MATCH,etag);
		req = req.method(HttpMethod.DELETE);
		try {
			ContentResponse contentRes = req.send();
			assertEquals("Should response with 200 OK.",Response.Status.OK.getStatusCode(),contentRes.getStatus());
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test response in delete method if IF-Match = False should return 412 Precondition Failed.
	 */
	@Test
	public void testEtagFromDeleteMatchFalse() {
		long testId = 1999445;
		post(testId);
		String path = serviceUrl + testId;
		Request req = client.newRequest(path);
		req = req.header(HttpHeader.IF_MATCH, DEMO_ETAG);
		req = req.method(HttpMethod.DELETE);
		try {
			ContentResponse contentRes = req.send();
			assertEquals("Should response with 412 Precondition Failed.",Response.Status.PRECONDITION_FAILED.getStatusCode(),contentRes.getStatus());
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
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

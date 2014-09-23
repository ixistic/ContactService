package contact.service;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import contact.server.JettyMain;

public class WebServiceTest {
	private static String serviceUrl;
	private static HttpClient client;

	@BeforeClass
	public static void doFirst() {
		// Start the Jetty server.
		// Suppose this method returns the URL (with port) of the server
		String uri = "";
		try {
			uri = JettyMain.startServer(8080);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		serviceUrl = uri + "contacts/";
		client = new HttpClient();
		try {
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
	
	@Test
	public void testPostSuccess() {
		System.out.println("POST");
		StringContentProvider content = new StringContentProvider(
				"<contact id=\"110\">"
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
		System.out.println(contentRes.getStatus());
		assertEquals(Response.Status.CREATED.getStatusCode(), contentRes.getStatus());
	}
	
	@Test
	public void testPostFail() {
		System.out.println("POST");
		StringContentProvider content = new StringContentProvider(
				"<contact id=\"110\">"
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
		assertEquals(Response.Status.CONFLICT.getStatusCode(),
				contentRes.getStatus());
	}

	@Test
	public void testGetSuccess() {
		System.out.println("GET");
		ContentResponse contentRes = null;
		try {
			contentRes = client.GET(serviceUrl);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		assertEquals(Response.Status.OK.getStatusCode(), contentRes.getStatus());
	}
	
	@Test
	public void testGetFail() {
		System.out.println("GET");
		ContentResponse contentRes = null;
		try {
			contentRes = client.GET(serviceUrl + "100000000");
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
				contentRes.getStatus());
	}

	@Test
	public void testPutSuccess() {
		System.out.println("PUT");
		String path = serviceUrl + "110";
		Request req = client.newRequest(path);
		req = req.method(HttpMethod.PUT);
		StringContentProvider content = new StringContentProvider(
				"<contact id=\"110\">"
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
		assertEquals(Response.Status.OK.getStatusCode(), contentRes.getStatus());
	}
	
	@Test
	public void testPutFail() {
		System.out.println("PUT");
		String path = serviceUrl + "110";
		Request req = client.newRequest(path);
		req = req.method(HttpMethod.PUT);
		StringContentProvider content = new StringContentProvider(
				"<contact id=\"102\">"
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
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), contentRes.getStatus());
	}

	@Test
	public void testDeleteSuccess() {
		System.out.println("DELETE");
		String path = serviceUrl + "110";
		Request req = client.newRequest(path);
		req = req.method(HttpMethod.DELETE);
		ContentResponse contentRes = null;
		try {
			contentRes = req.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
		assertEquals(Response.Status.OK.getStatusCode(), contentRes.getStatus());
	}

	@Test
	public void testDeleteFail() {
		System.out.println("DELETE");
		String path = serviceUrl + "1000000";
		Request req = client.newRequest(path);
		req = req.method(HttpMethod.DELETE);
		ContentResponse contentRes = null;
		try {
			contentRes = req.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
		assertEquals(Response.Status.OK.getStatusCode(), contentRes.getStatus());
	}
}

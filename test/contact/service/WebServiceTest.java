package contact.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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
		client = new HttpClient();
		String uri = "";
		try {
			uri = JettyMain.startServer(8080);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		serviceUrl = uri + "contacts/";
	}

	@AfterClass
	public static void doLast() {
		// stop the Jetty server after the last test
		JettyMain.stopServer();
	}

	@Test
	public void testGet() {
		System.out.println("GET");
		HttpClient client = new HttpClient();
		try {
			client.start();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		ContentResponse contentRes = null;
		try {
			contentRes = client.GET(serviceUrl);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		System.out.println(contentRes.getHeaders());
		// System.out.println(contentRes.getContentAsString());
		try {
			client.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPost() {
		System.out.println("POST");
		HttpClient client = new HttpClient();
		try {
			client.start();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		StringContentProvider content = new StringContentProvider(
				"<contact id=\"102\">"
						+ "<title>contact nickname or title</title>"
						+ "<name>contact's full name</name>"
						+ "<email>contact's email address</email>"
						+ "<phoneNumber>contact's telephone number</phoneNumber>"
						+ "</contact>");
		ContentResponse res = null;
		try {
			res = client.newRequest(serviceUrl)
					.content(content, "application/xml")
					.method(HttpMethod.POST).send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
		try {
			res = client.GET(serviceUrl);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		System.out.println(res.getContentAsString());
		try {
			client.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPut() {
		System.out.println("PUT");
		HttpClient client = new HttpClient();
		try {
			client.start();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String path = serviceUrl + "102";
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
		try {
			req.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
		ContentResponse res = null;
		try {
			res = client.GET(path);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		System.out.println(res.getContentAsString());
		System.out.println(res.getHeaders());
		try {
			client.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDelete() {
		System.out.println("DELETE");
		HttpClient client = new HttpClient();
		try {
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String path = serviceUrl + "102";
		Request req = client.newRequest(path);
		req = req.method(HttpMethod.DELETE);
		try {
			req.send();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
		ContentResponse res = null;
		try {
			res = client.GET(path);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		System.out.println(res.getContentAsString());
		try {
			client.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

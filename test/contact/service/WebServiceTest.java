package contact.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import contact.server.JettyMain;

public class WebServiceTest {
	private static String serviceUrl;

	@BeforeClass
	public static void doFirst() {
		// Start the Jetty server.
		// Suppose this method returns the URL (with port) of the server
		serviceUrl = JettyMain.startServer(8080);
	}

	@AfterClass
	public static void doLast() {
		// stop the Jetty server after the last test
		JettyMain.stopServer();
	}

	@Test
	public void testGet() {
		HttpClient client = new HttpClient();
		ContentResponse contentRes = null;
		try {
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			contentRes = client.GET(serviceUrl);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		System.out.println(contentRes.getContentAsString());
	}
	
	@Test
	public void testPut() {
		
	}

	@Test
	public void testPost() {
		
	}
	
	@Test
	public void testDelete() {
		
	}
}

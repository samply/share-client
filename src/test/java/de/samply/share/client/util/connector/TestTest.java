package de.samply.share.client.util.connector;

import com.google.common.net.MediaType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

public class TestTest {

	private final int exposedPort = 80;
	@Test
	@Disabled
	public void myTest1() {

		GenericContainer testAppContainer = createTestAppContainer();

		testAppContainer.start();
		Integer mappedPort = testAppContainer.getMappedPort(exposedPort);
		//MediaType.HTML_UTF_8
		// TODO:
		// GET http://localhost:mappedPort -> response
		// response.geteBody() -> result
		// result als HTML -> extractHtmlBody -> htmlBody
		// assertEquals ("Hello World!", htmlBody)
		testAppContainer.stop();

	}

	private GenericContainer<?> createTestAppContainer() {

		// TODO: Andere Version von httpd nutzen (e.g. latest-alpine?)
		GenericContainer<?> httpdContainer = new GenericContainer<>("httpd:alpine");
		httpdContainer.withFileSystemBind(
						"./src/test/resources/de/samply/share/client/util/connector/testAppIndex.html",
						"/usr/local/apache2/htdocs/index.html")
				.addExposedPort(exposedPort);

		return httpdContainer;

	}

}

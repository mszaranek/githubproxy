package mszaranek.githubproxy;


import mszaranek.githubproxy.controller.GithubProxyController;
import mszaranek.githubproxy.exception.CustomException;
import mszaranek.githubproxy.service.GithubBranchesService;
import mszaranek.githubproxy.service.GithubRepositoryService;
import mszaranek.githubproxy.service.impl.GithubBranchesServiceImpl;
import mszaranek.githubproxy.service.impl.GithubRepositoryServiceImpl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@SpringBootTest
public class GithubProxyControllerTests {

    final Logger logger = LoggerFactory.getLogger(GithubProxyControllerTests.class);

    private String baseUrl = "http://localhost:8082";
    private MockWebServer mockWebServer;
    private GithubRepositoryService githubproxyService;
    private GithubProxyController githubProxyController;

    private GithubBranchesService githubBranchesService;


    @BeforeEach
    void setupMockWebServer() throws Exception {

        String testRepoData = new String(IOUtils.toByteArray(GithubProxyControllerTests.class.getClassLoader().getResourceAsStream("testrepodata.json")));
        String testBranchData = new String(IOUtils.toByteArray(GithubProxyControllerTests.class.getClassLoader().getResourceAsStream("testbranchdata.json")));


        mockWebServer = new MockWebServer();
        mockWebServer.start(8082);
        final Dispatcher dispatcher = new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) {

                logger.debug(request.getPath());

                switch (request.getPath()) {
                    case "/users/testuser/repos":
                        return new MockResponse().setResponseCode(200).setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setBody(testRepoData);
                    case "/repos/testuser/testrepo/branches":
                        return new MockResponse().setResponseCode(200).setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setBody(testBranchData);

                }
                return new MockResponse().setResponseCode(404);
            }
        };
        mockWebServer.setDispatcher(dispatcher);

        githubBranchesService = new GithubBranchesServiceImpl(baseUrl);

        logger.info("baseURL: " + baseUrl);

        githubproxyService = new GithubRepositoryServiceImpl(githubBranchesService, baseUrl);

        githubProxyController = new GithubProxyController(githubproxyService);


    }

    @AfterEach
    void shutdownMockWebServer() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void testCorrectPath() throws Exception {

        githubProxyController.getRepos("testuser", null).collectList().block();


        RecordedRequest request = mockWebServer.takeRequest();

        logger.info(request.getPath());
        Assertions.assertEquals(request.getMethod(), "GET");
        Assertions.assertEquals(request.getPath(), "/users/testuser/repos");

    }


    @Test
    void testUserNotFoundException() {

        WebClientResponseException thrown = Assertions.assertThrows(WebClientResponseException.class, () -> {
            githubProxyController.getRepos("notexist", null).collectList().block();
        });
        Assertions.assertEquals(404, thrown.getStatusCode().value());
    }

    @Test
    void testWrongAcceptHeaderException() {

        CustomException thrown = Assertions.assertThrows(CustomException.class, () -> {
            githubProxyController.getRepos("testuser", "application/xml").collectList().block();
        });

        Assertions.assertEquals("Wrong accept header, application/json or wildcard expected", thrown.getMessage());
    }
}

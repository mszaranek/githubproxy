package mszaranek.githubproxy;


import mszaranek.githubproxy.controller.GithubProxyController;
import mszaranek.githubproxy.domain.GithubRepository;
import mszaranek.githubproxy.exception.CustomException;
import mszaranek.githubproxy.service.GithubProxyService;
import mszaranek.githubproxy.service.impl.GithubProxyServiceImpl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;


@SpringBootTest
public class GithubProxyControllerTests {

    final Logger logger = LoggerFactory.getLogger(GithubProxyControllerTests.class);
    private MockWebServer mockWebServer;
    private GithubProxyService githubproxyService;
    private GithubProxyController githubProxyController;

    @BeforeEach
    void setupMockWebServer() throws Exception {

        String testRepoData = new String(IOUtils.toByteArray(GithubProxyControllerTests.class.getClassLoader().getResourceAsStream("testrepodata.json")));
        String testBranchData = new String(IOUtils.toByteArray(GithubProxyControllerTests.class.getClassLoader().getResourceAsStream("testbranchdata.json")));

        mockWebServer = new MockWebServer();
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

        githubproxyService = new GithubProxyServiceImpl("http://localhost:" + mockWebServer.getPort());

        githubProxyController = new GithubProxyController(githubproxyService);

    }

    @Test
    void testGetRepos() throws Exception {

        List<GithubRepository> repositories = githubProxyController.getRepos("testuser", null).getBody().block();

        RecordedRequest request = mockWebServer.takeRequest();

        Assertions.assertEquals(request.getMethod(), "GET");
        Assertions.assertEquals(repositories.size(), 1);
        Assertions.assertEquals(repositories.get(0).getBranches().size(), 1);
        Assertions.assertEquals(repositories.get(0).getName(), "testrepo");
        Assertions.assertEquals(repositories.get(0).getOwnerLogin(), "testuser");
        Assertions.assertEquals(repositories.get(0).getBranches().get(0).getSha(), "de64150bb6c78h5f0527b000aejb7e764bb6527d");
        Assertions.assertEquals(repositories.get(0).getBranches().get(0).getName(), "master");
    }


    @Test
    void testUserNotFoundException() {

        CustomException thrown = Assertions.assertThrows(CustomException.class, () -> {

            githubProxyController.getRepos("notexist", null).getBody().block();

        });
        Assertions.assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void testWrongAcceptHeaderException() {

        CustomException thrown = Assertions.assertThrows(CustomException.class, () -> {

            githubProxyController.getRepos("testuser", "application/xml").getBody().block();

        });

        Assertions.assertEquals("Wrong accept header, application/json or wildcard expected", thrown.getMessage());
    }
}

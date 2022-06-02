package mszaranek.githubproxy;


import mszaranek.githubproxy.domain.Branch;
import mszaranek.githubproxy.domain.Commit;
import mszaranek.githubproxy.domain.GithubRepository;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;


@SpringBootTest
public class GithubRepositoryServiceTests {

    private String baseUrl = "http://localhost:8082";
    private MockWebServer mockWebServer;
    private GithubRepositoryService githubproxyService;

    private GithubBranchesService githubBranchesService;


    @BeforeEach
    void setupMockWebServer() throws Exception {

        String testRepoData = new String(IOUtils.toByteArray(GithubRepositoryServiceTests.class.getClassLoader().getResourceAsStream("testrepodata.json")));
        String testRepoForkData = new String(IOUtils.toByteArray(GithubRepositoryServiceTests.class.getClassLoader().getResourceAsStream("testforkrepodata.json")));
        String testBranchData = new String(IOUtils.toByteArray(GithubRepositoryServiceTests.class.getClassLoader().getResourceAsStream("testbranchdata.json")));


        mockWebServer = new MockWebServer();
        mockWebServer.start(8082);
        final Dispatcher dispatcher = new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) {


                switch (request.getPath()) {
                    case "/users/testuser/repos":
                        return new MockResponse().setResponseCode(200).setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setBody(testRepoData);
                    case "/users/testuserfork/repos":
                        return new MockResponse().setResponseCode(200).setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setBody(testRepoForkData);
                    case "/repos/testuser/testrepo/branches":
                        return new MockResponse().setResponseCode(200).setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setBody(testBranchData);
                }
                return new MockResponse().setResponseCode(404);
            }
        };
        mockWebServer.setDispatcher(dispatcher);

        githubBranchesService = new GithubBranchesServiceImpl(baseUrl);


        githubproxyService = new GithubRepositoryServiceImpl(githubBranchesService, baseUrl);


    }

    @AfterEach
    void shutdownMockWebServer() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void testGetRepos() {

        Commit testCommit = new Commit("de64150bb6c78h5f0527b000aejb7e764bb6527d");
        Branch testBranch = new Branch("master", testCommit.getSha(), testCommit);
        GithubRepository testRepository = new GithubRepository("testrepo", "testuser", Arrays.asList(testBranch), false);

        List<GithubRepository> repositories = githubproxyService.getRepos("testuser", null).collectList().block();

        Assertions.assertEquals(Arrays.asList(testRepository), repositories);
    }

    @Test
    void testGetReposEmptyWhenAllReposAreForks() {

        List<GithubRepository> repositories = githubproxyService.getRepos("testuserfork", null).collectList().block();
        Assertions.assertTrue(repositories.isEmpty());
    }

}

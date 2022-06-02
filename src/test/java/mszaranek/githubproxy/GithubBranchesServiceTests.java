package mszaranek.githubproxy;

import mszaranek.githubproxy.domain.Branch;
import mszaranek.githubproxy.domain.Commit;
import mszaranek.githubproxy.service.GithubBranchesService;
import mszaranek.githubproxy.service.impl.GithubBranchesServiceImpl;
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
public class GithubBranchesServiceTests {

    private String baseUrl = "http://localhost:8082";
    private GithubBranchesService githubBranchesService;
    private MockWebServer mockWebServer;


    @BeforeEach
    void setupMockWebServer() throws Exception {

        String testBranchData = new String(IOUtils.toByteArray(GithubProxyControllerTests.class.getClassLoader().getResourceAsStream("testbranchdata.json")));

        mockWebServer = new MockWebServer();
        mockWebServer.start(8082);
        final Dispatcher dispatcher = new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) {

                        return new MockResponse().setResponseCode(200).setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setBody(testBranchData);
            }
        };
        mockWebServer.setDispatcher(dispatcher);

        githubBranchesService = new GithubBranchesServiceImpl(baseUrl);
    }

    @AfterEach
    void shutdownMockWebServer() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void testGetBranches() {

        Commit testCommit = new Commit("de64150bb6c78h5f0527b000aejb7e764bb6527d");
        Branch testBranch = new Branch("master", testCommit.getSha(), testCommit);

        List<Branch> branches = githubBranchesService.getBranches("testrepo", "testuser").collectList().block();

        Assertions.assertEquals(Arrays.asList(testBranch), branches);
    }


}

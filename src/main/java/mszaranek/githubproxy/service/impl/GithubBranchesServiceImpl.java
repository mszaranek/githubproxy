package mszaranek.githubproxy.service.impl;

import mszaranek.githubproxy.domain.Branch;
import mszaranek.githubproxy.service.GithubBranchesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class GithubBranchesServiceImpl implements GithubBranchesService {

    private final WebClient client;
    private String baseUrl;

    public GithubBranchesServiceImpl(@Value("${base.url}") String baseUrl) {
        this.client = WebClient.builder().baseUrl(baseUrl).build();
        this.baseUrl = baseUrl;
    }

    @Override
    public Flux<Branch> getBranches(String repositoryName, String login) {

        return client
                .get()
                .uri("repos/" + login + "/" + repositoryName + "/branches")
                .retrieve()
                .bodyToFlux(Branch.class)
                .doOnNext(branch -> branch.setSha(branch.getCommit().getSha()));
    }
}

package mszaranek.githubproxy.service.impl;

import mszaranek.githubproxy.domain.Branch;
import mszaranek.githubproxy.domain.GithubRepository;
import mszaranek.githubproxy.exception.CustomException;
import mszaranek.githubproxy.service.GithubProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Service
public class GithubProxyServiceImpl implements GithubProxyService {

    final Logger logger = LoggerFactory.getLogger(GithubProxyService.class);
    private final String BASE_URL = "https://api.github.com/";

    private WebClient client;

    public GithubProxyServiceImpl() {
        this.client = WebClient.builder().baseUrl(BASE_URL).build();
    }

    public GithubProxyServiceImpl(String baseUrl) {
        this.client = WebClient.builder().baseUrl(baseUrl).build();
    }


    @Override
    public Mono<List<GithubRepository>> getRepos(String login) {

        logger.info("Get repos called");
        Flux<GithubRepository> repositoriesFlux = githubRepositoryFlux(client, login);
        Mono<List<GithubRepository>> repositories = repositoriesFlux.collectList();
        Mono<Map<String, Branch[]>> branches = branchMono(client, repositoriesFlux, login);
        return combinator(repositories, branches);
    }


    private Flux<GithubRepository> githubRepositoryFlux(WebClient client, String login) {

        logger.info("Getting repositories");
        Flux<GithubRepository> repositoryFlux = client.get().uri("users/" + login + "/repos").
                exchangeToFlux(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Flux.error(new CustomException(clientResponse.statusCode(), "User not found"));
                    } else if (clientResponse.statusCode().equals(HttpStatus.FORBIDDEN)) {
                        return Flux.error(new CustomException(clientResponse.statusCode(), "GitHub API rate limit exceeded"));
                    }
                    return clientResponse.bodyToFlux(GithubRepository.class);
                }).filter(githubRepository -> !githubRepository.isFork()).doOnNext(githubRepository -> githubRepository.setOwnerLogin(githubRepository.getOwner().getLogin()));


        return repositoryFlux;
    }


    private Mono<Map<String, Branch[]>> branchMono(WebClient client, Flux<GithubRepository> githubRepositoryFlux, String login) {

        logger.info("Getting branches");
        return githubRepositoryFlux.flatMap(repository -> client.get().uri("repos/" + login + "/" + repository.getName() + "/branches").retrieve().bodyToMono(Branch[].class).doOnNext(branches -> {

            for (Branch branch : branches) {
                branch.setSha(branch.getCommit().getSha());
                branch.setRepositoryName(repository.getName());
            }
        })).collectMap(branches1 -> branches1[0].getRepositoryName());
    }


    private Mono<List<GithubRepository>> combinator(Mono<List<GithubRepository>> repositories, Mono<Map<String, Branch[]>> branches) {

        logger.info("Merging repositories with branches");
        return Mono.zip(repositories, branches, (githubRepositories, stringMap) -> {
            for (GithubRepository repository : githubRepositories
            ) {
                repository.setBranches(Arrays.asList(stringMap.get(repository.getName())));
                logger.info("Setting branches for " + repository.getName() + " repository");
            }
            return githubRepositories;
        });
    }


}

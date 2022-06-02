package mszaranek.githubproxy.service.impl;

import mszaranek.githubproxy.domain.GithubRepository;
import mszaranek.githubproxy.exception.CustomException;
import mszaranek.githubproxy.service.GithubBranchesService;
import mszaranek.githubproxy.service.GithubRepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;


@Service
public class GithubRepositoryServiceImpl implements GithubRepositoryService {

    final Logger logger = LoggerFactory.getLogger(GithubRepositoryService.class);
    private final WebClient client;
    private final GithubBranchesService githubBranchesService;

    private String baseUrl;


    public GithubRepositoryServiceImpl(GithubBranchesService githubBranchesService, @Value("${base.url}") String baseUrl) {
        this.client = WebClient.builder().baseUrl(baseUrl).build();
        this.githubBranchesService = githubBranchesService;
        this.baseUrl = baseUrl;
    }

    @Override
    public Flux<GithubRepository> getRepos(String login, String accept) {

        Optional.ofNullable(accept).ifPresent(s -> {
            if (!(s.contains(MediaType.APPLICATION_JSON_VALUE)) && !(s.contains(MediaType.ALL_VALUE))) {
                throw new CustomException(HttpStatus.NOT_ACCEPTABLE, "Wrong accept header, application/json or wildcard expected");
            }
        });

        logger.info("Get repos called");
        return githubRepositoryFlux(client, login);
    }


    private Flux<GithubRepository> githubRepositoryFlux(WebClient client, String login) {

        logger.info("Getting repositories");
        return client
                .get()
                .uri("users/" + login + "/repos")
                .retrieve()
                .bodyToFlux(GithubRepository.class)
                .filter(githubRepository -> !githubRepository.isFork())
                .doOnNext(githubRepository -> githubRepository.setOwnerLogin(githubRepository.getOwner().getLogin()))
                .collectList()
                .flatMapMany(githubRepositories -> mapWithBranches(login, githubRepositories));

    }

    private Flux<GithubRepository> mapWithBranches(String login, List<GithubRepository> githubRepositories) {
        return Flux.fromIterable(githubRepositories)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(githubRepository -> Mono.just(githubRepository)
                        .zipWith(githubBranchesService.getBranches(githubRepository.getName(), login).collectList(), (githubRepository1, branches) ->
                        {
                            githubRepository1.setBranches(branches);
                            return githubRepository1;
                        }))
                .sequential();
    }

}

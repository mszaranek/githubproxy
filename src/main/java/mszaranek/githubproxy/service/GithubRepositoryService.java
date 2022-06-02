package mszaranek.githubproxy.service;

import mszaranek.githubproxy.domain.GithubRepository;
import reactor.core.publisher.Flux;


public interface GithubRepositoryService {

    Flux<GithubRepository> getRepos(String login, String accept);
}

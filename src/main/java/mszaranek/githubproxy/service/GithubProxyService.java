package mszaranek.githubproxy.service;

import mszaranek.githubproxy.domain.GithubRepository;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GithubProxyService {

    Mono<List<GithubRepository>> getRepos(String login);
}

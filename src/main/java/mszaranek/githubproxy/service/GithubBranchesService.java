package mszaranek.githubproxy.service;

import mszaranek.githubproxy.domain.Branch;
import reactor.core.publisher.Flux;

public interface GithubBranchesService {

    Flux<Branch> getBranches(String repositoryName, String login);
}

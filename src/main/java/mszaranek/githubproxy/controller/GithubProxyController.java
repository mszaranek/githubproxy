package mszaranek.githubproxy.controller;


import mszaranek.githubproxy.domain.GithubRepository;
import mszaranek.githubproxy.service.GithubRepositoryService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class GithubProxyController {

    private final GithubRepositoryService githubproxyService;

    public GithubProxyController(GithubRepositoryService githubproxyService) {
        this.githubproxyService = githubproxyService;
    }

    @GetMapping(value = "/users/{login}/repos")
    public Flux<GithubRepository> getRepos(@PathVariable String login, @RequestHeader(value = "accept", required = false) String accept) {
        return githubproxyService.getRepos(login, accept);
    }

}

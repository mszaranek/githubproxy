package mszaranek.githubproxy.controller;


import mszaranek.githubproxy.domain.GithubRepository;
import mszaranek.githubproxy.exception.CustomException;
import mszaranek.githubproxy.service.GithubProxyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class GithubProxyController {

    private final GithubProxyService githubproxyService;

    public GithubProxyController(GithubProxyService githubproxyService) {
        this.githubproxyService = githubproxyService;
    }

    @GetMapping(value = "/users/{login}/repos")
    public ResponseEntity<Mono<List<GithubRepository>>> getRepos(@PathVariable String login, @RequestHeader(value = "accept", required = false) String accept) {

        Optional.ofNullable(accept).ifPresent(s -> {
            if (!(s.contains(MediaType.APPLICATION_JSON_VALUE)) && !(s.contains(MediaType.ALL_VALUE))) {
                throw new CustomException(HttpStatus.NOT_ACCEPTABLE, "Wrong accept header, application/json or wildcard expected");
            }
        });

        return ResponseEntity.ok().body(githubproxyService.getRepos(login));
    }

}

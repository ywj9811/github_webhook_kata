package com.github.test.domain.github.service;

import com.github.test.domain.github.dto.GithubResponse;
import com.github.test.domain.github.utils.GithubToUser;
import com.github.test.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class GithubService {
    private final WebClient webClient;
    private final GithubToUser githubToUser;
    public Mono<User> getUserData(String accessToken) {
        System.out.println(accessToken);
        return webClient.get()
                .uri("https://api.github.com/user")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .retrieve()
                .onStatus((HttpStatus) -> HttpStatus.ACCEPTED.is4xxClientError(), response -> Mono.error(new ClassCastException()))
                //적절한 예외 처리
                .onStatus((HttpStatus) -> HttpStatus.ACCEPTED.is5xxServerError(), response -> Mono.error(new ClassCastException()))
                //적절한 예외 처리
                .bodyToMono(GithubResponse.class)
                .flatMap(githubToUser::fromGit);
    }
}

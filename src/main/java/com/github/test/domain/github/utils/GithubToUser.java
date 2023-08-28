package com.github.test.domain.github.utils;

import com.github.test.domain.github.dto.GithubOauthResponse;
import com.github.test.domain.user.entity.User;
import com.github.test.domain.user.entity.UserRole;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class GithubToUser {
    public Mono<User> fromGit(GithubOauthResponse githubResponse) {
        return Mono.just(User.builder()
                .userId(githubResponse.getLogin())
                .userName(githubResponse.getName())
                .profile(githubResponse.getLogin())
                .role(UserRole.USER)
                .build());
    }
}

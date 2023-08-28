package com.github.test.domain.github.utils;

import com.github.test.domain.github.dto.GithubResponse;
import com.github.test.domain.user.entity.User;
import com.github.test.domain.user.entity.UserRole;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class GithubToUser {
    public Mono<User> fromGit(GithubResponse githubResponse) {
        return Mono.just(User.builder()
                .userId(githubResponse.login)
                .userName(githubResponse.name)
                .profile(githubResponse.avatar_url)
                .role(UserRole.USER)
                .build());
    }
}

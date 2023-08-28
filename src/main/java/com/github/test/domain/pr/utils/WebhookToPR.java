package com.github.test.domain.pr.utils;

import com.github.test.domain.github.dto.GithubPRRequest;
import com.github.test.domain.pr.entity.PR;
import com.github.test.domain.user.entity.User;
import com.github.test.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebhookToPR {
    private final UserRepository userRepository;

    public PR fromWebhook(GithubPRRequest prRequest) {
        User user = userRepository.findByUserId(prRequest.getUserId()).orElseThrow();
        return PR.builder()
                .prUrl(prRequest.getPull_request().getHtml_url())
                .user(user)
                .status(prRequest.getAction())
                .build();
    }
}

package com.github.test.domain.github.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GithubWebhookRequest {
    private String accessToken;
    private String userId;
    private String repo;
    private String host;
}

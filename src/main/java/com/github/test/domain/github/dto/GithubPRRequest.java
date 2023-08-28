package com.github.test.domain.github.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GithubPRRequest {
    private String action;
    private PullRequest pull_request;
    private String userId;

    public void insertUser(String userId) {
        this.userId = userId;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public
    class PullRequest {
        private String html_url;
    }
}



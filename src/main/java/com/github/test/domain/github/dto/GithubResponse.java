package com.github.test.domain.github.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GithubResponse {
    String login;
    String name;
    String avatar_url;
}

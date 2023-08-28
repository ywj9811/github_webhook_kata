package com.github.test.domain.github.service;

import com.github.test.global.dto.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class GithubWebhookService {
    private final WebClient webClient;

    public SuccessResponse<Mono<String>> setWebhook(String host, String accessToken, String userId, String repo) {
        String api = "https://api.github.com/repos/" + userId + "/" + repo + "/hooks";
        String endPoint = host + "/github/webHook/" + userId;
        String webhookConfig = "{\"name\": \"web\", \"active\": true, \"events\": [\"pull_request\"], \"config\": {\"url\": \""
                + endPoint + "\", \"content_type\": \"json\"}}";
        log.info("api : {}", api);
        log.info("endPont : {}", endPoint);
        log.info("accessToken {}", accessToken);
        Mono<String> result = webClient.post()
                .uri(api)
                .headers(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .body(BodyInserters.fromValue(webhookConfig))
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> "Webhook registered successfully!");
        return SuccessResponse.create(HttpStatus.OK.value(), "ok", result);
    }
}

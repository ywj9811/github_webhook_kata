package com.github.test.domain.github.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.test.domain.github.dto.GithubPRRequest;
import com.github.test.domain.github.dto.GithubPRRequest.PullRequest;
import com.github.test.domain.github.dto.GithubWebhookRequest;
import com.github.test.domain.github.service.GithubWebhookService;
import com.github.test.global.dto.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/github")
@RequiredArgsConstructor
@Slf4j
public class GithubWebhookController {
    private final GithubWebhookService webHookService;
    @PostMapping("/setting/webhook")
    public ResponseEntity<SuccessResponse<Mono<String>>> setWebhook(@RequestBody GithubWebhookRequest request) {
        SuccessResponse<Mono<String>> result = webHookService.setWebhook(request.getHost(), request.getAccessToken(), request.getUserId(), request.getRepo());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/webhook/{userId}")
    public void getWebhook(@PathVariable String userId, @RequestBody GithubPRRequest prRequest) throws JsonProcessingException {
        log.info("webHook : {}", prRequest.toString());
        webHookService.sendMessage(prRequest, userId);
    }
}

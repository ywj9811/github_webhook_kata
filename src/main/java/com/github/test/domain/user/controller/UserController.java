package com.github.test.domain.user.controller;

import com.github.test.domain.github.dto.GithubRequest;
import com.github.test.domain.user.service.UserService;
import com.github.test.global.dto.SuccessResponse;
import com.github.test.global.security.jwt.TokenInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/github/login")
    public ResponseEntity<Mono<SuccessResponse<TokenInfoResponse>>> githubLogin(@RequestBody GithubRequest request) {
        Mono<SuccessResponse<TokenInfoResponse>> login = userService.login(request.getAccessToken());
        return ResponseEntity.ok(login);
    }
}

package com.github.test.domain.user.service;

import com.github.test.domain.github.service.GithubOauthService;
import com.github.test.domain.user.entity.User;
import com.github.test.domain.user.repository.UserRepository;
import com.github.test.global.dto.SuccessResponse;
import com.github.test.global.security.jwt.JwtProvider;
import com.github.test.global.security.jwt.PrincipalDetails;
import com.github.test.global.security.jwt.TokenInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;

import static org.springframework.http.HttpStatus.OK;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final GithubOauthService githubService;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    public Mono<SuccessResponse<TokenInfoResponse>> login(String accessToken) {
        return githubService.getUserData(accessToken)
                .flatMap(user -> {
                    User existingUser = userRepository.findByUserId(user.getUserId())
                            .orElseGet(() -> signUp(user));

                    log.info("로그인 작업 완료 토큰 발행 시작");
                    TokenInfoResponse tokenInfoResponse = jwtProvider.createToken(getAuthentication(existingUser));
                    log.info("토큰 발행 완료");
                    return Mono.just(SuccessResponse.create(OK.value(), "ok", tokenInfoResponse));
                })
                .onErrorResume(error -> {
                    // 에러 처리 로직을 넣으세요.
                    throw new ClassCastException();
                });
    }

    private User signUp(User user) {
        log.info("회원가입 진행");
        return userRepository.save(user);
    }

    private Authentication getAuthentication(User user) {
        log.info("authentication 시작");
        try {
            PrincipalDetails principalDetails = new PrincipalDetails(user);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("authentication저장");
            return authentication;
        } catch (Exception e) {
            log.info("exception : {}", e.getMessage());
            log.info("stack {}", e.getStackTrace());
            throw e;
        }
    }
}

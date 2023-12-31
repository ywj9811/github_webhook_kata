package com.github.test.global.security.jwt;

import com.github.test.domain.user.entity.User;
import com.github.test.domain.user.repository.UserRepository;
import com.github.test.global.redis.RedisRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider implements InitializingBean {
    private final RedisRepository redisRepository;
    private static final String AUTHORITIES_KEY = "auth";
    private static final String USER_IDX = "userIdx";
    private static final String ACCESS = "Access";
    private static final String REFRESH = "Refresh";
    private static final String GRANT = "Bearer";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValidityTime;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityTime;

    private final UserRepository userRepository;

    private Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenInfoResponse createToken(Authentication authentication) {
        log.info("토큰 발행 진입");
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Date current = new Date();
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        Long userIdx = principal.getUser().getUserIdx();

        String accessToken = createAccessToken(authorities, current);
        String refreshToken = createRefreshToken(authorities, current);

        updateRefreshToken(userIdx, refreshToken);

        return TokenInfoResponse.from(GRANT, accessToken, refreshToken, accessTokenValidityTime);
    }

    private String createRefreshToken(String authorities, Date current) {
        Date refreshTokenValidity = new Date(current.getTime() + this.refreshTokenValidityTime);

        String refreshToken = Jwts.builder()
                .setSubject(REFRESH)
                .claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(current)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(refreshTokenValidity)
                .compact();
        return refreshToken;
    }

    private String createAccessToken(String authorities, Date current) {
        Date accessTokenValidity = new Date(current.getTime() + this.accessTokenValidityTime);

        String accessToken = Jwts.builder()
                .setSubject(ACCESS)
                .claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(current)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(accessTokenValidity)
                .compact();

        return accessToken;
    }

    public void updateRefreshToken(Long userIdx, String refreshToken) {
        try {
            log.info("refreshToken 저장");
            log.info("refresh : {}", refreshToken);
            redisRepository.setValues(String.valueOf(userIdx), refreshToken, Duration.ofSeconds(refreshTokenValidityTime));
        } catch (NoSuchElementException e) {
            log.error("일치하는 회원이 없습니다.");
            throw e;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        String userIdx = String.valueOf(claims.get(USER_IDX));

        User user = this.userRepository.findById(Long.parseLong(userIdx))
                .orElseThrow();

        return new UsernamePasswordAuthenticationToken(new PrincipalDetails(user), token, authorities);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw e;
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (UnsupportedJwtException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    public boolean validateRefreshToken(String token) {
        if (!getSubject(token).equals(REFRESH))
            throw new IllegalArgumentException("Refresh Token이 아닙니다.");
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            String userIdx = getUserIdx(token);
            String redisToken = redisRepository.getValues(userIdx).orElseThrow();
            if (!redisToken.equals(token))
                throw new IllegalArgumentException("Refresh Token이 다릅니다.");
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            throw e;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            throw e;
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            throw e;
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            throw e;
        } catch (NoSuchElementException e) {
            log.error("유효하지 않은 JWT 입니다.");
            throw e;
        }
    }

    private String getSubject(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    public String getUserIdx(String token) {
        Claims claims = parseClaims(token);

        return claims.get(USER_IDX).toString();
    }

    public Long getExpiration(String token) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}

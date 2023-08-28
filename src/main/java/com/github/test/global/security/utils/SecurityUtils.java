package com.github.test.global.security.utils;

import com.github.test.domain.user.entity.User;
import com.github.test.global.security.jwt.PrincipalDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtils {
    public Optional<User> getLoggedInUser() {
        return Optional.ofNullable(
                ((PrincipalDetails) (SecurityContextHolder.getContext().getAuthentication()).getPrincipal()).getUser());
    }
}

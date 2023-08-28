package com.github.test.domain.user.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userIdx;

    private String userId;
    private String userName;
    private String email;
    private String profile;
    @Enumerated(EnumType.STRING)
    private UserRole role;
}

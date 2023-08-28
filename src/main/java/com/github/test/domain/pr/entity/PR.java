package com.github.test.domain.pr.entity;

import com.github.test.domain.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prIdx;
    private String prUrl;
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;
}

package com.github.test.domain.pr;

import com.github.test.domain.pr.entity.PR;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PRRepository extends JpaRepository<PR, Long> {
}

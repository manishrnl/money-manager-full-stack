package com.example.moneymanager.repository;


import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByUser(ProfileEntity user);

    Optional<Session> findByRefreshToken(String refreshToken);

    List<Session> findByUserIdOrderByCreatedAtAsc(Long id);

    boolean existsByRefreshToken(String refreshToken);
}

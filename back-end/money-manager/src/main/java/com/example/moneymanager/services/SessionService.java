package com.example.moneymanager.services;

import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.entity.Session;
import com.example.moneymanager.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final int SESSION_LIMIT = 2;


    @Transactional
    public void validateSession(String refreshToken) {
        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new SessionAuthenticationException("Session not found or expired."));

        // Update the activity timestamp
        session.setLastUsedAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    // SessionService.java
    @Transactional
    public Session generateNewSession(ProfileEntity user, String refreshToken) {
        List<Session> userSessions = sessionRepository.findByUserIdOrderByCreatedAtAsc(user.getId());

        while (userSessions.size() >= SESSION_LIMIT) {
            Session oldest = userSessions.removeFirst();
            sessionRepository.delete(oldest);
        }

        Session newSession = Session.builder()
                .user(user)
                .refreshToken(refreshToken)
                .createdAt(LocalDateTime.now())
                .lastUsedAt(LocalDateTime.now())
                .build();

        return sessionRepository.save(newSession); // Return the saved entity with its ID
    }

    @Transactional
    public Session updateSession(String oldToken, String newToken) {
        // This handles "Rotation" for a device that is ALREADY logged in
        Session session = sessionRepository.findByRefreshToken(oldToken)
                .orElseThrow(() -> new SessionAuthenticationException("Session hijacked or expired."));

        session.setRefreshToken(newToken);
        session.setLastUsedAt(LocalDateTime.now());

        return sessionRepository.save(session);
    }


    // ADD THIS METHOD to use in your Security Filter
    @Transactional(readOnly = true)
    public boolean isSessionActive(String refreshToken) {
        return sessionRepository.findByRefreshToken(refreshToken).isPresent();
    }

}
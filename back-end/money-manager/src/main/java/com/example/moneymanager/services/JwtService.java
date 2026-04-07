package com.example.moneymanager.services;

import com.example.moneymanager.entity.ProfileEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${jwt.accessToken.expiration}")
    private String accessTokenExpiration;

    @Value("${jwt.refreshToken.expiration}")
    private String refreshTokenExpiration;


    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateRefreshToken(ProfileEntity user) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(Long.parseLong(refreshTokenExpiration));

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getSecretKey())
                .compact();
    }

    public String generateAccessToken(ProfileEntity user, Long sessionId) { // Add sessionId param
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(Long.parseLong(accessTokenExpiration));

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("sessionId", sessionId) // Store the DB ID here
                .claim("roles", user.getRoles().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getSecretKey())
                .compact();
    }

    @Cacheable(cacheNames = "tokenValidation", key = "#token")
        /* COMMENT: We cache the boolean result of a valid token.
           In a high-traffic app, parsing and verifying the cryptographic
           signature for every single request consumes CPU. By caching this,
           we skip the crypto math if we've seen this exact token recently.
        */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey()) // 1. Check Signature
                    .build()
                    .parseSignedClaims(token)   // 2. Parse & Check Expiration automatically
                    .getPayload();

            String emailInToken = claims.get("email", String.class);

            return (emailInToken.equals(userDetails.getUsername()));

        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }


    @Cacheable(cacheNames = "tokenClaims", key = "'email_' + #token")
        /* COMMENT: Parsing a JWT to get a claim is a CPU-bound task.
           Since an Access Token's payload never changes once issued,
           we can cache the email associated with that specific token string.
        */
    public String getEmailFromAccessToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("email", String.class);
    }


    @Cacheable(cacheNames = "tokenClaims", key = "'uid_refresh_' + #token")
        /* COMMENT: Refresh tokens are used less frequently than access tokens,
           but caching here still saves CPU cycles during the 'Refresh Session'
           flow, especially if the secret key is rotated or large.
        */
    public Long getUserIdFromRefreshToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }

    @Cacheable(cacheNames = "tokenClaims", key = "'uid_access_' + #token")
        /* COMMENT: Similar to email, the Subject (User ID) is fixed.
           We use a prefix like 'uid_access_' to ensure different claims
           for the same token don't overwrite each other in Redis.
        */
    public Long getUserIdFromAccessToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }


    @Cacheable(cacheNames = "tokenClaims", key = "'sid_' + #token")
        /* COMMENT: Caching the Session ID linked to a token is useful because
           this ID is often used to check if a user is 'logged out' in the DB.
           Storing it in the cache speeds up the lookup.
        */
    public Long getSessionIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("sessionId", Long.class);
    }

}

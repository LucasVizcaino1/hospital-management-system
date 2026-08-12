package com.hospital_management_system.demo.security;

import com.hospital_management_system.demo.exception.InvalidTokenException;
import com.hospital_management_system.demo.exception.TokenExpiredException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {

    private static final String ISSUER = "hospital-api";

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expirationMs}")
    private Long expirationMs;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT secret must be at least 256 bits (32 bytes). " +
                            "Generate one with KeyGenerator.main()");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        log.info("✅ JWT signing key initialized ({} bytes)", keyBytes.length);
    }

    public String generateToken(String username, String rol, Long userId, Long principalId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol);
        claims.put("userId", userId);
        claims.put("principalId", principalId);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuer(ISSUER)
                .audience().add(ISSUER).and()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    public String generateToken(String username, String rol) {
        return generateToken(username, rol, null, null);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRol(String token) {
        return extractAllClaims(token).get("rol", String.class);
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public Long extractPrincipalId(String token) {
        return extractAllClaims(token).get("principalId", Long.class);
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenValid(String token, String expectedUsername) {
        try {
            Claims claims = extractAllClaims(token);
            return expectedUsername.equals(claims.getSubject())
                    && !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(ISSUER)
                    .requireAudience(ISSUER)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Token has expired");
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("Malformed token");
        } catch (SignatureException e) {
            throw new InvalidTokenException("Invalid signature");
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid token: " + e.getMessage());
        }
    }
}
package com.reon.titan_backend.jwt.impl;

import com.reon.titan_backend.common.UniqueIdGenerator;
import com.reon.titan_backend.document.User;
import com.reon.titan_backend.jwt.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final String JWT_SECRET;
    private final String ISSUER;
    private final Long ACCESS_TOKEN_EXPIRY;
    private final String COOKIE_NAME;

    public JwtServiceImpl(
            @Value("${security.jwt.secret-key}") String jwtSecret,
            @Value("${security.jwt.issuer}")String issuer,
            @Value("${security.jwt.access-token-expiry}") Long accessTokenExpiry,
            @Value("${security.cookie.name}") String cookieName
    ) {
        JWT_SECRET = jwtSecret;
        ISSUER = issuer;
        ACCESS_TOKEN_EXPIRY = accessTokenExpiry;
        COOKIE_NAME = cookieName;
    }

    @Override
    public String extractJwtFromRequest(HttpServletRequest request) {
        log.info("Extracting jwt from request:.............");
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(COOKIE_NAME)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public String generateToken(User user) {
        log.info("Generating a access token:.............");
        String jti = UniqueIdGenerator.uniqueIdGenerator();
        String email = user.getEmail();
        String roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        Date now = new Date();
        Date expiry = new Date(now.getTime() + ACCESS_TOKEN_EXPIRY * 1000);

        return Jwts.builder()
                .id(jti)
                .subject(email)
                .issuer(ISSUER)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key())
                .compact();
    }

    @Override
    public String extractUsernameFromToken(String token) {
        log.info("Extracting username from token:..............");
        return extractClaims(token).getSubject();
    }

    @Override
    public boolean isTokenValid(String token) {
        log.info("Checking if token is valid:.............");
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    @Override
    public String extractTokenId(String token) {
        return extractClaims(token).getId();
    }

    @Override
    public long secondsUntilExpiry(String token) {
        long millisLeft = extractClaims(token).getExpiration().getTime() - System.currentTimeMillis();
        return millisLeft > 0 ? millisLeft / 1000 : 0;
    }

    private SecretKey key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(JWT_SECRET));
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

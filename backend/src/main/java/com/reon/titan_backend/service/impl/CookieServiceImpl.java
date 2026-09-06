package com.reon.titan_backend.service.impl;

import com.reon.titan_backend.service.CookieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class CookieServiceImpl implements CookieService {

    private final String COOKIE_NAME;
    private final Long COOKIE_EXPIRY;
    private final boolean COOKIE_SECURE;

    public CookieServiceImpl(
            @Value("${security.cookie.name}") String cookieName,
            @Value("${security.cookie.expiry}") Long cookieExpiry,
            @Value("${security.cookie.secure}") boolean cookieSecure
    ) {
        COOKIE_NAME = cookieName;
        COOKIE_EXPIRY = cookieExpiry;
        COOKIE_SECURE = cookieSecure;
    }

    @Override
    public ResponseCookie createAccessTokenCookie(String token) {
        log.info("Creating access token cookie:.............");
        return ResponseCookie.from(COOKIE_NAME, token)
                // javascript cannot read it -> an XSS bug cannot steal the token
                .httpOnly(true)
                // https only. false in dev, otherwise the browser drops it on http://localhost
                .secure(COOKIE_SECURE)
                // sent on every path of this api
                .path("/")
                // not sent on requests coming from another site -> csrf protection
                .sameSite("Strict")
                // browser deletes it after this many seconds (match the token expiry)
                .maxAge(Duration.ofSeconds(COOKIE_EXPIRY))
                .build();
    }
}

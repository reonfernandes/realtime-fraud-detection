package com.reon.titan_backend.service;

import org.springframework.http.ResponseCookie;

public interface CookieService {
    ResponseCookie createAccessTokenCookie(String token);
}

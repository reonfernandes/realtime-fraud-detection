package com.reon.titan_backend.jwt;

import com.reon.titan_backend.document.User;
import jakarta.servlet.http.HttpServletRequest;

public interface JwtService {
    String extractJwtFromRequest(HttpServletRequest request);
    String generateToken(User user);
    String extractUsernameFromToken(String token);
    boolean isTokenValid(String token);
}

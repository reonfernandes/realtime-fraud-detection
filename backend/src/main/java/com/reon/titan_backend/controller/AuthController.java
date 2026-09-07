package com.reon.titan_backend.controller;

import com.reon.titan_backend.common.ClientIpResolver;
import com.reon.titan_backend.dto.SignInRequest;
import com.reon.titan_backend.dto.SignUpRequest;
import com.reon.titan_backend.dto.response.ApiResponse;
import com.reon.titan_backend.dto.response.SignInResponse;
import com.reon.titan_backend.dto.response.SignUpResponse;
import com.reon.titan_backend.service.AuthService;
import com.reon.titan_backend.service.CookieService;
import com.reon.titan_backend.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final CookieService cookieService;
    private final RateLimiterService rateLimiterService;

    public AuthController(AuthService authService, CookieService cookieService,
                          RateLimiterService rateLimiterService) {
        this.authService = authService;
        this.cookieService = cookieService;
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<ApiResponse<SignUpResponse>> signUp(@Valid @RequestBody SignUpRequest request,
                                                             HttpServletRequest httpRequest) {
        rateLimiterService.enforceAuthRateLimit(ClientIpResolver.resolve(httpRequest));

        log.info("Incoming request for signUp:..............{}", request.email());
        SignUpResponse signUpResponse = authService.generateUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "SignUp success",
                        signUpResponse
                ));
    }

    @PostMapping("/signIn")
    public ResponseEntity<ApiResponse<SignInResponse>> signIn(@Valid @RequestBody SignInRequest request,
                                                             HttpServletRequest httpRequest) {
        rateLimiterService.enforceAuthRateLimit(ClientIpResolver.resolve(httpRequest));

        log.info("Incoming request for signIn:..............{}", request.email());
        SignInResponse signInResponse = authService.authenticateUser(request);

        // browsers get the token as an httpOnly cookie (Set-Cookie header),
        // non browser clients can read it from the response body instead
        ResponseCookie cookie = cookieService.createAccessTokenCookie(signInResponse.token());

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse<>(
                        true,
                        "SignIn success",
                        signInResponse
                ));
    }
}

package com.reon.titan_backend.service;

import com.reon.titan_backend.dto.SignInRequest;
import com.reon.titan_backend.dto.SignUpRequest;
import com.reon.titan_backend.dto.response.SignInResponse;
import com.reon.titan_backend.dto.response.SignUpResponse;

public interface AuthService {
    SignUpResponse generateUser(SignUpRequest signUpRequest);
    SignInResponse authenticateUser(SignInRequest signInRequest);
}

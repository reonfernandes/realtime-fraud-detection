package com.reon.titan_backend.mapper;

import com.reon.titan_backend.document.User;
import com.reon.titan_backend.dto.SignUpRequest;
import com.reon.titan_backend.dto.response.SignUpResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    public User mapToEntity(SignUpRequest signUpRequest) {
        return User.builder()
                .email(signUpRequest.email())
                .password(signUpRequest.password())
                .build();
    }

    public SignUpResponse responseToUser(User user) {
        return SignUpResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRoles())
                .createdOn(user.getCreatedAt())
                .build();
    }
}

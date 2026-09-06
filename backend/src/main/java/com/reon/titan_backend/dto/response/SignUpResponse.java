package com.reon.titan_backend.dto.response;

import com.reon.titan_backend.document.type.Role;
import lombok.Builder;

import java.time.Instant;
import java.util.Set;

@Builder
public record SignUpResponse(
        String id,
        String email,
        Set<Role> role,
        Instant createdOn
) {
}

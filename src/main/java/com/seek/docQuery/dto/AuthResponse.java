package com.seek.docQuery.dto;

public record AuthResponse(
        String token,
        String email
) {
}

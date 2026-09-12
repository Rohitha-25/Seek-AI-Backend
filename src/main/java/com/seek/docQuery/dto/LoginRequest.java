package com.seek.docQuery.dto;

public record LoginRequest(
        String email,
        String password
) {
}

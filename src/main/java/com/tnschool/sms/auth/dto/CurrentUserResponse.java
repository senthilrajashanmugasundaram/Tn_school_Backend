package com.tnschool.sms.auth.dto;

public record CurrentUserResponse(
        String id,
        String name,
        String phone,
        String email,
        String role
) {
}

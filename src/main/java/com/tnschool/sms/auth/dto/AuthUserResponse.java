package com.tnschool.sms.auth.dto;

public record AuthUserResponse(
        String id,
        String name,
        String phone,
        String role
) {
}

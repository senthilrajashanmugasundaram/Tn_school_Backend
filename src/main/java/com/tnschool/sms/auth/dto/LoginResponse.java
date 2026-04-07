package com.tnschool.sms.auth.dto;

public record LoginResponse(
        String accessToken,
        AuthUserResponse user
) {
}

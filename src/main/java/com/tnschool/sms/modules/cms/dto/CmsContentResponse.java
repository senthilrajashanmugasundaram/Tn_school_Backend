package com.tnschool.sms.modules.cms.dto;

public record CmsContentResponse(
        String id,
        String contentType,
        String title,
        String body,
        boolean isPublished,
        String publishedAt,
        String expiresAt,
        String createdAt
) {
}

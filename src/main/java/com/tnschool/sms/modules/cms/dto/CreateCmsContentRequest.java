package com.tnschool.sms.modules.cms.dto;

import com.tnschool.sms.modules.cms.model.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record CreateCmsContentRequest(
        @NotNull(message = "Content type is required") ContentType contentType,
        @NotBlank(message = "Title is required") String title,
        @NotBlank(message = "Body is required") String body,
        boolean isPublished,
        OffsetDateTime expiresAt
) {
}

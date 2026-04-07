package com.tnschool.sms.modules.homework.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateHomeworkRequest(
        @NotBlank(message = "Description is required") String description
) {
}

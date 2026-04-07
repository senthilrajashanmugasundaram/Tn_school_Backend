package com.tnschool.sms.modules.academic.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSubjectRequest(
        @NotBlank(message = "Subject name is required") String name,
        @NotBlank(message = "Subject code is required") String code
) {
}

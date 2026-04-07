package com.tnschool.sms.modules.academic.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateClassRequest(
        @NotBlank(message = "Grade is required") String grade,
        @NotBlank(message = "Section is required") String section,
        @NotBlank(message = "Academic term is required") String academicTermId,
        String roomNo
) {
}

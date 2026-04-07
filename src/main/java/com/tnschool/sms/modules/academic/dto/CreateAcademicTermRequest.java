package com.tnschool.sms.modules.academic.dto;

import com.tnschool.sms.modules.academic.model.TermType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateAcademicTermRequest(
        @NotBlank(message = "Term name is required") String name,
        @NotNull(message = "Term type is required") TermType type,
        @NotBlank(message = "Academic year is required") String academicYear,
        @NotNull(message = "Start date is required") LocalDate startDate,
        @NotNull(message = "End date is required") LocalDate endDate
) {
}

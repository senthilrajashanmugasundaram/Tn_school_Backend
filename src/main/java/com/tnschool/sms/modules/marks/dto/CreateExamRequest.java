package com.tnschool.sms.modules.marks.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateExamRequest(
        @NotBlank(message = "Exam name is required") String name,
        @NotBlank(message = "Academic term is required") String academicTermId,
        @NotNull(message = "Exam date is required") LocalDate examDate,
        @NotNull(message = "Total marks is required")
        @Min(value = 1, message = "Total marks must be at least 1")
        Integer totalMarks
) {
}

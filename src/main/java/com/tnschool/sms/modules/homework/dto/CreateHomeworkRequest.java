package com.tnschool.sms.modules.homework.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateHomeworkRequest(
        @NotBlank(message = "Class id is required") String classId,
        @NotBlank(message = "Subject id is required") String subjectId,
        @NotNull(message = "Homework date is required") LocalDate homeworkDate,
        @NotBlank(message = "Description is required") String description
) {
}

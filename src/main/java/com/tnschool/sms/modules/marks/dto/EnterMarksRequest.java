package com.tnschool.sms.modules.marks.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record EnterMarksRequest(
        @NotBlank(message = "Exam id is required") String examId,
        @NotBlank(message = "Subject id is required") String subjectId,
        @NotEmpty(message = "Mark records are required") List<@Valid MarkLine> records
) {
    public record MarkLine(
            @NotBlank(message = "Student id is required") String studentId,
            @NotNull(message = "Marks obtained is required")
            @DecimalMin(value = "0.0", message = "Marks obtained cannot be negative")
            Double marksObtained,
            @NotNull(message = "Maximum marks is required")
            @DecimalMin(value = "1.0", message = "Maximum marks must be at least 1")
            Double maxMarks
    ) {
    }
}

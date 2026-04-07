package com.tnschool.sms.modules.fees.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateFeeStructureRequest(
        @NotBlank(message = "Class id is required") String classId,
        @NotBlank(message = "Academic term id is required") String academicTermId,
        @NotBlank(message = "Fee type is required") String feeType,
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
        Double amount,
        LocalDate dueDate
) {
}

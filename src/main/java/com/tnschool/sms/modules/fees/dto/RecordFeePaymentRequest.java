package com.tnschool.sms.modules.fees.dto;

import com.tnschool.sms.modules.fees.model.FeePaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RecordFeePaymentRequest(
        @NotBlank(message = "Student id is required") String studentId,
        @NotBlank(message = "Fee structure id is required") String feeStructureId,
        @NotNull(message = "Paid amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Paid amount must be greater than zero")
        Double paidAmount,
        LocalDate paidDate,
        @NotNull(message = "Status is required") FeePaymentStatus status,
        String receiptNo,
        String paymentMode,
        String remarks
) {
}

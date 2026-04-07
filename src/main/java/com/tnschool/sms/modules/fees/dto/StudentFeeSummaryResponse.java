package com.tnschool.sms.modules.fees.dto;

import java.util.List;

public record StudentFeeSummaryResponse(
        StudentSummary student,
        List<FeeLineItem> summary,
        Totals totals
) {
    public record StudentSummary(String name, String admissionNo) {}
    public record FeeLineItem(String feeType, Double amount, Double paidAmount, String status, String dueDate) {}
    public record Totals(Double totalAmount, Double totalPaid, Double totalPending) {}
}

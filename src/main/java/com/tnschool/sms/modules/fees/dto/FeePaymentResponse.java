package com.tnschool.sms.modules.fees.dto;

public record FeePaymentResponse(
        String id,
        String studentId,
        String feeStructureId,
        Double paidAmount,
        String paidDate,
        String status,
        String receiptNo,
        String paymentMode,
        String remarks,
        StudentSummary student,
        FeeStructureSummary feeStructure
) {
    public record StudentSummary(String name, String admissionNo, ClassSummary schoolClass, ParentSummary parent) {}
    public record ClassSummary(String grade, String section) {}
    public record ParentSummary(String name, String phone) {}
    public record FeeStructureSummary(String feeType, Double amount) {}
}

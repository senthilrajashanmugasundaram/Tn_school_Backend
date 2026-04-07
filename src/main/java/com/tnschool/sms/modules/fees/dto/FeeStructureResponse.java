package com.tnschool.sms.modules.fees.dto;

public record FeeStructureResponse(
        String id,
        String classId,
        String academicTermId,
        String feeType,
        Double amount,
        String dueDate,
        ClassSummary schoolClass
) {
    public record ClassSummary(String grade, String section) {}
}

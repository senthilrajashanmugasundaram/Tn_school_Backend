package com.tnschool.sms.modules.academic.dto;

public record ClassResponse(
        String id,
        String grade,
        String section,
        String academicTermId,
        String roomNo,
        AcademicTermSummary academicTerm
) {
    public record AcademicTermSummary(String name, boolean isActive) {}
}

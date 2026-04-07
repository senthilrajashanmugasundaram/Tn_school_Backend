package com.tnschool.sms.modules.marks.dto;

public record ExamResponse(
        String id,
        String name,
        String academicTermId,
        String examDate,
        boolean marksLocked,
        Integer totalMarks,
        AcademicTermSummary academicTerm
) {
    public record AcademicTermSummary(String name) {}
}

package com.tnschool.sms.modules.marks.dto;

public record MarkResponse(
        String id,
        String examId,
        String studentId,
        String subjectId,
        Double marksObtained,
        Double maxMarks,
        String grade,
        ExamSummary exam,
        SubjectSummary subject
) {
    public record ExamSummary(String name, String examDate, boolean marksLocked, AcademicTermSummary academicTerm) {}
    public record AcademicTermSummary(String name) {}
    public record SubjectSummary(String name, String code) {}
}

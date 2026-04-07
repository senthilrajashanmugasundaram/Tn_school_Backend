package com.tnschool.sms.modules.academic.dto;

public record TeacherMappingResponse(
        String id,
        String teacherUserId,
        String classId,
        String subjectId,
        String academicTermId,
        boolean isClassTeacher,
        TeacherSummary teacher,
        ClassSummary schoolClass,
        SubjectSummary subject,
        TermSummary academicTerm
) {
    public record TeacherSummary(String id, String name) {}
    public record ClassSummary(String grade, String section) {}
    public record SubjectSummary(String name, String code) {}
    public record TermSummary(String name) {}
}

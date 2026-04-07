package com.tnschool.sms.modules.homework.dto;

public record HomeworkResponse(
        String id,
        String classId,
        String subjectId,
        String teacherUserId,
        String homeworkDate,
        String description,
        boolean smsSent,
        SubjectSummary subject,
        TeacherSummary teacher,
        ClassSummary schoolClass
) {
    public record SubjectSummary(String name, String code) {}
    public record TeacherSummary(String name) {}
    public record ClassSummary(String grade, String section) {}
}

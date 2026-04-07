package com.tnschool.sms.modules.academic.dto;

public record TimetableSlotResponse(
        String id,
        String classId,
        String subjectId,
        String teacherId,
        String day,
        Integer periodNo,
        String startTime,
        String endTime,
        SubjectSummary subject,
        TeacherSummary teacher,
        ClassSummary schoolClass
) {
    public record SubjectSummary(String name, String code) {}
    public record TeacherSummary(String name) {}
    public record ClassSummary(String grade, String section) {}
}

package com.tnschool.sms.modules.attendance.dto;

public record AttendanceRecordResponse(
        String id,
        String studentId,
        String classId,
        String date,
        String status,
        String remarks,
        StudentSummary student,
        MarkedBySummary markedBy
) {
    public record StudentSummary(String id, String name, String admissionNo) {}
    public record MarkedBySummary(String name) {}
}

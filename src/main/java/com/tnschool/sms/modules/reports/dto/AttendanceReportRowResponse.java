package com.tnschool.sms.modules.reports.dto;

public record AttendanceReportRowResponse(
        StudentSummary student,
        long present,
        long absent,
        long late,
        long total,
        long percentage
) {
    public record StudentSummary(String id, String name, String admissionNo) {}
}

package com.tnschool.sms.modules.reports.dto;

import java.util.Map;

public record AdminSnapshotResponse(
        long totalStudents,
        long totalTeachers,
        long totalClasses,
        long pendingFeeCount,
        Map<String, Long> todayAttendance
) {
}

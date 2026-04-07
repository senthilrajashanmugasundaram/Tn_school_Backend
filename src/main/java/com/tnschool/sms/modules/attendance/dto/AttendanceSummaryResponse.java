package com.tnschool.sms.modules.attendance.dto;

public record AttendanceSummaryResponse(
        long total,
        long present,
        long absent,
        long late,
        long percentage
) {
}

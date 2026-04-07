package com.tnschool.sms.modules.attendance.dto;

import java.util.List;

public record StudentAttendanceResponse(
        List<AttendanceRecordResponse> records,
        AttendanceSummaryResponse summary
) {
}

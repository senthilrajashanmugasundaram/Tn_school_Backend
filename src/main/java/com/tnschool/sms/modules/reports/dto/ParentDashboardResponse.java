package com.tnschool.sms.modules.reports.dto;

import com.tnschool.sms.modules.fees.dto.FeePaymentResponse;
import com.tnschool.sms.modules.homework.dto.HomeworkResponse;
import com.tnschool.sms.modules.marks.dto.MarkResponse;

import java.util.List;

public record ParentDashboardResponse(
        StudentSummary student,
        AttendanceSummary attendanceThisMonth,
        List<MarkResponse> recentMarks,
        List<FeePaymentResponse> pendingFees,
        List<HomeworkResponse> todaysHomework
) {
    public record StudentSummary(String name, String admissionNo) {}
    public record AttendanceSummary(long present, long total, long percentage) {}
}

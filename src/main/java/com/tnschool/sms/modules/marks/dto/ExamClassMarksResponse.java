package com.tnschool.sms.modules.marks.dto;

import java.util.List;

public record ExamClassMarksResponse(
        StudentSummary student,
        List<MarkResponse> marks
) {
    public record StudentSummary(String id, String name, String admissionNo) {}
}

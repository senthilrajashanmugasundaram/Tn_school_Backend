package com.tnschool.sms.modules.activities.dto;

public record ActivityParticipantResponse(
        String id,
        String activityId,
        String studentId,
        String achievement,
        String position,
        boolean certificate,
        String notes,
        ActivitySummary activity,
        StudentSummary student
) {
    public record ActivitySummary(String title, String category, String activityDate) {}
    public record StudentSummary(String name, String admissionNo) {}
}

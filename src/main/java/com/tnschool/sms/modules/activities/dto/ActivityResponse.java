package com.tnschool.sms.modules.activities.dto;

public record ActivityResponse(
        String id,
        String academicTermId,
        String category,
        String title,
        String description,
        String activityDate,
        int participantCount
) {
}

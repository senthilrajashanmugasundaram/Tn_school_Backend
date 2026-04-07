package com.tnschool.sms.modules.activities.dto;

import jakarta.validation.constraints.NotBlank;

public record AddActivityParticipantRequest(
        @NotBlank(message = "Activity id is required") String activityId,
        @NotBlank(message = "Student id is required") String studentId,
        String achievement,
        String position,
        boolean certificate,
        String notes
) {
}

package com.tnschool.sms.modules.notifications.dto;

import jakarta.validation.constraints.NotBlank;

public record BroadcastHolidayRequest(
        @NotBlank(message = "Message is required") String message
) {
}

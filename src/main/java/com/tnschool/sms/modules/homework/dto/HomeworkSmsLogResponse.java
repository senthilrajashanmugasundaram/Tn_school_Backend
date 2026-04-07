package com.tnschool.sms.modules.homework.dto;

public record HomeworkSmsLogResponse(
        String id,
        String studentId,
        String parentPhone,
        String homeworkDate,
        String smsBody,
        String providerStatus,
        String sentAt
) {
}

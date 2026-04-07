package com.tnschool.sms.modules.activities.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UpdateActivityRequest(
        String academicTermId,
        @NotBlank(message = "Category is required") String category,
        @NotBlank(message = "Title is required") String title,
        String description,
        LocalDate activityDate
) {
}

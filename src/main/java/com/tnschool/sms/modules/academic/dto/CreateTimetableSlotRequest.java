package com.tnschool.sms.modules.academic.dto;

import com.tnschool.sms.modules.academic.model.WeekDay;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record CreateTimetableSlotRequest(
        @NotBlank(message = "Class id is required") String classId,
        @NotBlank(message = "Subject id is required") String subjectId,
        @NotBlank(message = "Teacher id is required") String teacherId,
        @NotNull(message = "Day is required") WeekDay day,
        @NotNull(message = "Period number is required")
        @Min(value = 1, message = "Period number must be at least 1")
        @Max(value = 12, message = "Period number must be at most 12")
        Integer periodNo,
        @NotNull(message = "Start time is required") LocalTime startTime,
        @NotNull(message = "End time is required") LocalTime endTime
) {
}

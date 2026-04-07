package com.tnschool.sms.modules.attendance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record MarkAttendanceRequest(
        @NotBlank(message = "Class id is required") String classId,
        @NotNull(message = "Date is required") LocalDate date,
        @NotEmpty(message = "Attendance records are required") List<@Valid AttendanceLine> records
) {
    public record AttendanceLine(
            @NotBlank(message = "Student id is required") String studentId,
            @NotBlank(message = "Status is required") String status,
            String remarks
    ) {
    }
}

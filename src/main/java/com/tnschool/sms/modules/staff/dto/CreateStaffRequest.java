package com.tnschool.sms.modules.staff.dto;

import com.tnschool.sms.modules.staff.model.StaffType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateStaffRequest(
        @NotBlank(message = "User id is required") String userId,
        @NotNull(message = "Staff type is required") StaffType staffType,
        @NotBlank(message = "Employee id is required") String employeeId,
        String designation,
        String department,
        @NotNull(message = "Join date is required") LocalDate joinDate
) {
}

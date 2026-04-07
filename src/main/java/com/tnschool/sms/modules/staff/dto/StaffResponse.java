package com.tnschool.sms.modules.staff.dto;

public record StaffResponse(
        String id,
        String userId,
        String staffType,
        String employeeId,
        String designation,
        String department,
        String joinDate,
        UserSummary user
) {
    public record UserSummary(String id, String name, String phone, String email, boolean isActive) {}
}

package com.tnschool.sms.modules.students.dto;

public record StudentResponse(
        String id,
        String name,
        String admissionNo,
        String classId,
        String parentUserId,
        String gender,
        String dateOfBirth,
        String bloodGroup,
        boolean isActive,
        ClassSummary schoolClass,
        ParentSummary parent
) {
    public record ClassSummary(String grade, String section) {}
    public record ParentSummary(String id, String name, String phone) {}
}

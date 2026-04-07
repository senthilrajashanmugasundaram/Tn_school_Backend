package com.tnschool.sms.modules.students.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateStudentRequest(
        @NotBlank(message = "Student name is required") String name,
        @NotBlank(message = "Admission number is required") String admissionNo,
        @NotBlank(message = "Class id is required") String classId,
        @NotBlank(message = "Parent user id is required") String parentUserId,
        String gender,
        LocalDate dateOfBirth,
        String bloodGroup
) {
}

package com.tnschool.sms.modules.academic.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTeacherMappingRequest(
        @NotBlank(message = "Teacher user id is required") String teacherUserId,
        @NotBlank(message = "Class id is required") String classId,
        @NotBlank(message = "Subject id is required") String subjectId,
        @NotBlank(message = "Academic term id is required") String academicTermId,
        boolean isClassTeacher
) {
}

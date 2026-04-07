package com.tnschool.sms.modules.academic.dto;

public record AcademicTermDto(
        String id,
        String name,
        String type,
        String academicYear,
        String startDate,
        String endDate,
        boolean active
) {
}

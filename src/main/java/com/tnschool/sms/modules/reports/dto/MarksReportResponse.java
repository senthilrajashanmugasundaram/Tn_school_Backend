package com.tnschool.sms.modules.reports.dto;

import java.util.List;
import java.util.Map;

public record MarksReportResponse(
        List<SubjectStat> subjectStats,
        Map<String, Long> gradeDistribution
) {
    public record SubjectStat(String name, double avg, double highest) {}
}

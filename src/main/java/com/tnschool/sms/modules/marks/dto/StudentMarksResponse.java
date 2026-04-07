package com.tnschool.sms.modules.marks.dto;

import java.util.List;
import java.util.Map;

public record StudentMarksResponse(
        List<MarkResponse> marks,
        Map<String, List<MarkResponse>> grouped
) {
}

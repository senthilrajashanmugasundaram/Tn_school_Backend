package com.tnschool.sms.modules.reports.controller;

import com.tnschool.sms.modules.reports.service.ReportsService;
import com.tnschool.sms.modules.students.service.StudentService;
import com.tnschool.sms.shared.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final ReportsService reportsService;
    private final StudentService studentService;

    public ReportsController(ReportsService reportsService, StudentService studentService) {
        this.reportsService = reportsService;
        this.studentService = studentService;
    }

    @GetMapping("/attendance/class/{classId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> attendanceReport(@PathVariable String classId) {
        return ResponseEntity.ok(ApiResponse.ok("Attendance report loaded", reportsService.getAttendanceReport(classId)));
    }

    @GetMapping("/marks/exam/{examId}/class/{classId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> marksReport(@PathVariable String examId, @PathVariable String classId) {
        return ResponseEntity.ok(ApiResponse.ok("Marks report loaded", reportsService.getMarksReport(examId, classId)));
    }

    @GetMapping("/parent/child/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','PARENT')")
    public ResponseEntity<ApiResponse<?>> parentDashboard(
            @PathVariable String studentId,
            org.springframework.security.core.Authentication authentication
    ) {
        boolean isParent = authentication.getAuthorities().stream().anyMatch(item -> item.toString().equals("ROLE_PARENT"));
        if (isParent && !studentService.getStudent(studentId).parentUserId().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view dashboard data for your linked child");
        }
        return ResponseEntity.ok(ApiResponse.ok("Parent dashboard loaded", reportsService.getParentDashboard(studentId)));
    }

    @GetMapping("/admin/snapshot")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> adminSnapshot() {
        return ResponseEntity.ok(ApiResponse.ok("Admin snapshot loaded", reportsService.getAdminSnapshot()));
    }
}

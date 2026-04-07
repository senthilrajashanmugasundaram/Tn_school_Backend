package com.tnschool.sms.modules.attendance.controller;

import com.tnschool.sms.modules.attendance.dto.MarkAttendanceRequest;
import com.tnschool.sms.modules.attendance.service.AttendanceService;
import com.tnschool.sms.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/mark")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<Void>> markAttendance(
            org.springframework.security.core.Authentication authentication,
            @Valid @RequestBody MarkAttendanceRequest request
    ) {
        attendanceService.markAttendance(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.ok("Attendance marked successfully", null));
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<?>> getClassAttendance(
            @PathVariable String classId,
            @RequestParam(required = false) java.time.LocalDate date
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Class attendance loaded", attendanceService.getClassAttendance(classId, date)));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','PARENT')")
    public ResponseEntity<ApiResponse<?>> getStudentAttendance(
            @PathVariable String studentId,
            org.springframework.security.core.Authentication authentication
    ) {
        boolean isParent = authentication.getAuthorities().stream().anyMatch(item -> item.toString().equals("ROLE_PARENT"));
        if (isParent && !attendanceService.getParentUserIdForStudent(studentId).equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view attendance for your linked child");
        }
        return ResponseEntity.ok(ApiResponse.ok("Student attendance loaded", attendanceService.getStudentAttendance(studentId)));
    }
}

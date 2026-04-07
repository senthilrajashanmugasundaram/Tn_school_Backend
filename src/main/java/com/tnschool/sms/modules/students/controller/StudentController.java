package com.tnschool.sms.modules.students.controller;

import com.tnschool.sms.modules.students.dto.CreateStudentRequest;
import com.tnschool.sms.modules.students.service.StudentService;
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
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<?>> getStudents(@RequestParam(required = false) String classId) {
        return ResponseEntity.ok(ApiResponse.ok("Students loaded", studentService.getStudents(classId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','PARENT')")
    public ResponseEntity<ApiResponse<?>> getStudent(
            @PathVariable String id,
            org.springframework.security.core.Authentication authentication
    ) {
        var student = studentService.getStudent(id);
        boolean isAdminOrTeacher = authentication.getAuthorities().stream()
                .anyMatch(item -> item.toString().equals("ROLE_ADMIN") || item.toString().equals("ROLE_TEACHER"));
        if (!isAdminOrTeacher && !student.parentUserId().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your linked child");
        }
        return ResponseEntity.ok(ApiResponse.ok("Student loaded", student));
    }

    @GetMapping("/my/children")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<?>> myChildren(org.springframework.security.core.Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok("Parent children loaded", studentService.getChildrenOfParent(authentication.getName())));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Student created", studentService.createStudent(request)));
    }
}

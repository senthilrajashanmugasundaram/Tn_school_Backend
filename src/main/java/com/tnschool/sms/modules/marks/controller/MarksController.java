package com.tnschool.sms.modules.marks.controller;

import com.tnschool.sms.modules.marks.dto.CreateExamRequest;
import com.tnschool.sms.modules.marks.dto.EnterMarksRequest;
import com.tnschool.sms.modules.marks.service.MarksService;
import com.tnschool.sms.modules.students.service.StudentService;
import com.tnschool.sms.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/marks")
public class MarksController {

    private final MarksService marksService;
    private final StudentService studentService;

    public MarksController(MarksService marksService, StudentService studentService) {
        this.marksService = marksService;
        this.studentService = studentService;
    }

    @GetMapping("/exams")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<?>> getExams(@RequestParam(required = false) String termId) {
        return ResponseEntity.ok(ApiResponse.ok("Exams loaded", marksService.getExams(termId)));
    }

    @PostMapping("/exams")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createExam(@Valid @RequestBody CreateExamRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Exam created", marksService.createExam(request)));
    }

    @PatchMapping("/exams/{id}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> toggleLock(@PathVariable String id) {
        marksService.toggleLock(id);
        return ResponseEntity.ok(ApiResponse.ok("Exam lock status updated", Map.of("id", id)));
    }

    @PostMapping("/enter")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<Void>> enterMarks(
            org.springframework.security.core.Authentication authentication,
            @Valid @RequestBody EnterMarksRequest request
    ) {
        marksService.enterMarks(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.ok("Marks saved successfully", null));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','PARENT')")
    public ResponseEntity<ApiResponse<?>> getStudentMarks(
            @PathVariable String studentId,
            org.springframework.security.core.Authentication authentication
    ) {
        boolean isParent = authentication.getAuthorities().stream().anyMatch(item -> item.toString().equals("ROLE_PARENT"));
        if (isParent && !studentService.getStudent(studentId).parentUserId().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view marks for your linked child");
        }
        return ResponseEntity.ok(ApiResponse.ok("Student marks loaded", marksService.getStudentMarks(studentId)));
    }

    @GetMapping("/exam/{examId}/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<?>> getExamClassMarks(@PathVariable String examId, @PathVariable String classId) {
        return ResponseEntity.ok(ApiResponse.ok("Exam class marks loaded", marksService.getExamClassMarks(examId, classId)));
    }
}

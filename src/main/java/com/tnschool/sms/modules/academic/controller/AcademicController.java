package com.tnschool.sms.modules.academic.controller;

import com.tnschool.sms.modules.academic.dto.CreateAcademicTermRequest;
import com.tnschool.sms.modules.academic.dto.CreateClassRequest;
import com.tnschool.sms.modules.academic.dto.CreateSubjectRequest;
import com.tnschool.sms.modules.academic.dto.CreateTeacherMappingRequest;
import com.tnschool.sms.modules.academic.dto.CreateTimetableSlotRequest;
import com.tnschool.sms.modules.academic.service.AcademicService;
import com.tnschool.sms.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/academic")
public class AcademicController {

    private final AcademicService academicService;

    public AcademicController(AcademicService academicService) {
        this.academicService = academicService;
    }

    @GetMapping("/terms")
    public ResponseEntity<ApiResponse<?>> getTerms() {
        return ResponseEntity.ok(ApiResponse.ok("Academic terms loaded", academicService.getTerms()));
    }

    @GetMapping("/terms/active")
    public ResponseEntity<ApiResponse<?>> getActiveTerm() {
        return ResponseEntity.ok(ApiResponse.ok("Active term loaded", academicService.getActiveTerm()));
    }

    @PostMapping("/terms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createTerm(@Valid @RequestBody CreateAcademicTermRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Academic term created", academicService.createTerm(request)));
    }

    @PatchMapping("/terms/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> activateTerm(@PathVariable String id) {
        academicService.activateTerm(id);
        return ResponseEntity.ok(ApiResponse.ok("Academic term activated", Map.of("id", id)));
    }

    @DeleteMapping("/terms/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteTerm(@PathVariable String id) {
        academicService.deleteTerm(id);
        return ResponseEntity.ok(ApiResponse.ok("Academic term deleted", Map.of("id", id)));
    }

    @GetMapping("/classes")
    public ResponseEntity<ApiResponse<?>> getClasses(@RequestParam(required = false) String termId) {
        return ResponseEntity.ok(ApiResponse.ok("Classes loaded", academicService.getClasses(termId)));
    }

    @GetMapping("/classes/{id}")
    public ResponseEntity<ApiResponse<?>> getClassById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Class loaded", academicService.getClassById(id)));
    }

    @PostMapping("/classes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createClassroom(@Valid @RequestBody CreateClassRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Class created", academicService.createClass(request)));
    }

    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<?>> getSubjects() {
        return ResponseEntity.ok(ApiResponse.ok("Subjects loaded", academicService.getSubjects()));
    }

    @PostMapping("/subjects")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createSubject(@Valid @RequestBody CreateSubjectRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Subject created", academicService.createSubject(request)));
    }

    @PostMapping("/mappings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createMapping(@Valid @RequestBody CreateTeacherMappingRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Teacher mapping created", academicService.createMapping(request)));
    }

    @GetMapping("/mappings/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<?>> getTeacherMappings(
            @PathVariable String teacherId,
            org.springframework.security.core.Authentication authentication
    ) {
        assertAdminOrSameTeacher(authentication, teacherId);
        return ResponseEntity.ok(ApiResponse.ok("Teacher mappings loaded", academicService.getTeacherMappings(teacherId)));
    }

    @DeleteMapping("/mappings/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteMapping(@PathVariable String id) {
        academicService.deleteMapping(id);
        return ResponseEntity.ok(ApiResponse.ok("Teacher mapping deleted", Map.of("id", id)));
    }

    @PostMapping("/timetable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createTimetableSlot(@Valid @RequestBody CreateTimetableSlotRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Timetable slot created", academicService.createTimetableSlot(request)));
    }

    @GetMapping("/timetable/class/{classId}")
    public ResponseEntity<ApiResponse<?>> getClassTimetable(@PathVariable String classId) {
        return ResponseEntity.ok(ApiResponse.ok("Class timetable loaded", academicService.getClassTimetable(classId)));
    }

    @GetMapping("/timetable/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<?>> getTeacherTimetable(
            @PathVariable String teacherId,
            org.springframework.security.core.Authentication authentication
    ) {
        assertAdminOrSameTeacher(authentication, teacherId);
        return ResponseEntity.ok(ApiResponse.ok("Teacher timetable loaded", academicService.getTeacherTimetable(teacherId)));
    }

    @DeleteMapping("/timetable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteTimetableSlot(@PathVariable String id) {
        academicService.deleteTimetableSlot(id);
        return ResponseEntity.ok(ApiResponse.ok("Timetable slot deleted", Map.of("id", id)));
    }

    private void assertAdminOrSameTeacher(org.springframework.security.core.Authentication authentication, String teacherId) {
        Collection<?> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.stream().anyMatch(item -> item.toString().equals("ROLE_ADMIN"));
        if (!isAdmin && !authentication.getName().equals(teacherId)) {
            throw new IllegalArgumentException("You can only view your own teacher data");
        }
    }
}

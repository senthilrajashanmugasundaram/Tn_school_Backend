package com.tnschool.sms.modules.activities.controller;

import com.tnschool.sms.modules.activities.dto.AddActivityParticipantRequest;
import com.tnschool.sms.modules.activities.dto.CreateActivityRequest;
import com.tnschool.sms.modules.activities.dto.UpdateActivityRequest;
import com.tnschool.sms.modules.activities.service.ActivitiesService;
import com.tnschool.sms.modules.students.service.StudentService;
import com.tnschool.sms.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/activities")
public class ActivitiesController {

    private final ActivitiesService activitiesService;
    private final StudentService studentService;

    public ActivitiesController(ActivitiesService activitiesService, StudentService studentService) {
        this.activitiesService = activitiesService;
        this.studentService = studentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PARENT')")
    public ResponseEntity<ApiResponse<?>> getActivities(
            @RequestParam(required = false) String termId,
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Activities loaded", activitiesService.getActivities(termId, category)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PARENT')")
    public ResponseEntity<ApiResponse<?>> getActivity(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Activity loaded", activitiesService.getActivity(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createActivity(@Valid @RequestBody CreateActivityRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Activity created", activitiesService.createActivity(request)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateActivity(@PathVariable String id, @Valid @RequestBody UpdateActivityRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Activity updated", activitiesService.updateActivity(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteActivity(@PathVariable String id) {
        activitiesService.deleteActivity(id);
        return ResponseEntity.ok(ApiResponse.ok("Activity deleted", Map.of("id", id)));
    }

    @PostMapping("/participants")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> addParticipant(@Valid @RequestBody AddActivityParticipantRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Activity participant added", activitiesService.addParticipant(request)));
    }

    @DeleteMapping("/participants/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> removeParticipant(@PathVariable String id) {
        activitiesService.removeParticipant(id);
        return ResponseEntity.ok(ApiResponse.ok("Activity participant removed", Map.of("id", id)));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','PARENT')")
    public ResponseEntity<ApiResponse<?>> getStudentActivities(
            @PathVariable String studentId,
            org.springframework.security.core.Authentication authentication
    ) {
        boolean isParent = authentication.getAuthorities().stream().anyMatch(item -> item.toString().equals("ROLE_PARENT"));
        if (isParent && !studentService.getStudent(studentId).parentUserId().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view activities for your linked child");
        }
        return ResponseEntity.ok(ApiResponse.ok("Student activities loaded", activitiesService.getStudentActivities(studentId)));
    }
}

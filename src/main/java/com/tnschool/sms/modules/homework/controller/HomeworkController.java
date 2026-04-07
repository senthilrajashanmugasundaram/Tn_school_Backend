package com.tnschool.sms.modules.homework.controller;

import com.tnschool.sms.modules.homework.dto.CreateHomeworkRequest;
import com.tnschool.sms.modules.homework.dto.UpdateHomeworkRequest;
import com.tnschool.sms.modules.homework.service.HomeworkService;
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

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/homework")
public class HomeworkController {

    private final HomeworkService homeworkService;

    public HomeworkController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<?>> createHomework(
            org.springframework.security.core.Authentication authentication,
            @Valid @RequestBody CreateHomeworkRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Homework created", homeworkService.createHomework(authentication.getName(), request)));
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','PARENT')")
    public ResponseEntity<ApiResponse<?>> getClassHomework(
            @PathVariable String classId,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            org.springframework.security.core.Authentication authentication
    ) {
        boolean isParent = authentication.getAuthorities().stream().anyMatch(item -> item.toString().equals("ROLE_PARENT"));
        if (isParent && !homeworkService.parentHasChildInClass(authentication.getName(), classId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view homework for your linked child's class");
        }
        return ResponseEntity.ok(ApiResponse.ok("Class homework loaded", homeworkService.getClassHomework(classId, date, from, to)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<?>> myHomework(org.springframework.security.core.Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok("Teacher homework loaded", homeworkService.getTeacherHomework(authentication.getName())));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<?>> updateHomework(
            @PathVariable String id,
            org.springframework.security.core.Authentication authentication,
            @Valid @RequestBody UpdateHomeworkRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Homework updated", homeworkService.updateHomework(authentication.getName(), id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteHomework(
            @PathVariable String id,
            org.springframework.security.core.Authentication authentication
    ) {
        homeworkService.deleteHomework(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.ok("Homework deleted", Map.of("id", id)));
    }
}

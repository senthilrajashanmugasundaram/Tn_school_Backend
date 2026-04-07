package com.tnschool.sms.modules.staff.controller;

import com.tnschool.sms.modules.staff.dto.CreateStaffRequest;
import com.tnschool.sms.modules.staff.dto.UpdateStaffRequest;
import com.tnschool.sms.modules.staff.service.StaffService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getStaff(@RequestParam(required = false) String type) {
        return ResponseEntity.ok(ApiResponse.ok("Staff loaded", staffService.getStaff(type)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Staff created", staffService.createStaff(request)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateStaff(@PathVariable String id, @Valid @RequestBody UpdateStaffRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Staff updated", staffService.updateStaff(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteStaff(@PathVariable String id) {
        staffService.deleteStaff(id);
        return ResponseEntity.ok(ApiResponse.ok("Staff deleted", Map.of("id", id)));
    }
}

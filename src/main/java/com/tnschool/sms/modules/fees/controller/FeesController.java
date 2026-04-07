package com.tnschool.sms.modules.fees.controller;

import com.tnschool.sms.modules.fees.dto.CreateFeeStructureRequest;
import com.tnschool.sms.modules.fees.dto.RecordFeePaymentRequest;
import com.tnschool.sms.modules.fees.service.FeesService;
import com.tnschool.sms.modules.students.service.StudentService;
import com.tnschool.sms.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/fees")
public class FeesController {

    private final FeesService feesService;
    private final StudentService studentService;

    public FeesController(FeesService feesService, StudentService studentService) {
        this.feesService = feesService;
        this.studentService = studentService;
    }

    @GetMapping("/structures")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getStructures(
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) String termId
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Fee structures loaded", feesService.getStructures(classId, termId)));
    }

    @PostMapping("/structures")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createStructure(@Valid @RequestBody CreateFeeStructureRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Fee structure created", feesService.createStructure(request)));
    }

    @PostMapping("/payments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> recordPayment(@Valid @RequestBody RecordFeePaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Fee payment recorded", feesService.recordPayment(request)));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','PARENT')")
    public ResponseEntity<ApiResponse<?>> getStudentFees(
            @PathVariable String studentId,
            org.springframework.security.core.Authentication authentication
    ) {
        boolean isParent = authentication.getAuthorities().stream().anyMatch(item -> item.toString().equals("ROLE_PARENT"));
        if (isParent && !studentService.getStudent(studentId).parentUserId().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view fees for your linked child");
        }
        return ResponseEntity.ok(ApiResponse.ok("Student fee summary loaded", feesService.getStudentFees(studentId)));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getPendingFees() {
        return ResponseEntity.ok(ApiResponse.ok("Pending fees loaded", feesService.getPendingFees()));
    }
}

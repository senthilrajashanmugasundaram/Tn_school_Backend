package com.tnschool.sms.modules.transport.controller;

import com.tnschool.sms.modules.transport.dto.CreateTransportVanRequest;
import com.tnschool.sms.modules.transport.dto.UpdateTransportVanRequest;
import com.tnschool.sms.modules.transport.service.TransportService;
import com.tnschool.sms.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transport")
public class TransportController {

    private final TransportService transportService;

    public TransportController(TransportService transportService) {
        this.transportService = transportService;
    }

    @GetMapping("/vans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getVans() {
        return ResponseEntity.ok(ApiResponse.ok("Transport vans loaded", transportService.getVans()));
    }

    @PostMapping("/vans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createVan(@Valid @RequestBody CreateTransportVanRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Transport van created", transportService.createVan(request)));
    }

    @PatchMapping("/vans/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateVan(@PathVariable String id, @Valid @RequestBody UpdateTransportVanRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Transport van updated", transportService.updateVan(id, request)));
    }
}

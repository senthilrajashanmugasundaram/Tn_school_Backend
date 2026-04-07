package com.tnschool.sms.modules.cms.controller;

import com.tnschool.sms.modules.cms.dto.CreateCmsContentRequest;
import com.tnschool.sms.modules.cms.dto.UpdateCmsContentRequest;
import com.tnschool.sms.modules.cms.service.CmsService;
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
@RequestMapping("/api/cms")
public class CmsController {

    private final CmsService cmsService;

    public CmsController(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getPublished(@RequestParam(required = false) String type) {
        return ResponseEntity.ok(ApiResponse.ok("Published CMS content loaded", cmsService.getPublished(type)));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getAllAdmin() {
        return ResponseEntity.ok(ApiResponse.ok("Admin CMS content loaded", cmsService.getAllAdmin()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createContent(@Valid @RequestBody CreateCmsContentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("CMS content created", cmsService.createContent(request)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateContent(@PathVariable String id, @Valid @RequestBody UpdateCmsContentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("CMS content updated", cmsService.updateContent(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteContent(@PathVariable String id) {
        cmsService.deleteContent(id);
        return ResponseEntity.ok(ApiResponse.ok("CMS content deleted", Map.of("id", id)));
    }
}

package com.tnschool.sms.modules.notifications.controller;

import com.tnschool.sms.modules.notifications.dto.BroadcastHolidayRequest;
import com.tnschool.sms.modules.notifications.service.NotificationSchedulerService;
import com.tnschool.sms.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/scheduler")
public class NotificationsController {

    private final NotificationSchedulerService notificationSchedulerService;

    public NotificationsController(NotificationSchedulerService notificationSchedulerService) {
        this.notificationSchedulerService = notificationSchedulerService;
    }

    @PostMapping("/trigger/homework-sms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> triggerHomeworkSms(@RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.ok("Homework SMS generated", notificationSchedulerService.triggerHomeworkSms(date)));
    }

    @PostMapping("/trigger/fee-reminders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> triggerFeeReminders() {
        return ResponseEntity.ok(ApiResponse.ok("Fee reminder scheduler checked", notificationSchedulerService.triggerFeeReminders()));
    }

    @PostMapping("/broadcast/holiday")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> broadcastHoliday(@Valid @RequestBody BroadcastHolidayRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Holiday announcement queued", notificationSchedulerService.broadcastHoliday(request.message())));
    }
}

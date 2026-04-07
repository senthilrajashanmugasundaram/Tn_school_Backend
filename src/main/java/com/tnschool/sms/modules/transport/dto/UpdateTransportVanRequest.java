package com.tnschool.sms.modules.transport.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTransportVanRequest(
        @NotBlank(message = "Vehicle number is required") String vehicleNo,
        String model,
        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1")
        Integer capacity,
        @NotBlank(message = "Route name is required") String routeName,
        String routeDetails,
        String driverStaffId,
        boolean isActive
) {
}

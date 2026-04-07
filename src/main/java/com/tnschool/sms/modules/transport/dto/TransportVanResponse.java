package com.tnschool.sms.modules.transport.dto;

public record TransportVanResponse(
        String id,
        String vehicleNo,
        String model,
        Integer capacity,
        String routeName,
        String routeDetails,
        String driverStaffId,
        boolean isActive,
        DriverSummary driver
) {
    public record DriverSummary(String name, String phone) {}
}

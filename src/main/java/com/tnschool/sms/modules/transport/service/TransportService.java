package com.tnschool.sms.modules.transport.service;

import com.tnschool.sms.modules.staff.model.StaffEntity;
import com.tnschool.sms.modules.staff.repository.StaffRepository;
import com.tnschool.sms.modules.transport.dto.CreateTransportVanRequest;
import com.tnschool.sms.modules.transport.dto.TransportVanResponse;
import com.tnschool.sms.modules.transport.dto.UpdateTransportVanRequest;
import com.tnschool.sms.modules.transport.model.TransportVanEntity;
import com.tnschool.sms.modules.transport.repository.TransportVanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransportService {

    private final TransportVanRepository transportVanRepository;
    private final StaffRepository staffRepository;

    public TransportService(TransportVanRepository transportVanRepository, StaffRepository staffRepository) {
        this.transportVanRepository = transportVanRepository;
        this.staffRepository = staffRepository;
    }

    @Transactional(readOnly = true)
    public List<TransportVanResponse> getVans() {
        return transportVanRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TransportVanResponse createVan(CreateTransportVanRequest request) {
        TransportVanEntity van = new TransportVanEntity();
        apply(van, request.vehicleNo(), request.model(), request.capacity(), request.routeName(), request.routeDetails(), request.driverStaffId(), request.isActive());
        return toResponse(transportVanRepository.save(van));
    }

    @Transactional
    public TransportVanResponse updateVan(String id, UpdateTransportVanRequest request) {
        TransportVanEntity van = transportVanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transport van not found"));
        apply(van, request.vehicleNo(), request.model(), request.capacity(), request.routeName(), request.routeDetails(), request.driverStaffId(), request.isActive());
        return toResponse(van);
    }

    private void apply(
            TransportVanEntity van,
            String vehicleNo,
            String model,
            Integer capacity,
            String routeName,
            String routeDetails,
            String driverStaffId,
            boolean active
    ) {
        van.setVehicleNo(vehicleNo);
        van.setModel(model);
        van.setCapacity(capacity);
        van.setRouteName(routeName);
        van.setRouteDetails(routeDetails);
        StaffEntity driver = driverStaffId == null || driverStaffId.isBlank()
                ? null
                : staffRepository.findById(driverStaffId).orElseThrow(() -> new IllegalArgumentException("Driver staff not found"));
        van.setDriver(driver);
        van.setActive(active);
    }

    private TransportVanResponse toResponse(TransportVanEntity entity) {
        return new TransportVanResponse(
                entity.getId(),
                entity.getVehicleNo(),
                entity.getModel(),
                entity.getCapacity(),
                entity.getRouteName(),
                entity.getRouteDetails(),
                entity.getDriver() == null ? null : entity.getDriver().getId(),
                entity.isActive(),
                entity.getDriver() == null
                        ? null
                        : new TransportVanResponse.DriverSummary(entity.getDriver().getUser().getName(), entity.getDriver().getUser().getPhone())
        );
    }
}

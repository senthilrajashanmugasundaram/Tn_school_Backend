package com.tnschool.sms.modules.transport.model;

import com.tnschool.sms.modules.staff.model.StaffEntity;
import com.tnschool.sms.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transport_vans")
public class TransportVanEntity extends BaseEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String vehicleNo;

    private String model;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private String routeName;

    private String routeDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_staff_id")
    private StaffEntity driver;

    @Column(nullable = false)
    private boolean active = true;

    @Override
    protected void assignIdIfMissing() {
        if (id == null || id.isBlank()) {
            id = newId();
        }
    }

    public String getId() {
        return id;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteDetails() {
        return routeDetails;
    }

    public void setRouteDetails(String routeDetails) {
        this.routeDetails = routeDetails;
    }

    public StaffEntity getDriver() {
        return driver;
    }

    public void setDriver(StaffEntity driver) {
        this.driver = driver;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

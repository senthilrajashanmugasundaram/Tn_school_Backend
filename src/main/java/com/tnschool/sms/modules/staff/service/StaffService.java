package com.tnschool.sms.modules.staff.service;

import com.tnschool.sms.auth.repository.UserRepository;
import com.tnschool.sms.modules.staff.dto.CreateStaffRequest;
import com.tnschool.sms.modules.staff.dto.StaffResponse;
import com.tnschool.sms.modules.staff.dto.UpdateStaffRequest;
import com.tnschool.sms.modules.staff.model.StaffEntity;
import com.tnschool.sms.modules.staff.model.StaffType;
import com.tnschool.sms.modules.staff.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;

    public StaffService(StaffRepository staffRepository, UserRepository userRepository) {
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> getStaff(String type) {
        List<StaffEntity> items = type == null || type.isBlank()
                ? staffRepository.findAll()
                : staffRepository.findAllByStaffTypeOrderByEmployeeIdAsc(StaffType.valueOf(type.toUpperCase()));
        return items.stream().map(this::toResponse).toList();
    }

    @Transactional
    public StaffResponse createStaff(CreateStaffRequest request) {
        StaffEntity staff = new StaffEntity();
        staff.setUser(userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
        staff.setStaffType(request.staffType());
        staff.setEmployeeId(request.employeeId());
        staff.setDesignation(request.designation());
        staff.setDepartment(request.department());
        staff.setJoinDate(request.joinDate());
        return toResponse(staffRepository.save(staff));
    }

    @Transactional
    public StaffResponse updateStaff(String id, UpdateStaffRequest request) {
        StaffEntity staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found"));
        staff.setUser(userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
        staff.setStaffType(request.staffType());
        staff.setEmployeeId(request.employeeId());
        staff.setDesignation(request.designation());
        staff.setDepartment(request.department());
        staff.setJoinDate(request.joinDate());
        return toResponse(staff);
    }

    @Transactional
    public void deleteStaff(String id) {
        staffRepository.deleteById(id);
    }

    private StaffResponse toResponse(StaffEntity entity) {
        return new StaffResponse(
                entity.getId(),
                entity.getUser().getId(),
                entity.getStaffType().name(),
                entity.getEmployeeId(),
                entity.getDesignation(),
                entity.getDepartment(),
                entity.getJoinDate().toString(),
                new StaffResponse.UserSummary(
                        entity.getUser().getId(),
                        entity.getUser().getName(),
                        entity.getUser().getPhone(),
                        entity.getUser().getEmail(),
                        entity.getUser().isActive()
                )
        );
    }
}

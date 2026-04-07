package com.tnschool.sms.modules.staff.repository;

import com.tnschool.sms.modules.staff.model.StaffEntity;
import com.tnschool.sms.modules.staff.model.StaffType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffRepository extends JpaRepository<StaffEntity, String> {
    List<StaffEntity> findAllByStaffTypeOrderByEmployeeIdAsc(StaffType staffType);
}

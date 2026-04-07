package com.tnschool.sms.modules.academic.repository;

import com.tnschool.sms.modules.academic.model.TimetableSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimetableSlotRepository extends JpaRepository<TimetableSlotEntity, String> {
    List<TimetableSlotEntity> findAllBySchoolClass_IdOrderByDayAscPeriodNoAsc(String classId);
    List<TimetableSlotEntity> findAllByTeacher_IdOrderByDayAscPeriodNoAsc(String teacherId);
}

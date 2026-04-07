package com.tnschool.sms.modules.attendance.repository;

import com.tnschool.sms.modules.attendance.model.AttendanceRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecordEntity, String> {
    List<AttendanceRecordEntity> findAllBySchoolClass_IdAndAttendanceDateOrderByStudent_NameAsc(String classId, LocalDate attendanceDate);
    List<AttendanceRecordEntity> findAllByStudent_IdOrderByAttendanceDateDesc(String studentId);
    Optional<AttendanceRecordEntity> findByStudent_IdAndAttendanceDate(String studentId, LocalDate attendanceDate);
}

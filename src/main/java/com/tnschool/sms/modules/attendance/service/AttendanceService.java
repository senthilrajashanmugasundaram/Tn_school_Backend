package com.tnschool.sms.modules.attendance.service;

import com.tnschool.sms.auth.model.UserEntity;
import com.tnschool.sms.auth.repository.UserRepository;
import com.tnschool.sms.modules.academic.model.SchoolClassEntity;
import com.tnschool.sms.modules.academic.repository.SchoolClassRepository;
import com.tnschool.sms.modules.attendance.dto.AttendanceRecordResponse;
import com.tnschool.sms.modules.attendance.dto.AttendanceSummaryResponse;
import com.tnschool.sms.modules.attendance.dto.MarkAttendanceRequest;
import com.tnschool.sms.modules.attendance.dto.StudentAttendanceResponse;
import com.tnschool.sms.modules.attendance.model.AttendanceRecordEntity;
import com.tnschool.sms.modules.attendance.model.AttendanceStatus;
import com.tnschool.sms.modules.attendance.repository.AttendanceRecordRepository;
import com.tnschool.sms.modules.students.model.StudentEntity;
import com.tnschool.sms.modules.students.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final UserRepository userRepository;

    public AttendanceService(
            AttendanceRecordRepository attendanceRecordRepository,
            StudentRepository studentRepository,
            SchoolClassRepository schoolClassRepository,
            UserRepository userRepository
    ) {
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.studentRepository = studentRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void markAttendance(String markedByUserId, MarkAttendanceRequest request) {
        SchoolClassEntity schoolClass = schoolClassRepository.findById(request.classId())
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));
        UserEntity markedBy = userRepository.findById(markedByUserId)
                .orElseThrow(() -> new IllegalArgumentException("Marked by user not found"));

        for (MarkAttendanceRequest.AttendanceLine line : request.records()) {
            StudentEntity student = studentRepository.findById(line.studentId())
                    .orElseThrow(() -> new IllegalArgumentException("Student not found: " + line.studentId()));

            AttendanceRecordEntity record = attendanceRecordRepository
                    .findByStudent_IdAndAttendanceDate(student.getId(), request.date())
                    .orElseGet(AttendanceRecordEntity::new);

            record.setStudent(student);
            record.setSchoolClass(schoolClass);
            record.setAttendanceDate(request.date());
            record.setStatus(AttendanceStatus.valueOf(line.status().toUpperCase()));
            record.setRemarks(line.remarks());
            record.setMarkedBy(markedBy);
            attendanceRecordRepository.save(record);
        }
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getClassAttendance(String classId, LocalDate date) {
        LocalDate targetDate = date == null ? LocalDate.now() : date;
        return attendanceRecordRepository.findAllBySchoolClass_IdAndAttendanceDateOrderByStudent_NameAsc(classId, targetDate).stream()
                .map(this::toRecordResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentAttendanceResponse getStudentAttendance(String studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        List<AttendanceRecordResponse> records = attendanceRecordRepository.findAllByStudent_IdOrderByAttendanceDateDesc(studentId).stream()
                .map(this::toRecordResponse)
                .toList();
        return new StudentAttendanceResponse(records, summarize(records));
    }

    @Transactional(readOnly = true)
    public String getParentUserIdForStudent(String studentId) {
        return studentRepository.findById(studentId)
                .map(student -> student.getParentUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
    }

    private AttendanceRecordResponse toRecordResponse(AttendanceRecordEntity entity) {
        return new AttendanceRecordResponse(
                entity.getId(),
                entity.getStudent().getId(),
                entity.getSchoolClass().getId(),
                entity.getAttendanceDate().toString(),
                entity.getStatus().name(),
                entity.getRemarks(),
                new AttendanceRecordResponse.StudentSummary(
                        entity.getStudent().getId(),
                        entity.getStudent().getName(),
                        entity.getStudent().getAdmissionNo()
                ),
                entity.getMarkedBy() == null ? null : new AttendanceRecordResponse.MarkedBySummary(entity.getMarkedBy().getName())
        );
    }

    private AttendanceSummaryResponse summarize(List<AttendanceRecordResponse> records) {
        long total = records.size();
        long present = records.stream().filter(item -> item.status().equals("PRESENT")).count();
        long absent = records.stream().filter(item -> item.status().equals("ABSENT")).count();
        long late = records.stream().filter(item -> item.status().equals("LATE")).count();
        long percentage = total == 0 ? 0 : Math.round((present * 100.0) / total);
        return new AttendanceSummaryResponse(total, present, absent, late, percentage);
    }
}

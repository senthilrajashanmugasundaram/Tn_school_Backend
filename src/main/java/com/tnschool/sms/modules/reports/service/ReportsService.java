package com.tnschool.sms.modules.reports.service;

import com.tnschool.sms.modules.academic.repository.SchoolClassRepository;
import com.tnschool.sms.modules.attendance.model.AttendanceStatus;
import com.tnschool.sms.modules.attendance.repository.AttendanceRecordRepository;
import com.tnschool.sms.modules.fees.dto.FeePaymentResponse;
import com.tnschool.sms.modules.fees.service.FeesService;
import com.tnschool.sms.modules.homework.dto.HomeworkResponse;
import com.tnschool.sms.modules.homework.service.HomeworkService;
import com.tnschool.sms.modules.marks.dto.MarkResponse;
import com.tnschool.sms.modules.marks.repository.MarkRepository;
import com.tnschool.sms.modules.marks.service.MarksService;
import com.tnschool.sms.modules.reports.dto.AdminSnapshotResponse;
import com.tnschool.sms.modules.reports.dto.AttendanceReportRowResponse;
import com.tnschool.sms.modules.reports.dto.MarksReportResponse;
import com.tnschool.sms.modules.reports.dto.ParentDashboardResponse;
import com.tnschool.sms.modules.staff.model.StaffType;
import com.tnschool.sms.modules.staff.repository.StaffRepository;
import com.tnschool.sms.modules.students.model.StudentEntity;
import com.tnschool.sms.modules.students.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportsService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final MarkRepository markRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final FeesService feesService;
    private final MarksService marksService;
    private final HomeworkService homeworkService;

    public ReportsService(
            AttendanceRecordRepository attendanceRecordRepository,
            MarkRepository markRepository,
            StudentRepository studentRepository,
            StaffRepository staffRepository,
            SchoolClassRepository schoolClassRepository,
            FeesService feesService,
            MarksService marksService,
            HomeworkService homeworkService
    ) {
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.markRepository = markRepository;
        this.studentRepository = studentRepository;
        this.staffRepository = staffRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.feesService = feesService;
        this.marksService = marksService;
        this.homeworkService = homeworkService;
    }

    @Transactional(readOnly = true)
    public List<AttendanceReportRowResponse> getAttendanceReport(String classId) {
        List<StudentEntity> students = studentRepository.findAllBySchoolClass_IdOrderByNameAsc(classId);
        return students.stream()
                .map(student -> {
                    var records = attendanceRecordRepository.findAllByStudent_IdOrderByAttendanceDateDesc(student.getId());
                    long present = records.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT).count();
                    long absent = records.stream().filter(r -> r.getStatus() == AttendanceStatus.ABSENT).count();
                    long late = records.stream().filter(r -> r.getStatus() == AttendanceStatus.LATE).count();
                    long total = records.size();
                    long percentage = total == 0 ? 0 : Math.round((present * 100.0) / total);
                    return new AttendanceReportRowResponse(
                            new AttendanceReportRowResponse.StudentSummary(student.getId(), student.getName(), student.getAdmissionNo()),
                            present,
                            absent,
                            late,
                            total,
                            percentage
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public MarksReportResponse getMarksReport(String examId, String classId) {
        var rows = markRepository.findAllByExam_IdAndStudent_SchoolClass_IdOrderByStudent_NameAscSubject_NameAsc(examId, classId);

        Map<String, List<Double>> subjectScores = new LinkedHashMap<>();
        Map<String, Double> subjectHigh = new LinkedHashMap<>();
        Map<String, Long> gradeDistribution = new LinkedHashMap<>();

        rows.forEach(mark -> {
            String subjectName = mark.getSubject().getName();
            subjectScores.computeIfAbsent(subjectName, key -> new java.util.ArrayList<>()).add(mark.getMarksObtained());
            subjectHigh.merge(subjectName, mark.getMarksObtained(), Math::max);
            gradeDistribution.merge(mark.getGrade() == null ? "NA" : mark.getGrade(), 1L, Long::sum);
        });

        List<MarksReportResponse.SubjectStat> stats = subjectScores.entrySet().stream()
                .map(entry -> new MarksReportResponse.SubjectStat(
                        entry.getKey(),
                        entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0),
                        subjectHigh.getOrDefault(entry.getKey(), 0.0)
                ))
                .sorted(Comparator.comparing(MarksReportResponse.SubjectStat::name))
                .toList();

        return new MarksReportResponse(stats, gradeDistribution);
    }

    @Transactional(readOnly = true)
    public ParentDashboardResponse getParentDashboard(String studentId) {
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        YearMonth currentMonth = YearMonth.now();
        LocalDate start = currentMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();

        var attendanceRecords = attendanceRecordRepository.findAllByStudent_IdOrderByAttendanceDateDesc(studentId).stream()
                .filter(record -> !record.getAttendanceDate().isBefore(start) && !record.getAttendanceDate().isAfter(end))
                .toList();

        long present = attendanceRecords.stream().filter(item -> item.getStatus() == AttendanceStatus.PRESENT).count();
        long total = attendanceRecords.size();
        long percentage = total == 0 ? 0 : Math.round((present * 100.0) / total);

        List<MarkResponse> recentMarks = marksService.getStudentMarks(studentId).marks().stream().limit(5).toList();
        List<FeePaymentResponse> pendingFees = feesService.getPendingFees().stream()
                .filter(item -> item.studentId().equals(studentId))
                .toList();
        List<HomeworkResponse> todaysHomework = homeworkService.getClassHomework(student.getSchoolClass().getId(), LocalDate.now(), null, null);

        return new ParentDashboardResponse(
                new ParentDashboardResponse.StudentSummary(student.getName(), student.getAdmissionNo()),
                new ParentDashboardResponse.AttendanceSummary(present, total, percentage),
                recentMarks,
                pendingFees,
                todaysHomework
        );
    }

    @Transactional(readOnly = true)
    public AdminSnapshotResponse getAdminSnapshot() {
        long totalStudents = studentRepository.count();
        long totalTeachers = staffRepository.findAll().stream().filter(item -> item.getStaffType() == StaffType.TEACHER).count();
        long totalClasses = schoolClassRepository.count();
        long pendingFeeCount = feesService.getPendingFees().size();

        Map<String, Long> todayAttendance = attendanceRecordRepository.findAll().stream()
                .filter(item -> item.getAttendanceDate().equals(LocalDate.now()))
                .collect(Collectors.groupingBy(item -> item.getStatus().name(), LinkedHashMap::new, Collectors.counting()));

        return new AdminSnapshotResponse(totalStudents, totalTeachers, totalClasses, pendingFeeCount, todayAttendance);
    }
}

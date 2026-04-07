package com.tnschool.sms.modules.notifications.service;

import com.tnschool.sms.modules.homework.dto.HomeworkSmsLogResponse;
import com.tnschool.sms.modules.homework.model.HomeworkEntity;
import com.tnschool.sms.modules.homework.model.HomeworkSmsLogEntity;
import com.tnschool.sms.modules.homework.repository.HomeworkRepository;
import com.tnschool.sms.modules.homework.repository.HomeworkSmsLogRepository;
import com.tnschool.sms.modules.fees.dto.FeePaymentResponse;
import com.tnschool.sms.modules.fees.service.FeesService;
import com.tnschool.sms.modules.students.model.StudentEntity;
import com.tnschool.sms.modules.students.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationSchedulerService {

    private final HomeworkRepository homeworkRepository;
    private final HomeworkSmsLogRepository homeworkSmsLogRepository;
    private final StudentRepository studentRepository;
    private final FeesService feesService;

    public NotificationSchedulerService(
            HomeworkRepository homeworkRepository,
            HomeworkSmsLogRepository homeworkSmsLogRepository,
            StudentRepository studentRepository,
            FeesService feesService
    ) {
        this.homeworkRepository = homeworkRepository;
        this.homeworkSmsLogRepository = homeworkSmsLogRepository;
        this.studentRepository = studentRepository;
        this.feesService = feesService;
    }

    @Transactional
    public List<HomeworkSmsLogResponse> triggerHomeworkSms(LocalDate date) {
        LocalDate targetDate = date == null ? LocalDate.now() : date;
        List<HomeworkEntity> homeworkItems = homeworkRepository.findAllByHomeworkDateOrderBySchoolClass_GradeAscSchoolClass_SectionAscSubject_NameAsc(targetDate);

        Map<String, List<HomeworkEntity>> byClass = homeworkItems.stream()
                .collect(Collectors.groupingBy(item -> item.getSchoolClass().getId()));

        for (HomeworkEntity item : homeworkItems) {
            item.setSmsSent(true);
        }

        for (Map.Entry<String, List<HomeworkEntity>> entry : byClass.entrySet()) {
            List<StudentEntity> students = studentRepository.findAllBySchoolClass_IdOrderByNameAsc(entry.getKey());
            for (StudentEntity student : students) {
                String message = buildHomeworkSms(student, entry.getValue());
                HomeworkSmsLogEntity log = homeworkSmsLogRepository
                        .findByStudent_IdAndHomeworkDate(student.getId(), targetDate)
                        .orElseGet(HomeworkSmsLogEntity::new);
                log.setStudent(student);
                log.setHomeworkDate(targetDate);
                log.setSmsBody(message);
                log.setSentAt(OffsetDateTime.now());
                log.setProviderStatus("GENERATED");
                homeworkSmsLogRepository.save(log);
            }
        }

        return homeworkSmsLogRepository.findAllByHomeworkDateOrderByStudent_NameAsc(targetDate).stream()
                .map(log -> new HomeworkSmsLogResponse(
                        log.getId(),
                        log.getStudent().getId(),
                        log.getStudent().getParentUser().getPhone(),
                        log.getHomeworkDate().toString(),
                        log.getSmsBody(),
                        log.getProviderStatus(),
                        log.getSentAt() == null ? null : log.getSentAt().toString()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, String>> triggerFeeReminders() {
        List<FeePaymentResponse> pending = feesService.getPendingFees();
        return pending.stream()
                .map(item -> Map.of(
                        "studentId", item.studentId(),
                        "parentPhone", item.student().parent().phone(),
                        "message", buildFeeReminderMessage(item)
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, String> broadcastHoliday(String message) {
        return Map.of("status", "Holiday announcement scaffold ready", "message", message);
    }

    private String buildHomeworkSms(StudentEntity student, List<HomeworkEntity> homeworkItems) {
        String header = "%s (Class %s-%s) Homework:".formatted(
                student.getName(),
                student.getSchoolClass().getGrade(),
                student.getSchoolClass().getSection()
        );

        String lines = homeworkItems.stream()
                .map(item -> "%s: %s".formatted(item.getSubject().getName(), item.getDescription()))
                .collect(Collectors.joining("\n"));

        return header + "\n" + lines;
    }

    private String buildFeeReminderMessage(FeePaymentResponse item) {
        return "Fee Reminder: %s (%s-%s) has pending %s fee. Paid: %.2f of %.2f. Contact school office for support."
                .formatted(
                        item.student().name(),
                        item.student().schoolClass().grade(),
                        item.student().schoolClass().section(),
                        item.feeStructure().feeType(),
                        item.paidAmount(),
                        item.feeStructure().amount()
                );
    }
}

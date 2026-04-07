package com.tnschool.sms.modules.homework.repository;

import com.tnschool.sms.modules.homework.model.HomeworkSmsLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HomeworkSmsLogRepository extends JpaRepository<HomeworkSmsLogEntity, String> {
    Optional<HomeworkSmsLogEntity> findByStudent_IdAndHomeworkDate(String studentId, LocalDate homeworkDate);
    List<HomeworkSmsLogEntity> findAllByHomeworkDateOrderByStudent_NameAsc(LocalDate homeworkDate);
}

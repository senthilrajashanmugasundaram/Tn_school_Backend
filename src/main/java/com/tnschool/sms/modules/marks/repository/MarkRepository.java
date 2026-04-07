package com.tnschool.sms.modules.marks.repository;

import com.tnschool.sms.modules.marks.model.MarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarkRepository extends JpaRepository<MarkEntity, String> {
    List<MarkEntity> findAllByStudent_IdOrderByExam_ExamDateDesc(String studentId);
    List<MarkEntity> findAllByExam_IdAndStudent_SchoolClass_IdOrderByStudent_NameAscSubject_NameAsc(String examId, String classId);
    Optional<MarkEntity> findByExam_IdAndStudent_IdAndSubject_Id(String examId, String studentId, String subjectId);
}

package com.tnschool.sms.modules.marks.repository;

import com.tnschool.sms.modules.marks.model.ExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<ExamEntity, String> {
    List<ExamEntity> findAllByAcademicTerm_IdOrderByExamDateAsc(String academicTermId);
}

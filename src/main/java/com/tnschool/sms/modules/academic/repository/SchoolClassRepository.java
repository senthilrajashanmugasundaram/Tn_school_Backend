package com.tnschool.sms.modules.academic.repository;

import com.tnschool.sms.modules.academic.model.SchoolClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchoolClassRepository extends JpaRepository<SchoolClassEntity, String> {
    List<SchoolClassEntity> findAllByAcademicTerm_IdOrderByGradeAscSectionAsc(String academicTermId);
}

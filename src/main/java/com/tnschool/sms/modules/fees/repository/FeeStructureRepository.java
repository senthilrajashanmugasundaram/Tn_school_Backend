package com.tnschool.sms.modules.fees.repository;

import com.tnschool.sms.modules.fees.model.FeeStructureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeStructureRepository extends JpaRepository<FeeStructureEntity, String> {
    List<FeeStructureEntity> findAllBySchoolClass_IdOrderByFeeTypeAsc(String classId);
    List<FeeStructureEntity> findAllBySchoolClass_IdAndAcademicTerm_IdOrderByFeeTypeAsc(String classId, String termId);
}

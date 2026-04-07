package com.tnschool.sms.modules.activities.repository;

import com.tnschool.sms.modules.activities.model.ActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<ActivityEntity, String> {
    List<ActivityEntity> findAllByAcademicTerm_IdOrderByActivityDateDescTitleAsc(String academicTermId);
    List<ActivityEntity> findAllByCategoryIgnoreCaseOrderByActivityDateDescTitleAsc(String category);
}

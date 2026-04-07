package com.tnschool.sms.modules.academic.repository;

import com.tnschool.sms.modules.academic.model.TeacherMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherMappingRepository extends JpaRepository<TeacherMappingEntity, String> {
    List<TeacherMappingEntity> findAllByTeacher_IdOrderBySchoolClass_GradeAscSchoolClass_SectionAsc(String teacherId);
}

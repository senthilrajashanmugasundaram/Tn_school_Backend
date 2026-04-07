package com.tnschool.sms.modules.students.repository;

import com.tnschool.sms.modules.students.model.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<StudentEntity, String> {
    List<StudentEntity> findAllBySchoolClass_IdOrderByNameAsc(String classId);
    List<StudentEntity> findAllByParentUser_IdOrderByNameAsc(String parentUserId);
    boolean existsByParentUser_IdAndSchoolClass_Id(String parentUserId, String classId);
}

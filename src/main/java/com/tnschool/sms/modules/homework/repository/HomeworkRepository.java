package com.tnschool.sms.modules.homework.repository;

import com.tnschool.sms.modules.homework.model.HomeworkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HomeworkRepository extends JpaRepository<HomeworkEntity, String> {
    List<HomeworkEntity> findAllBySchoolClass_IdOrderByHomeworkDateDescSubject_NameAsc(String classId);
    List<HomeworkEntity> findAllBySchoolClass_IdAndHomeworkDateOrderBySubject_NameAsc(String classId, LocalDate homeworkDate);
    List<HomeworkEntity> findAllBySchoolClass_IdAndHomeworkDateBetweenOrderByHomeworkDateDescSubject_NameAsc(String classId, LocalDate from, LocalDate to);
    List<HomeworkEntity> findAllByTeacherUser_IdOrderByHomeworkDateDesc(String teacherUserId);
    List<HomeworkEntity> findAllByHomeworkDateOrderBySchoolClass_GradeAscSchoolClass_SectionAscSubject_NameAsc(LocalDate homeworkDate);
}

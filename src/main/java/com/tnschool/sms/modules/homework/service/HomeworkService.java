package com.tnschool.sms.modules.homework.service;

import com.tnschool.sms.auth.model.Role;
import com.tnschool.sms.auth.model.UserEntity;
import com.tnschool.sms.auth.repository.UserRepository;
import com.tnschool.sms.modules.academic.model.SchoolClassEntity;
import com.tnschool.sms.modules.academic.model.SubjectEntity;
import com.tnschool.sms.modules.academic.model.TeacherMappingEntity;
import com.tnschool.sms.modules.academic.repository.SchoolClassRepository;
import com.tnschool.sms.modules.academic.repository.SubjectRepository;
import com.tnschool.sms.modules.academic.repository.TeacherMappingRepository;
import com.tnschool.sms.modules.homework.dto.CreateHomeworkRequest;
import com.tnschool.sms.modules.homework.dto.HomeworkResponse;
import com.tnschool.sms.modules.homework.dto.UpdateHomeworkRequest;
import com.tnschool.sms.modules.homework.model.HomeworkEntity;
import com.tnschool.sms.modules.homework.repository.HomeworkRepository;
import com.tnschool.sms.modules.students.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final TeacherMappingRepository teacherMappingRepository;
    private final StudentRepository studentRepository;

    public HomeworkService(
            HomeworkRepository homeworkRepository,
            SchoolClassRepository schoolClassRepository,
            SubjectRepository subjectRepository,
            UserRepository userRepository,
            TeacherMappingRepository teacherMappingRepository,
            StudentRepository studentRepository
    ) {
        this.homeworkRepository = homeworkRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.teacherMappingRepository = teacherMappingRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public HomeworkResponse createHomework(String actorUserId, CreateHomeworkRequest request) {
        UserEntity actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        SchoolClassEntity schoolClass = schoolClassRepository.findById(request.classId())
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));
        SubjectEntity subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));

        if (actor.getRole() == Role.TEACHER) {
            boolean allowed = teacherMappingRepository.findAllByTeacher_IdOrderBySchoolClass_GradeAscSchoolClass_SectionAsc(actorUserId).stream()
                    .anyMatch(mapping ->
                            mapping.getSchoolClass().getId().equals(schoolClass.getId()) &&
                            mapping.getSubject().getId().equals(subject.getId()));
            if (!allowed) {
                throw new IllegalArgumentException("Teacher is not mapped to this class and subject");
            }
        }

        HomeworkEntity homework = new HomeworkEntity();
        homework.setSchoolClass(schoolClass);
        homework.setSubject(subject);
        homework.setTeacherUser(actor);
        homework.setHomeworkDate(request.homeworkDate());
        homework.setDescription(request.description());
        homework.setSmsSent(false);
        return toResponse(homeworkRepository.save(homework));
    }

    @Transactional(readOnly = true)
    public List<HomeworkResponse> getClassHomework(String classId, LocalDate date, LocalDate from, LocalDate to) {
        List<HomeworkEntity> items;
        if (date != null) {
            items = homeworkRepository.findAllBySchoolClass_IdAndHomeworkDateOrderBySubject_NameAsc(classId, date);
        } else if (from != null && to != null) {
            items = homeworkRepository.findAllBySchoolClass_IdAndHomeworkDateBetweenOrderByHomeworkDateDescSubject_NameAsc(classId, from, to);
        } else {
            items = homeworkRepository.findAllBySchoolClass_IdOrderByHomeworkDateDescSubject_NameAsc(classId);
        }
        return items.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<HomeworkResponse> getTeacherHomework(String teacherUserId) {
        return homeworkRepository.findAllByTeacherUser_IdOrderByHomeworkDateDesc(teacherUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public HomeworkResponse updateHomework(String actorUserId, String homeworkId, UpdateHomeworkRequest request) {
        HomeworkEntity homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new IllegalArgumentException("Homework not found"));
        assertEditable(actorUserId, homework);
        homework.setDescription(request.description());
        homework.setSmsSent(false);
        return toResponse(homework);
    }

    @Transactional
    public void deleteHomework(String actorUserId, String homeworkId) {
        HomeworkEntity homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new IllegalArgumentException("Homework not found"));
        assertEditable(actorUserId, homework);
        homeworkRepository.delete(homework);
    }

    @Transactional(readOnly = true)
    public boolean parentHasChildInClass(String parentUserId, String classId) {
        return studentRepository.existsByParentUser_IdAndSchoolClass_Id(parentUserId, classId);
    }

    private void assertEditable(String actorUserId, HomeworkEntity homework) {
        UserEntity actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (actor.getRole() == Role.ADMIN) {
            return;
        }
        if (actor.getRole() == Role.TEACHER && homework.getTeacherUser().getId().equals(actorUserId)) {
            return;
        }
        throw new IllegalArgumentException("You cannot modify this homework");
    }

    private HomeworkResponse toResponse(HomeworkEntity entity) {
        return new HomeworkResponse(
                entity.getId(),
                entity.getSchoolClass().getId(),
                entity.getSubject().getId(),
                entity.getTeacherUser().getId(),
                entity.getHomeworkDate().toString(),
                entity.getDescription(),
                entity.isSmsSent(),
                new HomeworkResponse.SubjectSummary(entity.getSubject().getName(), entity.getSubject().getCode()),
                new HomeworkResponse.TeacherSummary(entity.getTeacherUser().getName()),
                new HomeworkResponse.ClassSummary(entity.getSchoolClass().getGrade(), entity.getSchoolClass().getSection())
        );
    }
}

package com.tnschool.sms.modules.students.service;

import com.tnschool.sms.auth.model.Role;
import com.tnschool.sms.auth.model.UserEntity;
import com.tnschool.sms.auth.repository.UserRepository;
import com.tnschool.sms.modules.academic.model.SchoolClassEntity;
import com.tnschool.sms.modules.academic.repository.SchoolClassRepository;
import com.tnschool.sms.modules.students.dto.CreateStudentRequest;
import com.tnschool.sms.modules.students.dto.StudentResponse;
import com.tnschool.sms.modules.students.model.StudentEntity;
import com.tnschool.sms.modules.students.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final UserRepository userRepository;

    public StudentService(
            StudentRepository studentRepository,
            SchoolClassRepository schoolClassRepository,
            UserRepository userRepository
    ) {
        this.studentRepository = studentRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getStudents(String classId) {
        List<StudentEntity> students = classId == null || classId.isBlank()
                ? studentRepository.findAll()
                : studentRepository.findAllBySchoolClass_IdOrderByNameAsc(classId);
        return students.stream()
                .sorted(Comparator.comparing(StudentEntity::getName))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudent(String id) {
        return studentRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getChildrenOfParent(String parentUserId) {
        return studentRepository.findAllByParentUser_IdOrderByNameAsc(parentUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        SchoolClassEntity schoolClass = schoolClassRepository.findById(request.classId())
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));
        UserEntity parent = userRepository.findById(request.parentUserId())
                .filter(user -> user.getRole() == Role.PARENT)
                .orElseThrow(() -> new IllegalArgumentException("Parent user not found"));

        StudentEntity student = new StudentEntity();
        student.setName(request.name());
        student.setAdmissionNo(request.admissionNo());
        student.setSchoolClass(schoolClass);
        student.setParentUser(parent);
        student.setGender(request.gender());
        student.setDateOfBirth(request.dateOfBirth());
        student.setBloodGroup(request.bloodGroup());
        student.setActive(true);
        return toResponse(studentRepository.save(student));
    }

    private StudentResponse toResponse(StudentEntity entity) {
        return new StudentResponse(
                entity.getId(),
                entity.getName(),
                entity.getAdmissionNo(),
                entity.getSchoolClass().getId(),
                entity.getParentUser().getId(),
                entity.getGender(),
                entity.getDateOfBirth() == null ? null : entity.getDateOfBirth().toString(),
                entity.getBloodGroup(),
                entity.isActive(),
                new StudentResponse.ClassSummary(entity.getSchoolClass().getGrade(), entity.getSchoolClass().getSection()),
                new StudentResponse.ParentSummary(entity.getParentUser().getId(), entity.getParentUser().getName(), entity.getParentUser().getPhone())
        );
    }
}

package com.tnschool.sms.modules.academic.service;

import com.tnschool.sms.auth.model.Role;
import com.tnschool.sms.auth.model.UserEntity;
import com.tnschool.sms.auth.repository.UserRepository;
import com.tnschool.sms.modules.academic.dto.AcademicTermDto;
import com.tnschool.sms.modules.academic.dto.ClassResponse;
import com.tnschool.sms.modules.academic.dto.CreateAcademicTermRequest;
import com.tnschool.sms.modules.academic.dto.CreateClassRequest;
import com.tnschool.sms.modules.academic.dto.CreateSubjectRequest;
import com.tnschool.sms.modules.academic.dto.CreateTeacherMappingRequest;
import com.tnschool.sms.modules.academic.dto.CreateTimetableSlotRequest;
import com.tnschool.sms.modules.academic.dto.SubjectResponse;
import com.tnschool.sms.modules.academic.dto.TeacherMappingResponse;
import com.tnschool.sms.modules.academic.dto.TimetableSlotResponse;
import com.tnschool.sms.modules.academic.model.AcademicTermEntity;
import com.tnschool.sms.modules.academic.model.SchoolClassEntity;
import com.tnschool.sms.modules.academic.model.SubjectEntity;
import com.tnschool.sms.modules.academic.model.TeacherMappingEntity;
import com.tnschool.sms.modules.academic.model.TimetableSlotEntity;
import com.tnschool.sms.modules.academic.repository.AcademicTermRepository;
import com.tnschool.sms.modules.academic.repository.SchoolClassRepository;
import com.tnschool.sms.modules.academic.repository.SubjectRepository;
import com.tnschool.sms.modules.academic.repository.TeacherMappingRepository;
import com.tnschool.sms.modules.academic.repository.TimetableSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AcademicService {

    private final AcademicTermRepository academicTermRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherMappingRepository teacherMappingRepository;
    private final TimetableSlotRepository timetableSlotRepository;
    private final UserRepository userRepository;

    public AcademicService(
            AcademicTermRepository academicTermRepository,
            SchoolClassRepository schoolClassRepository,
            SubjectRepository subjectRepository,
            TeacherMappingRepository teacherMappingRepository,
            TimetableSlotRepository timetableSlotRepository,
            UserRepository userRepository
    ) {
        this.academicTermRepository = academicTermRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.subjectRepository = subjectRepository;
        this.teacherMappingRepository = teacherMappingRepository;
        this.timetableSlotRepository = timetableSlotRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<AcademicTermDto> getTerms() {
        return academicTermRepository.findAll().stream()
                .sorted(Comparator.comparing(AcademicTermEntity::getStartDate))
                .map(this::toTermDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AcademicTermDto getActiveTerm() {
        return academicTermRepository.findByActiveTrue()
                .map(this::toTermDto)
                .orElse(null);
    }

    @Transactional
    public AcademicTermDto createTerm(CreateAcademicTermRequest request) {
        AcademicTermEntity entity = new AcademicTermEntity();
        entity.setName(request.name());
        entity.setType(request.type());
        entity.setAcademicYear(request.academicYear());
        entity.setStartDate(request.startDate());
        entity.setEndDate(request.endDate());
        entity.setActive(false);
        return toTermDto(academicTermRepository.save(entity));
    }

    @Transactional
    public void activateTerm(String id) {
        AcademicTermEntity target = academicTermRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Academic term not found"));
        academicTermRepository.findAll().forEach(term -> term.setActive(term.getId().equals(target.getId())));
    }

    @Transactional
    public void deleteTerm(String id) {
        academicTermRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ClassResponse> getClasses(String termId) {
        List<SchoolClassEntity> classes = termId == null || termId.isBlank()
                ? schoolClassRepository.findAll()
                : schoolClassRepository.findAllByAcademicTerm_IdOrderByGradeAscSectionAsc(termId);
        return classes.stream()
                .sorted(Comparator.comparing(SchoolClassEntity::getGrade).thenComparing(SchoolClassEntity::getSection))
                .map(this::toClassResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClassResponse getClassById(String id) {
        return schoolClassRepository.findById(id)
                .map(this::toClassResponse)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));
    }

    @Transactional
    public ClassResponse createClass(CreateClassRequest request) {
        AcademicTermEntity term = academicTermRepository.findById(request.academicTermId())
                .orElseThrow(() -> new IllegalArgumentException("Academic term not found"));

        SchoolClassEntity entity = new SchoolClassEntity();
        entity.setGrade(request.grade());
        entity.setSection(request.section());
        entity.setRoomNo(request.roomNo());
        entity.setAcademicTerm(term);
        return toClassResponse(schoolClassRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> getSubjects() {
        return subjectRepository.findAll().stream()
                .sorted(Comparator.comparing(SubjectEntity::getName))
                .map(this::toSubjectResponse)
                .toList();
    }

    @Transactional
    public SubjectResponse createSubject(CreateSubjectRequest request) {
        SubjectEntity entity = new SubjectEntity();
        entity.setName(request.name());
        entity.setCode(request.code().toUpperCase());
        return toSubjectResponse(subjectRepository.save(entity));
    }

    @Transactional
    public TeacherMappingResponse createMapping(CreateTeacherMappingRequest request) {
        UserEntity teacher = userRepository.findById(request.teacherUserId())
                .filter(user -> user.getRole() == Role.TEACHER)
                .orElseThrow(() -> new IllegalArgumentException("Teacher user not found"));
        SchoolClassEntity schoolClass = schoolClassRepository.findById(request.classId())
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));
        SubjectEntity subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        AcademicTermEntity term = academicTermRepository.findById(request.academicTermId())
                .orElseThrow(() -> new IllegalArgumentException("Academic term not found"));

        TeacherMappingEntity entity = new TeacherMappingEntity();
        entity.setTeacher(teacher);
        entity.setSchoolClass(schoolClass);
        entity.setSubject(subject);
        entity.setAcademicTerm(term);
        entity.setClassTeacher(request.isClassTeacher());
        return toTeacherMappingResponse(teacherMappingRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<TeacherMappingResponse> getTeacherMappings(String teacherId) {
        return teacherMappingRepository.findAllByTeacher_IdOrderBySchoolClass_GradeAscSchoolClass_SectionAsc(teacherId).stream()
                .map(this::toTeacherMappingResponse)
                .toList();
    }

    @Transactional
    public void deleteMapping(String id) {
        teacherMappingRepository.deleteById(id);
    }

    @Transactional
    public TimetableSlotResponse createTimetableSlot(CreateTimetableSlotRequest request) {
        SchoolClassEntity schoolClass = schoolClassRepository.findById(request.classId())
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));
        SubjectEntity subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        UserEntity teacher = userRepository.findById(request.teacherId())
                .filter(user -> user.getRole() == Role.TEACHER)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        TimetableSlotEntity entity = new TimetableSlotEntity();
        entity.setSchoolClass(schoolClass);
        entity.setSubject(subject);
        entity.setTeacher(teacher);
        entity.setDay(request.day());
        entity.setPeriodNo(request.periodNo());
        entity.setStartTime(request.startTime());
        entity.setEndTime(request.endTime());
        return toTimetableSlotResponse(timetableSlotRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Map<String, List<TimetableSlotResponse>> getClassTimetable(String classId) {
        Map<String, List<TimetableSlotResponse>> grouped = new LinkedHashMap<>();
        timetableSlotRepository.findAllBySchoolClass_IdOrderByDayAscPeriodNoAsc(classId).stream()
                .map(this::toTimetableSlotResponse)
                .forEach(slot -> grouped.computeIfAbsent(slot.day(), key -> new java.util.ArrayList<>()).add(slot));
        return grouped;
    }

    @Transactional(readOnly = true)
    public List<TimetableSlotResponse> getTeacherTimetable(String teacherId) {
        return timetableSlotRepository.findAllByTeacher_IdOrderByDayAscPeriodNoAsc(teacherId).stream()
                .map(this::toTimetableSlotResponse)
                .toList();
    }

    @Transactional
    public void deleteTimetableSlot(String id) {
        timetableSlotRepository.deleteById(id);
    }

    private AcademicTermDto toTermDto(AcademicTermEntity entity) {
        return new AcademicTermDto(
                entity.getId(),
                entity.getName(),
                entity.getType().name(),
                entity.getAcademicYear(),
                entity.getStartDate().toString(),
                entity.getEndDate().toString(),
                entity.isActive()
        );
    }

    private ClassResponse toClassResponse(SchoolClassEntity entity) {
        return new ClassResponse(
                entity.getId(),
                entity.getGrade(),
                entity.getSection(),
                entity.getAcademicTerm().getId(),
                entity.getRoomNo(),
                new ClassResponse.AcademicTermSummary(entity.getAcademicTerm().getName(), entity.getAcademicTerm().isActive())
        );
    }

    private SubjectResponse toSubjectResponse(SubjectEntity entity) {
        return new SubjectResponse(entity.getId(), entity.getName(), entity.getCode());
    }

    private TeacherMappingResponse toTeacherMappingResponse(TeacherMappingEntity entity) {
        return new TeacherMappingResponse(
                entity.getId(),
                entity.getTeacher().getId(),
                entity.getSchoolClass().getId(),
                entity.getSubject().getId(),
                entity.getAcademicTerm().getId(),
                entity.isClassTeacher(),
                new TeacherMappingResponse.TeacherSummary(entity.getTeacher().getId(), entity.getTeacher().getName()),
                new TeacherMappingResponse.ClassSummary(entity.getSchoolClass().getGrade(), entity.getSchoolClass().getSection()),
                new TeacherMappingResponse.SubjectSummary(entity.getSubject().getName(), entity.getSubject().getCode()),
                new TeacherMappingResponse.TermSummary(entity.getAcademicTerm().getName())
        );
    }

    private TimetableSlotResponse toTimetableSlotResponse(TimetableSlotEntity entity) {
        return new TimetableSlotResponse(
                entity.getId(),
                entity.getSchoolClass().getId(),
                entity.getSubject().getId(),
                entity.getTeacher().getId(),
                entity.getDay().name(),
                entity.getPeriodNo(),
                entity.getStartTime().toString(),
                entity.getEndTime().toString(),
                new TimetableSlotResponse.SubjectSummary(entity.getSubject().getName(), entity.getSubject().getCode()),
                new TimetableSlotResponse.TeacherSummary(entity.getTeacher().getName()),
                new TimetableSlotResponse.ClassSummary(entity.getSchoolClass().getGrade(), entity.getSchoolClass().getSection())
        );
    }
}

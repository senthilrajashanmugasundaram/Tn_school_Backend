package com.tnschool.sms.modules.marks.service;

import com.tnschool.sms.auth.model.Role;
import com.tnschool.sms.auth.model.UserEntity;
import com.tnschool.sms.auth.repository.UserRepository;
import com.tnschool.sms.modules.academic.model.AcademicTermEntity;
import com.tnschool.sms.modules.academic.model.SubjectEntity;
import com.tnschool.sms.modules.academic.model.TeacherMappingEntity;
import com.tnschool.sms.modules.academic.repository.AcademicTermRepository;
import com.tnschool.sms.modules.academic.repository.SubjectRepository;
import com.tnschool.sms.modules.academic.repository.TeacherMappingRepository;
import com.tnschool.sms.modules.marks.dto.CreateExamRequest;
import com.tnschool.sms.modules.marks.dto.EnterMarksRequest;
import com.tnschool.sms.modules.marks.dto.ExamClassMarksResponse;
import com.tnschool.sms.modules.marks.dto.ExamResponse;
import com.tnschool.sms.modules.marks.dto.MarkResponse;
import com.tnschool.sms.modules.marks.dto.StudentMarksResponse;
import com.tnschool.sms.modules.marks.model.ExamEntity;
import com.tnschool.sms.modules.marks.model.MarkEntity;
import com.tnschool.sms.modules.marks.repository.ExamRepository;
import com.tnschool.sms.modules.marks.repository.MarkRepository;
import com.tnschool.sms.modules.students.model.StudentEntity;
import com.tnschool.sms.modules.students.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MarksService {

    private final ExamRepository examRepository;
    private final MarkRepository markRepository;
    private final AcademicTermRepository academicTermRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final TeacherMappingRepository teacherMappingRepository;

    public MarksService(
            ExamRepository examRepository,
            MarkRepository markRepository,
            AcademicTermRepository academicTermRepository,
            SubjectRepository subjectRepository,
            StudentRepository studentRepository,
            UserRepository userRepository,
            TeacherMappingRepository teacherMappingRepository
    ) {
        this.examRepository = examRepository;
        this.markRepository = markRepository;
        this.academicTermRepository = academicTermRepository;
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.teacherMappingRepository = teacherMappingRepository;
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> getExams(String termId) {
        List<ExamEntity> exams = termId == null || termId.isBlank()
                ? examRepository.findAll()
                : examRepository.findAllByAcademicTerm_IdOrderByExamDateAsc(termId);
        return exams.stream()
                .sorted(Comparator.comparing(ExamEntity::getExamDate))
                .map(this::toExamResponse)
                .toList();
    }

    @Transactional
    public ExamResponse createExam(CreateExamRequest request) {
        AcademicTermEntity term = academicTermRepository.findById(request.academicTermId())
                .orElseThrow(() -> new IllegalArgumentException("Academic term not found"));
        ExamEntity exam = new ExamEntity();
        exam.setName(request.name());
        exam.setAcademicTerm(term);
        exam.setExamDate(request.examDate());
        exam.setTotalMarks(request.totalMarks());
        exam.setMarksLocked(false);
        return toExamResponse(examRepository.save(exam));
    }

    @Transactional
    public void toggleLock(String examId) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found"));
        exam.setMarksLocked(!exam.isMarksLocked());
    }

    @Transactional
    public void enterMarks(String enteredByUserId, EnterMarksRequest request) {
        ExamEntity exam = examRepository.findById(request.examId())
                .orElseThrow(() -> new IllegalArgumentException("Exam not found"));
        if (exam.isMarksLocked()) {
            throw new IllegalArgumentException("Marks are locked for this exam");
        }

        SubjectEntity subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        UserEntity enteredBy = userRepository.findById(enteredByUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (enteredBy.getRole() == Role.TEACHER) {
            boolean allowed = teacherMappingRepository.findAllByTeacher_IdOrderBySchoolClass_GradeAscSchoolClass_SectionAsc(enteredByUserId).stream()
                    .map(TeacherMappingEntity::getSubject)
                    .anyMatch(mappedSubject -> mappedSubject.getId().equals(subject.getId()));
            if (!allowed) {
                throw new IllegalArgumentException("Teacher is not mapped to this subject");
            }
        }

        for (EnterMarksRequest.MarkLine line : request.records()) {
            if (line.marksObtained() > line.maxMarks()) {
                throw new IllegalArgumentException("Marks obtained cannot exceed maximum marks");
            }

            StudentEntity student = studentRepository.findById(line.studentId())
                    .orElseThrow(() -> new IllegalArgumentException("Student not found: " + line.studentId()));

            MarkEntity mark = markRepository.findByExam_IdAndStudent_IdAndSubject_Id(exam.getId(), student.getId(), subject.getId())
                    .orElseGet(MarkEntity::new);

            mark.setExam(exam);
            mark.setStudent(student);
            mark.setSubject(subject);
            mark.setEnteredBy(enteredBy);
            mark.setMarksObtained(line.marksObtained());
            mark.setMaxMarks(line.maxMarks());
            mark.setGrade(computeGrade(line.marksObtained(), line.maxMarks()));
            markRepository.save(mark);
        }
    }

    @Transactional(readOnly = true)
    public StudentMarksResponse getStudentMarks(String studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        List<MarkResponse> marks = markRepository.findAllByStudent_IdOrderByExam_ExamDateDesc(studentId).stream()
                .map(this::toMarkResponse)
                .toList();
        Map<String, List<MarkResponse>> grouped = new LinkedHashMap<>();
        for (MarkResponse mark : marks) {
            grouped.computeIfAbsent(mark.exam().name(), key -> new java.util.ArrayList<>()).add(mark);
        }
        return new StudentMarksResponse(marks, grouped);
    }

    @Transactional(readOnly = true)
    public List<ExamClassMarksResponse> getExamClassMarks(String examId, String classId) {
        Map<String, List<MarkResponse>> byStudent = new LinkedHashMap<>();
        Map<String, ExamClassMarksResponse.StudentSummary> studentMeta = new LinkedHashMap<>();

        markRepository.findAllByExam_IdAndStudent_SchoolClass_IdOrderByStudent_NameAscSubject_NameAsc(examId, classId)
                .forEach(mark -> {
                    String studentId = mark.getStudent().getId();
                    studentMeta.putIfAbsent(studentId, new ExamClassMarksResponse.StudentSummary(
                            studentId,
                            mark.getStudent().getName(),
                            mark.getStudent().getAdmissionNo()
                    ));
                    byStudent.computeIfAbsent(studentId, key -> new java.util.ArrayList<>()).add(toMarkResponse(mark));
                });

        return byStudent.entrySet().stream()
                .map(entry -> new ExamClassMarksResponse(studentMeta.get(entry.getKey()), entry.getValue()))
                .toList();
    }

    private ExamResponse toExamResponse(ExamEntity exam) {
        return new ExamResponse(
                exam.getId(),
                exam.getName(),
                exam.getAcademicTerm().getId(),
                exam.getExamDate().toString(),
                exam.isMarksLocked(),
                exam.getTotalMarks(),
                new ExamResponse.AcademicTermSummary(exam.getAcademicTerm().getName())
        );
    }

    private MarkResponse toMarkResponse(MarkEntity mark) {
        return new MarkResponse(
                mark.getId(),
                mark.getExam().getId(),
                mark.getStudent().getId(),
                mark.getSubject().getId(),
                mark.getMarksObtained(),
                mark.getMaxMarks(),
                mark.getGrade(),
                new MarkResponse.ExamSummary(
                        mark.getExam().getName(),
                        mark.getExam().getExamDate().toString(),
                        mark.getExam().isMarksLocked(),
                        new MarkResponse.AcademicTermSummary(mark.getExam().getAcademicTerm().getName())
                ),
                new MarkResponse.SubjectSummary(mark.getSubject().getName(), mark.getSubject().getCode())
        );
    }

    private String computeGrade(Double obtained, Double max) {
        if (max == null || max == 0) {
            return null;
        }
        double pct = (obtained / max) * 100.0;
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B+";
        if (pct >= 60) return "B";
        if (pct >= 50) return "C";
        return "D";
    }
}

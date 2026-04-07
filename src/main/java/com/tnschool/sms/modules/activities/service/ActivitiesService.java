package com.tnschool.sms.modules.activities.service;

import com.tnschool.sms.modules.academic.model.AcademicTermEntity;
import com.tnschool.sms.modules.academic.repository.AcademicTermRepository;
import com.tnschool.sms.modules.activities.dto.ActivityParticipantResponse;
import com.tnschool.sms.modules.activities.dto.ActivityResponse;
import com.tnschool.sms.modules.activities.dto.AddActivityParticipantRequest;
import com.tnschool.sms.modules.activities.dto.CreateActivityRequest;
import com.tnschool.sms.modules.activities.dto.UpdateActivityRequest;
import com.tnschool.sms.modules.activities.model.ActivityEntity;
import com.tnschool.sms.modules.activities.model.ActivityParticipantEntity;
import com.tnschool.sms.modules.activities.repository.ActivityParticipantRepository;
import com.tnschool.sms.modules.activities.repository.ActivityRepository;
import com.tnschool.sms.modules.students.model.StudentEntity;
import com.tnschool.sms.modules.students.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivitiesService {

    private final ActivityRepository activityRepository;
    private final ActivityParticipantRepository activityParticipantRepository;
    private final AcademicTermRepository academicTermRepository;
    private final StudentRepository studentRepository;

    public ActivitiesService(
            ActivityRepository activityRepository,
            ActivityParticipantRepository activityParticipantRepository,
            AcademicTermRepository academicTermRepository,
            StudentRepository studentRepository
    ) {
        this.activityRepository = activityRepository;
        this.activityParticipantRepository = activityParticipantRepository;
        this.academicTermRepository = academicTermRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<ActivityResponse> getActivities(String termId, String category) {
        List<ActivityEntity> items;
        if (termId != null && !termId.isBlank()) {
            items = activityRepository.findAllByAcademicTerm_IdOrderByActivityDateDescTitleAsc(termId);
        } else if (category != null && !category.isBlank()) {
            items = activityRepository.findAllByCategoryIgnoreCaseOrderByActivityDateDescTitleAsc(category);
        } else {
            items = activityRepository.findAll();
        }
        return items.stream().map(this::toActivityResponse).toList();
    }

    @Transactional(readOnly = true)
    public ActivityResponse getActivity(String id) {
        return activityRepository.findById(id)
                .map(this::toActivityResponse)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));
    }

    @Transactional
    public ActivityResponse createActivity(CreateActivityRequest request) {
        ActivityEntity entity = new ActivityEntity();
        apply(entity, request.academicTermId(), request.category(), request.title(), request.description(), request.activityDate());
        return toActivityResponse(activityRepository.save(entity));
    }

    @Transactional
    public ActivityResponse updateActivity(String id, UpdateActivityRequest request) {
        ActivityEntity entity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));
        apply(entity, request.academicTermId(), request.category(), request.title(), request.description(), request.activityDate());
        return toActivityResponse(entity);
    }

    @Transactional
    public void deleteActivity(String id) {
        activityRepository.deleteById(id);
    }

    @Transactional
    public ActivityParticipantResponse addParticipant(AddActivityParticipantRequest request) {
        ActivityEntity activity = activityRepository.findById(request.activityId())
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));
        StudentEntity student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        ActivityParticipantEntity entity = new ActivityParticipantEntity();
        entity.setActivity(activity);
        entity.setStudent(student);
        entity.setAchievement(request.achievement());
        entity.setPosition(request.position());
        entity.setCertificate(request.certificate());
        entity.setNotes(request.notes());
        return toParticipantResponse(activityParticipantRepository.save(entity));
    }

    @Transactional
    public void removeParticipant(String id) {
        activityParticipantRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ActivityParticipantResponse> getStudentActivities(String studentId) {
        return activityParticipantRepository.findAllByStudent_IdOrderByActivity_ActivityDateDesc(studentId).stream()
                .map(this::toParticipantResponse)
                .toList();
    }

    private void apply(ActivityEntity entity, String termId, String category, String title, String description, java.time.LocalDate activityDate) {
        AcademicTermEntity term = termId == null || termId.isBlank()
                ? null
                : academicTermRepository.findById(termId).orElseThrow(() -> new IllegalArgumentException("Academic term not found"));
        entity.setAcademicTerm(term);
        entity.setCategory(category);
        entity.setTitle(title);
        entity.setDescription(description);
        entity.setActivityDate(activityDate);
    }

    private ActivityResponse toActivityResponse(ActivityEntity entity) {
        int participants = activityParticipantRepository.findAllByActivity_IdOrderByStudent_NameAsc(entity.getId()).size();
        return new ActivityResponse(
                entity.getId(),
                entity.getAcademicTerm() == null ? null : entity.getAcademicTerm().getId(),
                entity.getCategory(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getActivityDate() == null ? null : entity.getActivityDate().toString(),
                participants
        );
    }

    private ActivityParticipantResponse toParticipantResponse(ActivityParticipantEntity entity) {
        return new ActivityParticipantResponse(
                entity.getId(),
                entity.getActivity().getId(),
                entity.getStudent().getId(),
                entity.getAchievement(),
                entity.getPosition(),
                entity.isCertificate(),
                entity.getNotes(),
                new ActivityParticipantResponse.ActivitySummary(
                        entity.getActivity().getTitle(),
                        entity.getActivity().getCategory(),
                        entity.getActivity().getActivityDate() == null ? null : entity.getActivity().getActivityDate().toString()
                ),
                new ActivityParticipantResponse.StudentSummary(
                        entity.getStudent().getName(),
                        entity.getStudent().getAdmissionNo()
                )
        );
    }
}

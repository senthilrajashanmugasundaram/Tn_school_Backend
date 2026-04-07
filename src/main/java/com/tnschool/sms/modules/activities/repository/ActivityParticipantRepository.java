package com.tnschool.sms.modules.activities.repository;

import com.tnschool.sms.modules.activities.model.ActivityParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityParticipantRepository extends JpaRepository<ActivityParticipantEntity, String> {
    List<ActivityParticipantEntity> findAllByStudent_IdOrderByActivity_ActivityDateDesc(String studentId);
    List<ActivityParticipantEntity> findAllByActivity_IdOrderByStudent_NameAsc(String activityId);
}

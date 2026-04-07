package com.tnschool.sms.modules.academic.repository;

import com.tnschool.sms.modules.academic.model.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<SubjectEntity, String> {
}

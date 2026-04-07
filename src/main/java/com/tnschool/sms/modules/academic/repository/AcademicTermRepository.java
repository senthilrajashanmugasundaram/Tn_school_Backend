package com.tnschool.sms.modules.academic.repository;

import com.tnschool.sms.modules.academic.model.AcademicTermEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicTermRepository extends JpaRepository<AcademicTermEntity, String> {
    Optional<AcademicTermEntity> findByActiveTrue();
}

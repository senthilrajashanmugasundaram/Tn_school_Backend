package com.tnschool.sms.modules.fees.repository;

import com.tnschool.sms.modules.fees.model.FeePaymentEntity;
import com.tnschool.sms.modules.fees.model.FeePaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeePaymentRepository extends JpaRepository<FeePaymentEntity, String> {
    List<FeePaymentEntity> findAllByStudent_IdOrderByFeeStructure_FeeTypeAsc(String studentId);
    List<FeePaymentEntity> findAllByStatusInOrderByStudent_NameAscFeeStructure_FeeTypeAsc(List<FeePaymentStatus> statuses);
}

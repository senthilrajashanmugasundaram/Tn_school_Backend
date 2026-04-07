package com.tnschool.sms.modules.transport.repository;

import com.tnschool.sms.modules.transport.model.TransportVanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportVanRepository extends JpaRepository<TransportVanEntity, String> {
}

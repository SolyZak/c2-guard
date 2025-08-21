package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.entity.AlertTriggerSeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertTriggerSeverityRepository extends JpaRepository<AlertTriggerSeverity, Long> {

    List<AlertTriggerSeverity> findByTriggerIdAndServicePlatformId(final Long triggerId,
                                                                   final Long servicePlatformId);

    List<AlertTriggerSeverity> findByTriggerIdAndServicePlatformIdAndCustomerId(final Long triggerId,
                                                                                final Long servicePlatformId,
                                                                                final Long customerId);

}
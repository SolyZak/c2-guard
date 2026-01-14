package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.entity.AlertTriggerSeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertTriggerSeverityRepository extends JpaRepository<AlertTriggerSeverity, Long> {

    List<AlertTriggerSeverity> findByAlertTrigger_TriggerIdAndAlertTrigger_ServicePlatform_Id(
        Long triggerId,
        Long servicePlatformId
    );

    List<AlertTriggerSeverity> findByAlertTrigger_TriggerIdAndAlertTrigger_ServicePlatform_IdAndCustomerId(
        Long triggerId,
        Long servicePlatformId,
        Long customerId
    );
}

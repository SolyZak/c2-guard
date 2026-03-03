package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.entity.AlertTrigger;
import com.eden.eden_crm_sec_crm_back.repository.projections.AlertTriggerWithSeverityProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertTriggerRepository extends JpaRepository<AlertTrigger, Long>, JpaSpecificationExecutor<AlertTrigger> {
    
    @Query("""
        SELECT
            at.id as id,
            at.alertId as alertId,
            at.triggerId as triggerId,
            at.servicePlatform.id as servicePlatformId,
            at.servicePlatform.code as servicePlatformCode,
            ats.severity as severity
        FROM AlertTrigger at
        LEFT JOIN AlertTriggerSeverity ats ON at.id = ats.alertTrigger.id AND ats.customerId = :customerId
        """)
    List<AlertTriggerWithSeverityProjection> findAllWithSeverityByCustomerId(@Param("customerId") Long customerId);

    Optional<AlertTrigger> findByTriggerIdAndServicePlatformId(Long triggerId, Long servicePlatformId);
}

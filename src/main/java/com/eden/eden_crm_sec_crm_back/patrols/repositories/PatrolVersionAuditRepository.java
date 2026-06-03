package com.eden.eden_crm_sec_crm_back.patrols.repositories;

import com.eden.eden_crm_sec_crm_back.patrols.entities.PatrolVersionAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatrolVersionAuditRepository extends JpaRepository<PatrolVersionAudit, Long> {
}

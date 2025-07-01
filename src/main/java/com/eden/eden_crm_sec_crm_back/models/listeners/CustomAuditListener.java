package com.eden.eden_crm_sec_crm_back.models.listeners;

import com.eden.eden_crm_sec_crm_back.base.model.AuditTrail;
import com.eden.eden_crm_sec_crm_back.models.BaseAuditEntity;
import com.eden.eden_crm_sec_crm_back.models.BaseEntity;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAuditListener {

    @PrePersist
    public void setCreatedBy(Object entity) {
        UserData userData = Utils.getAuditor();
        Long id = Long.valueOf(userData.getId());
        String name = userData.getName();
        if (entity instanceof BaseAuditEntity baseAudit) {
            baseAudit.setCreatedBy(id);
            baseAudit.setCreatedByUser(name);
            baseAudit.setModifiedBy(id);
            baseAudit.setModifiedByUser(name);
            baseAudit.setCreatedDate(LocalDateTime.now());
            baseAudit.setModifiedDate(LocalDateTime.now());
        }
        else if (entity instanceof AuditTrail baseAudit) {
            baseAudit.setCreatedBy(id);
            baseAudit.setCreatedByUser(name);
            baseAudit.setModifiedBy(id);
            baseAudit.setModifiedByUser(name);
            baseAudit.setCreatedDate(LocalDateTime.now());
            baseAudit.setModifiedDate(LocalDateTime.now());
        }
        else if (entity instanceof BaseEntity baseAudit) {
            baseAudit.setCreatedBy(id);
            baseAudit.setModifiedBy(id);
            baseAudit.setCreatedDate(LocalDateTime.now());
            baseAudit.setModifiedDate(LocalDateTime.now());
        }
    }

    @PreUpdate
    public void setModifiedBy(Object entity) {
        UserData userData = Utils.getAuditor();
        Long id = Long.valueOf(userData.getId());
        String name = userData.getName();
        if (entity instanceof BaseAuditEntity baseAudit) {
            baseAudit.setModifiedBy(id);
            baseAudit.setModifiedByUser(name);
            baseAudit.setModifiedDate(LocalDateTime.now());
        }
        else if (entity instanceof AuditTrail baseAudit) {
            baseAudit.setModifiedBy(id);
            baseAudit.setModifiedByUser(name);
            baseAudit.setModifiedDate(LocalDateTime.now());
        }
        else if (entity instanceof BaseEntity baseAudit) {
            baseAudit.setModifiedBy(id);
            baseAudit.setModifiedDate(LocalDateTime.now());
        }
    }
}

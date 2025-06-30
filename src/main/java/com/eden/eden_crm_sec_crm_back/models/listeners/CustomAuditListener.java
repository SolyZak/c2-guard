package com.eden.eden_crm_sec_crm_back.models.listeners;

import com.eden.eden_crm_sec_crm_back.base.model.AuditTrail;
import com.eden.eden_crm_sec_crm_back.models.BaseAuditEntity;
import com.eden.eden_crm_sec_crm_back.models.BaseEntity;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

@Component
public class CustomAuditListener {
    @PrePersist
    public void setCreatedBy(Object entity) {
        UserData userData = Utils.getLoggedInUser();
        Long id = Long.valueOf(userData.getId());
        String name = userData.getName();
        if (entity instanceof BaseAuditEntity baseAudit) {
            baseAudit.setCreatedBy(id);
            baseAudit.setCreatedByUser(name);
            baseAudit.setModifiedBy(id);
            baseAudit.setModifiedByUser(name);
        }
        else if (entity instanceof AuditTrail baseAudit) {
            baseAudit.setCreatedBy(id);
            baseAudit.setCreatedByUser(name);
            baseAudit.setModifiedBy(id);
            baseAudit.setModifiedByUser(name);
        }
        else if (entity instanceof BaseEntity baseAudit) {
            baseAudit.setCreatedBy(id);
            baseAudit.setModifiedBy(id);
        }
    }

    @PreUpdate
    public void setModifiedBy(Object entity) {
        UserData userData = Utils.getLoggedInUser();
        Long id = Long.valueOf(userData.getId());
        String name = userData.getName();
        if (entity instanceof BaseAuditEntity baseAudit) {
            baseAudit.setModifiedBy(id);
            baseAudit.setModifiedByUser(name);
        }
        else if (entity instanceof AuditTrail baseAudit) {
            baseAudit.setModifiedBy(id);
            baseAudit.setModifiedByUser(name);
        }
        else if (entity instanceof BaseEntity baseAudit) {
            baseAudit.setModifiedBy(id);
        }
    }
}

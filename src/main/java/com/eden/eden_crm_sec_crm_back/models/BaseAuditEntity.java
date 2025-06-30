package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.models.listeners.CustomAuditListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@Setter
@Getter
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(CustomAuditListener.class)
public class BaseAuditEntity extends BaseEntity {
    @Column(name = "modified_by_user")
    @LastModifiedBy
    private String modifiedByUser;

    @CreatedBy
    @Column(updatable = false, name = "created_by_user")
    private String createdByUser;
}

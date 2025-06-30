package com.eden.eden_crm_sec_crm_back.base.model;

import java.time.LocalDateTime;
import com.eden.eden_crm_sec_crm_back.models.listeners.CustomAuditListener;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(CustomAuditListener.class)
public class AuditTrail {

    @Schema(description = "Audit Column")
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @Schema(description = "Audit Column")
    @CreatedBy
    @Column(updatable = false)
    private String createdByUser;

    @Schema(description = "Audit Column")
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Schema(description = "Audit Column")
    @LastModifiedBy
    private String modifiedByUser;

    @Column(name = "created_by")
    @CreatedBy
    private Long createdBy;

    @Column(name = "modified_by")
    @LastModifiedBy
    private Long modifiedBy;

    protected AuditTrail(LocalDateTime dateTime) {
        this.createdDate = dateTime;
    }

}

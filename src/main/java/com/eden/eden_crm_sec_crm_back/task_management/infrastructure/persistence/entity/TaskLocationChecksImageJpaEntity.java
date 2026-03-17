package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "task_location_checks_image")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskLocationChecksImageJpaEntity {

    @Id
    @SequenceGenerator(name = "task_location_checks_image_seq",
            sequenceName = "task_location_checks_image_seq",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_location_checks_image_seq")
    private Long id;

    @Column(name = "task_definition_id", nullable = false)
    private Long taskDefinitionId;

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @Column(name = "task_check_definition_id", nullable = false)
    private Long taskCheckDefinitionId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "ref_image", length = 500)
    private String refImage;

    @Builder.Default
    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "modified_by")
    private Long modifiedBy;

    @Column(name = "created_date", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdDate;

    @Column(name = "modified_date", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime modifiedDate;

    @PrePersist
    protected void onCreate() {
        if (this.createdDate == null) {
            this.createdDate = OffsetDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifiedDate = OffsetDateTime.now();
    }
}
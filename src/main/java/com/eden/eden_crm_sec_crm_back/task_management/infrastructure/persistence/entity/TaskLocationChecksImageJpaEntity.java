package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity;

import com.eden.eden_crm_sec_crm_back.models.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "task_location_checks_image")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskLocationChecksImageJpaEntity extends BaseEntity {

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
}
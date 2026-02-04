package com.eden.eden_crm_sec_crm_back.taskdistribution.entities;

import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.PatrolDetail;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "patrol_task_distribution", uniqueConstraints = {
    @UniqueConstraint(name = "uc_patroltaskdistribution_patroldetailid_servicetimeid", columnNames = {"patrol_detail_id", "service_time_id"})
}, indexes = {
    @Index(name = "idx_patrol_task_distribution_task_distribution_id", columnList = "task_distribution_id"),
    @Index(name = "idx_patrol_task_distribution_service_lookup", columnList = "service_id, service_time_id"),
    @Index(name = "idx_patrol_task_distribution_location_id", columnList = "location_id"),
    @Index(name = "idx_patrol_task_distribution_customer_id", columnList = "customer_id"),
    @Index(name = "idx_patrol_task_distribution_patrol_detail_id", columnList = "patrol_detail_id"),
    @Index(name = "idx_patrol_task_distribution_customer_location", columnList = "customer_id, location_id"),
    @Index(name = "idx_patrol_task_distribution_service_customer", columnList = "service_id, customer_id")
})
public class PatrolTaskDistribution {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_distribution_id")
    private TaskDistribution taskDistribution;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patrol_detail_id")
    private PatrolDetail patrolDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private LKCustomerContractService service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_time_id")
    private LKCustomerContractOperationService serviceTime;

    @Column(name = "distributed_quantity")
    private Integer distributedQuantity;

    @Column(name = "frequency_rate")
    private String frequencyRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}

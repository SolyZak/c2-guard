package com.eden.eden_crm_sec_crm_back.taskdistribution.entities;

import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.PatrolDetail;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "patrol_task_distribution")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatrolTaskDistribution {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_distribution_id")
    private TaskDistribution taskDistribution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patrol_detail_id")
    private PatrolDetail patrolDetailId; // Note one or many

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

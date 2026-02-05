package com.eden.eden_crm_sec_crm_back.taskdistribution.entities;

import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerUser;
import com.eden.eden_crm_sec_crm_back.models.Location;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "immediate_task_distribution", indexes = {
    @Index(name = "idx_immediate_task_distribution_task_distribution_id", columnList = "task_distribution_id"),
    @Index(name = "idx_immediate_task_distribution_location_id", columnList = "location_id"),
    @Index(name = "idx_immediate_task_distribution_customer_id", columnList = "customer_id"),
    @Index(name = "idx_immediate_task_distribution_dispatcher_id", columnList = "dispatcher_id"),
    @Index(name = "idx_immediate_task_distribution_created_at", columnList = "created_at"),
    @Index(name = "idx_immediate_task_distribution_customer_created", columnList = "customer_id, created_at"),
    @Index(name = "idx_immediate_task_distribution_dispatcher_customer", columnList = "dispatcher_id, customer_id")
})
public class ImmediateTaskDistribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_distribution_id", nullable = false)
    private TaskDistribution taskDistribution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatcher_id", nullable = false)
    private CustomerUser dispatcher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "longitude", precision = 13, scale = 10, columnDefinition = "NUMERIC(13,10)")
    private BigDecimal longitude;

    @Column(name = "latitude", precision = 13, scale = 10, columnDefinition = "NUMERIC(13,10)")
    private BigDecimal latitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}

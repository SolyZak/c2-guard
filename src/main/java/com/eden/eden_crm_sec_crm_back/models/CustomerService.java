package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.enums.ActivityEnum;
import com.eden.eden_crm_sec_crm_back.enums.UnitEnum;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "customer_service")
@Setter
@Getter
public class CustomerService extends BaseEntity<Long> {

    private String serviceName;
    @Enumerated(EnumType.STRING)
    private UnitEnum unit;
    @ElementCollection(targetClass = ActivityEnum.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "customer_service_activities", joinColumns = @JoinColumn(name = "customer_service_id"))
    private Set<ActivityEnum> activities = new HashSet<>();
    private boolean multiSite;
    @OneToMany(mappedBy = "customerService", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ServiceDetails> serviceDetails = new ArrayList<>();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;

    @Column(name = "created_at", updatable = false, nullable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;
}

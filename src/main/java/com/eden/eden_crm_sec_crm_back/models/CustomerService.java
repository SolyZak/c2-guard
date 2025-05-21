package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.enums.ActivityEnum;
import com.eden.eden_crm_sec_crm_back.enums.UnitEnum;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import jakarta.persistence.*;
import lombok.*;

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
    private List<ServiceDetails> serviceDetails = new ArrayList<>();
}

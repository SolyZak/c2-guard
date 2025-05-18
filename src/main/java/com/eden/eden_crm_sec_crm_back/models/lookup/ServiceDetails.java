package com.eden.eden_crm_sec_crm_back.models.lookup;

import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.models.CustomerService;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_service_details")
@Setter
@Getter
public class ServiceDetails extends BaseEntity<Long> {

    private Integer hours;
    private Integer days;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_service_id")
    private CustomerService customerService;

}

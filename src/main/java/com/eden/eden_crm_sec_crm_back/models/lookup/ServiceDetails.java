package com.eden.eden_crm_sec_crm_back.models.lookup;

import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.models.CustomerService;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_service_details")
@Setter
@Getter
public class ServiceDetails extends BaseEntity<Long> {

    private Long hours;
    private Long days;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_service_id")
    @JsonBackReference
    private CustomerService customerService;

}

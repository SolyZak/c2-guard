package com.eden.eden_crm_sec_crm_back.models.lookup;

import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.models.CustomerService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customer_agreement_service")
@Setter
@Getter
public class CustomerAgreementService extends BaseEntity<Long> {

    private int hours;
    private int days;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_service_id")
    private CustomerService customerService;

}

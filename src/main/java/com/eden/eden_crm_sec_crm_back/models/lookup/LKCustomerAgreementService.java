package com.eden.eden_crm_sec_crm_back.models.lookup;

import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.models.CustomerAgreement;
import com.eden.eden_crm_sec_crm_back.models.CustomerService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customer_agreement_service")
@Setter
@Getter
public class LKCustomerAgreementService extends BaseEntity<Long> {

    private Long quantity;
    private Long unitPrice;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_service_id")
    private CustomerService customerService;
    @ManyToOne
    @JoinColumn(name = "customer_agreement_id", nullable = false)
    private CustomerAgreement agreement;

}

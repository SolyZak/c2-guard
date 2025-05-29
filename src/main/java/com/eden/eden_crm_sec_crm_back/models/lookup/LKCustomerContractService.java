package com.eden.eden_crm_sec_crm_back.models.lookup;

import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customer_contract_service")
@Setter
@Getter
public class LKCustomerContractService extends BaseEntity<Long> {

    private Long quantity;
    private Double unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_details_id") // Changed column name
    private ServiceDetails customerService;
    @ManyToOne
    @JoinColumn(name = "customer_contract_id", nullable = false)
    private CustomerContract customerContract;

}

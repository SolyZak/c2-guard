package com.eden.eden_crm_sec_crm_back.models.lookup;

import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @Column(name = "distributed_quantity")
    private Long distributedQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_details_id") // Changed column name
    @JsonBackReference
    private ServiceDetails customerService;

    @ManyToOne
    @JoinColumn(name = "customer_contract_id", nullable = false)
    @JsonBackReference
    private CustomerContract customerContract;

}

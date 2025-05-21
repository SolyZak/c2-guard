package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.enums.CurrencyEnum;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "customer_contract",uniqueConstraints = {@UniqueConstraint(columnNames = {"agreementNumber"})})
@Setter
@Getter
public class CustomerContract extends BaseEntity<Long> {
    private String agreementNumber;
    private String agreementName;
    private String status;
    private LocalDate startAgreementDate;
    private LocalDate endAgreementDate;
    private Long securityCompanyId;
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;

    @OneToMany(mappedBy = "customerContract", cascade = CascadeType.MERGE,orphanRemoval = true)
    private List<LKCustomerContractService>  customerContractServices = new ArrayList<>();

}

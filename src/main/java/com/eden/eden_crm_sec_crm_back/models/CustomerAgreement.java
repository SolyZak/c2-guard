package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.enums.CurrencyEnum;
import com.eden.eden_crm_sec_crm_back.models.lookup.SecurityCompany;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "customer_agreement")
@Setter
@Getter
public class CustomerAgreement extends BaseEntity<Long> {
    private String agreementNumber;
    private String agreementName;
    private LocalDate agreementDate;
    @ManyToOne
    @JoinColumn(name = "security_company_id", nullable = false)
    private SecurityCompany securityCompany;
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;

    @OneToMany(mappedBy = "agreement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerAgreement> agreementServices = new ArrayList<>();

}

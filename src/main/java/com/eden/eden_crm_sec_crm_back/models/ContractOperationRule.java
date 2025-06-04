package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.enums.PresenceMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contract_operation_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractOperationRule {
    @Id
    @SequenceGenerator(name = "contract_operation_rule_seq",
            sequenceName = "contract_operation_rule_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contract_operation_rule_seq")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_contract_id")
    private CustomerContract customerAgreement;

    private Integer checkInBeforeMinutes;

    private Integer checkInAfterMinutes;

    private Integer checkOutBeforeMinutes;

    @Enumerated(EnumType.STRING)
    private PresenceMode presenceMode;

}

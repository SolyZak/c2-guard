package com.eden.eden_crm_sec_crm_back.models.lookup;

import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.enums.WeekDaysEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer_contract_operation_service")
@Setter
@Getter
public class LKCustomerContractOperationService extends BaseEntity<Long> {

    private Long quantity;
    @Enumerated(EnumType.STRING)
    private Set<WeekDaysEnum> days = new HashSet<>();
    private LocalTime fromTime;
    private LocalTime toTime;

}

package com.eden.eden_crm_sec_crm_back.dto.lookup;


import com.eden.eden_crm_sec_crm_back.base.dto.BaseDto;
import com.eden.eden_crm_sec_crm_back.enums.WeekDaysEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class LKCustomerContractOperationServiceDto extends BaseDto<Long> implements Serializable {
    private Long quantity;
    @Enumerated(EnumType.STRING)
    private Set<WeekDaysEnum> days ;
    private LocalTime fromTime;
    private LocalTime toTime;

}

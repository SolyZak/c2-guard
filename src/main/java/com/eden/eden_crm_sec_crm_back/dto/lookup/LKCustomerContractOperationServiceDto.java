package com.eden.eden_crm_sec_crm_back.dto.lookup;


import com.eden.eden_crm_sec_crm_back.base.dto.BaseDto;
import com.eden.eden_crm_sec_crm_back.enums.WeekDaysEnum;
import com.eden.eden_crm_sec_crm_back.payload.LocalTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
    private Set<WeekDaysEnum> days;
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime fromTime;
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime toTime;
    private Long siteDistributionId;

}

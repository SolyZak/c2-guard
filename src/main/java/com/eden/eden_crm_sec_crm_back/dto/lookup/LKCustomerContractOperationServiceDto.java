package com.eden.eden_crm_sec_crm_back.dto.lookup;

import com.eden.eden_crm_sec_crm_back.enums.WeekDaysEnum;
import com.eden.eden_crm_sec_crm_back.payload.LocalTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class LKCustomerContractOperationServiceDto {
    @NotNull(message = "{validation.distribution.details.quantity.required}")
    @Positive(message = "{validation.distribution.details.quantity.positive}")
    private Long quantity;

    @NotNull(message = "{validation.distribution.details.days.required}")
    @NotEmpty(message = "{validation.distribution.details.days.required}")
    @Enumerated(EnumType.STRING)
    private Set<WeekDaysEnum> days;

    @NotNull(message = "{validation.distribution.details.fromTime.required}")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime fromTime;

}

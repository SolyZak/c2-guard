package com.eden.eden_crm_sec_crm_back.dto.lookup;


import com.eden.eden_crm_sec_crm_back.base.dto.BaseDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;


import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class ServiceDetailsDto extends BaseDto<Long> implements Serializable {
    private int hours;
    private int days;
}

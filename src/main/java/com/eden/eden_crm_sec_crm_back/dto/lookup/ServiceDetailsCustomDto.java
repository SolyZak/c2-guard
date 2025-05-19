package com.eden.eden_crm_sec_crm_back.dto.lookup;


import com.eden.eden_crm_sec_crm_back.base.dto.BaseDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class ServiceDetailsCustomDto   implements Serializable {
    private Long hours;
    private Long days;
}

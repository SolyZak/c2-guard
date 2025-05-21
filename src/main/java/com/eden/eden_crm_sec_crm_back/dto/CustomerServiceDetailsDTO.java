package com.eden.eden_crm_sec_crm_back.dto;
import com.eden.eden_crm_sec_crm_back.base.dto.BaseDto;
import com.eden.eden_crm_sec_crm_back.dto.lookup.ServiceDetailsCustomDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class CustomerServiceDetailsDTO extends BaseDto<Long> implements Serializable {

    private String serviceName;
    private List<ServiceDetailsCustomDto> serviceDetails;
    public CustomerServiceDetailsDTO(Long id, String serviceName, List<ServiceDetailsCustomDto> serviceDetails) {
        this.setId(id); // from BaseDto
        this.serviceName = serviceName;
        this.serviceDetails = serviceDetails;
    }
}

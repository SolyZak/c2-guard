package com.eden.eden_crm_sec_crm_back.dto.lookup;


import com.eden.eden_crm_sec_crm_back.base.dto.BaseDto;
import com.eden.eden_crm_sec_crm_back.dto.CustomerServiceDetailsDTO;
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
public class LKCustomerContractServiceDto extends BaseDto<Long> implements Serializable {
    private Long quantity;
    private Long unitPrice;
    private CustomerServiceDetailsDTO customerService;
    private long customerContractId;

    public LKCustomerContractServiceDto(Long id, Long quantity, Long unitPrice,
                                        CustomerServiceDetailsDTO customerService, Long customerContractId) {
        this.setId(id); // from BaseDto
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.customerService = customerService;
        this.customerContractId = customerContractId;
    }
}

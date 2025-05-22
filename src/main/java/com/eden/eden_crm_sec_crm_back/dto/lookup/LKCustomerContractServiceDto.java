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
    private Long customerServiceId;
    private Long customerContractId;

    public LKCustomerContractServiceDto(Long id, Long quantity, Long unitPrice,
                                        Long customerServiceId, Long customerContractId) {
        this.setId(id);
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.customerServiceId = customerServiceId;
        this.customerContractId = customerContractId;
    }
}

package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.base.controller.BaseController;
import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.base.service.BaseService;
import com.eden.eden_crm_sec_crm_back.dto.CustomerServiceDTO;
import com.eden.eden_crm_sec_crm_back.dto.CustomerServiceDetailsDTO;
import com.eden.eden_crm_sec_crm_back.enums.ActivityEnum;
import com.eden.eden_crm_sec_crm_back.enums.UnitEnum;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerServiceMapper;
import com.eden.eden_crm_sec_crm_back.models.CustomerService;
import com.eden.eden_crm_sec_crm_back.service.CustomerServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/customers/service")
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceController extends BaseController<CustomerService, CustomerServiceDTO, Long> {

    private final CustomerServiceService customerServiceService;
    private final CustomerServiceMapper customerServiceMapper;

    @Override
    protected BaseService<CustomerService, Long> getService() {
        log.info("Getting service");
        return customerServiceService;
    }

    @Override
    protected BaseMapper<CustomerService, CustomerServiceDTO> getMapper() {
        log.info("Getting mapper");
        return customerServiceMapper;
    }

    @GetMapping("/activities-en")
    public List<String> getActivityEnumsEn() {
        return Arrays.stream(ActivityEnum.values())
                .map(ActivityEnum::getNameEn)
                .collect(Collectors.toList());
    }

    @GetMapping("/activities-ar")
    public List<String> getActivityEnumsAr() {
        return Arrays.stream(ActivityEnum.values())
                .map(ActivityEnum::getNameAr)
                .collect(Collectors.toList());
    }

    @GetMapping("/unit-en")
    public List<String> getUnitEnumsEn() {
        return Arrays.stream(UnitEnum.values())
                .map(UnitEnum::getNameEn)
                .collect(Collectors.toList());
    }

    @GetMapping("/unit-ar")
    public List<String> getUnitEnumsAr() {
        return Arrays.stream(UnitEnum.values())
                .map(UnitEnum::getNameAr)
                .collect(Collectors.toList());
    }
    @GetMapping("/all-services-details")
    public List<CustomerServiceDetailsDTO> getAllCustomerServices() {
        return customerServiceService.getAllCustomerServices();
    }
}

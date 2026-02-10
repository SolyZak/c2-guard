package com.eden.eden_crm_sec_crm_back.clients;

import com.eden.eden_crm_sec_crm_back.clients.dto.Currency;
import com.eden.eden_crm_sec_crm_back.clients.dto.SecurityCompanyData;
import com.eden.eden_crm_sec_crm_back.clients.dto.WorkforceFullDataDto;
import com.eden.eden_crm_sec_crm_back.dto.request.WorkforceLocationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${feign.clients.org-unit}", path = "/org-unit")
public interface OrgUnitClient {
    @GetMapping(value = "/external/security-companies/{id}")
    SecurityCompanyData getSecurityCompanyDetails(@RequestParam("id") Long id);

    @GetMapping(value = "/external/currencies/{id}")
    Currency getCurrencyDetails(@RequestParam("id") Long id);

    @GetMapping(value = "/external/workforce/{id}")
    WorkforceFullDataDto getWorkforceDetails(@RequestParam("id") Integer id);

    @PostMapping(value = "/external/workforce/{id}/location")
    void addWorkforceLocation(@PathVariable("id") Long id, @RequestBody WorkforceLocationRequest workforceLocationRequest);
}

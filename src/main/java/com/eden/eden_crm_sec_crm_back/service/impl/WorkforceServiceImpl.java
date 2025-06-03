package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.clients.OrgUnitClient;
import com.eden.eden_crm_sec_crm_back.clients.dto.WorkforceFullDataDto;
import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionServiceDto;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionWorkingPeriodDto;
import com.eden.eden_crm_sec_crm_back.enums.WeekDaysEnum;
import com.eden.eden_crm_sec_crm_back.enums.WorkingPeriodStatus;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerContractMapper;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerMapper;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerSiteMapper;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerSiteRepository;
import com.eden.eden_crm_sec_crm_back.service.WorkforceService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkforceServiceImpl implements WorkforceService {

    private final CustomerSiteRepository customerSiteRepository;
    private final CustomerContractRepository contractRepository;
    private final CustomerSiteMapper customerSiteMapper;
    private final CustomerMapper customerMapper;
    private final CustomerContractMapper customerContractMapper;
    private final OrgUnitClient orgUnitClient;

    @Override
    public List<GeneralDropdown> customersDropdown() {
        WorkforceFullDataDto workforceFullDataDto = getLoggedInWorkforce();
        return contractRepository.listCustomerBySecurityCompanyIdAndActiveToday(
                        workforceFullDataDto.securityCompany().id(), LocalDate.now()
                )
                .stream().map(customerMapper::toDropdown)
                .toList();
    }

    @Override
    public List<GeneralDropdown> customersContractDropdown(Long customerId) {
        WorkforceFullDataDto workforceFullDataDto = getLoggedInWorkforce();
        return contractRepository.listByCustomerIdSecurityCompanyIdAndActiveToday(
                        workforceFullDataDto.securityCompany().id(), customerId, LocalDate.now()
                )
                .stream().map(customerContractMapper::toDropdown)
                .toList();
    }

    @Override
    public List<WorkforceSiteDistributionDto> customersContractOperationSitesDropdown(Long customerId, Long contractId) {
        WorkforceFullDataDto workforceFullDataDto = getLoggedInWorkforce();
        CustomerContract contract = contractRepository.findByCustomerIdSecurityCompanyIdAndActiveToday(
                workforceFullDataDto.securityCompany().id(), customerId, contractId, LocalDate.now()
        ).orElseThrow(
                () -> new BusinessException(MessageUtil.getMessage("entity-not-found", new Object[]{MessageUtil.getMessage("contract")}), HttpStatus.NOT_FOUND)
        );
        WeekDaysEnum weekDaysEnum = Utils.getTodayWeekDayEnum();

        Map<CustomerSite, List<SiteDistribution>> groupedBySite = contract.getSiteDistributions().stream().filter(sd ->
                        sd.getOperationServices().stream().anyMatch(os -> os.getDays().contains(weekDaysEnum))
                )
                .collect(Collectors.groupingBy(SiteDistribution::getSite));
        List<WorkforceSiteDistributionDto> workforceSiteDistributions = new ArrayList<>();

        groupedBySite.forEach((site, distributions) -> {
            WorkforceSiteDistributionDto workforceSiteDistributionDto = WorkforceSiteDistributionDto.builder()
                    .id(site.getId())
                    .name(site.getName())
                    .services(distributions.stream().map(d -> {
                                ServiceDetails details = d.getLkCustomerContractService().getCustomerService();
                                return WorkforceSiteDistributionServiceDto.builder()
                                        .id(d.getLkCustomerContractService().getId())
                                        .name(details.getCustomerService().getServiceName())
                                        .hours(details.getHours())
                                        .days(details.getDays())
                                        .periods(
                                                d.getOperationServices().stream()
                                                .map(os -> WorkforceSiteDistributionWorkingPeriodDto.builder()
                                                        .fromTime(os.getFromTime())
                                                        .toTime(os.getToTime())
                                                        .status(WorkingPeriodStatus.IN_TIME)// todo need to be enhanced depends on contract agreement (Rules)
                                                        .build())
                                                .toList()
                                        )
                                        .build();
                            }).toList()
                    )
                    .build();
            workforceSiteDistributions.add(workforceSiteDistributionDto);
        });
        return workforceSiteDistributions;
    }

    @Override
    public List<GeneralDropdown> operationSitesDropdown() {
        return customerSiteRepository.findAll().stream().map(customerSiteMapper::toDropdown).toList();
    }

    private WorkforceFullDataDto getLoggedInWorkforce() {
        Integer workforceId = Utils.getLoggedInWorkforceId().intValue();
        try {
            // int, for better performance we can cache result here for some time like 15 minutes
            WorkforceFullDataDto workforceFullDataDto = orgUnitClient.getWorkforceDetails(workforceId);
            if (workforceFullDataDto == null) throw new Exception();
            return workforceFullDataDto;
        } catch (Exception e) {
            log.error("WorkforceServiceImpl::customersDropdown, Error while try to get workforce details from org unit, error: {}", e.getMessage());
            throw new UserNotProvided();
        }
    }
}

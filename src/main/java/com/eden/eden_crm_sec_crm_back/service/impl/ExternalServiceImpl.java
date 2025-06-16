package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.external.*;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.enums.WeekDaysEnum;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.mapper.ExternalMapper;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.dto.external.AttendanceWorkingPeriodData;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerSiteRepository;
import com.eden.eden_crm_sec_crm_back.repository.SiteDistributionRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractOperationServiceRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractServiceRepository;
import com.eden.eden_crm_sec_crm_back.service.ExternalService;
import com.eden.eden_crm_sec_crm_back.service.WorkforceService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExternalServiceImpl implements ExternalService {

    private final CustomerSiteRepository customerSiteRepository;
    private final CustomerRepository customerRepository;
    private final SiteDistributionRepository siteDistributionRepository;
    private final WorkforceService workforceService;
    private final ExternalMapper externalMapper;
    private final LKCustomerContractOperationServiceRepository contractOperationServiceRepository;
    private final LKCustomerContractServiceRepository lkCustomerContractServiceRepository;

    // This function will provide information abut operation site today status
    @Override
    public OperationSiteInfo getOperationSiteDetails(Long id) {
        CustomerSite operationSite = customerSiteRepository.findById(id).orElseThrow(
                () -> new BusinessException("Can`t find operation site by id: %d".formatted(id), HttpStatus.NOT_FOUND)
        );
        Optional<SiteDistribution> firstSiteDistributed = siteDistributionRepository.findByActiveTodayAndSiteId(id, LocalDate.now()).stream().findFirst();

        OperationSiteInfo.OperationSiteInfoBuilder operationSiteInfoBuilder = OperationSiteInfo.builder()
                .id(operationSite.getId())
                .name(operationSite.getName())
                .operationSiteName(operationSite.getName())
                .operationSiteId(operationSite.getId())
                .customerId(operationSite.getCustomer().getId())
                .customerName(operationSite.getCustomer().getName());

        if (firstSiteDistributed.isPresent()) {
            CustomerContract contract = firstSiteDistributed.get().getCustomerContract();
            operationSiteInfoBuilder
                    .contractName(contract.getAgreementName())
                    .contractId(contract.getId())
                    .securityCompanyId(contract.getSecurityCompanyId())
                    .securityCompanyName(contract.getSecurityCompanyName());
        }

        return operationSiteInfoBuilder.build();
    }

    @Override
    public List<OperationSiteData> getCustomerOperationSites(Long customerId) {
        return customerSiteRepository.findByCustomerId(customerId).stream().map(externalMapper::fromEntity).toList();
    }

    @Override
    public CustomerInfo getCustomerInfo(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new BusinessException("Can`t find customer by id: %d".formatted(id), HttpStatus.NOT_FOUND)
        );
        return CustomerInfo.builder()
                .id(customer.getId())
                .name(customer.getName())
                .code(customer.getCode())
                .build();
    }

    @Override
    public WorkforceSiteDistributionDto operationSiteServicesDropdown(Long id) {
        return workforceService.operationSiteServicesDropdown(id);
    }

    @Override
    public List<AttendanceStatsData> getAttendanceStats(AttendanceStatsDto dto) {
        List<AttendanceStatsData> attendanceStatsData = new ArrayList<>();
        Map<CustomerSite, Map<CustomerContract, List<SiteDistribution>>> groupedMap =
                siteDistributionRepository
                        .listSiteDistributionForStats(
                                dto.getFrom(),
                                dto.getTo(),
                                dto.getSecurityCompanyId(),
                                dto.getCustomerId(),
                                dto.getContractId(),
                                dto.getOperationSiteId()
                        )
                        .stream()
                        .collect(Collectors.groupingBy(
                                SiteDistribution::getSite,
                                Collectors.groupingBy(SiteDistribution::getCustomerContract)
                        ));

        for (Map.Entry<CustomerSite, Map<CustomerContract, List<SiteDistribution>>> siteEntry : groupedMap.entrySet()) {
            CustomerSite site = siteEntry.getKey();
            Map<CustomerContract, List<SiteDistribution>> contractMap = siteEntry.getValue();

            for (Map.Entry<CustomerContract, List<SiteDistribution>> contractEntry : contractMap.entrySet()) {
                CustomerContract contract = contractEntry.getKey();
                List<SiteDistribution> distributions = contractEntry.getValue();
                Map<WeekDaysEnum, Long> weekdayPlanned = new HashMap<>();
                distributions.stream().flatMap(d -> d.getOperationServices().stream()).forEach(os -> {
                    os.getDays().forEach(weekday -> weekdayPlanned.merge(weekday, os.getQuantity(), Long::sum));
                });
                attendanceStatsData.add(AttendanceStatsData.builder()
                        .operationSiteId(site.getId())
                        .operationSiteName(site.getName())
                        .contractId(contract.getId())
                        .contractName(contract.getAgreementName())
                        .securityCompanyId(contract.getSecurityCompanyId())
                        .securityCompanyName(contract.getSecurityCompanyName())
                        .totalAttended(0L)
                        .totalPlanned(distributions.stream().mapToLong(d -> d.getLkCustomerContractService().getQuantity()).sum())
                        .weekdayPlanned(weekdayPlanned)
                        .build());
            }
        }
        return attendanceStatsData;
    }

    @Override
    public List<AttendanceWorkingPeriodData> getAttendanceDateWorkingPeriod(Long customerId, Long contractId, Long operationSiteId, LocalDate date) {
        WeekDaysEnum todayWeekday = Utils.getWeekdayEnum(date);
        return contractOperationServiceRepository.findContractOperationServices(
                        customerId, operationSiteId, contractId
                ).stream()
                .filter(os -> os.getDays().contains(todayWeekday))
                .map(os -> AttendanceWorkingPeriodData.builder()
                        .id(os.getId())
                        .quantity(os.getQuantity())
                        .fromTime(os.getFromTime())
                        .toTime(os.getToTime())
                        .build())
                .toList();
    }

    @Override
    public List<ContractPlannedQntDto> getContractPlannedQnt(
            Long customerId, Long securityCompanyId, List<Long> contractId, LocalDate from, LocalDate to
    ) {
        if (from == null && to == null)
            return lkCustomerContractServiceRepository.sumPlannedQuantityByContract(customerId, securityCompanyId, contractId);
        if (from != null && to == null) {
            throw new BusinessException(MessageUtil.getMessage("to.required"), HttpStatus.BAD_REQUEST);
        }
        if (from != null && to.isBefore(from)) {
            throw new BusinessException(MessageUtil.getMessage("to.must-be-after-from"), HttpStatus.BAD_REQUEST);
        }
        return lkCustomerContractServiceRepository.sumPlannedQuantityByContract(customerId, securityCompanyId, contractId, from, to);
    }
}

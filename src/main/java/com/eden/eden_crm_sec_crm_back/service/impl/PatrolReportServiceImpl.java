package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.response.PremiseResponseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.PatrolReportResponseDto;
import com.eden.eden_crm_sec_crm_back.models.ContractOperationSiteDistributionPatrol;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.Premise;
import com.eden.eden_crm_sec_crm_back.models.projections.LocationProjection;
import com.eden.eden_crm_sec_crm_back.repository.ContractOperationSiteDistributionPatrolRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.repository.LocationRepository;
import com.eden.eden_crm_sec_crm_back.repository.PremiseRepository;
import com.eden.eden_crm_sec_crm_back.service.PatrolReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatrolReportServiceImpl implements PatrolReportService {

    private final CustomerContractRepository customerContractRepository;
    private final ContractOperationSiteDistributionPatrolRepository patrolRepository;
    private final PremiseRepository premiseRepository;
    private final LocationRepository locationRepository;

    public PatrolReportServiceImpl(CustomerContractRepository customerContractRepository,
                                   ContractOperationSiteDistributionPatrolRepository patrolRepository,
                                   PremiseRepository premiseRepository,
                                   LocationRepository locationRepository) {
        this.customerContractRepository = customerContractRepository;
        this.patrolRepository = patrolRepository;
        this.premiseRepository = premiseRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    @Transactional
    public PatrolReportResponseDto generatePatrolReport(Long securityCompanyId, Long contractId) {
        Optional<CustomerContract> contractOpt = customerContractRepository.findById(contractId);
        if (contractOpt.isEmpty() || !Objects.equals(contractOpt.get().getSecurityCompanyId(), securityCompanyId)) {
            throw new NoSuchElementException("Contract not found for given security company");
        }
        CustomerContract contract = contractOpt.get();

        List<ContractOperationSiteDistributionPatrol> patrols = patrolRepository.findAllByCustomerContractId(contract.getId());

        // gather locationIds from patrols
        Set<Long> locationIds = patrols.stream()
                .map(ContractOperationSiteDistributionPatrol::getLocation)
                .filter(Objects::nonNull)
                .map(Location::getId)
                .collect(Collectors.toSet());

        Map<Long, Long> locationToPremise = new HashMap<>();
        if (!locationIds.isEmpty()) {
            List<LocationProjection> locations = locationRepository.listAllLoggedInCustomerLocationsByIds(contract.getCustomer().getId(), locationIds);
            for (LocationProjection lp : locations) {
                if (lp.getPremise() != null) {
                    locationToPremise.put(lp.getId(), lp.getPremise().getId());
                }
            }
        }

        // prepare premises list (only those referenced)
        Set<Long> premiseIds = new HashSet<>(locationToPremise.values());
        List<PremiseResponseDto> premises;
        if (premiseIds.isEmpty()) {
            // fallback: all customer's premises
            List<Premise> customerPremises = premiseRepository.getCustomerPremises(contract.getCustomer().getId());
            premises = customerPremises.stream()
                    .map(p -> new PremiseResponseDto(p.getId(), p.getName(), p.getCode()))
                    .collect(Collectors.toList());
        } else {
            List<Premise> customerPremises = premiseRepository.findAllById(premiseIds);
            premises = customerPremises.stream()
                    .map(p -> new PremiseResponseDto(p.getId(), p.getName(), p.getCode()))
                    .collect(Collectors.toList());
        }

        Map<Long, List<Map<String, Object>>> patrolsByPremise = new HashMap<>();

        for (ContractOperationSiteDistributionPatrol p : patrols) {
            Long premiseId = null;
            if (p.getLocation() != null) premiseId = locationToPremise.get(p.getLocation().getPremise().getId());
            Long keyId = premiseId != null ? premiseId : Optional.ofNullable(p.getSite().getPremise().getId()).orElse(-1L);
            List<Map<String, Object>> list = patrolsByPremise.computeIfAbsent(keyId, k -> new ArrayList<>());
            Map<String, Object> entry = new HashMap<>();
            entry.put("patrolId", Optional.ofNullable(p.getPatrol().getId()).orElse(0L));
            entry.put("patrolName", Optional.ofNullable(p.getPatrol().getName()).orElse(""));
            entry.put("patrolStartDate", Optional.ofNullable(p.getStartDate()).map(Object::toString).orElse(null));
            entry.put("patrolFrequencyType", p.getPatrolFrequencyType());
            entry.put("patrolAssignedTasksCount", 0);
            entry.put("patrolFinishedTasksCount", 0);
            list.add(entry);
        }

        // convert map keys to strings to match requested JSON structure
        Map<String, List<Map<String, Object>>> patrolsByPremiseStringKey = new HashMap<>();
        for (Map.Entry<Long, List<Map<String, Object>>> e : patrolsByPremise.entrySet()) {
            patrolsByPremiseStringKey.put(String.valueOf(e.getKey()), e.getValue());
        }

        return new PatrolReportResponseDto(patrolsByPremiseStringKey, premises);
    }
}

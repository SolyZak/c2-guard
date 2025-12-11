package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.response.PremiseResponseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.PatrolReportResponseDto;
import com.eden.eden_crm_sec_crm_back.enums.TaskDistributionStatus;
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

import java.time.LocalDate;
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
        // also include premises referenced directly by patrol.site
        for (ContractOperationSiteDistributionPatrol p : patrols) {
            if (p.getSite() != null && p.getSite().getPremise() != null) {
                premiseIds.add(p.getSite().getPremise().getId());
            }
        }

        List<PremiseResponseDto> premises;
        if (premiseIds.isEmpty()) {
            // fallback: all customer's premises
            List<Premise> customerPremises = premiseRepository.getCustomerPremises(contract.getCustomer().getId());
            premises = customerPremises.stream()
                    .map(p -> new PremiseResponseDto(p.getId(), p.getName(), p.getCode(), Collections.emptyList()))
                    .collect(Collectors.toList());
        } else {
            List<Premise> customerPremises = premiseRepository.findAllById(premiseIds);
            premises = customerPremises.stream()
                    .map(p -> new PremiseResponseDto(p.getId(), p.getName(), p.getCode(), Collections.emptyList()))
                    .collect(Collectors.toList());
        }

        // Aggregate patrol distributions by premiseId and patrolId (one entry per patrol per premise)
        Map<Long, Map<Long, AggregatedPatrol>> aggByPremise = new HashMap<>();

        // determine premise id helper
        for (ContractOperationSiteDistributionPatrol p : patrols) {
            Long premiseId = null;
            if (p.getLocation() != null) {
                // use location id to lookup mapped premise
                premiseId = locationToPremise.get(p.getLocation().getId());
            }
            if (premiseId == null && p.getSite() != null && p.getSite().getPremise() != null) {
                premiseId = p.getSite().getPremise().getId();
            }
            if (premiseId == null) premiseId = -1L;

            Long patrolId = Optional.ofNullable(p.getPatrol()).map(pl -> pl.getId()).orElse(0L);

            Map<Long, AggregatedPatrol> inner = aggByPremise.computeIfAbsent(premiseId, k -> new HashMap<>());
            AggregatedPatrol agg = inner.get(patrolId);
            if (agg == null) {
                agg = new AggregatedPatrol();
                agg.patrolId = patrolId;
                agg.patrolName = Optional.ofNullable(p.getPatrol()).map(pl -> pl.getName()).orElse("");
                agg.patrolFrequencyType = p.getPatrolFrequencyType();
                agg.patrolStartDate = p.getStartDate();
                agg.assignedCount = 0;
                agg.finishedCount = 0;
                inner.put(patrolId, agg);
            }

            // increment assigned
            agg.assignedCount++;
            // increment finished if status is FINISHED
            if (p.getStatus() != null && p.getStatus().equals(TaskDistributionStatus.FINISHED.name())) {
                agg.finishedCount++;
            }
            // pick the smallest startDate
            if (p.getStartDate() != null) {
                if (agg.patrolStartDate == null || p.getStartDate().isBefore(agg.patrolStartDate)) {
                    agg.patrolStartDate = p.getStartDate();
                }
            }
            // ensure frequency and name are set if missing
            if ((agg.patrolFrequencyType == null || agg.patrolFrequencyType.isEmpty()) && p.getPatrolFrequencyType() != null) {
                agg.patrolFrequencyType = p.getPatrolFrequencyType();
            }
            if ((agg.patrolName == null || agg.patrolName.isEmpty()) && p.getPatrol() != null && p.getPatrol().getName() != null) {
                agg.patrolName = p.getPatrol().getName();
            }
        }

        // Build patrol summaries per premise
        Map<Long, List<com.eden.eden_crm_sec_crm_back.dto.response.PatrolSummaryDto>> patrolsPerPremise = new HashMap<>();
        for (Map.Entry<Long, Map<Long, AggregatedPatrol>> e : aggByPremise.entrySet()) {
            Long premiseId = e.getKey();
            List<com.eden.eden_crm_sec_crm_back.dto.response.PatrolSummaryDto> list = new ArrayList<>();
            for (AggregatedPatrol agg : e.getValue().values()) {
                com.eden.eden_crm_sec_crm_back.dto.response.PatrolSummaryDto summary = new com.eden.eden_crm_sec_crm_back.dto.response.PatrolSummaryDto(
                        Optional.ofNullable(agg.patrolId).orElse(0L),
                        Optional.ofNullable(agg.patrolName).orElse(""),
                        Optional.ofNullable(agg.patrolStartDate).map(LocalDate::toString).orElse(null),
                        agg.patrolFrequencyType,
                        agg.assignedCount,
                        agg.finishedCount
                );
                list.add(summary);
            }
            patrolsPerPremise.put(premiseId, list);
        }

        // attach patrols to premises
        List<com.eden.eden_crm_sec_crm_back.dto.response.PremiseResponseDto> premisesWithPatrols = premises.stream()
                .map(pr -> new com.eden.eden_crm_sec_crm_back.dto.response.PremiseResponseDto(
                        pr.id(), pr.name(), pr.code(), patrolsPerPremise.getOrDefault(pr.id(), Collections.emptyList())
                ))
                .collect(Collectors.toList());

        return new PatrolReportResponseDto(premisesWithPatrols);
    }

    // small holder for aggregation
    private static class AggregatedPatrol {
        Long patrolId;
        String patrolName;
        LocalDate patrolStartDate;
        String patrolFrequencyType;
        int assignedCount;
        int finishedCount;
    }
}

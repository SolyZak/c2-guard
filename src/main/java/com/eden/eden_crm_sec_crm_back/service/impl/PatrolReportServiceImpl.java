package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.PatrolReportRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.*;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.Patrol;
import com.eden.eden_crm_sec_crm_back.models.Premise;
import com.eden.eden_crm_sec_crm_back.models.projections.PatrolReportDetailsAggregation;
import com.eden.eden_crm_sec_crm_back.patrols.repositories.PatrolRepository;
import com.eden.eden_crm_sec_crm_back.repository.*;
import com.eden.eden_crm_sec_crm_back.models.projections.PatrolPremiseAggregation;
import com.eden.eden_crm_sec_crm_back.service.PatrolReportService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatrolReportServiceImpl implements PatrolReportService {

    private final CustomerContractRepository customerContractRepository;
    private final ContractOperationSiteDistributionPatrolRepository distributionPatrolRepository;
    private final PremiseRepository premiseRepository;
    private final LocationRepository locationRepository;
    private final PatrolRepository patrolRepository;

    @Override
    @Transactional
    public List<PatrolReportResponseDto> generatePatrolReport(PatrolReportRequest reportRequest) {
        Optional<CustomerContract> contractOpt = customerContractRepository.findById(reportRequest.contractId());
        if (contractOpt.isEmpty() || !Objects.equals(contractOpt.get().getSecurityCompanyId(), reportRequest.securityCompanyId()))
            throw new BusinessException(MessageUtil.getMessage("validation.security-company.contract-id.not-found"), HttpStatus.BAD_REQUEST);

        CustomerContract contract = contractOpt.get();
        List<PatrolPremiseAggregation> aggs = distributionPatrolRepository.aggregatePatrolsByPremiseAndPatrol(
                contract.getId(),
                reportRequest.premiseIds(),
                reportRequest.patrolIds(),
                reportRequest.locationIds(),
                reportRequest.fromDate(),
                reportRequest.toDate()
        );

        Set<Long> premiseIds = aggs.stream()
            .map(PatrolPremiseAggregation::getPremiseId)
            .collect(Collectors.toSet());

        List<Premise> premises = premiseRepository.findAllById(premiseIds);

        Map<Long, List<PatrolSummaryDto>> patrolsPerPremise = new HashMap<>();

        aggs.forEach(agg -> {
            Long premiseId = agg.getPremiseId();
            patrolsPerPremise
                .computeIfAbsent(premiseId, k -> new ArrayList<>())
                .add(
                    PatrolSummaryDto.builder()
                        .id(agg.getPatrolId())
                        .name(agg.getPatrolName())
                        .startDate(agg.getPatrolStartDate())
                        .frequencyType(agg.getPatrolFrequencyType())
                        .assignedTasksCount(agg.getAssignedCount())
                        .finishedTasksCount(agg.getFinishedCount())
                        .build()
                );
        });

        return premises.stream()
            .map(pr -> PatrolReportResponseDto.builder()
                .id(pr.getId())
                .name(pr.getName())
                .code(pr.getCode())
                .patrols(patrolsPerPremise.getOrDefault(pr.getId(), Collections.emptyList()))
                .build())
            .toList();
    }

    @Override
    public PatrolReportDetailsResponse getPatrolReportDetails(
            Long premiseId, Long patrolId) {
        // Migrated to new patrols module:
        // patrols/services/PatrolServiceImpl.getPatrolReportDetails()
        // Endpoint: /customer/patrols/report/premise/{id}/patrol/{id}
        throw new BusinessException(
                "This endpoint has been migrated to /customer/patrols/report",
                HttpStatus.GONE);
    }
}

package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.response.PatrolSummaryDto;
import com.eden.eden_crm_sec_crm_back.dto.response.PatrolReportResponseDto;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.Premise;
import com.eden.eden_crm_sec_crm_back.repository.ContractOperationSiteDistributionPatrolRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.repository.PatrolPremiseAggregation;
import com.eden.eden_crm_sec_crm_back.repository.PremiseRepository;
import com.eden.eden_crm_sec_crm_back.service.PatrolReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatrolReportServiceImpl implements PatrolReportService {

    private final CustomerContractRepository customerContractRepository;
    private final ContractOperationSiteDistributionPatrolRepository patrolRepository;
    private final PremiseRepository premiseRepository;

    @Override
    @Transactional
    public List<PatrolReportResponseDto> generatePatrolReport(Long securityCompanyId, Long contractId) {
        Optional<CustomerContract> contractOpt = customerContractRepository.findById(contractId);
        if (contractOpt.isEmpty() || !Objects.equals(contractOpt.get().getSecurityCompanyId(), securityCompanyId))
            throw new BusinessException("Contract not found for given security company", HttpStatus.BAD_REQUEST);

        CustomerContract contract = contractOpt.get();
        List<PatrolPremiseAggregation> aggs = patrolRepository.aggregatePatrolsByPremiseAndPatrol(contract.getId());

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
}

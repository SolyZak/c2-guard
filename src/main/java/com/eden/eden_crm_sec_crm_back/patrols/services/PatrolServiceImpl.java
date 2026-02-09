package com.eden.eden_crm_sec_crm_back.patrols.services;

import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.Patrol;
import com.eden.eden_crm_sec_crm_back.models.Premise;
import com.eden.eden_crm_sec_crm_back.models.projections.PatrolPremiseAggregation;
import com.eden.eden_crm_sec_crm_back.models.projections.PatrolReportDetailsAggregation;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.request.PatrolReportRequest;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.response.*;
import com.eden.eden_crm_sec_crm_back.patrols.mappers.PatrolReportMapper;
import com.eden.eden_crm_sec_crm_back.patrols.services.base.PatrolService;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.repository.LocationRepository;
import com.eden.eden_crm_sec_crm_back.repository.PatrolRepository;
import com.eden.eden_crm_sec_crm_back.repository.PremiseRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.PatrolTaskDistributionRepository;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatrolServiceImpl implements PatrolService {
    private final CustomerContractRepository customerContractRepository;
    private final PatrolTaskDistributionRepository patrolTaskDistributionRepository;
    private final PremiseRepository premiseRepository;
    private final LocationRepository locationRepository;
    private final PatrolRepository patrolRepository;
    private final PatrolReportMapper patrolReportMapper;

    @Override
    @Transactional
    public List<PatrolReportResponseDto> generatePatrolReport(PatrolReportRequest reportRequest) {
        Optional<CustomerContract> contractOpt = customerContractRepository.findById(reportRequest.contractId());
        if (contractOpt.isEmpty() || !Objects.equals(contractOpt.get().getSecurityCompanyId(), reportRequest.securityCompanyId()))
            throw new BusinessException(MessageUtil.getMessage("validation.security-company.contract-id.not-found"), HttpStatus.BAD_REQUEST);

        CustomerContract contract = contractOpt.get();
        List<PatrolPremiseAggregation> aggs = patrolTaskDistributionRepository.aggregatePatrolsByPremiseAndPatrol(
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

        Map<Long, List<PatrolSummaryDto>> patrolsPerPremise = aggs.stream()
                .collect(Collectors.groupingBy(
                        PatrolPremiseAggregation::getPremiseId,
                        Collectors.mapping(patrolReportMapper::toPatrolSummary, Collectors.toList())
                ));

        return premises.stream()
                .map(pr -> patrolReportMapper.toPatrolReportResponse(pr, patrolsPerPremise.get(pr.getId())))
                .toList();
    }

    @Override
    public PatrolReportDetailsResponse getPatrolReportDetails(Long premiseId, Long patrolId) {
        List<PatrolReportDetailsAggregation> taskDetails = patrolTaskDistributionRepository.findPatrolDetails(premiseId, patrolId);

        Optional<Patrol> patrolOpt = patrolRepository.findById(patrolId);
        Optional<Premise> premiseOpt = premiseRepository.findById(premiseId);

        if (patrolOpt.isEmpty() || premiseOpt.isEmpty())
            throw new BusinessException(MessageUtil.getMessage("validation.security-company.contract-id.not-found"), HttpStatus.BAD_REQUEST);

        Patrol patrol = patrolOpt.get();
        Premise premise = premiseOpt.get();

        List<Long> locationIds = taskDetails.stream()
                .map(PatrolReportDetailsAggregation::getLocationId)
                .toList();

        List<Location> locations = locationRepository.findAllById(locationIds);

        Map<Long, List<PatrolTaskDetailsResponse>> tasksPerLocation = taskDetails.stream()
                .map(patrolReportMapper::toTaskDetails)
                .collect(Collectors.groupingBy(PatrolTaskDetailsResponse::locationId));

        List<PatrolLocationDetailsResponse> locationDetails = locations.stream()
                .map(loc -> patrolReportMapper.toLocationDetails(loc, tasksPerLocation.get(loc.getId())))
                .toList();

        return patrolReportMapper.toPatrolReportDetails(premise, patrol, locationDetails);
    }
}

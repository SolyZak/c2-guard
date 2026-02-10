package com.eden.eden_crm_sec_crm_back.patrols.services;

import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.Patrol;
import com.eden.eden_crm_sec_crm_back.models.Premise;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.request.PatrolReportRequest;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.response.*;
import com.eden.eden_crm_sec_crm_back.patrols.mappers.PatrolReportMapper;
import com.eden.eden_crm_sec_crm_back.patrols.services.base.PatrolService;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.patrols.repositories.PatrolRepository;
import com.eden.eden_crm_sec_crm_back.repository.PremiseRepository;
import com.eden.eden_crm_sec_crm_back.patrols.repositories.projections.PatrolPremiseAggregation;
import com.eden.eden_crm_sec_crm_back.patrols.repositories.projections.PatrolReportDetailsAggregation;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PatrolServiceImpl implements PatrolService {
    private final CustomerContractRepository customerContractRepository;
    private final PatrolRepository patrolRepository;
    private final PremiseRepository premiseRepository;
    private final PatrolReportMapper patrolReportMapper;

    @Override
    @Transactional
    public List<PatrolReportResponseDto> generatePatrolReport(PatrolReportRequest reportRequest) {
        Optional<CustomerContract> contractOpt = customerContractRepository.findById(reportRequest.contractId());
        if (contractOpt.isEmpty() || !Objects.equals(contractOpt.get().getSecurityCompanyId(), reportRequest.securityCompanyId()))
            throw new BusinessException(MessageUtil.getMessage("validation.security-company.contract-id.not-found"), HttpStatus.BAD_REQUEST);

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startDateTime = OffsetDateTime.from(
                reportRequest.fromDate().atTime(LocalTime.MIN).atZone(now.getOffset())
        );
        OffsetDateTime endDateTime = OffsetDateTime.from(
                reportRequest.toDate().atTime(LocalTime.MAX).atZone(now.getOffset())
        );

        CustomerContract contract = contractOpt.get();
        List<PatrolPremiseAggregation> aggs = patrolRepository.aggregatePatrolsByPremiseAndPatrol(
                contract.getId(),
                startDateTime,
                endDateTime,
                reportRequest.premiseIds(),
                reportRequest.patrolIds(),
                reportRequest.locationIds()
        );
        return patrolReportMapper.toPatrolReportResponses(aggs);
    }

    @Override
    public PatrolReportDetailsResponse getPatrolReportDetails(Long premiseId, Long patrolId) {
        Optional<Premise> premiseOpt = premiseRepository.findById(premiseId);
        Optional<Patrol> patrolOpt = patrolRepository.findById(patrolId);
        if (patrolOpt.isEmpty() || premiseOpt.isEmpty())
            throw new BusinessException(MessageUtil.getMessage("validation.security-company.contract-id.not-found"), HttpStatus.BAD_REQUEST);
        
        Patrol patrol = patrolOpt.get();
        Premise premise = premiseOpt.get();
        List<PatrolReportDetailsAggregation> aggs = patrolRepository.findPatrolDetails(premiseId, patrolId);
        return patrolReportMapper.toPatrolReportDetails(premise, patrol, aggs);
    }
}

package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.PremiseRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.PremiseResponseDto;
import com.eden.eden_crm_sec_crm_back.exception.ValidationException;
import com.eden.eden_crm_sec_crm_back.mapper.PremiseMapper;
import com.eden.eden_crm_sec_crm_back.models.Premise;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.PremiseRepository;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PremiseServiceImpl {
    private final CustomerRepository customerRepository;

    private final PremiseRepository premiseRepository;
    private final PremiseMapper premiseMapper;
    private final Utils utils;

    public PremiseResponseDto addPremise(PremiseRequestDto requestDto) {
        //Customer customer = customerRepository.findById(getLoggedInCustomerId()).orElseThrow(UserNotProvided::new);
        Premise premise = premiseMapper.toEntity(requestDto);
        Optional<Premise> premiseExists = premiseRepository.findByCodeOrName(premise.getCode(), premise.getName());
        if (premiseExists.isPresent()) {
            throw new ValidationException("message", MessageUtil.getMessage("validation.premise.duplicate"));
        }

        premiseRepository.save(premise);

        return premiseMapper.fromEntity(premise);
    }

    public List<PremiseResponseDto> getPremises() {
        return premiseRepository.findAll().stream().map(premiseMapper::fromEntity).toList();
    }
}
